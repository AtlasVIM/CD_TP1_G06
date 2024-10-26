package primeserverapp;

import io.grpc.stub.StreamObserver;
import primeserverstubs.*;

public class PrimeClientService extends PrimeContractServiceGrpc.PrimeContractServiceImplBase {

    public PrimeClientService(int svcPort){
        System.out.println("PrimeClientService is available on port:" + svcPort);
    }

    @Override
    public StreamObserver<RingRequest> ringMessage(StreamObserver<VoidResponse> responseObserver) {
        System.out.println("PrimeServer Id: "+PrimeServer.uuid +", RingMessage called! returned a stream to receive requests");

        return new StreamObserver<RingRequest>() {
            @Override
            public void onNext(RingRequest ringRequest) {
                System.out.println("PrimeServer Id: "+PrimeServer.uuid +" receive call next from "+ringRequest.getPrimeServerId());
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("client completed requests -> complete response");
            }

            @Override
            public void onCompleted() {
                System.out.println("client completed requests -> complete response");
            }
        };
    }
}
