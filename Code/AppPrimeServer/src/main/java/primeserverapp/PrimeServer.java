package primeserverapp;

import io.grpc.ManagedChannel;
import io.grpc.ServerBuilder;
import primeclientstubs.PrimeClientServiceGrpc;

public class PrimeServer {

    private static String myIP = "localhost";
    //private static String svcIP = "35.246.73.129";
    private static int myPort = 8500;
    private static ManagedChannel channel;
    private static PrimeClientServiceGrpc.PrimeClientServiceBlockingStub blockingStub;
    private static PrimeClientServiceGrpc.PrimeClientServiceStub noBlockStub;

    public static void main(String[] args) {
        try {
            if (args.length > 0) {
                myPort = Integer.parseInt(args[0]);
            }

            io.grpc.Server svc = ServerBuilder
                    .forPort(myPort)
                    .addService(new ClientService(myPort))
                    .build();
            svc.start();
            System.out.println(String.format("PrimeServer started. Listening on Port: %s ", myPort));

            svc.awaitTermination();
            svc.shutdown();

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
