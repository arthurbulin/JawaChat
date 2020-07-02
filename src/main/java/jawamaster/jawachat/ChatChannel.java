/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat;

import java.util.HashSet;
import java.util.UUID;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;

/**
 *
 * @author alexander
 */
public class ChatChannel {

    private String name;
    private String permission;
    private UUID owner;
    private boolean isSystem;
    private boolean isPrivate;

    public ChatChannel(String name, UUID owner, boolean isPrivate) {
        this.owner = owner;
        this.isPrivate = isPrivate;
        this.name = name;
    }

    public ChatChannel(String name, String permission) {
        this.owner = UUID.fromString("00000000-0000-0000-0000-000000000000"); //System uuid
        this.isPrivate = true;
        this.isSystem = true;
        this.name = name;
        if (name.equals("GLOBAL"))
        this.permission = permission;
    }
    
    public ChatChannel(String name){
        this.owner = UUID.fromString("00000000-0000-0000-0000-000000000000"); //System uuid
        this.isPrivate = false;
        this.isSystem = true;
        this.name = name;
    }

    private boolean canJoin(Player player, boolean invited){
        if (isSystem){
            return player.hasPermission(permission);
        } else {
            return !isPrivate || (isPrivate && invited);
        }
    }
    
    public boolean join(Player player, boolean invited) {
        if (canJoin(player, invited))return true;
        else return false;
    }
    

}
