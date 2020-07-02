/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package jawamaster.jawachat.commands;

import java.util.Arrays;
import jawamaster.jawachat.handlers.ChatHandler;
import net.jawasystems.jawacore.PlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Arthur Bulin
 */
public class PrivateMessage implements CommandExecutor {
    /*
    Private message logic:
        /pm <player> <message>
        /pm <message> - sends message to last person you used /pm on
        /pmc <player> - creates a private chat conversation to the other player.
    */
    
    @Override
    public boolean onCommand(CommandSender commandSender, Command arg1, String arg2, String[] arg3) {
        //PlayerDataObject player = PlayerManager.getPlayerDataObject(((Player) commandSender).getUniqueId());
        //PlayerDataObject target = PlayerManager.getPlayerDataObject(arg3[0]);
        if (!(commandSender instanceof Player)){
            System.out.println("This is for players only.");
            return true;
        }
        
        int range = 1;
        Player toPlayer;
        if (!PlayerManager.isValidPlayer(arg3[0]) && !ChatHandler.hasReplier((Player) commandSender)) { 
            commandSender.sendMessage(ChatColor.RED + " > Error: That player is not an online player! Try their actual minecraft name instead of nickname.");
            return true;
        } else if (ChatHandler.hasReplier((Player) commandSender)) {
            toPlayer = ChatHandler.replyTO((Player) commandSender);
            range = 0;
        } else {
            toPlayer = PlayerManager.getPlayer(arg3[0]);
            ChatHandler.setReplier((Player) commandSender, toPlayer);
        }
        ChatHandler.privateMessage((Player) commandSender, toPlayer, Arrays.copyOfRange(arg3, range, arg3.length));
        //target.sendMessage(ChatColor.DARK_GRAY + "[" + player.getFriendlyName() + ChatColor.DARK_GRAY + " > you]: " + ChatColor.WHITE + String.join(" ", Arrays.copyOfRange(arg3, 1, arg3.length)).trim());
        //player.sendMessage(ChatColor.DARK_GRAY + "[You > " + target.getDisplayName() + ChatColor.DARK_GRAY + "]: " + ChatColor.WHITE + String.join(" ", Arrays.copyOfRange(arg3, 1, arg3.length)).trim());
        
        return true;
    }
    
}
