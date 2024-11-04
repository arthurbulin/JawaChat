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

import net.jawasystems.jawacore.PlayerManager;
import net.jawasystems.jawacore.dataobjects.PlayerDataObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/** This command is used to create, delete, or provide info for links to discord
 * players. This command is useless unless the discordbot is online.
 * @author Jawamaster (Arthur Bulin)
 */
public class DiscordLink implements CommandExecutor {
    private static final TextComponent LINKED = Component.text("> You are linked to discord as ", NamedTextColor.GREEN);
    private static final TextComponent CODE = Component.text("> Click to copy the link command and PM FoxelBot on discord with the following message: ", NamedTextColor.GREEN);
    private static final TextComponent PLAYERERRORMSG = Component.text(" > Error: That Player wasn't found either online or offline. Try using the player's actual minecraft name and not their nickname.", NamedTextColor.RED);
    private static final TextComponent PLAYERNOTLINKED = Component.text(" > That user is not linked to Discord.", NamedTextColor.RED);
    
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        PlayerDataObject pdObject = PlayerManager.getPlayerDataObject((Player) commandSender);

        if (args == null || args.length == 0) {
            if (pdObject.isDiscordLinked()) {
                TextComponent linked = Component.empty()
                        .append(LINKED)
                        .append(Component.text(pdObject.getDiscordName(), NamedTextColor.GREEN));
                commandSender.sendMessage(linked);
            } else {
                //String discordCode = pdObject.generateDiscordCode();
                String discordCode = pdObject.generateDiscordCode();
                PlayerManager.putPlayerCode(discordCode, ((Player) commandSender).getUniqueId());
                TextComponent message = Component.empty()
                        .append(CODE)
                        .append(Component.text(discordCode, NamedTextColor.WHITE))
                        .clickEvent(ClickEvent.copyToClipboard("!FoxelBot link ".concat(discordCode)))
                        .hoverEvent(HoverEvent.showText(Component.text("!FoxelBot link ".concat(discordCode))));
                commandSender.sendMessage(message);
            }
        } else {
            
            if (args[0].matches("i?n?f?o?")) {
                resolveInfo(args, commandSender);
            } else if (args[0].matches("r?e?m?o?v?e?")) {

            }
        }

        return true;
    }

    private boolean resolveInfo(String[] args, CommandSender commandSender) {
        PlayerDataObject target = resolvePlayer(args, commandSender);
        if (target == null) {
            commandSender.sendMessage(PLAYERERRORMSG);
            return true;
        }
        if (!target.isDiscordLinked()){
            commandSender.sendMessage(PLAYERNOTLINKED);
            return true;
        }
        
        TextComponent message = Component.text("> User, ", NamedTextColor.GREEN)
                .append(target.getFriendlyName())
                .append(Component.text(" , is linked to Discord with username ", NamedTextColor.GREEN))
                .append(Component.text(target.getDiscordName(), NamedTextColor.GRAY));
        commandSender.sendMessage(message);
//        BaseComponent[] baseComp = new ComponentBuilder("> User, ").color(ChatColor.GREEN)
//                .append(target.getFriendlyName())
//                .append(" is linked to Discord with username ").color(ChatColor.GREEN)
//                .append(target.getDiscordName())
//                .create();
//        
//        commandSender.spigot().sendMessage(baseComp);
        return true;
               
    }
    
    private PlayerDataObject resolvePlayer(String[] args, CommandSender commandSender){
        if (args.length == 1) {
            return PlayerManager.getPlayerDataObject((Player) commandSender);
        } else {
            return PlayerManager.getPlayerDataObject(args[1]);
        }


    }
}
