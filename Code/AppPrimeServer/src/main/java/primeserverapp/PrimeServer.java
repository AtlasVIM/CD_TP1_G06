package primeserverapp;

import io.grpc.ManagedChannel;
import io.grpc.ServerBuilder;
import primeclientstubs.PrimeClientServiceGrpc;

import java.util.UUID;

public class PrimeServer {

    public static UUID uuid = UUID.randomUUID();
    public static ServerAddress nextPrimeAddress = new ServerAddress("localhost", 8502);
    public static ServerAddress redisAddress = new ServerAddress("localhost", 8503);
    private static ServerAddress myAddress = new ServerAddress("localhost", 8500);
    //private static String svcIP = "35.246.73.129";
    private static ServerAddress managerAddress = new ServerAddress("localhost", 8501);
    private static ManagedChannel channel;
    private static PrimeClientServiceGrpc.PrimeClientServiceBlockingStub blockingStub;
    private static PrimeClientServiceGrpc.PrimeClientServiceStub noBlockStub;

    public static void main(String[] args) {
        try {
            if (args.length > 0) {
                myAddress.port = Integer.parseInt(args[0]);
            }

            io.grpc.Server svc = ServerBuilder
                    .forPort(myAddress.port)
                    .addService(new ClientService(myAddress.port))
                    .addService(new PrimeClientService(myAddress.port))
                    .addService(new RingManagerService(myAddress, managerAddress))
                    .build();
            svc.start();
            System.out.println(String.format("PrimeServer %s started. Listening on Port: %s ", uuid, myAddress.port));

            svc.awaitTermination();
            svc.shutdown();

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
