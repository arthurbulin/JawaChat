/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;

/**
 *
 * @author alexander
 */
public class JawaJanitor {
    
    private JawaChat plugin;
    private HashMap<String,Integer> maintenanceTasks;
    
    public JawaJanitor (JawaChat plugin) {
        this.plugin = plugin;
        maintenanceTasks = new HashMap();
        Logger.getLogger("[JawaChat][JawaJanitor]").log(Level.INFO, "Creating Maintenance Task");
        scheduleMaintenanceTasks();
    } 
    
    private void scheduleMaintenanceTasks(){
//        int taskid;
//        
//        taskid = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(JawaChat.plugin, () -> {
// 
//        }, 10, 10);
        maintenanceTasks.put("OPLIST", scheduleOpListCleanup());
        
        
    }
    
    private int scheduleOpListCleanup(){
        int taskid = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(JawaChat.plugin, () -> {
            int opChatPlayers = 0;
            opChatPlayers = Bukkit.getServer().getOnlinePlayers().stream().filter((player) -> 
                    (player.hasPermission("jawachat.opchat") || player.isOp())).map((_item) -> 1).reduce(opChatPlayers, Integer::sum);
//            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
//                if (player.hasPermission("jawachat.opchat") || player.isOp()) opChatPlayers++;
//            }
            if (opChatPlayers != JawaChat.opsOnline.size()) {
                JawaChat.opsOnline.clear();
                
                //Repopulate
                Bukkit.getServer().getOnlinePlayers().stream().filter((player) -> (player.hasPermission("jawachat.opchat") || player.isOp())).forEachOrdered((player) -> {
                    JawaChat.opsOnline.put(player.getUniqueId(), player);
                });
                if (JawaChat.debug) Logger.getLogger("[JawaChat][OpListCleanUp] ").log(Level.INFO, "The online ops list has been dumped and resynced. This means something strange happened.");
//                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
//                    if (player.hasPermission("jawachat.opchat") || player.isOp()) JawaChat.opsOnline.put(player.getUniqueId(), player);
//                }
            }
        }, 3000, 3000);
        
        //System.out.println("Task ID: " + taskid);
        return taskid;
    }
    
}
