/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.listeners;

import jawamaster.jawachat.JawaChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
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
//        System.out.println("Async Chat Event");
        if (e.getMessage().startsWith("#")){
//            System.out.println("contains #");
            Player player = e.getPlayer();
            String message = e.getMessage();
            AsyncPlayerChatEvent opChat;
            if (!player.isOp() && !player.hasPermission("jawachat.opchat")){
                player.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform that function.");

            } else {
                String replaceFirst =  message.substring(1);
//                System.out.println(replaceFirst);
//                String replaceFirst = message.replaceFirst("#", ChatColor.YELLOW + "#" + JawaChat.playerCompiledName.get(player.getUniqueId()) + ChatColor.WHITE +": ");
                //Bukkit.getServer().broadcast(replaceFirst, "jawachat.opchat");
//                
//                System.out.println(JawaChat.opsOnline);
//                opChat = new AsyncPlayerChatEvent(true, player, replaceFirst, JawaChat.opsOnline);
//                System.out.println("event creation");
//                opChat.setMessage(replaceFirst);
//                opChat.setFormat(ChatColor.YELLOW + "[OP] " + player.getDisplayName() + ": %2$s");
//                System.out.println("event call");
                Bukkit.getServer().broadcast(ChatColor.YELLOW + "[OP] " + player.getDisplayName() + ": " + replaceFirst, Server.BROADCAST_CHANNEL_ADMINISTRATIVE);
                
                
            }
            e.setCancelled(true);                        
        }
    }
}
