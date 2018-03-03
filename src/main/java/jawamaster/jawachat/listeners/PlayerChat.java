/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.listeners;

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
    public static void PlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
//        String format = JawaChat.playerCompiledName.get(player.getUniqueId()) + ": %2$s";
        String format = player.getDisplayName() + ": %2$s";
        e.setFormat(format);
        
    }
    
}
