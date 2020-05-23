/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.listeners;

import jawamaster.jawachat.handlers.ChatHandler;
import net.jawasystems.jawacore.PlayerManager;
import net.jawasystems.jawacore.dataobjects.PlayerDataObject;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 *
 * @author alexander
 */
public class OnPlayerJoin implements Listener {
        @EventHandler
    public static void OnPlayerJoin(PlayerJoinEvent event) {
        PlayerDataObject target = PlayerManager.getPlayerDataObject(event.getPlayer());
        
        //Access if a player is muted
            ChatHandler.playerJoin(target);
    }
}
