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


    private static ManagedChannel channelPrimeServer;
    private static ManagedChannel channelRingManager;
    private static RingManagerClientServiceGrpc.RingManagerClientServiceBlockingStub ringBlockStub;
    private static PrimeClientServiceGrpc.PrimeClientServiceBlockingStub primeServerBlockStub;


    public static void main(String[] args) {
        try {
            if (args.length == 2) {
                clientIP = args[0];
                clientPort = Integer.parseInt(args[1]);
                ServerAddress ringManagerAddress = new ServerAddress(clientIP, clientPort);
            }
            System.out.println("Connecting to Ring Manager in "+clientIP+":"+ clientPort);

            channelRingManager = ManagedChannelBuilder
                    .forAddress(clientIP, clientPort)
                    .usePlaintext()
                    .build();



            ringBlockStub = RingManagerClientServiceGrpc.newBlockingStub(channelRingManager);

            PrimeServerAddress address = ringBlockStub.getPrimeServer(VoidRequest.newBuilder()
                    .build());
            System.out.println("Server available in " + address.getIp() + " in port " + address.getPort());

            System.out.println("Connecting to Prime Server...");


            channelPrimeServer = ManagedChannelBuilder
                    .forAddress(address.getIp(), address.getPort())
                    .usePlaintext()
                    .build();

            System.out.println("Connected!");

            primeServerBlockStub = PrimeClientServiceGrpc.newBlockingStub(channelPrimeServer);
        //Teste de conexao com ring manager e prime server foi bem sucedido
            // Tem apenas um erro, caso algum cliente se desconecte, o numero de clientes dentro do prime server nao diminui.
            // Isto significa que quando um novo cliente se conecta pode nao estar a ser conectar com o server com menos pessoas.

            while (true) {
                switch (Menu()) {
                    case 0:
                        System.exit(0);
                        break;
                    case 1:
                        checkPrimality();
                        break;
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
            num = scan.nextInt();
        } while (!(num == 0));
        return num;
    }

}
