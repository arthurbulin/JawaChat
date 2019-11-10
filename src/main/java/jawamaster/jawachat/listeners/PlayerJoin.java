/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.listeners;

import java.io.IOException;
import jawamaster.jawachat.handlers.ESDataHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 *
 * @author Arthur Bulin
 */
public class PlayerJoin implements Listener{
    
    @EventHandler
    public static void PlayerJoin(PlayerJoinEvent event) throws IOException{
        Player player = event.getPlayer();
        ESDataHandler.compilePlayerNameJoin(player);
        //JawaPermissions.plugin.getServer().getPluginManager().subscribeToPermission("jawachat.opchat", player);
    }
}
