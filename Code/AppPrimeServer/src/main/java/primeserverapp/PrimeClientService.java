package primeserverapp;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import primeserverstubs.PrimeContractServiceGrpc;
import primeserverstubs.RingRequest;
import primeserverstubs.VoidResponse;
import redis.clients.jedis.Jedis;

public class PrimeClientService extends PrimeContractServiceGrpc.PrimeContractServiceImplBase {

    private static PrimeContractServiceGrpc.PrimeContractServiceStub noBlockStubPrimeClient;
    private static ManagedChannel channelPrimeClient;
    private static StreamObserver<RingRequest> streamRingRequestClient;

    public PrimeClientService(int svcPort){
        System.out.println("PrimeClientService is available on port:" + svcPort);

        openChannelNextPrimeServer();

    }

    @Override
    public StreamObserver<RingRequest> ringMessage(StreamObserver<VoidResponse> responseObserver) {
        //here primeServer is server
        System.out.println("PrimeServer Id: "+PrimeServer.uuid +", RingMessage called! returned a stream to receive requests");

        return new StreamObserver<RingRequest>() {
            @Override
            public void onNext(RingRequest ringRequest) {
                System.out.println("PrimeServer Id: "+PrimeServer.uuid +" receive call next from "+ringRequest.getPrimeServerId());

                var key = Long.toString(ringRequest.getNumber());
                var nrIsPrime = PrimeServer.getIsPrimeFromRedis(key);

                //Means message runs all ring and no Prime had the answer isPrime
                if (ringRequest.getPrimeServerId().equals(PrimeServer.uuid)){
                    if (ringRequest.getWasPrimeCalculated()){
                        if (nrIsPrime.isBlank()) {
                            setIsPrimeToRedis(key, Boolean.toString(ringRequest.getIsPrime()));
                        }
                        //retornar para o client numero primo.
                        //possivel solucao: nao fazer nada aqui nesse metodo
                        //e colocar um while na classe ClientService consultando o dicionario redis, a cada 30seg.
                    }
                    else {
                        //chamar container que calcula o numero primo
                        //esperar uns segundos e verificar se o numero tem no redis local
                    }
                }
                else {
                    if (ringRequest.getWasPrimeCalculated() && nrIsPrime.isBlank()) {
                        setIsPrimeToRedis(key, Boolean.toString(ringRequest.getIsPrime()));
                        nrIsPrime = Boolean.toString(ringRequest.getIsPrime());
                    }

                    sendMessageNextPrimeServerAsync(ringRequest.getPrimeServerId(),
                                            ringRequest.getNumber(),
                                            nrIsPrime.isBlank() ? false : Boolean.getBoolean(nrIsPrime),
                                            !nrIsPrime.isBlank());

                }

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

    static void setIsPrimeToRedis(String key, String value){
        System.out.println("PrimeServer Id: "+PrimeServer.uuid +" is connecting on Redis "+PrimeServer.redisAddress.ip+
                ":"+PrimeServer.redisAddress.port+ " to set number "+key);

        try (Jedis jedis = new Jedis(PrimeServer.redisAddress.ip, PrimeServer.redisAddress.port)){
            jedis.set(key, value);
        }
        catch (Exception ex){
            System.out.println("PrimeServer Id: "+PrimeServer.uuid +" error connecting on Redis "+ex.getMessage());
        }
    }

    static void sendMessageNextPrimeServerAsync(String primeServerId, long number, boolean isPrime, boolean isCalculated){
        //here primeServer is client
        try {
            var nextRingMessage = RingRequest.newBuilder()
                    .setPrimeServerId(primeServerId)
                    .setNumber(number)
                    .setIsPrime(isPrime)
                    .setWasPrimeCalculated(isCalculated)
                    .build();

            streamRingRequestClient.onNext(nextRingMessage);
        }
        catch (Exception ex)
        {
            System.out.println("PrimeServer Id: "+PrimeServer.uuid +" Error sendMessageNextPrimeServerAsync:"+ex.getMessage());
        }
    }

    static void openChannelNextPrimeServer(){
        if (channelPrimeClient.isTerminated())
            System.out.println("PrimeServer Id: "+PrimeServer.uuid +" channel is closed()");

        channelPrimeClient = ManagedChannelBuilder.forAddress(PrimeServer.nextPrimeAddress.ip, PrimeServer.nextPrimeAddress.port)
                .usePlaintext()
                .build();

        noBlockStubPrimeClient = PrimeContractServiceGrpc.newStub(channelPrimeClient);
        streamRingRequestClient = noBlockStubPrimeClient.ringMessage(new RingMessageStream());
    }

    static void completeChannelWithNextPrimeServer(){
        if (!channelPrimeClient.isTerminated()) {
            streamRingRequestClient.onCompleted();
            channelPrimeClient.shutdown();
        }
    }
}
