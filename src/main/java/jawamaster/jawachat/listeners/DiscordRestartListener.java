/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.listeners;

import jawamaster.jawachat.JawaChat;
import jawamaster.jawachat.events.DiscordRestartEvent;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author Jawamaster (Arthur Bulin)
 */
public class DiscordRestartListener implements Listener{
    @EventHandler
    public static void DiscordRestartListener(DiscordRestartEvent event) {
        if (event.announce()) {
            Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + "ALERT: A discord admin has called for a server restart. The server will restart in " + event.delay() + " seconds.");
        }
        
        Bukkit.getServer().getScheduler().runTaskLater(JawaChat.plugin, () -> {
            Bukkit.getServer().getOnlinePlayers().forEach((player) -> {
                player.kickPlayer(ChatColor.YELLOW + "The server is now going down for restart. See you on the other side.");
            });
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "restart");
        }, event.delay()*20);
    }
    
}
