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
    private static boolean debugMode = true;
    private static String registerIP;
    private static int registerPort;

    private static ManagedChannel registerChannel;
    private static ManagedChannel svcChannel;
    private static RegisterClientServiceGrpc.RegisterClientServiceBlockingStub registerBlockStub;
    private static SvcClientServiceGrpc.SvcClientServiceStub svcStub;
    private static SvcClientServiceGrpc.SvcClientServiceBlockingStub svcBlockingStub;

    public static void main(String[] args) {
        try {
            if (args.length == 2) {
                registerIP = args[0];
                registerPort = Integer.parseInt(args[1]);
            }

            registerChannel = ManagedChannelBuilder
                    .forAddress(registerIP, registerPort)
                    .usePlaintext()
                    .build();


            registerBlockStub = RegisterClientServiceGrpc.newBlockingStub(registerChannel);
            System.out.println("Register Server at " + registerIP + ":" + registerPort + " connected");
            SvcServerAddress svcServerAddress = registerBlockStub.getSvcServer(VoidRequest.newBuilder().build());

            svcChannel = ManagedChannelBuilder
                    .forAddress(svcServerAddress.getIp(), svcServerAddress.getPort())
                    .usePlaintext()
                    .build();

            System.out.println("Connected to SVC Server at " + svcServerAddress.getIp() + ":" + svcServerAddress.getPort());

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
        String phrase = markScanner.nextLine();
        String[] marks = phrase.split(" ");

        var id = UUID.randomUUID().toString();
        var imageModelBytes = buildImageModelInBytes(id, imgPath, marks);

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
            }
        });

        int chunkSize = 1024; // Tamanho do bloco
        int totalChunks = (int) Math.ceil((double) imageModelBytes.length / chunkSize);
        int chunkIndex = 0;

        //System.out.println("Uploading Image, please hold ...");

        while (chunkIndex * chunkSize < imageModelBytes.length) {

            UploadRequest uploadRequest = createUploadRequestWithChunk(imageModelBytes, chunkIndex++,
                    chunkSize, id, totalChunks);

            req.onNext(uploadRequest);
        }
        req.onCompleted();
        System.out.println("Upload Image has been complete. Waiting Request Id...");
    }

    private static UploadRequest createUploadRequestWithChunk(byte[] imageModelBytes, int chunkIndex, int chunkSize,
                                                     String id, int totalChunks){
        int endIndex = Math.min((chunkIndex + 1) * chunkSize, imageModelBytes.length);
        byte[] chunk = new byte[endIndex - chunkIndex * chunkSize];
        System.arraycopy(imageModelBytes, chunkIndex * chunkSize, chunk, 0, chunk.length);

        return UploadRequest.newBuilder()
                .setId(id)
                .setUploadObject(ByteString.copyFrom(chunk))
                .setTotalChunks(totalChunks)
                .setChunkIndex(chunkIndex)
                .build();
    }

    private static void download() {

        System.out.println("Type image ID to download: ");
        Scanner idScanner = new Scanner(System.in);
        String requestId = idScanner.next();
        System.out.println("Type path to store file");
        Scanner pathScanner = new Scanner(System.in);
        String path = pathScanner.nextLine();
        DownloadRequest req = DownloadRequest
                .newBuilder()
                .setIdRequest(requestId)
                .build();
        StreamObserver<DownloadResponse> res = new StreamObserver<>() {
            FileOutputStream fileOutputStream;
            BufferedOutputStream bufferedOutputStream;

            @Override
            public void onNext(DownloadResponse downloadResponse) {

                try {
                    if (downloadResponse.getProcessCompleted()) {
                        Gson gson = new Gson();
                        byte[] byteArr = downloadResponse.getDownloadObject().toByteArray();
                        String jsonString = new String(byteArr, StandardCharsets.UTF_8);
                        ImageModel downloadedImageObj = gson.fromJson(jsonString, ImageModel.class);
                        if (debugMode) {
                            System.out.println(
                                    "DOWNLOADING IMAGE "
                                            + downloadedImageObj.getImageName() + ": "
                                            + " CHUNK "
                                            + downloadResponse.getChunkIndex()
                                            + " OUT OF "
                                            + downloadResponse.getTotalChunks()
                            );
                        }

                        if (fileOutputStream == null && bufferedOutputStream == null) {

                            File downloadedImage = new File(path, downloadedImageObj.getImageName());

                            fileOutputStream = new FileOutputStream(downloadedImage, true);
                            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

                        }
                        byte[] imgData = downloadResponse.getDownloadObject().toByteArray();

                        bufferedOutputStream.write(imgData);
                        bufferedOutputStream.flush();
                    } else {
                        System.out.println(downloadResponse.getMessage());
                    }

                } catch (Exception e) {
                        e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("DOWNLOAD ERROR: " + throwable.getMessage());
                try {
                    if (bufferedOutputStream != null && fileOutputStream != null) {
                    bufferedOutputStream.close();
                    fileOutputStream.close();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onCompleted() {
                //System.out.println("DOWNLOAD COMPLETED");
                try {
                    if (bufferedOutputStream != null && fileOutputStream != null) {
                        bufferedOutputStream.close();
                        fileOutputStream.close();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }


        };

        svcStub.download(req,res);


    }

    private static byte[] buildImageModelInBytes(String id, String imagePath, String[] marks) throws FileNotFoundException{
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
        if (svcChannel != null && !svcChannel.isShutdown()) {
            System.out.println(" SHUTTING DOWN CONNECTION WITH SVC SERVER");
            svcChannel.shutdownNow();
            SvcServerAddress svcServerAddress = registerBlockStub.getSvcServer(VoidRequest.newBuilder().build());


            svcChannel = ManagedChannelBuilder
                    .forAddress(svcServerAddress.getIp(), svcServerAddress.getPort())
                    .usePlaintext()
                    .build();

            System.out.println("Connected to new SVC Server at" + svcServerAddress.getIp() + ":" + svcServerAddress.getPort());

            svcStub = SvcClientServiceGrpc.newStub(svcChannel);
            svcBlockingStub = SvcClientServiceGrpc.newBlockingStub(svcChannel);
        } else if (svcChannel == null) {
            SvcServerAddress svcServerAddress = registerBlockStub.getSvcServer(VoidRequest.newBuilder().build());


            svcChannel = ManagedChannelBuilder
                    .forAddress(svcServerAddress.getIp(), svcServerAddress.getPort())
                    .usePlaintext()
                    .build();

            System.out.println("Connected to new SVC Server at" + svcServerAddress.getIp() + ":" + svcServerAddress.getPort());

            svcStub = SvcClientServiceGrpc.newStub(svcChannel);
            svcBlockingStub = SvcClientServiceGrpc.newBlockingStub(svcChannel);
        }
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
        } while (!(num >= 0 && num < 4));
        return num;
    }

}
