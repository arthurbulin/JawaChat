/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.commands;

import java.util.Arrays;
import java.util.UUID;
import jawamaster.jawachat.JawaChat;
import jawamaster.jawachat.handlers.FormattingHandler;
import jawamaster.jawapermissions.PlayerDataObject;
import jawamaster.jawapermissions.handlers.ESHandler;
import jawamaster.jawapermissions.handlers.PlayerDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.JSONObject;

/**
 *
 * @author Arthur Bulin
 */
public class SetTag implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command arg1, String arg2, String[] arg3) {
        String usage = "/settag <playername> tag" +
                "/settag <playername>";
        PlayerDataObject pdObject;
        boolean remove = false;
        boolean online = false;
        UUID targetUUID;
        String tag = "";
        
        if (arg3.length == 1){
            remove = true;
        } else if (arg3.length > 1) {
            remove = false;
            tag = String.join(" ", Arrays.asList(Arrays.copyOfRange(arg3, 1, arg3.length)));
        }

        

//###############################################################################
//# Assess offline status for target
//###############################################################################
        //Check if target is offline
        Player target = Bukkit.getPlayer(arg3[0]);

        //If player is online
        if (target != null) {
            targetUUID = target.getUniqueId();
            online = true;
        } else { //if player is offline
            online = false;
            pdObject = ESHandler.findOfflinePlayer(arg3[0], false);
            if ( pdObject == null) {
                commandSender.sendMessage(ChatColor.RED + " > ERROR: " + arg3[0] + " was not found! Please check the player name!");
                return true;//Short circuit in the event the player is not found
            }
            targetUUID = UUID.fromString(pdObject.getPlayerUUID());
        }
        
//###############################################################################
//# Check Immunity for the command
//###############################################################################
        //TODO check immunity

        
//###############################################################################
//# Build Update
//###############################################################################
        JSONObject tagChange = PlayerDataHandler.createPlayerTagChangeData(tag);
        ESHandler.asyncUpdateData(targetUUID.toString(), tagChange);
        
        // TODO FIXME clean up these messages. see setnick
        if (online) {
            FormattingHandler.recompilePlayerName(target);
            if (!remove) {
                JawaChat.playerTags.put(targetUUID, tag);
            } else {
                JawaChat.playerTags.remove(targetUUID);
            }
        }
        if (!remove) {
            commandSender.sendMessage(ChatColor.GREEN + " > " + arg3[0] + "'s tag has been changed to " + ChatColor.translateAlternateColorCodes('$', tag));
        } else {
            commandSender.sendMessage(ChatColor.GREEN + " > " + arg3[0] + "'s tag has been removed.");
        }
        
        //rebuild player name
     
     return true;

    }
    
}
