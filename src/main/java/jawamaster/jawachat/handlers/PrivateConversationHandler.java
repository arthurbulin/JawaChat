/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.handlers;

import java.util.HashMap;
import java.util.UUID;
import jawamaster.jawachat.JawaChat;
import net.jawasystems.jawacore.JawaCore;
import net.jawasystems.jawacore.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 *
 * @author alexander
 */
public class PrivateConversationHandler {
    
    private static HashMap<UUID, UUID> conversations;
    //target, offerer
    private static HashMap<UUID, UUID> offers;
    
    public PrivateConversationHandler(){
        conversations = new HashMap();
        offers = new HashMap();
    }
    
    public static void offerConversation(UUID player, UUID target){
        offers.put(target, player);
        PlayerManager.getPlayerDataObject(target).sendMessage(ChatColor.GREEN + " > " + PlayerManager.getPlayerDataObject(player).getDisplayName() + ChatColor.GREEN + " want's to start a conversation with you. Do /pc to accept. If you are already in a conversation this will replace it.");
        PlayerManager.getPlayerDataObject(player).sendMessage(ChatColor.GREEN + " > Private convsersation request sent to " + PlayerManager.getPlayerDataObject(target).getDisplayName() + ChatColor.GREEN + ". /pc to cancel.");
        
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(JawaChat.getPlugin(), () -> {
                cancelOffer(target, player, true);
            }, 600);
    }
    
    
    private static void spawnOfferExpire(){
        
    }
    
    public static void cancelOffer(UUID target, UUID player, boolean expired){
        if (offers.containsKey(target)) {
            offers.remove(target);
            if (expired) {
                PlayerManager.getPlayerDataObject(target).sendMessage(ChatColor.RED + " > Chat offer from " + PlayerManager.getPlayerDataObject(player).getDisplayName() + ChatColor.RED + " has expired.");
                PlayerManager.getPlayerDataObject(player).sendMessage(ChatColor.RED + " > Your chat offer to " + PlayerManager.getPlayerDataObject(target).getDisplayName() + ChatColor.RED + " has expired.");
            } else {
                PlayerManager.getPlayerDataObject(target).sendMessage(ChatColor.RED + " > Chat offer from " + PlayerManager.getPlayerDataObject(player).getDisplayName() + ChatColor.RED + " has been canceled.");
                PlayerManager.getPlayerDataObject(player).sendMessage(ChatColor.RED + " > Your chat offer to " + PlayerManager.getPlayerDataObject(target).getDisplayName() + ChatColor.RED + " has been canceled.");
            }
        }
    }
    
    public static void acceptOffer(UUID target){
        conversations.put(target, offers.get(target));
        conversations.put( offers.get(target), target);
        offers.remove(target);
        PlayerManager.getPlayerDataObject(target).sendMessage(ChatColor.GREEN + " > Private conversation started with " + PlayerManager.getPlayerDataObject(conversations.get(target)).getDisplayName());
        PlayerManager.getPlayerDataObject(conversations.get(target)).sendMessage(ChatColor.GREEN + " > Private conversation started with " + PlayerManager.getPlayerDataObject(target).getDisplayName());
    }
}
