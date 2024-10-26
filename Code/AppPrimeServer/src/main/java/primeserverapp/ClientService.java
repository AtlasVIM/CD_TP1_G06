package primeserverapp;

import io.grpc.stub.StreamObserver;
import primeclientstubs.Number;
import primeclientstubs.PrimalityResult;
import primeclientstubs.PrimeClientServiceGrpc;

public class ClientService extends PrimeClientServiceGrpc.PrimeClientServiceImplBase {

    public ClientService(int svcPort){
        System.out.println("ClientService is available on port:" + svcPort);
    }

    @Override
    public void isPrime(Number request, StreamObserver<PrimalityResult> responseObserver){
        System.out.println("PrimeServer Id: "+PrimeServer.uuid +", method isPrime called! Number "+request.getNumber());

        responseObserver.onNext(PrimalityResult.newBuilder().setIsPrime(false).build());
        responseObserver.onCompleted();
    }
}
