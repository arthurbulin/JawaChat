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
import jawamaster.jawachat.handlers.ChatHandler;
import net.jawasystems.jawacore.PlayerManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/** This command allows private messaging of another player.
 * @author Jawamaster (Arthur Bulin)
 */
public class PrivateMessage implements CommandExecutor {
    /*
    Private message logic:
        /pm <player> <message>
        /pm <message> - sends message to last person you used /pm on
        /pmc <player> - creates a private chat conversation to the other player.
    */
    
    public static final String[] USAGE = {
                                            ChatColor.BLUE + "> Private message command (/pm)",
                                            ChatColor.BLUE + " > " + ChatColor.GOLD + "/pm <player> <message> " + ChatColor.BLUE + "- Sends <message> to <player>",
                                            ChatColor.BLUE + " > " + ChatColor.GOLD + "/pm <message> " + ChatColor.BLUE + "- Sends <message> to the last player you pm'd this session",
                                            //ChatColor.BLUE + " > " + ChatColor.GOLD + "/pm <player> " + ChatColor.BLUE + "- Creates a private chat conversation with <player>. You won't need the /pm command.",
                                            ChatColor.BLUE + " > " + ChatColor.GOLD + "/pm " + ChatColor.BLUE + "- Displays this help and resets your replier (i.e. /pm <message> won't go to the last person."};
    
    @Override
    public boolean onCommand(CommandSender commandSender, Command arg1, String arg2, String[] arg3) {
        //PlayerDataObject player = PlayerManager.getPlayerDataObject(((Player) commandSender).getUniqueId());
        //PlayerDataObject target = PlayerManager.getPlayerDataObject(arg3[0]);
        if (!(commandSender instanceof Player)){
            System.out.println("This is for players only.");
            return true;
        }
        
        if (arg3 == null || arg3.length == 0){
            Player sender = (Player) commandSender;
            PlayerManager.getPlayerDataObject(sender).sendMessage(USAGE);
            ChatHandler.resetReplier(sender);
            sender.sendMessage(ChatColor.GREEN + "> Your private chat replier has been reset.");
            
        }
        
        int range = 1;
        Player toPlayer;
        boolean validPlayer = PlayerManager.isValidPlayer(arg3[0]);
        if (!validPlayer && !ChatHandler.hasReplier((Player) commandSender)) { 
            commandSender.sendMessage(ChatColor.RED + " > Error: That player is not an online player! Try their actual minecraft name instead of nickname.");
            return true;
        } else if (ChatHandler.hasReplier((Player) commandSender) && !validPlayer) {
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
