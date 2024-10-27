package primeserverapp;

import io.grpc.stub.StreamObserver;
import primeserverstubs.VoidResponse;

public class RingMessageStream implements StreamObserver<VoidResponse> {
    boolean completed=false;

    @Override
    public void onNext(VoidResponse voidResponse) {
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
