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
import jawamaster.jawachat.handlers.FormattingHandler;
import net.jawasystems.jawacore.PlayerManager;
import net.jawasystems.jawacore.dataobjects.PlayerDataObject;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/** Give a player a tag at the beginning of their name. Just something fun.
 * @author Jawamaster (Arthur Bulin)
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
