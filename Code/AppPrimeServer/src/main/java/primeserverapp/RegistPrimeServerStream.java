package primeserverapp;

import io.grpc.stub.StreamObserver;
import ringmanagerprimestubs.NextPrimeServerAddress;

public class RegistPrimeServerStream implements StreamObserver<NextPrimeServerAddress> {
    boolean completed=false;

    @Override
    public void onNext(NextPrimeServerAddress nextPrimeServerAddress) {
        System.out.println("RegistPrimeServer onNext. My next PrimeServer is: Ip:"+nextPrimeServerAddress.getNextIp() + " Port: "+ nextPrimeServerAddress.getNextPort());
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("RegistPrimeServer Completed with error:"+throwable.getMessage());
        completed=true;
    }

    @Override
    public void onCompleted() {
        System.out.println("RegistPrimeServer completed");
        completed=true;
    }

    public boolean isCompleted() {
        return completed;
    }
}
