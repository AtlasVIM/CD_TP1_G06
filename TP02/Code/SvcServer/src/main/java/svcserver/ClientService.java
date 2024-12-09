package svcserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import svcclientstubs.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeoutException;

public class ClientService extends SvcClientServiceGrpc.SvcClientServiceImplBase {

    @Override
    public StreamObserver<UploadRequest> upload(StreamObserver<UploadResponse> responseObserver) {

        //return StreamObserver to client send request
        return new StreamObserver<UploadRequest>() {
            @Override
            public void onNext(UploadRequest uploadRequest) {

                var idRequest = uploadRequest.getId();
                if (!ProcessManager.processExists(idRequest)){
                    ProcessManager.addNewProcess(idRequest, uploadRequest.getTotalChunks());
                }

                byte[] chunk = uploadRequest.getUploadObject().toByteArray();
                try {

                    var process = ProcessManager.getProcess(idRequest);
                    assert process != null; //verifica objeto, se nulo dispara erro
                    var uploadRequestObject = process.getUploadRequestObject();
                    uploadRequestObject.write(chunk);

                    ProcessManager.setUploadRequestObject(idRequest, uploadRequestObject);
                    ProcessManager.setChunks(idRequest, uploadRequest.getChunkIndex()+1);

                    if (SvcServer.debugMode)
                        System.out.println("Request Id: " + uploadRequest.getId() + ", " + uploadRequest.getChunkIndex() + " of " + uploadRequest.getTotalChunks() + " received.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() {
                if (SvcServer.debugMode)
                    System.out.println("An Upload process has been completed! Beginning ");

                var process = ProcessManager.getProcessComplete(); //Retorna processo completo e que esta com o status inicial de RECEIVING
                assert process != null; //verifica objeto, se nulo dispara erro

                ProcessManager.setStatusUploadCompleted(process.getId()); //seta status UPLOAD_COMPLETED para evitar ser processado em outro Svc

                byte[] binData = process.getUploadRequestObject().toByteArray();

                // Desserializando o JSON para um objeto
                Gson js = new GsonBuilder().create();
                String newJsonString = new String(binData, StandardCharsets.UTF_8);
                ImageModel imageModel = js.fromJson(newJsonString, ImageModel.class);

                ProcessManager.setImageName(process.getId(), imageModel.getImageName()); //Seta o nome da Imagem na lista de Processos

                byte[] imageBytes = Base64.getDecoder().decode(imageModel.getImage());
                String destinoGlusterFS = "/var/sharedfiles/"+imageModel.getImageName();

                try (FileOutputStream fos = new FileOutputStream(destinoGlusterFS)) {
                    fos.write(imageBytes);
                    System.out.println("Image saved successfully: "+imageModel.getId()+", "+imageModel.getImageName());

                    UploadResponse resp = UploadResponse.newBuilder()
                                        .setIdRequest(imageModel.getId())
                                        .build();

                    responseObserver.onNext(resp);
                    responseObserver.onCompleted();
                    sendNewMessageToRabbitMQ(imageModel);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public void download(DownloadRequest request, StreamObserver<DownloadResponse> responseObserver) {
        var idRequest = request.getIdRequest();

        var process = ProcessManager.getProcess(idRequest);
        if (process == null){
            DownloadResponse responseError = DownloadResponse.newBuilder()
                    .setProcessCompleted(false)
                    .setMessage("Sorry! We couldn't find the process with Request Id: "+idRequest+". Please check and try again.")
                    .build();
            responseObserver.onNext(responseError);
            responseObserver.onCompleted();
        }
        else if (process.getStatus() != ProcessStatus.PROCESSED){ //Imagem ainda esta sendo processada
            DownloadResponse responseError = DownloadResponse.newBuilder()
                    .setProcessCompleted(false)
                    .setMessage("Sorry! We are still processing your request. Please try again later.")
                    .build();
            responseObserver.onNext(responseError);
            responseObserver.onCompleted();
        }
        else {

            String sourceGlusterFS = "/var/sharedfiles/" + process.getImageNameMarks();
            byte[] imageModelBytes = new byte[0];
            try {
                imageModelBytes = BuildImageModelInBytesWithImage(sourceGlusterFS);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                DownloadResponse responseError = DownloadResponse.newBuilder()
                        .setProcessCompleted(false)
                        .setMessage("Sorry! An unexpected error occurred")
                        .build();
                responseObserver.onNext(responseError);
                responseObserver.onCompleted();
            }

            int chunkSize = 1024; // Tamanho do bloco
            int totalChunks = (int) Math.ceil((double) imageModelBytes.length / chunkSize);
            int chunkIndex = 0;

            while (chunkIndex * chunkSize < imageModelBytes.length) {

                DownloadResponse response = CreateDownloadResponseWithChunk(imageModelBytes, chunkIndex++,
                        chunkSize, totalChunks);

                responseObserver.onNext(response);
            }

            responseObserver.onCompleted();
            System.out.println("Download Image has been completed.. Request id: " + idRequest);
        }
    }

    private DownloadResponse CreateDownloadResponseWithChunk(byte[] imageModelBytes, int chunkIndex, int chunkSize,
                                                              int totalChunks){
        int endIndex = Math.min((chunkIndex + 1) * chunkSize, imageModelBytes.length);
        byte[] chunk = new byte[endIndex - chunkIndex * chunkSize];
        System.arraycopy(imageModelBytes, chunkIndex * chunkSize, chunk, 0, chunk.length);

        return DownloadResponse.newBuilder()
                .setProcessCompleted(true)
                .setDownloadObject(ByteString.copyFrom(chunk))
                .setTotalChunks(totalChunks)
                .setChunkIndex(chunkIndex)
                .build();
    }

    public void sendNewMessageToRabbitMQ(ImageModel imageModel) throws IOException, TimeoutException {
        var message = imageModel.toString();
        SvcServer.channelRabbitMq.basicPublish("ExchangeD", "", true, null, message.getBytes());
        System.out.println("Svc Message Sent to RabbitMQ:" + message);
    }

    private byte[] BuildImageModelInBytesWithImage(String imagePath) throws FileNotFoundException {
        Gson gson = new GsonBuilder().create();
        File img = new File(imagePath);

        ImageModel imgObj = new ImageModel();
        imgObj.setImageName(img.getName());

        byte[] fileContent = new byte[(int) img.length()]; //Carrega imagem na memoria
        FileInputStream imgInputStream = new FileInputStream(img);

        try {
            imgInputStream.read(fileContent);
            imgInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String encodedString = Base64.getEncoder().encodeToString(fileContent);

        imgObj.setImage(encodedString);
        String base64Img = gson.toJson(imgObj);
        return base64Img.getBytes(StandardCharsets.UTF_8);
    }



}
