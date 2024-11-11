package primeserverapp;

import io.grpc.stub.StreamObserver;
import primeserverstubs.VoidResponse;

public class RingMessageStream implements StreamObserver<VoidResponse> {
    boolean completed=false;

    @Override
    public void onNext(VoidResponse voidResponse) {
        System.out.println("RingMessageStream onNext called.");
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("RingMessageStream onError. Details: "+throwable.getMessage());
        throwable.printStackTrace();
        completed=true;
    }

    @Override
    public void onCompleted() {
        System.out.println("RingMessageStream onCompleted called.");
        completed=true;
    }

    public boolean isCompleted() {
        return completed;
    }
}
