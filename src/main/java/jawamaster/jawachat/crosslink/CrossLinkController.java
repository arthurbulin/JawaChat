/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.crosslink;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import jawamaster.jawachat.JawaChat;

/**
 *
 * @author alexander
 */
public class CrossLinkController {

    private static final Logger LOGGER = Logger.getLogger("CrossLink");
    private static Thread receiverThread;

    private static int serverPort;
    private static UUID serverUUID;
    
    //Random number generator for tracking threads
    private static final Random randomGen = new Random();
    
    //Map of all nodes in the config
    private static final HashMap<String, UUID> authorizedNodes = new HashMap();
    
    //List of server uuids that have authenticated to the controller
    private static final List<UUID> hasBeenAuthorized = new ArrayList();
    
    //Queue code maps code->name and name->code
    private static final HashMap<Integer,String> queueMap = new HashMap();
    private static final HashMap<String, Integer> serverMap = new HashMap();
    
    public static void startCrossLinkController(int port) {
        serverPort = port;
        if (initCrossLinkValidation(JawaChat.getConfiguration().getConfigurationSection("crosslink").getStringList("authorized-nodes"))) {
            createController();
        }
    }


    /**
     * Create controller thread for crosslink. When a client spawns this thread
     * will respond by spawning a new thread to handle the client's operations.
     */
    private static void createController() {

            //Create the reciver thread with propper functions
            receiverThread = new Thread() {
                private ServerSocket serverSocket = null;

                @Override
                public void run() {
                    try {
                        //Create the serversocket on the port
                        serverSocket = new ServerSocket(serverPort);

                        LOGGER.log(Level.INFO, "A CrossLink Controller has been intiated on {0}", this.serverSocket);

                        //Initiate a while loop that will end when the thread is interrupted
                        while (true) {
                            Socket socket = null;
                            //Accept connections to the socket
                            socket = serverSocket.accept();
                            if (socket.isConnected()) {
                                int queueCode = randomGen.nextInt();
                                synchronized (this){
                                    CrossLinkMessageHandler.registerQueue(queueCode);
                                    if (JawaChat.debug){
                                        LOGGER.log(Level.INFO, "Creating a message queue with id: {0}", queueCode);
                                    }
                                }

                                //create an object input stream to receive messages
                                ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                                CrossLinkInput crossLinkInput = new CrossLinkInput(objectInputStream, queueCode);
                                crossLinkInput.setDaemon(true);
                                crossLinkInput.start();

                                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                                CrossLinkOutput crossLinkOutput = new CrossLinkOutput(objectOutputStream, queueCode);
                                crossLinkOutput.setDaemon(true);
                                crossLinkOutput.start();
                                
                                synchronized (this) {
                                    //Track the new threads created
                                    CrossLinkMessageHandler.registerInputThread(queueCode, crossLinkInput);
                                    CrossLinkMessageHandler.registerOutPutThread(queueCode, crossLinkOutput);
                                }
                            }
                        }
                        //Close the socekt when done
                    } catch (IOException ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                    }
                }
            };

            //Set the thread to daemon, name it, and start it.
            receiverThread.setDaemon(true);
            receiverThread.setName("controller");
            receiverThread.start();
    }
    
    /** Initiate the crosslink validator with authorized nodes.
     * @param authorizedNodesConfig
     * @return 
     */
    public static boolean initCrossLinkValidation(List<String> authorizedNodesConfig) {
        if (authorizedNodesConfig.isEmpty()) {
            LOGGER.log(Level.INFO, "CrossLink is enabled but there are no authorized nodes. "
                    + "The CrossLink controller has not been initiated. "
                    + "Add nodes to the config and restart the server to reinitialize the CrossLink.");
            return false;
        } else {
            for (String server : authorizedNodesConfig) {
                String[] split = server.split(":");
                try {
                    UUID uuid = UUID.fromString(split[1]);
                    authorizedNodes.put(server, uuid);
                } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException ex) {
                    LOGGER.log(Level.SEVERE, "There has been an issue parsing the authorized nodes from the config file. Entry: {0} is not formatted correctly. It should take the form 'nameofnode:UUID'", server);
                    return false;
                }
            }
            LOGGER.log(Level.INFO, "There are {0} authorized nodes for CrossLink Connections", authorizedNodes.size());
            return true;
        }
    }
    
    // TODO Add encrypted password verification
    /** Check if a server is authorized.If it is add the the information to the
     * authorized node info and track the input thread for that server.
     * @param serverName
     * @param uuid
     * @param t
     * @param queueCode
     * @return 
     */
    public static boolean isAuthorized(String serverName, UUID uuid, CrossLinkInput t, int queueCode) {
        if (authorizedNodes.containsValue(uuid)) {
            LOGGER.log(Level.INFO, "{0} has been authenticated as an authorized node with queueCode {1}.", new Object[]{serverName,queueCode});
            hasBeenAuthorized.add(uuid);
            queueMap.put(queueCode, serverName);
            serverMap.put(serverName, queueCode);
            CrossLinkMessage validationApproval = new CrossLinkMessage(serverUUID, CrossLinkMessage.MESSAGETYPE.VALIDATEAPPROVAL, JawaChat.getServerName());
            CrossLinkMessageHandler.sendMessage(validationApproval, queueCode);
            return true;
        } else {
            LOGGER.log(Level.INFO, "{0} has been NOT been authenticated as an authorized node with uuid {1}.", new Object[]{serverName,uuid.toString()});
            return false;
        }
    }
}
