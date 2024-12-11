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
    public ByteArrayOutputStream someClass = new ByteArrayOutputStream(); //TODO nome temporario

    @Override
    public StreamObserver<UploadRequest> upload(StreamObserver<UploadResponse> responseObserver) {

        //return StreamObserver to client send request
        return new StreamObserver<UploadRequest>() {
            @Override
            public void onNext(UploadRequest uploadRequest) {
                //System.out.println("SvcServer receive call next");

                byte[] chunk = uploadRequest.getUploadObject().toByteArray();
                try {
                    someClass.write(chunk);
                    //System.out.println("Bloco do Id: " + uploadRequest.getId() + ", " + uploadRequest.getChunkIndex() + " de " + uploadRequest.getTotalChunks() + " recebido.");
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
                //System.out.println("Upload completo!");
                byte[] binData = someClass.toByteArray();

                // Desserializando o JSON para um objeto
                Gson js = new GsonBuilder().create();
                String newJsonString = new String(binData, StandardCharsets.UTF_8);
                ImageModel imageModel = js.fromJson(newJsonString, ImageModel.class);

                //System.out.println(imageModel.getId());

                byte[] imageBytes = Base64.getDecoder().decode(imageModel.getImage());
                String destinoGlusterFS = "/var/sharedfiles/"+imageModel.getImageName();

                try (FileOutputStream fos = new FileOutputStream(destinoGlusterFS)) {
                    fos.write(imageBytes);
                    System.out.println("Svc Image saved successfully: "+imageModel.getId()+", "+imageModel.getImageName());

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

        //TODO buscar nome imagem lista processos
        //TODO verificar se processo foi finalizado

        String sourceGlusterFS = "/var/sharedfiles/"+imageModel.getImageName();
        byte[] imageModelBytes = new byte[0];
        try {
            imageModelBytes = BuildImageModelInBytesWithImage(sourceGlusterFS);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            DownloadResponse responseError = DownloadResponse.newBuilder()
                    .setProcessCompleted(false)
                    .setMessage("An unexpected error occurred")
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
        System.out.println("Download Image has been complete.. Request id: "+idRequest);
    }

    private static DownloadResponse CreateDownloadResponseWithChunk(byte[] imageModelBytes, int chunkIndex, int chunkSize,
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

    private static byte[] BuildImageModelInBytesWithImage(String imagePath) throws FileNotFoundException {
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
