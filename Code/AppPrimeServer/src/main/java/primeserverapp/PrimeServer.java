package primeserverapp;

import primeclientstubs.*;
import io.grpc.ManagedChannel;

public class PrimeServer {

    private static String svcIP = "localhost";
    //private static String svcIP = "35.246.73.129";
    private static int svcPort = 8500;
    private static ManagedChannel channel;
    private static PrimeClientServiceGrpc.PrimeClientServiceBlockingStub blockingStub;
    private static PrimeClientServiceGrpc.PrimeClientServiceStub noBlockStub;

    public static void main(String[] args) {
        try{
            if(args.length == 2){
                svcIP = args[0];
                svcPort = Integer.parseInt(args[1]);
            }

            System.out.println("PrimeServer. IP: {} ")

        }
    }
}
