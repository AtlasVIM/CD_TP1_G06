package primeserverapp;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import primeclientstubs.PrimeClientServiceGrpc;
import primeserverstubs.PrimeContractServiceGrpc;
import primeserverstubs.RingRequest;
import primeserverstubs.RingRequestOrBuilder;
import primeserverstubs.VoidResponse;
import redis.clients.jedis.Jedis;

import java.util.Objects;

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
                var key = Long.toString(ringRequest.getNumber());
                var number = getIsPrimeFromRedis(key);

                if (!Objects.equals(number, "") && ringRequest.getWasPrimeCalculated()){
                    var isPrime = ringRequest.getIsPrime();
                    setIsPrimeToRedis(key, Boolean.toString(isPrime));
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

    static String getIsPrimeFromRedis(String key){
        System.out.println("PrimeServer Id: "+PrimeServer.uuid +" is connecting on Redis "+PrimeServer.redisAddress.ip+
                ":"+PrimeServer.redisAddress.port+ " to get number "+key);

        try (Jedis jedis = new Jedis(PrimeServer.redisAddress.ip, PrimeServer.redisAddress.port)){
            return jedis.get(key);
        }
        catch (Exception ex){
            System.out.println("PrimeServer Id: "+PrimeServer.uuid +" error connecting on Redis "+ex.getMessage());
            return "";
        }
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
        PrimeContractServiceGrpc.PrimeContractServiceStub noBlockStub;
        ManagedChannel channel = ManagedChannelBuilder.forAddress(PrimeServer.nextPrimeAddress.ip, PrimeServer.nextPrimeAddress.port)
                .usePlaintext()
                .build();

        noBlockStub = PrimeContractServiceGrpc.newStub(channel);

        var nextRingMessage = new StreamObserver<RingRequest>() {
            @Override
            public void onNext(RingRequest ringRequest) {

            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {

            }
        }RingRequest.newBuilder()
                .setPrimeServerId(primeServerId)
                .setNumber(number)
                .setIsPrime(isPrime)
                .setWasPrimeCalculated(isCalculated)
                .build();

        RingMessageStream ringMessageStream = new RingMessageStream();
        noBlockStub.ringMessage(ringMessageStream, );
        while(!ringMessageStream.isCompleted()){

        }
    }

}
