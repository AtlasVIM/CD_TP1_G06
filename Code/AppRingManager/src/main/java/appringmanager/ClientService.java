package appringmanager;

import io.grpc.stub.StreamObserver;
import ringmanagerclientstubs.VoidRequest;
import ringmanagerclientstubs.RingManagerClientServiceGrpc;
import sharedstubs.PrimeServerAddress;

public class ClientService extends RingManagerClientServiceGrpc.RingManagerClientServiceImplBase {

    private final SharedServerList sharedServerList; // Referência à lista compartilhada

    // Construtor que recebe a lista compartilhada
    public ClientService(SharedServerList sharedServerList) {
        this.sharedServerList = sharedServerList;
    }

    @Override
    public void getPrimeServer(VoidRequest request, StreamObserver<PrimeServerAddress> responseObserver) {
        // Obtém o próximo servidor com a menor contagem de clientes
        PrimeServerAddress server = sharedServerList.getServerWithLeastClients();

        if (server != null) {
            sharedServerList.incrementClientCount(server); // Incrementa contagem de clientes
            responseObserver.onNext(server);
        } else {
            responseObserver.onError(new RuntimeException("No available servers"));
        }
        responseObserver.onCompleted();
    }
}
