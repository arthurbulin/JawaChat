/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package jawamaster.jawachat.commands;

import java.util.Arrays;
import jawamaster.jawachat.JawaChat;
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
public class PrivateMessage implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender commandSender, Command arg1, String arg2, String[] arg3) {
        Player player = (Player) commandSender;
        Player target = (Player) Bukkit.getServer().getPlayer(arg3[0]);
        
        if (!(commandSender instanceof Player)){
            System.out.println("This is for players only.");
            return true;
        }
        
//        target.sendMessage(ChatColor.DARK_GRAY + "[" + JawaChat.playerCompiledName.get(player.getUniqueId()) + ChatColor.DARK_GRAY + "> you]: " + ChatColor.WHITE + String.join(" ", Arrays.copyOfRange(arg3, 1, arg3.length)).trim());
//        player.sendMessage(ChatColor.DARK_GRAY + "[You > " + JawaChat.playerCompiledName.get(target.getUniqueId()) + ChatColor.DARK_GRAY + "]: " + ChatColor.WHITE + String.join(" ", Arrays.copyOfRange(arg3, 1, arg3.length)).trim());
        target.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.translateAlternateColorCodes('&', player.getDisplayName()) + ChatColor.DARK_GRAY + " > you]: " + ChatColor.WHITE + String.join(" ", Arrays.copyOfRange(arg3, 1, arg3.length)).trim());
        player.sendMessage(ChatColor.DARK_GRAY + "[You > " + ChatColor.translateAlternateColorCodes('&', target.getDisplayName()) + ChatColor.DARK_GRAY + "]: " + ChatColor.WHITE + String.join(" ", Arrays.copyOfRange(arg3, 1, arg3.length)).trim());
        
        return true;
    }
    
}
