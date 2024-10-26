package primeserverapp;

import io.grpc.stub.StreamObserver;
import primeserverstubs.RingRequest;

public class RingMessageStream implements StreamObserver<RingRequest> {
    boolean completed=false;

    @Override
    public void onNext(RingRequest ringRequest) {
        System.out.println("PrimeServer Id: "+PrimeServer.uuid +" RingMessageStream next called.");
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("PrimeServer Id: "+PrimeServer.uuid +" RingMessageStream completed with error:"+throwable.getMessage());
        completed=true;
    }

    @Override
    public void onCompleted() {
        System.out.println("PrimeServer Id: "+PrimeServer.uuid +" RingMessageStream completed!");
        completed=true;
    }

    public boolean isCompleted() {
        return completed;
    }
}
