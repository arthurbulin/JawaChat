/*
 * Copyright (C) 2021 Jawamaster (Arthur Bulin)
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
import jawamaster.jawapermissions.handlers.PermissionsHandler;
import net.jawasystems.jawacore.PlayerManager;
import net.jawasystems.jawacore.dataobjects.PlayerDataObject;
import org.bukkit.entity.Player;

/**
 *
 * @author Jawamaster (Arthur Bulin)
 */
public class ChatMessage {
    private final UUID PLAYERUUID;
    private final String PLAYERFRIENDLYNAME;
    private final String PLAYERTAG;
    private final int PLAYERIMMUNITY;
    private final String MESSAGE;
    private final String CHANNEL;
    
    public ChatMessage(Player player, String message, String channel){
        this.PLAYERUUID = player.getUniqueId();
        this.CHANNEL = channel;
        this.MESSAGE = message;
        
        PlayerDataObject pdo = PlayerManager.getPlayerDataObject(player);
        this.PLAYERFRIENDLYNAME = pdo.getFriendlyName();
        this.PLAYERTAG = pdo.getTag();
        this.PLAYERIMMUNITY = PermissionsHandler.getImmunity(pdo.getRank());
    }
}
