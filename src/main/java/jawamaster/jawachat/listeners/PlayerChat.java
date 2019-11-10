/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.listeners;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import jawamaster.jawachat.JawaChat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 *
 * @author Arthur Bulin
 */
public class PlayerChat implements Listener {
    
    @EventHandler
    public static void PlayerChat(AsyncPlayerChatEvent e) throws IOException {
        Player player = e.getPlayer();
//        String format = JawaChat.playerCompiledName.get(player.getUniqueId()) + ": %2$s";
        String format = player.getDisplayName() + ": %2$s";
        e.setFormat(format);
        
        //This should forward a plugin message to other bungeecord servers running 
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF("ALL");
        out.writeUTF("JawaChat-"+JawaChat.ServerName);
        out.writeUTF("message"); //Should be the subchannel
        
        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);
        msgout.writeUTF("[" + player.getWorld().getName() + "] " + player.getDisplayName() + ": " + e.getMessage());
        
        out.writeShort(msgbytes.toByteArray().length);
        out.write(msgbytes.toByteArray());
        
//        JSONObject message = new JSONObject();
//        message.put("message", e.getMessage());
//        message.put("user", e.getPlayer().getUniqueId());
//        message.put("username", e.getPlayer().getDisplayName());
    }
    
}
