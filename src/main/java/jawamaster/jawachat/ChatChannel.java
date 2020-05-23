/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat;

import java.util.HashSet;
import java.util.UUID;
import org.bukkit.entity.Player;

/**
 *
 * @author alexander
 */
public class ChatChannel {
    
    private UUID owner;
    private boolean isPrivate;
    private HashSet<Player> members;
    
    
    public ChatChannel(String name, UUID owner, boolean isPrivate) {
        this.owner = owner;
        this.isPrivate = isPrivate;
    }
}
