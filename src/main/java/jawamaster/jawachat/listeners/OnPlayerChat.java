/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.listeners;

import jawamaster.jawachat.JawaChat;
import jawamaster.jawachat.handlers.ChatHandler;
import net.jawasystems.jawacore.handlers.SessionTrackHandler;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
        
/** This class is an EventHandler for AsyncPlayerChatEvents. It will cancel chat events and deal with them
 * accordingly based on the user's permissions and mute status. It will then pass the message along to the
 * appropriate method within the ChatHandler depending if the message is general chat, or op chat and will
 * take into account if the user is muted.
 * @author Arthur Bulin
 */
public class OnPlayerChat implements Listener {
    
    /** Listens for the AsyncPlayerChatEvent and will resolve the user permissions and status. It will route
     * to either op or general chat and resolve mute status and permissions. It will log the message to the
     * ES chatlog datastream. 
     * @param event The passed event
     */
    @EventHandler
    public static void OnPlayerChat(AsyncPlayerChatEvent event)  {
        //Cancel the event
        event.setCancelled(true);
        
        //Set Logging variables
        boolean muted = false;
        String type = "broadcast";
        
        //If the messate is opchat
        if (event.getMessage().startsWith("#")) {
            type = "opchat";
            if (JawaChat.opsOnline.containsKey(event.getPlayer().getUniqueId()) || event.getPlayer().hasPermission("jawachat.opchat")){
                ChatHandler.opChat(event.getPlayer(), event.getMessage());
            } else {
                event.getPlayer().sendMessage(ChatColor.RED + "You do not have permission to perform that function.");
                muted = true;
            }
        } 
        //If the user identifies @staff
        else if (event.getMessage().contains("@staff")){
            ChatHandler.generalChat(event.getPlayer(), event.getMessage());
            JawaChat.getFoxelBot().notifyStaff(event.getPlayer(), event.getMessage());
        } 
        //If the user is muted
        else if (ChatHandler.isMuted(event.getPlayer().getUniqueId())){
            ChatHandler.mutedChat(event.getPlayer(), event.getMessage());
        } 
        //General chat with no specific conditions
        else {
            ChatHandler.generalChat(event.getPlayer(), event.getMessage());
        }
        
        //If enabled log to the datastream
        if (JawaChat.isChatLoggingEnabled()) {
            ChatHandler.logMessage(event.getMessage(), type, JawaChat.getIdentityServerName(), SessionTrackHandler.getSessionID(event.getPlayer()), event.getPlayer().getUniqueId().toString(), muted, false);
        }
    }
    
}
