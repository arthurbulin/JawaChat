/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.listeners;

import jawamaster.jawachat.JawaChat;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

/**
 *
 * @author Arthur Bulin
 */
public class OnPluginMessage implements PluginMessageListener {
    
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message){
        if ("BungeeCord".equals(channel)) {
            JawaChat.getPlugin().getServer().getPluginManager().callEvent(new AsyncPlayerChatEvent(true, player, Server.BROADCAST_CHANNEL_USERS, JawaChat.getPlugin().getServer().getOnlinePlayers()));
        }
        
    }
}
