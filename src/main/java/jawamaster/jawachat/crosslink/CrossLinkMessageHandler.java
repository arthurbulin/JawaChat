/*
 * Copyright (C) 2020 Jawamaster (Arthur Bulin)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jawamaster.jawachat.crosslink;

import java.util.HashMap;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import jawamaster.jawachat.JawaChat;

/**
 *
 * @author Jawamaster (Arthur Bulin)
 */
public class CrossLinkMessageHandler {
    private static final Logger LOGGER = Logger.getLogger("ClientHandler");

    private static UUID serverUUID;
    private static boolean isController;
    private static String serverAddress;
    private static int serverPort;
    
    //=======================Queues===========================
    //Message queue for node
    private static final Queue<Message> messageQueue = new LinkedBlockingQueue<>();
    
    //Message queues for Controller
    private static final HashMap<Integer,Queue<Message>> messageQueues = new HashMap();
    
    //=======================Thread Maps===========================
    //Threads mapped to server names for the Controller
    private static final HashMap<Integer,CrossLinkInput> inputThreads = new HashMap();
    private static final HashMap<Integer,CrossLinkOutput> outputThreads = new HashMap();
    
    //Threads for the node mode
    private static CrossLinkInput nodeInputThread;
    private static CrossLinkOutput nodeOutputThread;


    public static void initializeCrossLinkMessageHandler(String role, UUID uuid, String address, int port) {
        serverUUID = uuid;
        serverAddress = address;
        serverPort = port;

        if (role.equalsIgnoreCase("CONTROLLER")) {
            //initCrossLinkValidation(JawaChat.getConfiguration().getConfigurationSection("crosslink").getStringList("authorized-nodes"));
            CrossLinkController.startCrossLinkController(port);
            isController = true;
        } else if (role.equalsIgnoreCase("NODE")){
            isController = false;
            CrossLinkNode.startCrossLinkNode(uuid, address, port);
        }
        else
            LOGGER.log(Level.SEVERE, "The defined crosslink role was not understood. Crosslink has not been started. Please choose either controller or node");
    }
    
    public static UUID getUUID(){
        return serverUUID;
    }    
    
    /** This will get the corresponding chat queue from the map and poll it for the
     * latest message.
     * @param queueCode
     * @return 
     */
    public static synchronized Message pollQueue(int queueCode) {
        if (isController)
            return messageQueues.get(queueCode).poll();
        else
            return messageQueue.poll();
    }
    
    public static synchronized void registerQueue(int queueCode){
        messageQueues.put(queueCode, new LinkedBlockingDeque());
        //System.out.println(messageQueues);
    }
    
    public static synchronized void unregisterQueue(int queueCode){
        messageQueues.remove(queueCode);
        //System.out.println(messageQueues);
    }
    
    public static synchronized void registerInputThread(int queueCode, CrossLinkInput t){
        if (isController)
            inputThreads.put(queueCode, t);
        else
            nodeInputThread = t;
    }
    
    public static synchronized void unregisterInputThread(int queueCode){
        if (isController){
            inputThreads.remove(queueCode);
            //System.out.print(outputThreads);
        }
        else
            nodeInputThread = null;
    }
    
    public static synchronized void registerOutPutThread(int queueCode, CrossLinkOutput t){
        if (isController){
            outputThreads.put(queueCode, t);
            //System.out.print(outputThreads);
        }
        else
            nodeOutputThread = t;
    }
    
    public static synchronized void unregisterOutPutThread(int queueCode){
        if (isController){
            outputThreads.remove(queueCode);
            //System.out.print(outputThreads);
        }
        else
            nodeOutputThread = null;
    }
    
    /** Send a message to a single specific node.
     * @param messageIn
     * @param queueCode 
     */
    public static void sendMessage(Message messageIn, int queueCode){
        if (JawaChat.debug) {
            LOGGER.log(Level.INFO, "Sending message to ", queueCode);
        }
        messageQueues.get(queueCode).add(messageIn);
        synchronized (outputThreads.get(queueCode)) {
            outputThreads.get(queueCode).notify();
        }
    }
    
    /** Send a message to all connected nodes.
     * @param messageIn 
     */
    public static void sendMessage(Message messageIn) {
        //If server is a Controller then we need to add the message to each active queue
        //then notify each server thread
        //TODO deal with message routing
        if (isController) {
            if (!messageQueues.isEmpty()) { //Stop null notifications
                for (int queueCode : messageQueues.keySet()) {
                    messageQueues.get(queueCode).add(messageIn);
                    synchronized (outputThreads.get(queueCode)) {
                        outputThreads.get(queueCode).notify();
                    }
                }
            }
        } else {
            messageQueue.add(messageIn);
            synchronized (nodeOutputThread){
                nodeOutputThread.notify();
            }
            //sendMessage(messageIn, 0);
        }
        
    }
}
