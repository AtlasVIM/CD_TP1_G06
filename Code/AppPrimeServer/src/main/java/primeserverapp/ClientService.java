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

        var nrIsPrime = PrimeServer.getIsPrimeFromRedis(Long.toString(request.getNumber()));
        if (nrIsPrime.isBlank()){
            PrimeClientService.sendMessageNextPrimeServerAsync(PrimeServer.uuid,
                    request.getNumber(),
                    false,
                    false);
            //todo enviar esse numero calculado de volta pro client
            //possivel solução: colocar um while consultando o dicionario redis, a cada 30seg.
        }
        else {
            responseObserver.onNext(PrimalityResult.newBuilder().setIsPrime(Boolean.getBoolean(nrIsPrime)).build());
            responseObserver.onCompleted();
        }
    }
}
