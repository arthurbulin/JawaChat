/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.listeners;

import jawamaster.jawachat.JawaChat;
import jawamaster.jawachat.handlers.FormattingHandler;
import jawamaster.jawapermissions.events.PlayerRankChange;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author Arthur Bulin
 */
public class OnPlayerRankChange implements Listener {
    
    @EventHandler
    public static void OnPlayerRankChange(PlayerRankChange e){
        Player target = e.getPlayer();
        String rank = e.getRank();
        
        JawaChat.playerRanks.put(target.getUniqueId(), rank);
        
        FormattingHandler.compilePlayerName(target);
    }
}
