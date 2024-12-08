package svcserver;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.simple.SimpleLoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class SvcServer {
    public static Channel channelRabbitMq;
    static Logger logger = new SimpleLoggerFactory().getLogger("RabbitMQ-configurator");

    public static void main(String[] args){

        try {
            int port = 50051;
            String ipRabbitMQ = "34.76.4.1";
            if (args.length == 2) {
                port = Integer.parseInt(args[0]);
                ipRabbitMQ = args[1];
            }

            io.grpc.Server svc = ServerBuilder
                    .forPort(port)
                    .addService(new ClientService())
                    .build();

            svc.start();
            System.out.println(String.format("SvcServer started. Listening on Port: %s ", port));
            connectRabbitMq(ipRabbitMQ);

            svc.awaitTermination();
            svc.shutdown();

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static void connectRabbitMq(String ipRabbitMQ) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(ipRabbitMQ);
        factory.setPort(5672);

        Connection connection = factory.newConnection();
        channelRabbitMq = connection.createChannel();
        channelRabbitMq.addReturnListener(new MessageRabbitMQ());
        System.out.println("SvcServer connected to RabbitMQ at "+ipRabbitMQ+":5672");
    }

}
