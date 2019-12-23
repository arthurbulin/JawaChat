/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.listeners;

import java.io.IOException;
import jawamaster.jawachat.handlers.FormattingHandler;
import jawamaster.jawapermissions.handlers.ESHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import jawamaster.jawapermissions.events.PlayerInfoLoaded;

/**
 *
 * @author Arthur Bulin
 */
public class PlayerInfoLoadedListener implements Listener{
    
    @EventHandler
    public static void PlayerInfoLoadedListener(PlayerInfoLoaded event) throws IOException{
        Player player = event.getPlayer();
        FormattingHandler.compilePlayerNameOnJoin(player, event.getPlayerDataObject());
        //FormattingHandler.compilePlayerNameOnJoin(player, event.getPlayerDataObject());
        //ESHandler.compilePlayerNameJoin(player);
        //JawaPermissions.plugin.getServer().getPluginManager().subscribeToPermission("jawachat.opchat", player);
    }
}
