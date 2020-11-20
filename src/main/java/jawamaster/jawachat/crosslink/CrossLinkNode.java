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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import jawamaster.jawachat.JawaChat;

/**
 *
 * @author Jawamaster (Arthur Bulin)
 */
public class CrossLinkNode {
    private static final Logger LOGGER = Logger.getLogger("CrossLink");
    
    private static UUID serverUUID;
    private static String serverAddress;
    private static int serverPort;
    private static Message validationRequest;
    
    private static Thread nodeThread;
    
    
    public static void startCrossLinkNode(UUID uuid, String address, int port){
        serverAddress = address;
        serverPort = port;
        serverUUID = uuid;
        
        validationRequest = new Message(uuid, Message.MESSAGETYPE.VALIDATEREQUEST, JawaChat.getServerName());
        createNode();
    }

    static void createNode() {
        nodeThread = new Thread() {
            private Socket socket = null;

            @Override
            public void run() {
                while (true) {
                    try {
                        //Create the connection socket
                        InetAddress ip = InetAddress.getByName(serverAddress);
                        socket = new Socket(ip, serverPort);
                        LOGGER.log(Level.INFO, "A CrossLink Node has been intiated on {0}", socket);
                        
                        //Create the ouput thread
                        OutputStream outputStream = socket.getOutputStream();
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                        CrossLinkOutput crossLinkOutput = new CrossLinkOutput(objectOutputStream);
                        crossLinkOutput.start();
                        
                        //Create the input thread
                        InputStream inputStream = socket.getInputStream();
                        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                        CrossLinkInput crossLinkInput = new CrossLinkInput(objectInputStream);
                        crossLinkInput.start();
                        
                        //Synchronize to main thread
                        synchronized (this) {
                            //Track the new threads created
                            CrossLinkMessageHandler.registerInputThread(0, crossLinkInput);
                            CrossLinkMessageHandler.registerOutPutThread(0, crossLinkOutput);
                            
//                          request validation from the Controller
//                          Schedule validation request
                            CrossLinkMessageHandler.sendMessage(validationRequest);
                        }
                        
                        //Terminate this thread as it is no longer needed.
                        this.interrupt();
                        
                    } catch (ConnectException ex) {
                        try {
                            LOGGER.log(Level.WARNING, "Unable to establish a node connection to {0}:{1}. Waiting 30 seconds to retry.", new Object[]{serverAddress, serverPort});
                            synchronized (this) {
                                this.wait(30000);
                            }
                            continue;
                        } catch (InterruptedException ex1) {
                            LOGGER.log(Level.SEVERE, null, ex1);
                        }
                    } catch (UnknownHostException ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                    }
                    break;
                }
            }
        };
        nodeThread.start();
    }

}
