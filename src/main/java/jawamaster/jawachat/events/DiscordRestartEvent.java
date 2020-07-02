/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author Jawamaster (Arthur Bulin)
 */
public class DiscordRestartEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    private boolean announce;
    private int delay;

    public DiscordRestartEvent(boolean announce, int delay) {
        super(true); //run the event Async
        this.announce = announce;
        this.delay = delay;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public boolean announce(){
        return announce;
    }
    
    public int delay(){
        return delay;
    }
    
}
