/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.handlers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import jawamaster.jawachat.ChatChannel;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;

/**
 *
 * @author alexander
 */
public class ChatChannelHandler {
    public static final String GLOBALCHATCHANNEL = "GLOBAL";
    private static final HashMap<UUID, Player> OPERATORSONLINE = new HashMap<>();
    private static final HashMap<String, HashSet<Player>> USERCHATCHANNELS = new HashMap<>();
    private static final HashMap<String, ChatChannel> CHATCHANNELS = new HashMap();
    //private HashMap<
    
    public ChatChannelHandler(){
        //Create global chat
        CHATCHANNELS.put(GLOBALCHATCHANNEL, new ChatChannel(GLOBALCHATCHANNEL));
        CHATCHANNELS.put("OPERATORS", new ChatChannel("OPERATORS", "jawachat.opchat"));
        //Create operator chat
        
    }

    private void broadcastToChannel(String channel, String message) {
        USERCHATCHANNELS.get(channel).forEach((player) -> {
            player.sendMessage(message);
        });
    }

    private void broadcastToChannel(String channel, BaseComponent[] baseComp) {
        USERCHATCHANNELS.get(channel).forEach((player) -> {
            player.spigot().sendMessage(baseComp);
        });
    }
    
    
}
