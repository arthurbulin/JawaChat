/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

/**
 *
 * @author Arthur Bulin
 */
public class OnPluginMessage implements PluginMessageListener {
    
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message){
        if (channel.startsWith("JawaChat-")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(message);
            String subChannel = in.readUTF();
            short len = in.readShort();
            
            byte[] msgbytes = new byte[len];
            in.readFully(msgbytes);
            
            DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
            try {
                String somedata = msgin.readUTF();
                System.out.println("Received from " + channel + ": " + somedata);
            } catch (IOException ex) {
                Logger.getLogger(OnPluginMessage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
}
