package primeserverapp;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import ringmanagerprimestubs.NextPrimeServerAddress;
import ringmanagerprimestubs.PrimeServerAddress;
import ringmanagerprimestubs.RingManagerPrimeServiceGrpc;

import java.util.Iterator;
import java.util.Objects;

public class RingManagerService extends RingManagerPrimeServiceGrpc.RingManagerPrimeServiceImplBase {

    private static RingManagerPrimeServiceGrpc.RingManagerPrimeServiceBlockingStub blockingStub;
    private static ManagedChannel channel;

    public RingManagerService(ServerAddress myAddress, ServerAddress managerAddress) {
        System.out.println("PrimeServer Id: "+PrimeServer.uuid +" is connecting to RingManager at " + managerAddress.ip + ":" + managerAddress.port);

        channel = ManagedChannelBuilder
                .forAddress(managerAddress.ip, managerAddress.port)
                .usePlaintext()
                .build();

        System.out.println("PrimeServer Id: "+PrimeServer.uuid +" is connected");
        registPrimeServer(myAddress);
    }

    static void registPrimeServer(ServerAddress myAddress){
        try {
            blockingStub = RingManagerPrimeServiceGrpc.newBlockingStub(channel);

            PrimeServerAddress request = PrimeServerAddress
                    .newBuilder()
                    .setIp(myAddress.ip)
                    .setPort(myAddress.port)
                    .build();

            Iterator<NextPrimeServerAddress> response = blockingStub.registServer(request);
            while (response.hasNext()) {
                ServerAddress nextPrimeAddress = new ServerAddress(response.next().getNextIp(), response.next().getNextPort());
                if (!Objects.equals(PrimeServer.nextPrimeAddress, nextPrimeAddress)){
                    PrimeClientService.completeChannelWithNextPrimeServer();
                    PrimeServer.nextPrimeAddress = nextPrimeAddress;
                    PrimeClientService.openChannelNextPrimeServer();
                }

                System.out.println("MyId is: "+PrimeServer.uuid+" NextPrimeServer is: " +nextPrimeAddress.ip +":"+nextPrimeAddress.port );
            }
        }
        catch (Exception ex){
            System.out.println("MyId is: "+PrimeServer.uuid+" Synchronous call error: "+ex.getMessage());
        }
    }

}
