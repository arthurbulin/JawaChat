/*
 * Copyright (C) 2020 Jawamaster (Arthur Bulin)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

/** Currently in development and is unused. Creates a private chat conversation
 * with another player. This will probably use chat channels to control conversation.
 * @author Jawamaster (Arthur Bulin)
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
