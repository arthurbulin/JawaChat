/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.listeners;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jawamaster.jawachat.JawaChat;
import jawamaster.jawachat.handlers.FormattingHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import net.jawasystems.jawacore.events.PlayerInfoLoaded;
import org.bukkit.Bukkit;

/**
 *
 * @author Arthur Bulin
 */
public class PlayerInfoLoadedListener implements Listener{
    private static int taskid;
    
    @EventHandler
    public static void PlayerInfoLoadedListener(PlayerInfoLoaded event) throws IOException{
        //Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, task, taskid)
        if (event.getPlayerDataObject().getPlayer() == null) {
            registerTask(event);
        }

    }
    
    private static void registerTask(PlayerInfoLoaded event){
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(JawaChat.plugin, () -> {
            if (event.getPlayerDataObject().getPlayer() != null) {
                
                //Trigger compiling of player name
                FormattingHandler.compilePlayerNameOnJoin(event.getUniqueID(), event.getPlayerDataObject());
                
                //Add a user to op tracking
                if ((event.getPlayerDataObject().getPlayer().isOp() || event.getPlayer().hasPermission("jawachat.opchat")) && !JawaChat.opsOnline.containsKey(event.getUniqueID())){
                    JawaChat.opsOnline.put(event.getUniqueID(), event.getPlayerDataObject().getPlayer());
                }

                //cancel this task when the above code executes
                //Bukkit.getServer().getScheduler().cancelTask(taskid);
                Logger.getLogger("JawaChat][InfoLoaded").log(Level.INFO, " Info load event finished for {0}", event.getPlayerDataObject().getName());

            } else {
                registerTask(event);
            }
        }, 10);
    }
}
