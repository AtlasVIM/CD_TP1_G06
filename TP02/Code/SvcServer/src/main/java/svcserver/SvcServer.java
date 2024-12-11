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
    public final static String SpreadUser = "Servers";
    public final static String SpreadGroup = "Servers";
    public final static boolean debugMode = true;
    static Logger logger = new SimpleLoggerFactory().getLogger("RabbitMQ-configurator");
    public static String myIp;
    public static int myPort;

    public static void main(String[] args){

        try {
            myPort = 50051;
            myIp = "34.78.207.63";
            String rabbitMQ_Ip = "34.76.4.1";
            String spreadIp = "34.78.207.63";
            if (args.length == 5) {
                myIp = args[0];
                myPort = Integer.parseInt(args[1]);
                rabbitMQ_Ip = args[2];
                spreadIp = args[3];
            }

            io.grpc.Server svc = ServerBuilder
                    .forPort(myPort)
                    .addService(new ClientService())
                    .build();

            svc.start();
            System.out.println(String.format("SvcServer started. Listening on Port: %s ", myPort));
            connectToRabbitMq(rabbitMQ_Ip);
            connectToSpread(myIp, myPort, spreadIp);

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
        System.out.println("Connected to RabbitMQ at "+ipRabbitMQ+":5672");
    }

    public static void connectToSpread(String myIp, int myPort, String ipSpread) throws SpreadException {
        spreadManager = new SpreadGroupManager(SpreadUser, ipSpread, 4803);
        spreadManager.joinToGroup(SpreadGroup);
        spreadManager.sendMessage(SpreadGroup, new SpreadGroupMessage(myIp, myPort));

        if (SvcServer.debugMode)
            System.out.println("Sent Message to Group");
    }

}
