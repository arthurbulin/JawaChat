/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.commands;

import net.jawasystems.jawacore.PlayerManager;
import net.jawasystems.jawacore.dataobjects.PlayerDataObject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Jawamaster (Arthur Bulin)
 */
public class DiscordLink implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        PlayerDataObject pdObject = PlayerManager.getPlayerDataObject((Player) commandSender);

        if (args == null || args.length == 0) {
            if (pdObject.isDiscordLinked()) {
                commandSender.sendMessage(ChatColor.GREEN + "> You are linked to discord as " + pdObject.getDiscordName());
            } else {
                //String discordCode = pdObject.generateDiscordCode();
                String discordCode = pdObject.generateDiscordCode();
                PlayerManager.putPlayerCode(discordCode, ((Player) commandSender).getUniqueId());
                BaseComponent[] message = new ComponentBuilder("> Click to copy the link command and PM FoxelBot on discord with the following message: ").color(ChatColor.GREEN)
                        .append(discordCode).color(ChatColor.WHITE)
                        .event(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "!FoxelBot link " + discordCode))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Copy Discord Link Command").create()))
                        .create();
                commandSender.spigot().sendMessage(message);
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
            commandSender.sendMessage(ChatColor.RED + "> Error: That player is not found! Try their actual minecraft name instead of nickname.");
            return true;
        }
        if (!target.isDiscordLinked()){
            commandSender.sendMessage(ChatColor.RED + "> That user is not linked to discord.");
            return true;
        }
        
        BaseComponent[] baseComp = new ComponentBuilder("> User, ").color(ChatColor.GREEN)
                .append(target.getFriendlyName())
                .append(" is linked to Discord with username ").color(ChatColor.GREEN)
                .append(target.getDiscordName())
                .create();
        
        commandSender.spigot().sendMessage(baseComp);
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
