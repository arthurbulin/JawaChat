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

import jawamaster.jawachat.handlers.ChatHandler;
import net.jawasystems.jawacore.PlayerManager;
import net.jawasystems.jawacore.dataobjects.PlayerDataObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/** This command mutes a player but still shows their chat in opchat.
 * @author Jawamaster (Arthur Bulin)
 */
public class Mute implements CommandExecutor {
    private static final TextComponent USAGE = Component.text(" > ", NamedTextColor.GREEN)
            .append(Component.text("/mute <player>", NamedTextColor.WHITE));
    private static final TextComponent PLAYERERRORMSG = Component.text(" > Error: That Player wasn't found either online or offline. Try using the player's actual minecraft name and not their nickname.", NamedTextColor.RED);
    
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args == null || args.length == 0) {
            commandSender.sendMessage(USAGE);
        } else if (args.length > 0) {
            PlayerDataObject target = PlayerManager.getPlayerDataObject(args[0]);
            if (target == null) {
                commandSender.sendMessage(PLAYERERRORMSG);
                return true;
            }
            
            PlayerDataObject admin = PlayerManager.getPlayerDataObject((Player) commandSender);
            if (!ChatHandler.isMuted(target)) {
                ChatHandler.mute(target);
                //commandSender.sendMessage(ChatColor.GREEN + "> " + target.getFriendlyName() + ChatColor.GREEN + " has been " + ChatColor.AQUA + "muted.");
                TextComponent broadcast = Component.text("[",NamedTextColor.GRAY)
                        .append(target.getColoredName())
                        .append(Component.text(" has been muted by ", NamedTextColor.GRAY))
                        .append(admin.getFriendlyName())
                        .append(Component.text("]", NamedTextColor.GRAY))
                        .decorate(TextDecoration.ITALIC);
                ChatHandler.opBroadcast(broadcast);

            } else {
                ChatHandler.unmute(target);
//                BaseComponent[] baseComp = new ComponentBuilder("[").color(ChatColor.GRAY)
//                        .italic(true)
//                        .append(target.getPlainNick() + " has been unmuted by ")
//                        .append(admin.getPlainNick())
//                        .append("]")
//                        .create();
//                ChatHandler.opBroadcast(baseComp);
                TextComponent broadcast = Component.text("[",NamedTextColor.GRAY)
                        .append(target.getColoredName())
                        .append(Component.text(" has been unmuted by ", NamedTextColor.GRAY))
                        .append(admin.getFriendlyName())
                        .append(Component.text("]", NamedTextColor.GRAY))
                        .decorate(TextDecoration.ITALIC);
                ChatHandler.opBroadcast(broadcast);
                //commandSender.sendMessage(ChatColor.GREEN + "> " + target.getFriendlyName() + ChatColor.GREEN + " has been " + ChatColor.GOLD + "unmuted.");
            }
        }
        return true;
    }
}
