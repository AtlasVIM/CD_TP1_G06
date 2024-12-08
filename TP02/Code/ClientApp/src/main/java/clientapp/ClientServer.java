package clientapp;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import registerclientstubs.*;
import svcclientstubs.*;
//import FileUtils.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Scanner;
import java.util.UUID;

public class ClientServer {
    private static String managerIP;
    private static int managerPort;

    private static ManagedChannel registerChannel;
    private static ManagedChannel svcChannel;
    private static RegisterClientServiceGrpc.RegisterClientServiceBlockingStub registerBlockStub;
    private static SvcClientServiceGrpc.SvcClientServiceStub svcStub;
    private static SvcClientServiceGrpc.SvcClientServiceBlockingStub svcBlockingStub;

    public static void main(String[] args) {
        try {
            /*managerIP = args[0];
            managerPort = Integer.parseInt(args[1]);

            registerChannel = ManagedChannelBuilder
                    .forAddress(managerIP, managerPort)
                    .usePlaintext()
                    .build();


            registerBlockStub = RegisterClientServiceGrpc.newBlockingStub(registerChannel);
            System.out.println("Register Server at " + managerIP + ":" + managerPort + " connected");
            SvcServerAddress svcServerAddress = registerBlockStub.getSvcServer(VoidRequest.newBuilder().build());
*/

            svcChannel = ManagedChannelBuilder
                    //.forAddress(svcServerAddress.getIp(), svcServerAddress.getPort())
                    .forAddress("34.78.207.63", 8000)
                    .usePlaintext()
                    .build();

            //System.out.println("Connected to SVC Server at" + svcServerAddress.getIp() + ":" + svcServerAddress.getPort());
            System.out.println("Connected to SVC Server at localhost:50051");

            svcStub = SvcClientServiceGrpc.newStub(svcChannel);
            svcBlockingStub = SvcClientServiceGrpc.newBlockingStub(svcChannel);
            while (true) {
                int choice = Menu();
                System.out.println("Menu choice: " + choice);
                switch (choice) {
                    case 0:
                        System.exit(0);
                        break;
                    case 1:
                        Upload();
                        break;
                    case 2:
                        download();
                        break;
                    case 3:
                        newSvcServer();
                    default:
                        throw new IllegalStateException("Unexpected value: " + choice);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private static void Upload() throws FileNotFoundException {

        System.out.println("Type image path: ");
        Scanner pathScanner = new Scanner(System.in);
        String imgPath = pathScanner.next();
        System.out.println("Type marks: ");
        Scanner markScanner = new Scanner(System.in);
        String phrase = markScanner.nextLine();
        String[] marks = phrase.split(" ");

        var id = UUID.randomUUID().toString();
        var imageModelBytes = BuildImageModelInBytes(id, imgPath, marks);

        StreamObserver<UploadRequest> req = svcStub.upload(new StreamObserver<UploadResponse>() {
            @Override
            public void onNext(UploadResponse uploadResponse) {
                System.out.println(" ");
                System.out.println("We have successfully received your image and it will be processed. Please keep your Request Id: " + uploadResponse.getIdRequest());
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("ERROR ON UPLOAD " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Upload process has been completed");
            }
        });

        int chunkSize = 1024; // Tamanho do bloco
        int totalChunks = (int) Math.ceil((double) imageModelBytes.length / chunkSize);
        int chunkIndex = 0;

        //System.out.println("Uploading Image, please hold ...");

        while (chunkIndex * chunkSize < imageModelBytes.length) {
            int endIndex = Math.min((chunkIndex + 1) * chunkSize, imageModelBytes.length);
            byte[] chunk = new byte[endIndex - chunkIndex * chunkSize];
            System.arraycopy(imageModelBytes, chunkIndex * chunkSize, chunk, 0, chunk.length);

            UploadRequest uploadRequest = UploadRequest.newBuilder()
                        .setId(id)
                        .setUploadObject(ByteString.copyFrom(chunk))
                        .setTotalChunks(totalChunks)
                        .setChunkIndex(chunkIndex++)
                        .build();
            req.onNext(uploadRequest);
        }
        req.onCompleted();
        System.out.println("Upload Image has been complete. Waiting Request Id...");
    }

    private static void download() {

        System.out.println("Type image ID to download: ");
        Scanner idScanner = new Scanner(System.in);
        String requestId = idScanner.next();
        DownloadRequest req = DownloadRequest
                .newBuilder()
                .setIdRequest(requestId)
                .build();

        StreamObserver<DownloadResponse> res = new StreamObserver<>() {

            @Override
            public void onNext(DownloadResponse downloadResponse) {
                System.out.println("DOWNLOADING IMAGE ");
                try {
                    Gson gson = new Gson();
                    byte[] byteArr = downloadResponse.getDownloadObject().toByteArray();
                    String jsonString = new String(byteArr, StandardCharsets.UTF_8);
                    ImageModel downloadedImageObj = gson.fromJson(jsonString, ImageModel.class);

                    FileOutputStream outputStream = new FileOutputStream(downloadedImageObj.getImageName());

                    //FALTA FAZER O DOWNLOAD (BUFFERED WRITTING)

                } catch (Exception e) {
                    System.err.println("Error writing to file: " + e.getMessage());
                }

            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {

            }
        };

        try {

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private static byte[] BuildImageModelInBytes(String id, String imagePath, String[] marks) throws FileNotFoundException{
        Gson gson = new GsonBuilder().create();
        File img = new File(imagePath);

        ImageModel imgObj = new ImageModel();
        imgObj.setId(id);
        imgObj.setMarks(marks);
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

    private static void newSvcServer() {

    }


    private static int Menu() {
        int num;
        Scanner scan = new Scanner(System.in);
        do {
            System.out.println();
            System.out.println("  ---  MENU  ---  ");
            System.out.println("0 - EXIT SERVER");
            System.out.println("1 - UPLOAD PHOTO");
            System.out.println("2 - DOWNLOAD PHOTO");
            System.out.println("3 - REQUEST NEW SVC SERVER");
            num = scan.nextInt();
        } while (!(num >= 0 && num < 2));
        return num;
    }

}
