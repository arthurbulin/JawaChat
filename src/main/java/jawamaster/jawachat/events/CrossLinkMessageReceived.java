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
package jawamaster.jawachat.events;

import jawamaster.jawachat.crosslink.Message;
import jawamaster.jawachat.handlers.ChatHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author Jawamaster (Arthur Bulin)
 */
public class CrossLinkMessageReceived extends Event{
    
    private static final HandlerList handlers = new HandlerList();
    
    public CrossLinkMessageReceived (Message message){
        //super(true);
        System.out.println("CrossLinkMessageReceived Event");
        //message.getChatMessage().getMessage();
        System.out.println(message.getChatMessage());
        //ChatHandler.broadcast(message.getChatMessage());
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
}
