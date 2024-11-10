package primeserverapp;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import io.grpc.ManagedChannel;
import io.grpc.ServerBuilder;
import primeclientstubs.PrimeClientServiceGrpc;
import redis.clients.jedis.Jedis;

import java.net.URI;
import java.util.UUID;

import static primeserverapp.RingManagerService.registPrimeServer;

public class PrimeServer {

    public static String uuid = UUID.randomUUID().toString();
    private static ServerAddress myAddress;
    public static ServerAddress nextPrimeAddress;
    public static ServerAddress redisAddress;
    private static ServerAddress managerAddress;

    private static ManagedChannel channel;
    private static PrimeClientServiceGrpc.PrimeClientServiceBlockingStub blockingStub;
    private static PrimeClientServiceGrpc.PrimeClientServiceStub noBlockStub;

    public static void main(String[] args) {
        try {
            if (args.length == 4) {
                myAddress = new ServerAddress(args[0], Integer.parseInt(args[1]));
                managerAddress = new ServerAddress(args[2], Integer.parseInt(args[3]));
            }

            io.grpc.Server svc = ServerBuilder
                    .forPort(myAddress.port)
                    .addService(new RingManagerService(myAddress, managerAddress))
                    .addService(new PrimeClientService())
                    .addService(new ClientService())
                    .build();

            svc.start();
            System.out.println(String.format("PrimeServer Id: %s started. Listening on Port: %s ", uuid, myAddress.port));
            startRedisContainer();
            registPrimeServer(myAddress);

            svc.awaitTermination();
            svc.shutdown();

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    //Start redis container.
    //Required option "Expose daemon on tcp://localhost:2375 without TLS" on docker desktop enable
    //Required "docker pull redis" exec this command before
    //Required cannot have container with same name
    static void startRedisContainer(){
        String containerName = "RedisForPrime"+myAddress.port;
        String imageName="redis";
        String dockerHOST = "tcp://localhost:2375";

        int redisHostPort= myAddress.port+1;

        DockerClient dockerclient = DockerClientBuilder
                .getInstance()
                .withDockerHttpClient(
                        new ApacheDockerHttpClient.Builder()
                                .dockerHost(URI.create(dockerHOST)).build()
                ).build();

        HostConfig hostConfig = HostConfig
                .newHostConfig()
                .withPortBindings(PortBinding.parse(redisHostPort+":6379"));

        CreateContainerResponse containerResponse = dockerclient
                .createContainerCmd(imageName)
                .withName(containerName)
                .withHostConfig(hostConfig)
                .exec();

        dockerclient.startContainerCmd(containerResponse.getId()).exec();

        System.out.println("Container Redis "+containerName+" ready on port "+myAddress.ip+":"+redisHostPort);
        redisAddress = new ServerAddress(myAddress.ip, redisHostPort);
    }

    //Check on Redis if number isPrime
    static String getIsPrimeFromRedis(String key){
        System.out.println("PrimeServer Id: "+PrimeServer.uuid +" is connecting on Redis "+PrimeServer.redisAddress.ip+
                ":"+PrimeServer.redisAddress.port+ " to get number "+key);

        try (Jedis jedis = new Jedis(PrimeServer.redisAddress.ip, PrimeServer.redisAddress.port)){
            System.out.println("connected on redis, get key");
            return jedis.get(key);
        }
        catch (Exception ex){
            System.out.println("PrimeServer Id: "+PrimeServer.uuid +" error connecting on Redis "+ex.getMessage());
            return "";
        }
    }

    //Start redis container.
    //Required option "Expose daemon on tcp://localhost:2375 without TLS" on docker desktop enable
    //Required "docker pull redis" exec this command before
    //Required cannot have container with same name
    static void startIsPrimeContainer(){
        String containerName = "RedisForPrime"+myAddress.port;
        String imageName="redis";
        String dockerHOST = "tcp://localhost:2375";

        int redisHostPort= myAddress.port+1;

        DockerClient dockerclient = DockerClientBuilder
                .getInstance()
                .withDockerHttpClient(
                        new ApacheDockerHttpClient.Builder()
                                .dockerHost(URI.create(dockerHOST)).build()
                ).build();

        HostConfig hostConfig = HostConfig
                .newHostConfig()
                .withPortBindings(PortBinding.parse(redisHostPort+":6379"));

        CreateContainerResponse containerResponse = dockerclient
                .createContainerCmd(imageName)
                .withName(containerName)
                .withHostConfig(hostConfig)
                .exec();

        dockerclient.startContainerCmd(containerResponse.getId()).exec();

        System.out.println("Container Redis "+containerName+" ready on port "+myAddress.ip+":"+redisHostPort);
        redisAddress = new ServerAddress(myAddress.ip, redisHostPort);
    }

}
