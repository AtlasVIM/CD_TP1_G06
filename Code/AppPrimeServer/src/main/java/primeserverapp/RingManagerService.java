package primeserverapp;

import io.grpc.stub.StreamObserver;
import ringmanagerprimestubs.*;

public class RingManagerService extends RingManagerPrimeServiceGrpc.RingManagerPrimeServiceImplBase {


    public RingManagerService(int svcPort) {
        System.out.println("RingManagerService is available on port:" + svcPort);
    }

    @Override
    public void registServer(PrimeServerAddress request, StreamObserver<NextPrimeServerAddress> responseObserver) {
        System.out.println("client completed requests -> complete response");

        super.registServer(request, responseObserver);
    }
}
