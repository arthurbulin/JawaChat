/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.handlers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import jawamaster.jawachat.JawaChat;
import jawamaster.jawachat.crosslink.CrossLinkMessageHandler;
import jawamaster.jawachat.crosslink.CrossLinkMessage;
import net.jawasystems.jawacore.JawaCore;
import net.jawasystems.jawacore.PlayerManager;
import net.jawasystems.jawacore.dataobjects.PlayerDataObject;
import net.jawasystems.jawacore.handlers.ESHandler;
import net.jawasystems.jawacore.utils.ESRequestBuilder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.JSONObject;

/**
 *
 * @author alexander
 */
public class ChatHandler {
    
    private static final Map<Player,Player> REPLIES = new HashMap();
    private static final HashSet<UUID> MUTED = new HashSet();
//    private static HashMap<Player, JSONObject> chatTrack = new HashMap();
//    private static HashMap<String, Pattern> patterns = new HashMap();
    
    /** Send a player message through the op channel.
     * @param player
     * @param message 
     */
    public static void opChat(Player player, String message) {
        //remove the # from the message
        String replaceFirst =  message.substring(1);
        
        BaseComponent[] baseComp = assembleChatMessage(createNamePredicate(player.getName(), player.getDisplayName(), ChatColor.YELLOW + "[OP] " + ChatColor.RESET, ":", ChatColor.WHITE), replaceFirst);
        
        if (JawaChat.crosslinkEnabled) {
            CrossLinkMessage clMessage = new CrossLinkMessage(CrossLinkMessageHandler.getUUID(), CrossLinkMessage.MESSAGETYPE.CHATOP, JawaChat.getServerName());
            clMessage.setChatMessage(player.getName(), player.getDisplayName(), ChatColor.YELLOW + "[OP] " + ChatColor.RESET, ":", replaceFirst);
            CrossLinkMessageHandler.sendMessage(clMessage);
        }
        
        opBroadcast(baseComp);
    }
    
    /** Send a player message through the general channel.
     * @param player
     * @param message 
     */
    public static void generalChat(Player player, String message) {
        BaseComponent[] baseComp = assembleChatMessage(createNamePredicate(player.getName(), player.getDisplayName(), "", ":", ChatColor.WHITE), message);
        
        if (JawaChat.crosslinkEnabled) {
            CrossLinkMessage clMessage = new CrossLinkMessage(CrossLinkMessageHandler.getUUID(), CrossLinkMessage.MESSAGETYPE.CHATGENERAL, JawaChat.getServerName());
            clMessage.setChatMessage(player.getName(), player.getDisplayName(), "", ":", message);
            CrossLinkMessageHandler.sendMessage(clMessage);
        }
        
        broadcast(baseComp);
    }
    
    public static void mutedChat(Player player, String message){
        BaseComponent[] toOperators = assembleChatMessage(createNamePredicate(player.getName(), player.getDisplayName(), ChatColor.RED + "[Muted] " + ChatColor.RESET, ":", ChatColor.WHITE), message);
        BaseComponent[] toMutedPlayer = assembleChatMessage(createNamePredicate(player.getName(), player.getDisplayName(), "", ":", ChatColor.WHITE), message);
        player.spigot().sendMessage(toMutedPlayer);
        opBroadcast(toOperators);
    }
    
    /** Send a private message to a player from a player.
     * @param fromPlayer
     * @param toPlayer
     * @param message 
     */
    public static void privateMessage(Player fromPlayer, Player toPlayer, String[] message) {
        PlayerDataObject fromPDO = PlayerManager.getPlayerDataObject(fromPlayer);
        PlayerDataObject toPDO = PlayerManager.getPlayerDataObject(toPlayer);
        //target.sendMessage(ChatColor.DARK_GRAY + "[" + player.getFriendlyName() + ChatColor.DARK_GRAY + " > you]: " + ChatColor.WHITE + String.join(" ", Arrays.copyOfRange(arg3, 1, arg3.length)).trim());
        //player.sendMessage(ChatColor.DARK_GRAY + "[You > " + target.getDisplayName() + ChatColor.DARK_GRAY + "]: " + ChatColor.WHITE + String.join(" ", Arrays.copyOfRange(arg3, 1, arg3.length)).trim());
        ComponentBuilder toComp = new ComponentBuilder(ChatColor.DARK_GRAY + "[" + fromPDO.getFriendlyName() + ChatColor.DARK_GRAY + " > you]" + ChatColor.WHITE + ":");
            toComp.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("PM " + fromPDO.getPlainNick())));
            toComp.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/pm " + fromPDO.getName() + " "));
            toComp.append(" ").reset();
            
        ComponentBuilder fromComp = new ComponentBuilder(ChatColor.DARK_GRAY + "[You > " + toPDO.getDisplayName() + ChatColor.DARK_GRAY + "]" + ChatColor.WHITE + ":");
            fromComp.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("PM " + toPDO.getPlainNick())));
            fromComp.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/pm " + toPDO.getName() + " "));
            fromComp.append(" ").reset();
            
        TextComponent messageComp = new TextComponent();
        for (String part : message) {
            if (part.matches("^.*(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|].*$")) {
                messageComp.addExtra(assembleURLComponent(part));
                messageComp.addExtra(" ");
            } else {
                messageComp.addExtra(part + " ");
            }
        }
        
        BaseComponent[] toMSG = toComp
                .append(messageComp)
                .create();
        BaseComponent[] fromMsg = fromComp
                .append(messageComp)
                .create();
        
        toPDO.sendMessageIf(toMSG);
        fromPDO.sendMessageIf(fromMsg);
    }
    
    /** Create the formatted player predicate for all player sent messages.
     * @param playerName
     * @param playerDisplayName
     * @param predicatePrefix
     * @param dividerString
     * @param dividerColor
     * @return 
     */
    public static ComponentBuilder createNamePredicate(String playerName, String playerDisplayName, String predicatePrefix, String dividerString, ChatColor dividerColor){
        
        //Generate the name portion of the predicate and affix any prefixes i.e. [op]
        TextComponent form = new TextComponent(predicatePrefix + playerDisplayName);
            form.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/pm " + playerName + " "));
            form.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("/pm " + playerName)));

        //Generate the divider component. The color specified will also be the base color for the rest of the message
        TextComponent divider = new TextComponent(dividerString);
        divider.setColor(dividerColor);

        //Append all of the parts into a single ComponentBuilder for other additions
        ComponentBuilder baseComp = new ComponentBuilder()
                .append(form)
                .append(divider)
                .append(" ").reset();

        return baseComp;
    }
    
    /** Assembles a chat message and resolves urls.
     * @param baseComp
     * @param message
     * @return 
     */
    public static BaseComponent[] assembleChatMessage(ComponentBuilder baseComp, String message){
        
        //Evaluate if the string contains a url matching the regex below
        if (message.matches("^.*(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|].*$")) {
            
            //Split the string into parts
            String[] messages = message.split(" ");

            /*
                Test each part to see if it is the url, if not, append it to baseComp with a trailing space. If it is the url
                then create a new text component and create the clickable link and add the popup text
            */
            for (String part : messages) {
                if (part.matches("^.*(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|].*$")) {
                    baseComp.append(assembleURLComponent(part));
                    baseComp.append(" ").reset();
                } else {
                    baseComp.append(part + " ");
                }
            }
        } else {
            baseComp.append(message)
                    .color(ChatColor.WHITE);
        }
        
        return baseComp.create();
    }
    
    /** Return a TextComponent for a URL. 
     * @param urlPart
     * @return 
     */
    public static TextComponent assembleURLComponent(String urlPart) {
        TextComponent urlComp = new TextComponent(urlPart);
                    urlComp.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, urlPart));
                    urlComp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Open Link")));
                    urlComp.setUnderlined(Boolean.TRUE);
        return urlComp;
    }
    
    /** Transmit a message to all online players.
     * @param baseComp 
     */
    public static void broadcast(BaseComponent[] baseComp){
        Bukkit.getServer().getOnlinePlayers().forEach((target) -> {
            target.spigot().sendMessage(baseComp);
        });
                String logmsg = "";
        for (BaseComponent comp : baseComp){
            logmsg += comp.toPlainText();
        }
        logMessageToConsole(logmsg, "general");
    }
    
    /** Transmit a message that originates outside of the server instance to all
     * online players.
     * @param serverName The name of the server that originates the message
     * @param baseComp The baseComponent portion of this message
     */
    public static void broadcast(String serverName, BaseComponent[] baseComp){
        BaseComponent[] comp = new ComponentBuilder(ChatColor.GREEN + "[" + serverName + "]")
                .append(baseComp)
                .create();
        Bukkit.getServer().getOnlinePlayers().forEach((target) -> {
            target.spigot().sendMessage(comp);
        });
        String logmsg = "";
        for (BaseComponent compp : comp){
            logmsg += compp.toPlainText();
        }
        logMessageToConsole(logmsg, "general");
    }

    /** Transmit a BaseComponent[] message to all online player with opchat permission.
     * @param baseComp The BaseComponent[] that makes up this message
     */
    public static void opBroadcast(BaseComponent[] baseComp){
        JawaChat.opsOnline.values().forEach((target) -> {
                    target.spigot().sendMessage(baseComp);
        });
        String logmsg = "";
        for (BaseComponent comp : baseComp){
            logmsg += comp.toPlainText();
        }
        logMessageToConsole(logmsg, "op channel");
    }
    
    /** Transmit a BaseComponent[] message with origins outside the server instance 
     * to all online players with the opchat permission.
     * @param serverName The name of the server that originates the message
     * @param baseComp The baseComponent portion of this message
     */
    public static void opBroadcast(String serverName, BaseComponent[] baseComp){
        BaseComponent[] comp = new ComponentBuilder(ChatColor.GREEN + "[" + serverName + "]")
                .append(baseComp)
                .create();
        JawaChat.opsOnline.values().forEach((target) -> {
                    target.spigot().sendMessage(comp);
        });
        String logmsg = "";
        for (BaseComponent compp : comp){
            logmsg += compp.toPlainText();
        }
        logMessageToConsole(logmsg, "op channel");
    }
    
    /** Transmit a String message to all online players with the opchat permission.
     * @param message 
     */
    public static void opBroadcast(String message){
        JawaChat.opsOnline.values().forEach((target) -> {
            target.sendMessage(message);
        });
        logMessageToConsole(message, "op channel");
    }
    
    public static void setReplier(Player from, Player to){
        REPLIES.put(from, to);
    }
    
    public static void resetReplier(Player from){
        if (REPLIES.containsKey(from)) {
            REPLIES.remove(from);
        }
    }
    
    public static Player replyTO(Player from){
        return REPLIES.get(from);
    }
    
    public static boolean hasReplier(Player from){
        return REPLIES.containsKey(from);
    }
    
    //MUTE Methods
    
    public static boolean isMuted(PlayerDataObject target){
        return isMuted(target.getUniqueID());
    }
    
    public static boolean isMuted(UUID uuid){
        return MUTED.contains(uuid);
    }
    
    public static void mute(PlayerDataObject player){
        MUTED.add(player.getUniqueID());
        player.mute();
    }
    
    public static void unmute(PlayerDataObject player){
        MUTED.remove(player.getUniqueID());
        player.unMute();
    }
    
    public static void playerJoin(PlayerDataObject player, String joinMessage){
        if (JawaChat.crosslinkEnabled) {
            CrossLinkMessage joinMsg = new CrossLinkMessage(CrossLinkMessageHandler.getUUID(), CrossLinkMessage.MESSAGETYPE.INFOBROADCAST, JawaChat.getServerName());
            joinMsg.setInfoBroadcast(joinMessage);
            CrossLinkMessageHandler.sendMessage(joinMsg);
        }
        
        if (player.isMuted()) {
            MUTED.add(player.getUniqueID());
        }
    }
    
    public static void playerQuit(Player player, String quitMessage){
        if (JawaChat.crosslinkEnabled) {
            CrossLinkMessage quitMsg = new CrossLinkMessage(CrossLinkMessageHandler.getUUID(), CrossLinkMessage.MESSAGETYPE.INFOBROADCAST, JawaChat.getServerName());
            quitMsg.setInfoBroadcast(quitMessage);
            CrossLinkMessageHandler.sendMessage(quitMsg);
        }
        
        if (MUTED.contains(player.getUniqueId())){
            MUTED.remove(player.getUniqueId());
        }
    }
    
    
    private static void logMessageToConsole(String message, String channel){
        Logger.getLogger("ServerChat").log(Level.INFO, "[{0}] {1}", new Object[]{channel, message});
    }
    
    /** Log a message to the ES database log
     * @param message String message being sent
     * @param recipient The UUID of the pm recipient or one of the following: broadcast, opchat
     * @param server The server the message takes place one
     * @param sessionID The player's session ID, null if session tracking is disabled
     * @param sender The UUID of the sender
     * @param mute boolean, true if the user is muted, false if the user is not
     * @param routed boolean, true if the logging server is not the same as the origin server
     */
    public static void logMessage(String message, String recipient, String server, String sessionID, String sender, boolean mute, boolean routed){
        Bukkit.getServer().getScheduler().runTaskAsynchronously(JawaChat.plugin, () -> {
                JSONObject chatLog = new JSONObject();
                chatLog.put("message", message.split("\\s+"));
                chatLog.put("recipient", recipient);
                chatLog.put("server", server);
                chatLog.put("session-id", sessionID);
                chatLog.put("sender", sender);
                chatLog.put("mute", mute);
                chatLog.put("routed", routed);
                chatLog.put("@timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

                ESHandler.runAsyncSingleIndexRequest(ESRequestBuilder.createIndexRequest("chatlog-minecraft-" + JawaCore.chatIndexIdentity(), chatLog));
        });
    }
    
    public static void generatePatterns(){
        InputStream io = JawaChat.getPlugin().getResource("chat_filters.json");
        String objStr = new BufferedReader(new InputStreamReader(io)).lines().collect(Collectors.joining("\n"));
        
        JSONObject patternJSON = new JSONObject(objStr);
        for (String key : patternJSON.keySet()){
            
        }
        
    }
    
    private static void screenChat(Player player, String message){
        //if (message.matches(message))
    }
}
