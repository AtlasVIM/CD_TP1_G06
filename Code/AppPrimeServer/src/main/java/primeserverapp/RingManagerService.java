package primeserverapp;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import ringmanagerprimestubs.NextPrimeServerAddress;
import ringmanagerprimestubs.PrimeServerAddress;
import ringmanagerprimestubs.RingManagerPrimeServiceGrpc;

import java.util.Iterator;
import java.util.Objects;

public class RingManagerService extends RingManagerPrimeServiceGrpc.RingManagerPrimeServiceImplBase {

    private static RingManagerPrimeServiceGrpc.RingManagerPrimeServiceStub noBlockStub;
    private static ManagedChannel channel;

    public RingManagerService(ServerAddress myAddress, ServerAddress managerAddress) {
        channel = ManagedChannelBuilder
                .forAddress(managerAddress.ip, managerAddress.port)
                .usePlaintext()
                .build();

        System.out.println("PrimeServer Id: "+PrimeServer.uuid +" is connected to RingManager successfully");
    }

    static void registPrimeServer(ServerAddress myAddress){
        try {
            noBlockStub = RingManagerPrimeServiceGrpc.newStub(channel);

            PrimeServerAddress request = PrimeServerAddress
                    .newBuilder()
                    .setIp(myAddress.ip)
                    .setPort(myAddress.port)
                    .build();

            System.out.println("PrimeServer Id: "+PrimeServer.uuid +" sending registServer to Ring Manager");
            RegistPrimeServerStream response = new RegistPrimeServerStream();
            noBlockStub.registServer(request, response);

        }
        catch (Exception ex){
            System.out.println("MyId is: "+PrimeServer.uuid+" asynchronous call error: "+ex.getMessage());
        }
    }

}
