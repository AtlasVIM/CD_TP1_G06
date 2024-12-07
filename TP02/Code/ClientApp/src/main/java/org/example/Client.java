package org.example;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import registerclientstubs.*;
import svcclientstubs.*;
//import FileUtils.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Scanner;

public class Client {
    private static String managerIP;
    private static int managerPort;

    private static ManagedChannel registerChannel;
    private static ManagedChannel svcChannel;
    private static RegisterClientServiceGrpc.RegisterClientServiceBlockingStub registerBlockStub;
    private static SvcClientServiceGrpc.SvcClientServiceStub svcStub;

    public static void main(String[] args) {
        try {
            managerIP = args[0];
            managerPort = Integer.parseInt(args[1]);

            registerChannel = ManagedChannelBuilder
                    .forAddress(managerIP, managerPort)
                    .usePlaintext()
                    .build();


            registerBlockStub = RegisterClientServiceGrpc.newBlockingStub(registerChannel);

            System.out.println("Register Server at " + managerIP + ":" + managerPort + " connected");


            SvcServerAddress svcServerAddress = registerBlockStub.getSvcServer(VoidRequest.newBuilder().build());


            svcChannel = ManagedChannelBuilder
                    .forAddress(svcServerAddress.getIp(), svcServerAddress.getPort())
                    .usePlaintext()
                    .build();

            System.out.println("Connected to SVC Server at" + svcServerAddress.getIp() + ":" + svcServerAddress.getPort());

            svcStub = SvcClientServiceGrpc.newStub(svcChannel);
            while (true) {
                int choice = Menu();
                System.out.println("Menu choice: " + choice);
                switch (choice) {
                    case 0:
                        System.exit(0);
                        break;
                    case 1:
                        upload();
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

private static void upload() throws FileNotFoundException {

    System.out.println("Type image path: ");
    Scanner pathScanner = new Scanner(System.in);
    String imgPath = pathScanner.next();
    System.out.println("Type marks: ");
    Scanner markScanner = new Scanner(System.in);
    String[] marks = markScanner.next().split(" ");


    Gson gson = new GsonBuilder().create();

    File img = new File(imgPath);

    ImageModel imgObj = new ImageModel();
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
    byte[] binData = base64Img.getBytes(StandardCharsets.UTF_8);

        StreamObserver<UploadRequest> req = svcStub.upload(new StreamObserver<>() {
            @Override
            public void onNext(UploadResponse uploadResponse) {
                System.out.println("UPLOADING..." + " Request ID: " + uploadResponse.getNrRequest());
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("ERROR ON UPLOAD " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("UPLOAD COMPLETE");
            }
        });

        byte[] buffer = new byte[1024 * 1024];

        // FALTA FAZER STREAMING PARA O SVC (ON NEXT BUFFERED READING)


       /* try {

            while ((bytesRead = fileStream.read(buffer)) != -1) {
                UploadRequest chunkRequest = UploadRequest.newBuilder()
                       .setUploadObject(bi)
                        .build();
                req.onNext(chunkRequest);
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("An error occurred while uploading: " + e.getMessage());
            e.printStackTrace();
        }
        req.onCompleted();*/
    }

    private static void download() {

        System.out.println("Type image ID to download: ");
        Scanner idScanner = new Scanner(System.in);
        int id = idScanner.nextInt();
        DownloadRequest req = DownloadRequest
                .newBuilder()
                .setNrRequest(id)
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
