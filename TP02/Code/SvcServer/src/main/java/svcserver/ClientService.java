package svcserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.grpc.stub.StreamObserver;
import svcclientstubs.*;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
        super.download(request, responseObserver);
    }

    public void sendNewMessageToRabbitMQ(ImageModel imageModel) throws IOException, TimeoutException {
        var message = imageModel.toString();
        SvcServer.channelRabbitMq.basicPublish("ExchangeD", "", true, null, message.getBytes());
        System.out.println("Svc Message Sent to RabbitMQ:" + message);
    }

}
