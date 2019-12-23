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
        String usage = "/tag <playername> tag" +
                "/tag <playername>";
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
        Player target = JawaChat.plugin.getServer().getPlayer(arg3[0]);

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
        if (remove) JawaChat.playerTags.put(targetUUID, tag);
        else JawaChat.playerTags.remove(targetUUID);
        
        if (online) FormattingHandler.recompilePlayerName(target);
        //rebuild player name

//Should take format settag <playername> <tag>
//        String tag;
//        
//        if (arg3.length < 1){
//            if (commandSender instanceof Player) ((Player) commandSender).sendMessage("You must specify a player name at minimum.");
//            else System.out.println("You must specify a player name at minimum");
//            return true;
//        }
//        else if (arg3.length == 1) {
//            //This will remove the player tag
//            tag = null;
//        } else { 
//            //This will make sure that any spaces in the tag are preserved since it will assemble it from all the remaining units within arg3
//            tag = "";
//            int i = 1;
//            while (arg3.length > i){
//                tag = tag + " " + arg3[i];
//                //System.out.println("While loop run: " + i + " Nick name: " + nickName);
//                i++;
//            }
//        }
//        
//        
//        //Attempt to get player from name
//        try{
//            Player target = Bukkit.getPlayer(arg3[0]);
//            //JawaChat.playerTags.put(target.getUniqueId(), tag); //Just do this here as that will help
//            JSONObject obj = new JSONObject();
//            obj.put("tag", target);
//            ESRequestBuilder.updateRequestBuilder(obj, "players", target.getUniqueId().toString(), true);
//            ESHandler.singleUpdateRequest(request);
//        } catch( Exception e){
//            if (commandSender instanceof Player) ((Player) commandSender).sendMessage("Player must be online to change tag or the player you specified is invalid. (use tab complete)");
//            else System.out.println("Player must be online to change tag or the player you specified is invalid.");
//            return true;
//        }
//
//
//        
     return true;

    }
    
}
