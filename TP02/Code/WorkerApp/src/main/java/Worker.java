import com.google.gson.Gson;
import com.rabbitmq.client.*;
import spread.SpreadConnection;
import spread.SpreadGroup;
import spread.SpreadMessage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Worker class that consumes messages from a RabbitMQ queue, processes images by adding text to them,
 * and sends notifications to a Spread group after processing the image.
 */
public class Worker {

    // RabbitMQ parameters
    private String rabbitMqIp;
    private static int RABBITMQ_PORT = 15672;
    private String exchangeName;
    private String queueName;

    // Gluster file path for reading and saving images
    private static final String GLUSTER_PATH = "/var/sharedfiles";

    // Spread group for notification of completed tasks
    private static final String SPREAD_GROUP_NAME = "Servers";
    private SpreadConnection spreadConnection;

    // Adicione o Gson como parte da classe Worker
    private final Gson gson = new Gson();

    /**
     * Constructs the Worker object and sets up connections with Spread and RabbitMQ.
     *
     * @param rabbitMqIp    The RabbitMQ server IP.
     * @param exchangeName  The RabbitMQ exchange name.
     * @param queueName     The RabbitMQ queue name.
     * @throws Exception If an error occurs while setting up the connections.
     */
    public Worker(String rabbitMqIp, String exchangeName, String queueName) throws Exception {
        this.rabbitMqIp = rabbitMqIp;
        this.exchangeName = exchangeName;
        this.queueName = queueName;

        // Configure connection with Spread
        spreadConnection = new SpreadConnection();
        spreadConnection.connect(null, 0, "Worker", false, true);
        System.out.println("Worker connected to Spread!");

    }



    /**
     * Starts consuming messages from the RabbitMQ queue, processes each message, and
     * performs image processing. After processing, it notifies the Spread group of completion.
     *
     * @throws Exception If an error occurs during the message consumption or processing.
     */
    public void processMessages() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitMqIp);
        factory.setPort(RABBITMQ_PORT);

        // Establish connection to RabbitMQ and set up the channel
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            // Declare exchange and queue
            channel.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT);
            channel.queueDeclare(queueName, true, false, false, null);
            channel.queueBind(queueName, exchangeName, "");

            System.out.println("Worker waiting for messages...");

            // Consumer with manual acknowledge and basicNack support
            channel.basicConsume(queueName, false, (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                String routingKey = delivery.getEnvelope().getRoutingKey();
                System.out.println("Message received: " + message);
                System.out.println("Consumer Tag:" + consumerTag + " | Routing Key:" + routingKey);

                try {
                    // Convert JSON to ImageModel model
                    ImageModel imageModel = gson.fromJson(message, ImageModel.class);
                    System.out.println("Parsed ImageModel: " + imageModel);

                    // Process the image with the ImageModel information
                    String fileName = imageModel.getImageName();
                    String[] marks = imageModel.getMarks();

                    // Combine marks into a single string to add to the image
                    String combinedMarks = String.join(", ", marks);

                    // Process the image and then notify completion
                    processImage(fileName, combinedMarks);
                    notifyCompletion(fileName);

                    // Acknowledge message processing
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    System.out.println("Message processed and confirmed!");

                } catch (Exception e) {
                    System.err.println("Error processing message: " + e.getMessage());

                    // Reject message and requeue if there's an error
                    channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
                    System.out.println("Message rejected and requeued!");
                }
            }, consumerTag -> {});
        }
    }

    /**
     * Processes the image by adding the given text (words) onto the image.
     * The processed image is saved with a "_marks" suffix.
     *
     * @param fileName The name of the image file in Gluster.
     * @param words The text to be added to the image.
     * @throws IOException If an error occurs while reading or writing the image file.
     */
    private void processImage(String fileName, String words) throws IOException {
        // Load the image from Gluster
        File inputFile = new File(Paths.get(GLUSTER_PATH, fileName).toString());
        BufferedImage image = ImageIO.read(inputFile);

        // Add text (words) to the image
        Graphics2D g = image.createGraphics();
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString(words, 10, 25); // Draw text at position (10, 25)
        g.dispose();

        // Save the processed image with a new file name
        File outputFile = new File(Paths.get(GLUSTER_PATH, fileName.replace(".png", "_marks.png")).toString());
        ImageIO.write(image, "png", outputFile);
        System.out.println("Image processed and saved: " + outputFile.getAbsolutePath());
    }

    /**
     * Sends a multicast message to the Spread group notifying the completion of the image processing.
     *
     * @param fileName The name of the processed file uploaded by the user.
     * @throws Exception If an error occurs while sending the multicast message.
     */
    private void notifyCompletion(String fileName) throws Exception {
        // Create and send a notification message to Spread group
        String notification = "Processed: " + fileName +" uploaded by user!";
        SpreadMessage spreadMessage = new SpreadMessage();
        spreadMessage.setReliable(); // Ensure message delivery reliability
        spreadMessage.addGroup(SPREAD_GROUP_NAME); // Send to the "Servers" group
        spreadMessage.setData(notification.getBytes());

        spreadConnection.multicast(spreadMessage); // Send the message
        System.out.println("Notification sent to servers: " + notification);
    }

    /**
     * The main method to initialize the Worker and start processing messages.
     *
     * @param args Command-line arguments.
     *             args[0] - RabbitMQ IP
     *             args[1] - Exchange name
     *             args[2] - Queue name
     */
    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: java Worker <RabbitMQ_IP> <Exchange_Name> <Queue_Name>");
            return;
        }

        try {
            // Create and run the Worker
            String rabbitMqIp = args[0];
            String exchangeName = args[1];
            String queueName = args[2];

            Worker worker = new Worker(rabbitMqIp, exchangeName, queueName);
            worker.processMessages();
        } catch (Exception e) {
            // Handle any errors that occur during the initialization or message processing
            System.err.println("Error initializing Worker: " + e.getMessage());
        }
    }
}
