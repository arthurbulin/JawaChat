/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.commands;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jawamaster.jawachat.JawaChat;
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
public class SetNick implements CommandExecutor {

    //TODO FIXME be able to set player nick back to default
    @Override
    public boolean onCommand(CommandSender commandSender, Command arg1, String arg2, String[] arg3) {
        String nickName = null;
        switch (arg3.length) {
            case 0:
                if (commandSender instanceof Player) ((Player) commandSender).sendMessage("You must specify a playername and a nickname.");
                else System.out.println("You must specity a playername and a nickname.");
                return true;
            case 1:
                nickName = null;
                break;
            default:
                int i = 1;
                nickName = "";
                while (arg3.length > i){
                    nickName = nickName + " " + arg3[i];
                    i++;
                }   nickName = ChatColor.translateAlternateColorCodes('&', nickName);
                break;
        }
        

        
        try {
            Player target = Bukkit.getPlayer(arg3[0]);
            ESDataHandler.setNickName(target, commandSender, nickName);
        } catch (IOException ex) {
            Logger.getLogger(SetNick.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    
}
