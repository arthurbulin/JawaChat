/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.commands;

import jawamaster.jawachat.handlers.ESDataHandler;
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
public class SetStar implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command arg1, String arg2, String[] arg3) {
        String star;
        switch (arg3.length){
            case 0:
                if (commandSender instanceof Player) ((Player) commandSender).sendMessage("You must specify a playername at minimum");
                else System.out.println("You must specity a playername at minimum.");
                return true;
            case 1: //This will remove the player's star
                star = "$$";
                break;
            default:
                star = arg3[1];

                if (null != star) switch (star) {
            case "g":
                star = ChatColor.GREEN + "*";
                break;
            case "r":
                star = ChatColor.RED + "*";
                break;
            default:
                if (commandSender instanceof Player) ((Player) commandSender).sendMessage("You must specify r for red or g for green.");
                else System.out.println("You must specify r for red or g for green.");
                return true;
        }
                break;
        }
        
        try{
            Player target = Bukkit.getPlayer(arg3[0]);
            ESDataHandler.setStar(target, commandSender, star);
        } catch (Exception e){
            if (commandSender instanceof Player) ((Player) commandSender).sendMessage("It is likely you are trying to star an offline player. This is not currently implimented.");
            else System.out.println("It is likely you are trying to star an offline player. This is not currently implimented.");
        }
        
        return true;
    }
    
}
