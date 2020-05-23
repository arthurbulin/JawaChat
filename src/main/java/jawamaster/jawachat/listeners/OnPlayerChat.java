/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.listeners;

import java.io.IOException;
import jawamaster.jawachat.JawaChat;
import jawamaster.jawachat.handlers.ChatHandler;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
        
/**
 *
 * @author Arthur Bulin
 */
public class OnPlayerChat implements Listener {
    
    @EventHandler
    public static void OnPlayerChat(AsyncPlayerChatEvent e) throws IOException {
        Player player = e.getPlayer();
//        String format = JawaChat.playerCompiledName.get(player.getUniqueId()) + ": %2$s";
        //FIXME Could be a problem later on since I am setting display name to include the star and tag it seems
//        String format = player.getDisplayName() + ChatColor.WHITE + ": %2$s";
//        e.setFormat(format);
        e.setCancelled(true);
        
        if (e.getMessage().startsWith("#")) {
            if (JawaChat.opsOnline.containsKey(player.getUniqueId()) || player.hasPermission("jawachat.opchat")){
                ChatHandler.opChat(player, e.getMessage());
            } else {
                player.sendMessage(ChatColor.RED + "You do not have permission to perform that function.");
            }
                
        } else if (ChatHandler.isMuted(e.getPlayer().getUniqueId())){
            ChatHandler.mutedChat(player, e.getMessage());
        }
        else {
            ChatHandler.generalChat(player, e.getMessage());
        }

        //This should forward a plugin message to other bungeecord servers running 
//        ByteArrayDataOutput out = ByteStreams.newDataOutput();
//        out.writeUTF("Forward");
//        out.writeUTF("ALL");
//        out.writeUTF("JawaChat-"+JawaChat.ServerName);
//        out.writeUTF("message"); //Should be the subchannel
//        
//        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
//        DataOutputStream msgout = new DataOutputStream(msgbytes);
//        msgout.writeUTF("[" + player.getWorld().getName() + "] " + player.getDisplayName() + ": " + e.getMessage());
//        
//        out.writeShort(msgbytes.toByteArray().length);
//        out.write(msgbytes.toByteArray());
        
        //JSONObject message = new JSONObject();
//        message.put("message", e.getMessage());
//        message.put("user", e.getPlayer().getUniqueId());
//        message.put("username", e.getPlayer().getDisplayName());
    }
    
}
