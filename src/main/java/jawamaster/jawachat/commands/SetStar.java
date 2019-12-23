/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.commands;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import jawamaster.jawachat.handlers.FormattingHandler;
import jawamaster.jawapermissions.PlayerDataObject;
import jawamaster.jawapermissions.handlers.PlayerDataHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Arthur Bulin
 */
public class SetStar implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command arg1, String arg2, String[] arg3) {

        String usage = "/star <player> <r|g|y|clear>";
         Set options = new HashSet(Arrays.asList("r","g","y","g"));
         PlayerDataObject pdObject;
         
        switch (arg3.length) {
            case 0://return usage
                commandSender.sendMessage(usage);
                return true;
            case 1://TODO return player star info?
                return true;
            case 2://proceed like normal
                if (options.contains(arg3[1].charAt(0))) {//validate input
                    try {
                        pdObject = PlayerDataHandler.validatePlayer(commandSender, arg3[0]);
                        if (pdObject == null) return true; //Terminate if the player isnt found
                        
                        switch (arg3[1].charAt(0)){
                            case 'r':
                                //PlayerDataHandler.starData(starData, usage)
                                break;
                        }
                        
                    } catch (IOException ex) {
                        Logger.getLogger(SetStar.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } else {
                    commandSender.sendMessage(ChatColor.RED + " > ERROR: " + arg3[1] + " is not a valid argument flag.");
                    commandSender.sendMessage(ChatColor.WHITE + " > " + usage);
                }
                return true;
            default:
                break;
        }
        return true;
    }
    
}
              //        String star;
//        switch (arg3.length){
//            case 0:
//                if (commandSender instanceof Player) ((Player) commandSender).sendMessage("You must specify a playername at minimum");
//                else System.out.println("You must specity a playername at minimum.");
//                return true;
//            case 1: //This will remove the player's star
//                star = "$$";
//                break;
//            default:
//                star = arg3[1];
//
//                if (null != star) switch (star) {
//            case "g":
//                star = ChatColor.GREEN + "*";
//                break;
//            case "r":
//                star = ChatColor.RED + "*";
//                break;
//            default:
//                if (commandSender instanceof Player) ((Player) commandSender).sendMessage("You must specify r for red or g for green.");
//                else System.out.println("You must specify r for red or g for green.");
//                return true;
//        }
//                break;
//        }
//        
//        try{
//            Player target = Bukkit.getPlayer(arg3[0]);
//            ESDataHandler.setStar(target, commandSender, star);
//        } catch (Exception e){
//            if (commandSender instanceof Player) ((Player) commandSender).sendMessage("It is likely you are trying to star an offline player. This is not currently implimented.");
//            else System.out.println("It is likely you are trying to star an offline player. This is not currently implimented.");
//        }
//