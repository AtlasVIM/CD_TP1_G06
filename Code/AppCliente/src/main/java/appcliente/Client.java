package appcliente;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import primeclientstubs.Number;
import ringmanagerclientstubs.*;
import primeclientstubs.*;

import java.util.Scanner;

public class Client {
    private static String clientIP = "localhost";
    private static int clientPort = 8088;


    private static ManagedChannel channel;
    private static RingManagerClientServiceGrpc.RingManagerClientServiceBlockingStub ringBlockStub;
    private static PrimeClientServiceGrpc.PrimeClientServiceBlockingStub primeServerBlockStub;

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

            ringBlockStub = RingManagerClientServiceGrpc.newBlockingStub(channel);

            while (true) {
                switch (Menu()) {
                    case 0:
                        System.exit(0);
                    case 1:
                        checkPrimality();
                    case 2:
                        PrimeServerAddress address = ringBlockStub.getPrimeServer(VoidRequest.newBuilder()
                                .build());

                        System.out.println("Server available in " + address.getIp() + " in port " + address.getPort());
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void checkPrimality() {
        System.out.println("Enter a number to check if it's prime: ");
        Scanner scanner = new Scanner(System.in);
        long num = scanner.nextLong();

        Number req = Number.newBuilder().setNumber(num).build();

        try {
            PrimalityResult res = primeServerBlockStub.isPrime(req);
            System.out.println("The number '" + num + "' is " + (res.getIsPrime() ? "prime!" : "not prime!"));
        } catch (Exception e) {
            System.err.println("AN ERROR HAS OCCURRED WHILE CHECKING PRIMALITY: " +e.getMessage());
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
            System.out.println("2 - REQUEST SERVER");
            num = scan.nextInt();
        } while (!(num == 0));
        return num;
    }

}
