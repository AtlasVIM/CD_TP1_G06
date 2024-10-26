package appcliente;

import calcstubs.CalcServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Scanner;

public class Client {
    private static String clientIP = "localhost";
    private static int clientPort = 8500;
    private static ManagedChannel channel;
    private static CalcServiceGrpc.CalcServiceBlockingStub blockingStub;
    private static CalcServiceGrpc.CalcServiceStub noBlockStub;
    public static void main(String[] args) {
        try {
            if (args.length == 2) {
                clientIP = args[0];
                clientPort = Integer.parseInt(args[1]);
            }
            System.out.println("Connecting to "+clientIP+":"+ clientPort);
            channel = ManagedChannelBuilder.forAddress(clientIP, clientPort)
                    .usePlaintext()
                    .build();

            while (true) {
                switch (Menu()) {
                    case 0:
                        System.exit(0);
                    case 1:
                        //prime;
                    case 2:
                        //server info
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static int Menu() {
        int num;
        Scanner scan = new Scanner(System.in);
        do {
            System.out.println();
            System.out.println("  ---  MENU  ---  ");
            System.out.println("0 - EXIT SERVER");
            System.out.println("1 - PRIME NUMBER CLASSIFIER");
            System.out.println("2 - SERVER INFO");
            num = scan.nextInt();
        } while (!(num == 0));
        return num;
    }

}
