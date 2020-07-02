/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.listeners;

import jawamaster.jawachat.JawaChat;
import jawamaster.jawachat.events.DiscordLinkEvent;
import net.jawasystems.jawacore.PlayerManager;
import net.jawasystems.jawacore.dataobjects.PlayerDataObject;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author Jawamaster (Arthur Bulin)
 */
public class DiscordLinkListener implements Listener {

    @EventHandler
    public static void DiscordLinkListener(DiscordLinkEvent event) {
        PlayerDataObject player = PlayerManager.getPlayerDataObject(event.getUniqueId());
        player.linkToDiscord(event.getID(), event.getDiscordUser());
        PlayerManager.removePlayerCode(event.getCode(), event.getUniqueId());
        JawaChat.getFoxelBot().notifyUser(event.getChannel(), "@" + event.getDiscordUser() + " your link request has been completed. You are now linked as " + PlayerManager.getPlayerDataObject(event.getUniqueId()).getPlainNick());
    }

}
