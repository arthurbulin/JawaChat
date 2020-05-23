/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.crosslink;

import java.io.Serializable;

/**
 *
 * @author alexander
 */
public class ChatMessageObject implements Serializable {
    private final long serialVersionUID = 5950169519310163575L;
    private final String playerName;
    private final String msg;
    private final String serverName;
    private final String channel;
    private final String destination;
    
    public ChatMessageObject (String playerName, String message, String serverName, String channel, String destination) {
        this.playerName = playerName;
        this.msg = message;
        this.serverName = serverName;
        this.channel = channel;
        this.destination = destination;
    }
    
    public String getPlayerName(){
        return playerName;
    }
    
    public String getMessage(){
        return msg;
    }
    
    public String getOriginServer() {
        return serverName;
    }
    
    public String getDestination() {
        return destination;
    }
    
    public String getChannel(){
        return channel;
    }
}
