/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.listeners;

import java.util.UUID;
import jawamaster.jawachat.JawaChat;
import jawamaster.jawachat.handlers.ChatHandler;
import jawamaster.jawachat.handlers.FormattingHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author Arthur Bulin
 */
public class PlayerQuit implements Listener{
    
    @EventHandler
    public static void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        
        //if (JawaChat.playerCompiledName.containsKey(uuid)) JawaChat.playerCompiledName.remove(uuid);
//        if (JawaChat.playerNicks.containsKey(uuid)) JawaChat.playerNicks.remove(uuid);
//        //if (JawaChat.playerRanks.containsKey(uuid)) JawaChat.playerRanks.remove(uuid);
//        if (JawaChat.playerStars.containsKey(uuid)) JawaChat.playerStars.remove(uuid);
//        if (JawaChat.playerTags.containsKey(uuid)) JawaChat.playerTags.remove(uuid);
//        if (JawaChat.playerCompiledName.containsKey(uuid)) JawaChat.playerCompiledName.remove(uuid);
        if (JawaChat.opsOnline.containsKey(uuid)) JawaChat.opsOnline.remove(uuid);
        FormattingHandler.clearHashs(uuid);
        ChatHandler.playerQuit(player, event.getQuitMessage());
        
    }
}
