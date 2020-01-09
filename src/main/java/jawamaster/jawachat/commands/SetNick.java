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
import jawamaster.jawapermissions.utils.ESRequestBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Arthur Bulin
 */
public class SetNick implements CommandExecutor {

    //TODO FIXME be able to set player nick back to default
    @Override
    public boolean onCommand(CommandSender commandSender, Command arg1, String arg2, String[] arg3) {
        String usage = "/nick <playername> <nickname>" +
                "/nick <playername>";
        PlayerDataObject pdObject;
        boolean remove = false;
        boolean online;
        UUID targetUUID;
        String nick = "";
        if (arg3 == null || arg3.length == 0){
          return true;  
        } else if (arg3.length == 1){
            remove = true;
        } else if (arg3.length > 1) {
            remove = false;
            nick = String.join(" ", Arrays.asList(Arrays.copyOfRange(arg3, 1, arg3.length)));
            System.out.println("nick1: " + nick);
        }        

//###############################################################################
//# Assess offline status for target
//###############################################################################
        //Check if target is offline
//        Player target = JawaChat.plugin.getServer().getPlayer(arg3[0]);
        Player target = Bukkit.getPlayer(arg3[0]);

        //If player is online
        if (target != null) {
            targetUUID = target.getUniqueId();
            online = true;
            pdObject = new PlayerDataObject(targetUUID);
            pdObject = ESHandler.runMultiIndexSearch(ESRequestBuilder.buildSingleMultiSearchRequest("players", "_id", targetUUID.toString()), pdObject);
        } else { //if player is offline
            online = false;
            pdObject = ESHandler.findOfflinePlayer(arg3[0], true);
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
        
        pdObject.addUpdateData();
        pdObject.updateNick(nick);
        pdObject.triggerAsyncUpdate();
        
        if (online) {
            if (!remove) {
                JawaChat.playerNicks.put(targetUUID, nick);
            } else {
                JawaChat.playerNicks.remove(targetUUID);
            }
            FormattingHandler.recompilePlayerName(target);
            
            if (((Player) commandSender).getUniqueId().equals(target.getUniqueId()) ) {
                commandSender.sendMessage(ChatColor.GREEN + " > Your nickname has been set to: " + nick);
            } else {
                commandSender.sendMessage(ChatColor.GREEN + " > " + target.getName() + " has been set to " + nick);
                target.sendMessage(ChatColor.GREEN + " > Your nickname has been set to " + nick );
            }
            
            System.out.println("[JawaChat]" + commandSender.getName() + " has set " + targetUUID.toString() + "'s nickname to " + nick);
        } else {
            System.out.println(commandSender.getName() + " has set " + targetUUID.toString() + "'s nickname to " + nick);
            commandSender.sendMessage(ChatColor.GREEN + " > " + arg3[0] + " has been set to " + nick);
        }
        
        

        return true;
    }
    
}
