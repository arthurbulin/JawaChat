/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.crosslink;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import jawamaster.jawachat.JawaChat;

/**
 *
 * @author alexander
 */
public class CrossLinkOutput extends Thread {
    private final ObjectOutputStream objectOutputStream;
    private final Logger LOGGER;
    private boolean isValidated = false;
    private final int queueCode;
    
    /** Creates a crossLinkOutput thread for the Controller. This queueCode should be
     * a random generated int. This makes sure that the queue requested matches to this server.
     * @param objectOutputStream
     * @param queueCode 
     */
    public CrossLinkOutput(ObjectOutputStream objectOutputStream, int queueCode) {
        this.objectOutputStream = objectOutputStream;
        this.LOGGER = Logger.getLogger("CrossLinkOutput");
        this.queueCode = queueCode;
    }
    
    /** Creates a crossLinkOutput thread for a node. This sets the queueCode to 0
     * because as a node there is nothing but the single queue.
     * @param objectOutputStream 
     */
    public CrossLinkOutput(ObjectOutputStream objectOutputStream) {
        this.objectOutputStream = objectOutputStream;
        this.LOGGER = Logger.getLogger("CrossLinkOutput");
        this.queueCode = 0;
    }
    
    @Override
    public void run() {
        if (JawaChat.debug){
            LOGGER.log(Level.INFO, "A CrossLinkOutput thread has been started");
        }
        while(true){
            CrossLinkMessage outputMessage;
            
            //Get message
            synchronized (this) {
                outputMessage = CrossLinkMessageHandler.pollQueue(queueCode);
            }
            
            //Transmit message
            if (outputMessage != null) {
                
                //Validate operation
                if (outputMessage.getMessageType() == CrossLinkMessage.MESSAGETYPE.VALIDATEAPPROVAL) {
                    isValidated = true;
                }
                
                //Request Validation
                if (outputMessage.getMessageType() == CrossLinkMessage.MESSAGETYPE.VALIDATEREQUEST) {
                    LOGGER.log(Level.INFO, "Sending validation request to Controller");
                }
                
                //Write out object into stream
                try {
                    objectOutputStream.writeObject(outputMessage);
                } catch (IOException ex) {
                    if (outputMessage.getMessageType() == CrossLinkMessage.MESSAGETYPE.TERMINATE) {
                        LOGGER.log(Level.SEVERE, "Failed to transmit terminiation message to remote thread. Link may already be down.");
                        this.interrupt();
                    }
                    else {
                        LOGGER.log(Level.SEVERE, "Connection lost");
                        this.interrupt();
                    }
                    //LOGGER.log(Level.SEVERE, null, ex);
                    
                }
                
                //Handle termination of connection
                if (outputMessage.getMessageType() == CrossLinkMessage.MESSAGETYPE.TERMINATE) {
                    try {
                        objectOutputStream.close();
                    } catch (IOException ex) {
                        LOGGER.log(Level.SEVERE, "Failed to close output stream, may already be closed");
                    }
                    
                    synchronized (this) {
                        CrossLinkMessageHandler.unregisterOutPutThread(queueCode);
                        CrossLinkMessageHandler.unregisterQueue(queueCode);
                    }
                    this.interrupt();
                }
                
            } else { //Wait
                synchronized (this) {
                    try {
                        this.wait();
                    } catch (InterruptedException ex) {
                        this.interrupt();
                        //LOGGER.log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        
    }
}
