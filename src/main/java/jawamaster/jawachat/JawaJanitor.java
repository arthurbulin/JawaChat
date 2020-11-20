/*
 * Copyright (C) 2020 Jawamaster (Arthur Bulin)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jawamaster.jawachat;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;

/** This class controls maintenance tasks. JawaChat uses various lists to track 
 * players properties (i.e. ops for opchat) and this class is used to make sure
 * those lists remain functional
 * @author Jawamaster (Arthur Bulin)
 */
public class JawaJanitor {
    
    private JawaChat plugin;
    private HashMap<String,Integer> maintenanceTasks;
    
    /** Construct the JawaJaniror object. This triggers the creation of the initial
     * Maintenance task.
     * @param plugin 
     */
    public JawaJanitor (JawaChat plugin) {
        this.plugin = plugin;
        maintenanceTasks = new HashMap();
        Logger.getLogger("JawaChat][JawaJanitor").log(Level.INFO, "Creating Maintenance Task");
        scheduleMaintenanceTasks();
    } 
    
    /** Putting calls to maintenance tasks here will alow them to be tracked. This
     * isn't really useful at the moment.
     */
    private void scheduleMaintenanceTasks(){
        maintenanceTasks.put("OPLIST", scheduleOpListCleanup());
    }
    
    /** Create a task to maintain the integrity of the OpsList. This prevents odd
     * events that aren't captured in code to repair a broken list.
     * @return 
     */
    private int scheduleOpListCleanup(){
        int taskid = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(JawaChat.plugin, () -> {
            int opChatPlayers = 0;
            opChatPlayers = Bukkit.getServer().getOnlinePlayers().stream().filter((player) -> 
                    (player.hasPermission("jawachat.opchat") || player.isOp())).map((_item) -> 1).reduce(opChatPlayers, Integer::sum);
            if (opChatPlayers != JawaChat.opsOnline.size()) {
                JawaChat.opsOnline.clear();
                
                //Repopulate
                Bukkit.getServer().getOnlinePlayers().stream().filter((player) -> (player.hasPermission("jawachat.opchat") || player.isOp())).forEachOrdered((player) -> {
                    JawaChat.opsOnline.put(player.getUniqueId(), player);
                });
                if (JawaChat.debug) Logger.getLogger("[JawaChat][OpListCleanUp] ").log(Level.INFO, "The online ops list has been dumped and resynced. This means something strange happened.");
            }
        }, 3000, 3000);
        
        return taskid;
    }
    
}
