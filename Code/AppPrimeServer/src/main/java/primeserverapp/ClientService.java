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
        if (nrIsPrime == null) {
            //If not found answer in redis local, send to nextPrime
            PrimeClientService.sendMessageNextPrimeServerAsync(PrimeServer.uuid,
                    request.getNumber(),
                    false,
                    false);

            //Wait and check if it has the answer on Redis local
            for (int i = 0; i <= 30; i++) {
                try {
                    nrIsPrime = PrimeServer.getIsPrimeFromRedis(Long.toString(request.getNumber()));
                    if (nrIsPrime == null) {
                        Thread.sleep(1 * 1000);
                    }
                    else {
                        System.out.println("We have answer! After sendMessageNextPrimeServerAsync. Number: "+request.getNumber() +" isPrime: "+nrIsPrime);
                        PrimeServer.removePrimeCalculatorContainer();

                        var response = PrimalityResult.newBuilder().setIsPrime(Boolean.parseBoolean(nrIsPrime)).build();
                        System.out.println("Returning response: "+response.getIsPrime());
                        responseObserver.onNext(response);
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("Error returning answer to client. Details: "+e.getMessage());
                    e.printStackTrace();
                }
            }

            responseObserver.onCompleted();
            System.out.println("Response was send. ");
        }
        else {
            System.out.println("We have answer in my local Redis! Number: "+request.getNumber() +" isPrime: "+nrIsPrime);
            try {
                var response = PrimalityResult.newBuilder().setIsPrime(Boolean.parseBoolean(nrIsPrime)).build();
                System.out.println("Returning response: "+response.getIsPrime());
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                System.out.println("Response was send. ");
            }
            catch (Exception ex){
                System.out.println("Error returning answer to client. Details: "+ex.getMessage());
                ex.printStackTrace();
            }

        }
    }
}
