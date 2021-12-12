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

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jawamaster.jawachat.JawaChat;
import jawamaster.jawachat.handlers.ChatHandler;

/** Reads messages sent from a remote crosslink
 * @author Jawamaster (Arthur Bulin)
 */
public class CrossLinkInput extends Thread {
    private final ObjectInputStream objectInputStream;
    private final Logger LOGGER;
    private boolean isValidated = false;
    private final int queueCode;
    
    /** Create a CrossLinkInput for a controller.
     * @param objectInputStream
     * @param queueCode 
     */
    public CrossLinkInput(ObjectInputStream objectInputStream, int queueCode) {
        this.objectInputStream = objectInputStream;
        this.LOGGER = Logger.getLogger("CrossLinkInput");
        this.queueCode = queueCode;
    }
    
    /** Create a CrossLinkInput for a node.
     * @param objectInputStream 
     */
    public CrossLinkInput(ObjectInputStream objectInputStream) {
        this.objectInputStream = objectInputStream;
        this.LOGGER = Logger.getLogger("CrossLinkInput");
        this.queueCode = 0;
    }

    
    @Override
    public void run() {
        if (JawaChat.debug){
            LOGGER.log(Level.INFO, "A CrossLinkInput thread has been started");
        }
        while (!this.isInterrupted()){
            CrossLinkMessage inputMessage;
            
            try {
                inputMessage = (CrossLinkMessage) objectInputStream.readObject();
                
                if (inputMessage != null) {
//                    Handle validation
                    if (!isValidated) { 
                        switch (inputMessage.getMessageType()) {
                            //Receive validation request on the controller side
                            case VALIDATEREQUEST:
                                //Bring into sync to validate
                                synchronized (this) {
                                    if (JawaChat.debug){
                                        LOGGER.log(Level.INFO, "Validate request for {0} with UUID {1}", new Object[]{inputMessage.getServerFriendlyName(), inputMessage.getOriginatingServer().toString()});
                                    }
                                    isValidated = CrossLinkController.isAuthorized(inputMessage.getServerFriendlyName(), inputMessage.getOriginatingServer(), this, queueCode);
                                    if (!isValidated) {
                                        LOGGER.log(Level.INFO, "Unable to validate node {0}:{1} Stopping thread...", new Object[]{inputMessage.getServerFriendlyName(), inputMessage.getOriginatingServer().toString()});
                                        synchronized (this) {
                                            CrossLinkMessageHandler.unregisterInputThread(queueCode);
                                            CrossLinkMessageHandler.unregisterOutPutThread(queueCode);
                                        }
                                        this.interrupt();
                                    }
                                }
                                break;
                            //Recevie validation request on the node side
                            case VALIDATEAPPROVAL:
                                isValidated = true;
                                LOGGER.log(Level.INFO, "This node has been validated with the controller");
                                break;
                            case VALIDATEDENIAL:
                                LOGGER.log(Level.INFO, "This node has NOT been validated with the controller and received a denial of validation. Stopping thread...");
                                synchronized (this) {
                                    CrossLinkMessageHandler.unregisterInputThread(queueCode);
                                    CrossLinkMessageHandler.unregisterOutPutThread(queueCode);
                                }
                                this.interrupt();
                                break;
                        }
                    } else {
                        //Handle terminate
                        //handl info
                        //Handle chat
                        switch (inputMessage.getMessageType()) {
                            case CHATGENERAL:
                                synchronized (this) {
                                    ChatHandler.broadcast(inputMessage.getServerFriendlyName(), inputMessage.getChatMessage());
                                    if (JawaChat.isChatLoggingEnabled()) {
                                        ChatHandler.logMessage(inputMessage.getChatMessageString(), "broadcast", inputMessage.getServerFriendlyName(), inputMessage.getPlayerSessionID(), inputMessage.getPlayerUUID(), isValidated, true);
                                    }
                                }
                                break;
                            case CHATOP:
                                synchronized (this) {
                                    ChatHandler.opBroadcast(inputMessage.getServerFriendlyName(), inputMessage.getChatMessage());
                                    if (JawaChat.isChatLoggingEnabled()) {
                                        ChatHandler.logMessage(inputMessage.getChatMessageString(), "broadcast", inputMessage.getServerFriendlyName(), inputMessage.getPlayerSessionID(), inputMessage.getPlayerUUID(), isValidated, true);
                                    }
                                }
                                break;
                            case INFOBROADCAST:
                                synchronized (this) {
                                    ChatHandler.broadcast(inputMessage.getServerFriendlyName(), inputMessage.getInfoBroadcast());
                                }
                                break;
                            case TERMINATE:
                                objectInputStream.close();
                                synchronized (this) {
                                    CrossLinkMessageHandler.unregisterInputThread(queueCode);
                                }
                                this.interrupt();
                                break;
                        }
                    }
                }
                
            } catch (SocketException ex) {
                synchronized (this) {
                    CrossLinkMessageHandler.unregisterInputThread(queueCode);
                }
                this.interrupt();
            } catch (EOFException ex) {
                //LOGGER.log(Level.SEVERE, null, ex);
                //Something fata went wrong, terminate the thread and connection
                synchronized (this) {
                    CrossLinkMessageHandler.unregisterInputThread(queueCode);
                }
                this.interrupt();
            } catch (IOException | ClassNotFoundException ex) {
                //LOGGER.log(Level.SEVERE, null, ex);
                synchronized (this) {
                    CrossLinkMessageHandler.unregisterInputThread(queueCode);
                }
                this.interrupt();
            }
        }
    }
}
