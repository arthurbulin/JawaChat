/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 *
 * @author Arthur Bulin
 */
public class OnBukkitMe implements Listener {
    @EventHandler
    public static void PlayerChat(PlayerCommandPreprocessEvent e) {
        String command = e.getMessage();
        Player player = e.getPlayer();

        
        if (command.toLowerCase().startsWith("/me")){
            //ChatHandler.meAction(player, command.replaceFirst("/me", ""));
            //TODO figure out italics
            //System.out.println("Plugin server: " + JawaChat.plugin.getServer());
//            System.out.println("OnlinePlayers: " + Bukkit.getServer().getOnlinePlayers());
            
            String replaceFirst = command.replaceFirst("/me", ChatColor.ITALIC + "*" + ChatColor.RESET + player.getDisplayName() + ChatColor.GRAY);
            Bukkit.getServer().getOnlinePlayers().forEach((target) -> {
                target.sendMessage(replaceFirst);
            }); 
            //JawaChat.getPlugin().getServer().broadcastMessage(replaceFirst);
            //Bukkit.getServer().broadcast(replaceFirst, Server.BROADCAST_CHANNEL_USERS);
            //Bukkit.getServer().br
            e.setCancelled(true);
        }
        
    }
    
}
