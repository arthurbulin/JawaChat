/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.commands;

import java.util.Arrays;
import jawamaster.jawachat.handlers.FormattingHandler;
import net.jawasystems.jawacore.PlayerManager;
import net.jawasystems.jawacore.dataobjects.PlayerDataObject;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Arthur Bulin
 */
public class SetTag implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command arg1, String arg2, String[] arg3) {
        String usage = "/settag <playername> tag"
                + "/settag <playername>";
        
        PlayerDataObject pdObject = PlayerManager.getPlayerDataObject(arg3[0]);
        if (pdObject == null){
            commandSender.sendMessage(ChatColor.RED + " > Error: That Player wasn't found either online or offline. Try using the player's actual minecraft name and not their nickname.");
            return true;
        }

        if (arg3 == null || arg3.length == 0){
          return true;  
        } else if (arg3.length == 1){
            pdObject.setTag("");
            pdObject.sendMessageIf(ChatColor.GREEN + " > Your tag has been removed.");
            if (!pdObject.equals(((Player) commandSender))) commandSender.sendMessage(ChatColor.GREEN + " > " + pdObject.getFriendlyName() + "'s tag has been removed.");
        } else if (arg3.length > 1) {
            pdObject.setTag(String.join(" ", Arrays.asList(Arrays.copyOfRange(arg3, 1, arg3.length))));
            
            //If player is online send message
            pdObject.sendMessageIf(ChatColor.GREEN + " > Your tag has been set to: " + ChatColor.translateAlternateColorCodes('&', String.join(" ", Arrays.asList(Arrays.copyOfRange(arg3, 1, arg3.length)))));

            //If the player is not the commandsender let the commandsender know what is being done
            if (!pdObject.equals((Player) commandSender)) {
                commandSender.sendMessage(ChatColor.GREEN + " > " + pdObject.getName() + "'s tag has been set to " + ChatColor.translateAlternateColorCodes('&', String.join(" ", Arrays.asList(Arrays.copyOfRange(arg3, 1, arg3.length)))));
            }

        }
        //If the player is online rebuild their name
        if (pdObject.isOnline()) {
            FormattingHandler.recompilePlayerName(pdObject.getPlayer());
        }
        return true;
    }

}
