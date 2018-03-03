/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.commands;

import jawamaster.jawachat.JawaChat;
import jawamaster.jawachat.handlers.ESDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Arthur Bulin
 */
import jawamaster.jawachat.handlers.ESDataHandler;
public class SetTag implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command arg1, String arg2, String[] arg3) {
        //Should take format settag <playername> <tag>
        String tag;
        
        if (arg3.length < 1){
            if (commandSender instanceof Player) ((Player) commandSender).sendMessage("You must specify a player name at minimum.");
            else System.out.println("You must specify a player name at minimum");
            return true;
        }
        else if (arg3.length == 1) {
            //This will remove the player tag
            tag = null;
        } else { 
            //This will make sure that any spaces in the tag are preserved since it will assemble it from all the remaining units within arg3
            tag = "";
            int i = 1;
            while (arg3.length > i){
                tag = tag + " " + arg3[i];
                //System.out.println("While loop run: " + i + " Nick name: " + nickName);
                i++;
            }
        }
        
        
        //Attempt to get player from name
        try{
            Player target = Bukkit.getPlayer(arg3[0]);
            //JawaChat.playerTags.put(target.getUniqueId(), tag); //Just do this here as that will help
            ESDataHandler.setTag(target, commandSender, tag);
        } catch( Exception e){
            if (commandSender instanceof Player) ((Player) commandSender).sendMessage("Player must be online to change tag or the player you specified is invalid. (use tab complete)");
            else System.out.println("Player must be online to change tag or the player you specified is invalid.");
            return true;
        }


        
        return true;

    }
    
}
