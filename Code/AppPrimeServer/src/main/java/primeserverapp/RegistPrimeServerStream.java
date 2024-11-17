package primeserverapp;

import io.grpc.stub.StreamObserver;
import ringmanagerprimestubs.NextPrimeServerAddress;

import java.util.Objects;

public class RegistPrimeServerStream implements StreamObserver<NextPrimeServerAddress> {
    boolean completed=false;

    @Override
    public void onNext(NextPrimeServerAddress nextPrimeServerAddress) {
        System.out.println("RegistPrimeServer onNext. My next PrimeServer is: Ip:"+nextPrimeServerAddress.getNextIp() + " Port: "+ nextPrimeServerAddress.getNextPort());

        ServerAddress nextPrimeAddress = new ServerAddress(nextPrimeServerAddress.getNextIp(), nextPrimeServerAddress.getNextPort());

        if (!Objects.equals(PrimeServer.nextPrimeAddress, nextPrimeAddress)){
            //PrimeClientService.completeChannelWithNextPrimeServer();
            PrimeServer.nextPrimeAddress = nextPrimeAddress;
            PrimeClientService.openChannelNextPrimeServer();
        }
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("RegistPrimeServer onError. Completed with error: "+throwable.getMessage());
        throwable.printStackTrace();
        completed=true;
    }

    @Override
    public void onCompleted() {
        System.out.println("RegistPrimeServer onCompleted");
        completed=true;
    }

    public boolean isCompleted() {
        return completed;
    }
}
