/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.events;

import java.util.UUID;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.javacord.api.entity.channel.TextChannel;
import org.json.JSONObject;

/**
 *
 * @author Jawamaster (Arthur Bulin)
 */
public class DiscordLinkEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private static Long id;
    private static String code;
    private static UUID uuid;
    private static TextChannel channel;
    private static String discordUser;

    /** Create an async discord link event so the info can be safely taken back into a 
     * synchronous thread via listener execution.
     * @param discordData
     * @param uuid
     * @param channel
     * @param discordUser 
     */
    public DiscordLinkEvent(Long id, String code, UUID uuid, TextChannel channel, String discordUser) {
        super(true); //run the event Async
        DiscordLinkEvent.id = id;
        DiscordLinkEvent.code = code;
        DiscordLinkEvent.uuid = uuid;
        DiscordLinkEvent.channel = channel;
        DiscordLinkEvent.discordUser = discordUser;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /** Get the discord data for the player.
     * @return 
     */
    public Long getID() {
        return DiscordLinkEvent.id;
    }
    
    public String getCode() {
        return code;
    }
    /** Get the UUID of the minecraft player.
     * @return 
     */
    public UUID getUniqueId() {
        return DiscordLinkEvent.uuid;
    }

    /** Get the Discord TextChannel that the link request was made in.
     * @return 
     */
    public TextChannel getChannel() {
        return DiscordLinkEvent.channel;
    }
    
    /** Get the discord user name who requested the link.
     * @return 
     */
    public String getDiscordUser(){
        return DiscordLinkEvent.discordUser;
    }

}
