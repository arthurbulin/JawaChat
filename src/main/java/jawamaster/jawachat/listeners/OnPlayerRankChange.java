/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.listeners;

import java.util.UUID;
import jawamaster.jawachat.handlers.FormattingHandler;
import net.jawasystems.jawacore.events.PlayerRankChange;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/** This listens for a PlayerRankChange event and triggers the FormattingHandler to 
 * recompile the user's name based on the new rank.
 * @author Arthur Bulin
 */
public class OnPlayerRankChange implements Listener {
    
    @EventHandler
    public static void OnPlayerRankChange(PlayerRankChange e){
        UUID target = e.getUUID();
        FormattingHandler.recompilePlayerName(target);
    }
}
