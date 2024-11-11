package appcliente;

import io.grpc.stub.StreamObserver;
import primeclientstubs.PrimalityResult;


public class IsPrimeStream implements StreamObserver<PrimalityResult> {
    boolean completed=false;

    @Override
    public void onNext(PrimalityResult primalityResult) {
        System.out.println("IsPrimeStream onNext. We have answer! ");

        System.out.println("The number '' is " + (primalityResult.getIsPrime() ? "prime!" : "not prime!"));
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("IsPrimeStream onError. Details: "+throwable.getMessage());
        throwable.printStackTrace();
        completed=true;

    }

    @Override
    public void onCompleted() {
        System.out.println("IsPrimeStream onCompleted called.");
        completed=true;
    }

    public boolean isCompleted() {
        return completed;
    }
}
