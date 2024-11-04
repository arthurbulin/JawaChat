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

import jawamaster.jawachat.handlers.FormattingHandler;
import net.jawasystems.jawacore.PlayerManager;
import net.jawasystems.jawacore.dataobjects.PlayerDataObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/** Set a * on at the beginning of a player name.
 * @author Jawamaster (Arthur Bulin)
 */
public class SetStar implements CommandExecutor {
        private static final TextComponent PLAYERERRORMSG = Component.text(" > Error: That Player wasn't found either online or offline. Try using the player's actual minecraft name and not their nickname.", NamedTextColor.RED);
        private static final TextComponent OPTIONERRORMSG = Component.text(" > That is not a valid star option. You may choose: r,y,g, or leave it empty to clear the star.", NamedTextColor.RED);
        private static final TextComponent USAGE = Component.text(" > ", NamedTextColor.GREEN)
                .append(Component.text("/star <player> [r|g|y]", NamedTextColor.WHITE));
        private static final TextComponent REMOVED = Component.text(" > Your star has been removed.", NamedTextColor.GREEN);
        private static final TextComponent ADDED = Component.text(" > You have been given a ", NamedTextColor.GREEN);
        
    @Override
    public boolean onCommand(CommandSender commandSender, Command arg1, String arg2, String[] arg3) {

        
//        String usage = "/star <player> [r|g|y]";
//        Set options = new HashSet(Arrays.asList("r", "g", "y"));

        PlayerDataObject pdObject = PlayerManager.getPlayerDataObject(arg3[0]);
        if (pdObject == null) {
            commandSender.sendMessage(PLAYERERRORMSG);
            return true;
        }

        if (arg3 == null || arg3.length == 0) {
            commandSender.sendMessage(USAGE);
            return true;
        } else if (arg3.length == 1) {
            pdObject.setStar("");
            pdObject.sendMessageIf(REMOVED);
            if (!pdObject.equals(((Player) commandSender))) {
                TextComponent removedMSG = Component.text(" > ", NamedTextColor.GREEN)
                        .append(pdObject.getFriendlyName())
                        .append(Component.text("'s star has been removed.", NamedTextColor.GREEN));
                commandSender.sendMessage(removedMSG);
            }
        } else if (arg3.length > 1) {
            if (!(arg3[1].equalsIgnoreCase("r") || arg3[1].equalsIgnoreCase("y") || arg3[1].equalsIgnoreCase("g"))){
                commandSender.sendMessage(OPTIONERRORMSG);
                return true;
            }
            pdObject.setStar(arg3[1].toLowerCase());

            //If player is online send message
            TextComponent addedMSG = ADDED.append(pdObject.getStarComponent());
            pdObject.sendMessageIf(addedMSG);
            if (!pdObject.equals(((Player) commandSender))) {
                TextComponent otherAddedMSG = Component.text(" > ", NamedTextColor.GREEN)
                        .append(pdObject.getFriendlyName())
                        .append(Component.text(" has been given a ", NamedTextColor.GREEN))
                        .append(pdObject.getStarComponent());
                commandSender.sendMessage(otherAddedMSG);
            }
        }
        //If the player is online rebuild their name
        if (pdObject.isOnline()) {
            FormattingHandler.recompilePlayerName(pdObject.getPlayer());
        }
        return true;
    }

}