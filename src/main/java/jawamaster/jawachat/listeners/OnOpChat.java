/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.listeners;

import jawamaster.jawachat.JawaChat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 *
 * @author Arthur Bulin
 */
public class OnOpChat implements Listener{
    @EventHandler
    public static void OnOpChat(AsyncPlayerChatEvent e){
        if (e.getMessage().startsWith("#")){
            e.setCancelled(true);
            Player player = e.getPlayer();
            String message = e.getMessage();
            
            if (JawaChat.opsOnline.containsKey(player.getUniqueId())){
                String replaceFirst =  message.substring(1);
                
                JawaChat.opsOnline.values().forEach((target) -> {
                    target.sendMessage(ChatColor.YELLOW + "[OP] " + player.getDisplayName() + ChatColor.WHITE + ": " + replaceFirst);
                });
                
            } else {
                player.sendMessage(ChatColor.RED + "You do not have permission to perform that function.");
            }
            
//                        if (!player.isOp() && !player.hasPermission("jawachat.opchat")){
//                player.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform that function.");
//            } else {
//                String replaceFirst =  message.substring(1);
//                Bukkit.getServer().broadcast(ChatColor.YELLOW + "[OP] " + player.getDisplayName() + ChatColor.WHITE + ": " + replaceFirst, Server.BROADCAST_CHANNEL_ADMINISTRATIVE);              
//            }
        }
    }
}
