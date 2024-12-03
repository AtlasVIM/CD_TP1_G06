package appregisterserver;

import io.grpc.stub.StreamObserver;
import registerclientstubs.RegisterClientServiceGrpc;
import registerclientstubs.SvcServerAddress;
import registerclientstubs.VoidRequest;

public class ClientService extends RegisterClientServiceGrpc.RegisterClientServiceImplBase {

    @Override
    public void getSvcServer(VoidRequest request, StreamObserver<SvcServerAddress> responseObserver) {
        super.getSvcServer(request, responseObserver);
    }
}
