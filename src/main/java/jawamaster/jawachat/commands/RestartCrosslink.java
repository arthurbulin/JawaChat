/*
 * Copyright (C) 2021 Jawamaster (Arthur Bulin)
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

import jawamaster.jawachat.JawaChat;
import jawamaster.jawachat.crosslink.CrossLinkMessageHandler;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Jawamaster (Arthur Bulin)
 */
public class RestartCrosslink implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        
        
        if (JawaChat.crosslinkEnabled) {
            commandSender.sendMessage(ChatColor.GREEN + "> Attempting to start crosslink...");
            CrossLinkMessageHandler.initializeCrossLinkMessageHandler(JawaChat.getCrossLinkRole(), JawaChat.getCrossLinkUUID(), JawaChat.getCrossLinkHost(), JawaChat.getCrossLinkPort());
        } else {
            commandSender.sendMessage(ChatColor.RED + "> Crosslink is not enabled on this server");
        }
        
        return true;
    }
    
}
