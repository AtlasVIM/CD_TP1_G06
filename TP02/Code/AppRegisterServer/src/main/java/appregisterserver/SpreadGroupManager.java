package appregisterserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spread.SpreadConnection;
import spread.SpreadException;
import spread.SpreadGroup;
import spread.SpreadMessage;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpreadGroupManager {

    private SpreadConnection connection;

    private SpreadAdvancedMessageListener advancedMsgHandling;

    public SpreadGroupManager(String user, String address, int port) {
        // Establish the spread connection.
        try  {
            connection = new SpreadConnection();
            connection.connect(InetAddress.getByName(address), port, user, false, true);

            advancedMsgHandling = new SpreadAdvancedMessageListener(connection);
            connection.add(advancedMsgHandling);
            System.out.println("Connected to Spread at "+address+":"+port);

        }
        catch(SpreadException e)  {
            System.err.println("There was an error connecting to the daemon.");
            e.printStackTrace();
            System.exit(1);
        }
        catch(UnknownHostException e) {
            System.err.println("Can't find the daemon " + address);
            System.exit(1);
        }
    }

    public void joinToGroup(String groupName) throws SpreadException {
        SpreadGroup newGroup = new SpreadGroup();
        newGroup.join(connection, groupName);
    }

    //Serialize object SpreadGroupMessage into byte[]
    private byte[] convertSpreadGroupMessageToBytes(SpreadGroupMessage grpMessage){
        Gson gson = new GsonBuilder().create();
        String base64Img = gson.toJson(grpMessage);
        return base64Img.getBytes(StandardCharsets.UTF_8);
    }

    public SpreadGroupMessage convertBytesToSpreadGroupMessage(byte[] grpMessage){
        Gson gson = new GsonBuilder().create();
        String newJsonString = new String(grpMessage, StandardCharsets.UTF_8);
        return gson.fromJson(newJsonString, SpreadGroupMessage.class);
    }

    public void close() throws SpreadException {
        // remove listener
        connection.remove(advancedMsgHandling);

        // Disconnect.
        connection.disconnect();
    }
}
