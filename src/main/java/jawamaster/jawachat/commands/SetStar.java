/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.commands;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
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
public class SetStar implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command arg1, String arg2, String[] arg3) {

        String usage = "/star <player> [r|g|y]";
        Set options = new HashSet(Arrays.asList("r", "g", "y"));

        PlayerDataObject pdObject = PlayerManager.getPlayerDataObject(arg3[0]);
        if (pdObject == null) {
            commandSender.sendMessage(ChatColor.RED + " > Error: That Player wasn't found either online or offline. Try using the player's actual minecraft name and not their nickname.");
            return true;
        }

        if (arg3 == null || arg3.length == 0) {
            commandSender.sendMessage(ChatColor.GREEN + " > " + ChatColor.WHITE + usage);
            return true;
        } else if (arg3.length == 1) {
            pdObject.setStar("");
            pdObject.sendMessageIf(ChatColor.GREEN + " > Your star has been removed.");
            if (!pdObject.equals(((Player) commandSender))) commandSender.sendMessage(ChatColor.GREEN + " > " + pdObject.getFriendlyName() + "'s star has been removed.");
        } else if (arg3.length > 1) {
            if (!(arg3[1].equalsIgnoreCase("r") || arg3[1].equalsIgnoreCase("y") || arg3[1].equalsIgnoreCase("g"))){
                commandSender.sendMessage(ChatColor.RED + " > That is not a valid star option. You may choose: r,y,g, or leave it empty to clear the star.");
                return true;
            }
            pdObject.setStar(arg3[1].toLowerCase());

            //If player is online send message
            pdObject.sendMessageIf(ChatColor.GREEN + " > You have been given a " + pdObject.getStar());
            if (!pdObject.equals(((Player) commandSender))) commandSender.sendMessage(ChatColor.GREEN + " > " + pdObject.getFriendlyName() + " has been given a " + pdObject.getStar());


        }
        //If the player is online rebuild their name
        if (pdObject.isOnline()) {
            FormattingHandler.recompilePlayerName(pdObject.getPlayer());
        }

        return true;
    }

}