/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package jawamaster.jawachat.commands;

import java.util.Arrays;
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
public class PrivateMessageConversation implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender commandSender, Command arg1, String arg2, String[] arg3) {
        PlayerDataObject player = PlayerManager.getPlayerDataObject((Player) commandSender);
        PlayerDataObject target = PlayerManager.getPlayerDataObject(arg3[0]);
        if (target == null || !target.isOnline()) { 
            commandSender.sendMessage(ChatColor.RED + " > Error: That player is not an online player! Try their actual minecraft name instead of nickname.");
            return true;
        }
        if (!(commandSender instanceof Player)){
            System.out.println("This is for players only.");
            return true;
        }
        
        if (player.isHavingConversation()) {
            player.sendMessage(ChatColor.RED + " > You must end your current conversation to start a new one. Use /pc to end your conversation.");
        } else {
            target.sendMessage(ChatColor.GREEN + " > Private Conversation started with " + target.getDisplayName() + ". To exit use /pc");
            
        }
        
        target.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.translateAlternateColorCodes('&', player.getDisplayName()) + ChatColor.DARK_GRAY + " > you]: " + ChatColor.WHITE + String.join(" ", Arrays.copyOfRange(arg3, 1, arg3.length)).trim());
        player.sendMessage(ChatColor.DARK_GRAY + "[You > " + target.getDisplayName() + ChatColor.DARK_GRAY + "]: " + ChatColor.WHITE + String.join(" ", Arrays.copyOfRange(arg3, 1, arg3.length)).trim());
        
        return true;
    }
    
    
}
