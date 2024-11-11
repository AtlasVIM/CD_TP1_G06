package primeserverapp;

import io.grpc.stub.StreamObserver;
import primeclientstubs.Number;
import primeclientstubs.PrimalityResult;
import primeclientstubs.PrimeClientServiceGrpc;

public class ClientService extends PrimeClientServiceGrpc.PrimeClientServiceImplBase {


    @Override
    public void isPrime(Number request, StreamObserver<PrimalityResult> responseObserver){
        System.out.println("PrimeServer Id: "+PrimeServer.uuid +", method isPrime called! Number "+request.getNumber());

        var nrIsPrime = PrimeServer.getIsPrimeFromRedis(Long.toString(request.getNumber()));
        System.out.println("ClientService isPrime: var nrIsPrime "+ nrIsPrime);
        if (nrIsPrime == null) {
            //If not found answer in redis local, send to nextPrime
            PrimeClientService.sendMessageNextPrimeServerAsync(PrimeServer.uuid,
                    request.getNumber(),
                    false,
                    false);

            //esperar uns segundos e verificar se o numero tem no redis local
            for (int i = 0; i <= 30; i++) {
                try {
                    var getIsPrime = PrimeServer.getIsPrimeFromRedis(Long.toString(request.getNumber()));
                    if (getIsPrime == null) {
                        Thread.sleep(1 * 1000);
                    }
                    else {
                        PrimeServer.removePrimeCalculatorContainer();
                        responseObserver.onNext(PrimalityResult.newBuilder().setIsPrime(Boolean.getBoolean(nrIsPrime)).build());
                        responseObserver.onCompleted();
                        break;
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        else {
            responseObserver.onNext(PrimalityResult.newBuilder().setIsPrime(Boolean.getBoolean(nrIsPrime)).build());
            responseObserver.onCompleted();
        }
    }
}
