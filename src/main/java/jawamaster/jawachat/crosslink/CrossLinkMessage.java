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
package jawamaster.jawachat.crosslink;

import java.io.Serializable;
import java.util.UUID;
import jawamaster.jawachat.handlers.ChatHandler;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

/**
 *
 * @author Jawamaster (Arthur Bulin)
 */
public class CrossLinkMessage implements Serializable {
    private final MESSAGETYPE type;
    private String playerName;
    private String playerDisplayName;
    private String predicatePrefix;
    private String dividerString;
    private String message; 
    private final UUID originatingServer;
    private final String friendlyServerName;
    private final String PLAYERUUID;
    private final String SESSIONID;
    
    public enum MESSAGETYPE {
        CHATGENERAL,
        CHATOP,
        INFO,
        INFOBROADCAST,
        COMMAND,
        VALIDATEREQUEST,
        VALIDATEAPPROVAL,
        VALIDATEDENIAL,
        TERMINATE
    }
    
    public CrossLinkMessage(UUID serverUUID, MESSAGETYPE type, String friendlyServerName){
        this.originatingServer = serverUUID;
        this.type = type;
        this.friendlyServerName = friendlyServerName;
        this.PLAYERUUID = null;
        this.SESSIONID = null;
    }
    
    public CrossLinkMessage(UUID serverUUID, MESSAGETYPE type, String friendlyServerName, String playerUUID, String sessionID){
        this.originatingServer = serverUUID;
        this.type = type;
        this.friendlyServerName = friendlyServerName;
        this.PLAYERUUID = playerUUID;
        this.SESSIONID = sessionID;
    }
    
//============ Common to all CrossLinkMessage types ===============
    /** Get the UUID of the originating server.
     * @return 
     */
    public UUID getOriginatingServer(){
        return originatingServer;
    }
    
    /** Returns the MESSAGETYPE enum for the type of message that this is.
     * @return 
     */
    public MESSAGETYPE getMessageType(){
        return type;
    }
    
    /** This returns the friendly name of the server sending the message. This will always be set.
     * @return 
     */
    public String getServerFriendlyName(){
        return friendlyServerName;
    }
    
    /** This returns the player's UUID in string form. This will always be set. This was added to support chat logging.
     * @return The player UUID in string form
     */
    public String getPlayerUUID(){
        return PLAYERUUID;
    }
    
    /** This returns the player's Session ID from the remote server. This will always be set. This was added to support chat logging.
     * @return  The player's session ID from the remote server. If the remote server does not have session tracking enabled this will be null.
     */
    public String getPlayerSessionID(){
        return SESSIONID;
    }

 //============ Common to CHAT* CrossLinkMessage types ===============
    /** Sets the Chat message  this is only applicable if the message type is a CHAT* type
     * @param playerName
     * @param playerDisplayName
     * @param predicatePrefix
     * @param dividerString
     * @param message 
     */
    public void setChatMessage(String playerName, String playerDisplayName, String predicatePrefix, String dividerString, String message){
        this.playerName = playerName;
        this.playerDisplayName = playerDisplayName;
        this.predicatePrefix = predicatePrefix;
        this.dividerString = dividerString;
        this.message = message;
    }
    
    /** This assembles a chat message and returns the BaseComponent[] to the requester.
     * This will not work unless the type is CHAT* message.
     * @return 
     */
    public BaseComponent[] getChatMessage(){
        if (type.equals(MESSAGETYPE.CHATGENERAL) || type.equals(MESSAGETYPE.CHATOP)){
            return ChatHandler.assembleChatMessage(ChatHandler.createNamePredicate(playerName, playerDisplayName, predicatePrefix, dividerString, ChatColor.WHITE), message);
        } else {
            return null;
        }
    }
    
    /** Returns the raw string message.
     * @return String message
     */
    public String getChatMessageString(){
        return message;
    }
    
//============ INFO CrossLinkMessage types ===============
    /** Get the message from the INFOBROADCAST message.
     * @return 
     */
    public BaseComponent[] getInfoBroadcast(){
        BaseComponent[] msgComp = new ComponentBuilder(message)
                .create();
        return msgComp;
    }
    
    /** Set the message to broadcast on the remote server.
     * @param infoBroadcastMsg 
     */
    public void setInfoBroadcast(String infoBroadcastMsg){
        this.message = infoBroadcastMsg;
    }

    
}
