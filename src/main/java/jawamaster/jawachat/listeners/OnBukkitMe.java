/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.listeners;

import java.util.Set;
import jawamaster.jawachat.JawaChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
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
        Set<Player> playerSet = e.getRecipients();
        if (command.toLowerCase().startsWith("/me")){
            e.setCancelled(true);
            //TODO figure out italics
            String replaceFirst = command.replaceFirst("/me", ChatColor.DARK_GRAY.ITALIC + "*" + player.getDisplayName() + ChatColor.DARK_GRAY);
            Bukkit.getServer().broadcastMessage(replaceFirst);
        }
        
    }
    
}
