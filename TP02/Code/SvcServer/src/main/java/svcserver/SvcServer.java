package svcserver;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.simple.SimpleLoggerFactory;
import spread.SpreadException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;


public class SvcServer {
    public static Channel channelRabbitMq;
    public static SpreadGroupManager spreadManager;
    public static String SpreadUser = "Servers";
    public static String SpreadGroup = "Servers";
    static Logger logger = new SimpleLoggerFactory().getLogger("RabbitMQ-configurator");
    public static boolean debugMode = true;

    public static void main(String[] args){

        try {
            int myPort = 50051;
            String myIp = "34.78.207.63";
            String ipRabbitMQ = "34.76.4.1";
            String ipSpread = "34.78.207.63";
            if (args.length == 5) {
                myIp = args[0];
                myPort = Integer.parseInt(args[1]);
                ipRabbitMQ = args[2];
                ipSpread = args[3];
            }

            io.grpc.Server svc = ServerBuilder
                    .forPort(myPort)
                    .addService(new ClientService())
                    .build();

            svc.start();
            System.out.println(String.format("SvcServer started. Listening on Port: %s ", myPort));
            connectToRabbitMq(ipRabbitMQ);
            connectToSpread(myIp, myPort, ipSpread);

            svc.awaitTermination();
            svc.shutdown();

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static void connectToRabbitMq(String ipRabbitMQ) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(ipRabbitMQ);
        factory.setPort(5672);

        Connection connection = factory.newConnection();
        channelRabbitMq = connection.createChannel();
        channelRabbitMq.addReturnListener(new MessageRabbitMQ());
        System.out.println("SvcServer connected to RabbitMQ at "+ipRabbitMQ+":5672");
    }

    public static void connectToSpread(String myIp, int myPort, String ipSpread) throws SpreadException {
        spreadManager = new SpreadGroupManager(SpreadUser, ipSpread, 4803);
        spreadManager.JoinToGroup(SpreadGroup);
        spreadManager.SendMessage(SpreadGroup, "New. Address: "+myIp+":"+myPort);
        System.out.println("SvcServer send Message to Group");
    }

}
