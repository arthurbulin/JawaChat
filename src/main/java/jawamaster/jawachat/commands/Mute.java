/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.commands;

import jawamaster.jawachat.handlers.ChatHandler;
import net.jawasystems.jawacore.PlayerManager;
import net.jawasystems.jawacore.dataobjects.PlayerDataObject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author alexander
 */
public class Mute implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        String usage = "/mute <player>";
        if (args == null || args.length == 0) {
            commandSender.sendMessage(ChatColor.GREEN + "> " + usage);
        } else if (args.length > 0) {
            PlayerDataObject target = PlayerManager.getPlayerDataObject(args[0]);
            if (target == null) {
                commandSender.sendMessage(ChatColor.RED + " > Error: That player is not found! Try their actual minecraft name instead of nickname.");
                return true;
            }
            
            PlayerDataObject admin = PlayerManager.getPlayerDataObject((Player) commandSender);
            if (!ChatHandler.isMuted(target)) {
                ChatHandler.mute(target);
                //commandSender.sendMessage(ChatColor.GREEN + "> " + target.getFriendlyName() + ChatColor.GREEN + " has been " + ChatColor.AQUA + "muted.");
                BaseComponent[] baseComp = new ComponentBuilder("[").color(ChatColor.GRAY)
                        .italic(true)
                        .append(target.getPlainNick() + " has been muted by ")
                        .append(admin.getPlainNick())
                        .append("]")
                        .create();
                ChatHandler.opBroadcast(baseComp);

            } else {
                ChatHandler.unmute(target);
                BaseComponent[] baseComp = new ComponentBuilder("[").color(ChatColor.GRAY)
                        .italic(true)
                        .append(target.getPlainNick() + " has been unmuted by ")
                        .append(admin.getPlainNick())
                        .append("]")
                        .create();
                ChatHandler.opBroadcast(baseComp);
                //commandSender.sendMessage(ChatColor.GREEN + "> " + target.getFriendlyName() + ChatColor.GREEN + " has been " + ChatColor.GOLD + "unmuted.");
            }
        }
        return true;
    }
}
