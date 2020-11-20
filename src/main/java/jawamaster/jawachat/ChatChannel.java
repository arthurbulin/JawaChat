/*
 * Copyright (C) 2020 Jawamaster (Arthur Bulin)
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
package jawamaster.jawachat;

import java.util.UUID;
import org.bukkit.entity.Player;

/** This class is a ChatChannel object used to create chat channels. It is currently
 * under development and is no used at the current moment.
 * @author Jawamaster (Arthur Bulin)
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
