/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jawamaster.jawachat.handlers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import jawamaster.jawachat.JawaChat;
import jawamaster.jawachat.crosslink.CrossLinkMessageHandler;
import jawamaster.jawachat.crosslink.Message;
import net.jawasystems.jawacore.PlayerManager;
import net.jawasystems.jawacore.dataobjects.PlayerDataObject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.JSONObject;

/**
 *
 * @author alexander
 */
public class ChatHandler {
    
    private static Map<Player,Player> replies = new HashMap();
    private static HashSet<UUID> muted = new HashSet();
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
            Message clMessage = new Message(CrossLinkMessageHandler.getUUID(), Message.MESSAGETYPE.CHATOP, JawaChat.getServerName());
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
            Message clMessage = new Message(CrossLinkMessageHandler.getUUID(), Message.MESSAGETYPE.CHATGENERAL, JawaChat.getServerName());
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
            toComp.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("PM " + fromPDO.getPlainNick()).create()));
            toComp.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/pm " + fromPDO.getName() + " "));
            toComp.append(" ").reset();
            
        ComponentBuilder fromComp = new ComponentBuilder(ChatColor.DARK_GRAY + "[You > " + toPDO.getDisplayName() + ChatColor.DARK_GRAY + "]" + ChatColor.WHITE + ":");
            fromComp.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("PM " + toPDO.getPlainNick()).create()));
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
            form.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("/pm " + playerName).create()));

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
                    urlComp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Open Link").create()));
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
        logMessage(logmsg, "general");
    }
    
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
        logMessage(logmsg, "general");
    }

    /** Transmit a BaseComponent[] message to all online player with opchat permission
     * @param baseComp 
     */
    public static void opBroadcast(BaseComponent[] baseComp){
        JawaChat.opsOnline.values().forEach((target) -> {
                    target.spigot().sendMessage(baseComp);
        });
        String logmsg = "";
        for (BaseComponent comp : baseComp){
            logmsg += comp.toPlainText();
        }
        logMessage(logmsg, "op channel");
    }
    
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
        logMessage(logmsg, "op channel");
    }
    
    /** Transmit a String message to all online players with the opchat permission.
     * @param message 
     */
    public static void opBroadcast(String message){
        JawaChat.opsOnline.values().forEach((target) -> {
            target.sendMessage(message);
        });
        logMessage(message, "op channel");
    }
    
    public static void setReplier(Player from, Player to){
        replies.put(from, to);
    }
    
    public static void resetReplier(Player from){
        if (replies.containsKey(from)) {
            replies.remove(from);
        }
    }
    
    public static Player replyTO(Player from){
        return replies.get(from);
    }
    
    public static boolean hasReplier(Player from){
        return replies.containsKey(from);
    }
    
    //MUTE Methods
    
    public static boolean isMuted(PlayerDataObject target){
        return isMuted(target.getUniqueID());
    }
    
    public static boolean isMuted(UUID uuid){
        return muted.contains(uuid);
    }
    
    public static void mute(PlayerDataObject player){
        muted.add(player.getUniqueID());
        player.mute();
    }
    
    public static void unmute(PlayerDataObject player){
        muted.remove(player.getUniqueID());
        player.unMute();
    }
    
    public static void playerJoin(PlayerDataObject player, String joinMessage){
        if (JawaChat.crosslinkEnabled) {
            Message joinMsg = new Message(CrossLinkMessageHandler.getUUID(), Message.MESSAGETYPE.INFOBROADCAST, JawaChat.getServerName());
            joinMsg.setInfoBroadcast(joinMessage);
            CrossLinkMessageHandler.sendMessage(joinMsg);
        }
        
        if (player.isMuted()) {
            muted.add(player.getUniqueID());
        }
    }
    
    public static void playerQuit(Player player, String quitMessage){
        if (JawaChat.crosslinkEnabled) {
            Message quitMsg = new Message(CrossLinkMessageHandler.getUUID(), Message.MESSAGETYPE.INFOBROADCAST, JawaChat.getServerName());
            quitMsg.setInfoBroadcast(quitMessage);
            CrossLinkMessageHandler.sendMessage(quitMsg);
        }
        
        if (muted.contains(player.getUniqueId())){
            muted.remove(player.getUniqueId());
        }
    }
    
    private static void logMessage(String message, String channel){
        Logger.getLogger("ServerChat").log(Level.INFO, "[{0}] {1}", new Object[]{channel, message});
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
