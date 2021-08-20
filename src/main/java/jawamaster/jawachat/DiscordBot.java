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

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jawamaster.jawachat.events.DiscordLinkEvent;
import jawamaster.jawachat.events.DiscordRestartEvent;
import net.jawasystems.jawacore.PlayerManager;
import net.jawasystems.jawacore.dataobjects.PlayerDataObject;
import net.jawasystems.jawacore.handlers.ESHandler;
import net.jawasystems.jawacore.utils.TimeParser;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

/** This class is under development but is currently in use. This class acts as an
 * instance of a discord bot. This bot will be allowed to linkup with the discord
 * chat so that players may link their accounts, alert staff when they aren't online,
 * as well as allow staff to remotely monitor the server. Documentation, methods,
 * and variables in this class are in a high state of flux.
 * @author Jawamaster (Arthur Bulin)
 */
public class DiscordBot {

    private static final Logger LOGGER = Logger.getLogger("DiscordBot");
    private String name;
    private String token;
    private DiscordApi api;
    private HashMap<String, Role> rankMap;
    private HashMap<String, Channel> channelMap;
    private ConfigurationSection discordPermissions;

    /**
     * Construct a DiscordBot. Currently this is only a linking bot and does not
     * allows custom bot types.
     * @param name
     * @param token
     */
    public DiscordBot(String name, String token) {
        this.name = name;
        this.token = token;
        this.api = new DiscordApiBuilder().setToken(token).login().join();
        
        //buildRankMap();
        //loadPermissions();
        
        
        //buildRankMap();
        //buildChannelMap();
        LOGGER.log(Level.INFO, "{0} has been initialized as a discord bot with token: {1}", new Object[]{name, token});
        
    }
    
    /** Load discord user permissions into the bot.
     */
    private void loadPermissions(){
        if (JawaChat.getConfiguration().getConfigurationSection("discord-configuration").contains("permissions") && (!JawaChat.getConfiguration().getConfigurationSection("discord-configuration").getConfigurationSection("permissions").getKeys(false).isEmpty())) {
            discordPermissions = JawaChat.getConfiguration().getConfigurationSection("discord-configuration").getConfigurationSection("permissions");
            LOGGER.log(Level.INFO, "Permissions for {0} roles loaded from the configuration file", discordPermissions.getKeys(false).size());
        } else {
            discordPermissions = JawaChat.getConfiguration().getConfigurationSection("discord-configuration").createSection("permissions");
            JawaChat.getPlugin().saveConfig();
            LOGGER.log(Level.INFO, "The discord permissions section does not exists. It has been created.");
        }
        
        
    }

    /**
     * Initiate the bot's listeners. This currently does not accept input but
     * will later accept a specification that will determine the type of bot
     * created.
     */
    public void initiateListener() {
        //buildRankMap();
        for (Server serv : api.getServers()){
            System.out.println(serv.getName());
        }
        
        api.addMessageCreateListener(event -> {
            /* Command Logic:
                All commands start with !foxelbot
                    - link commands - create and manage server-to-discord links
                    - identify commands - identify a user's converse identity
                    - development commands - commands to get data for development
                    - server commands - control and manage the minecraft server from discord
                    - config commands - configure the bot remotely
            */
            System.out.println(event.getMessageAuthor() + event.getMessageContent());
            if (event.getMessageContent().equals("!ping")){
                System.out.println("ping-pong");
                MessageBuilder msg = new MessageBuilder();
                msg.append("pong!");
                msg.send(event.getChannel());
            }
            if (event.getMessageContent().matches("!(FoxelBot|foxelbot)\\s.*")) {
                String[] commandMessage = event.getMessageContent().split(" ");
                if (commandMessage[1].equalsIgnoreCase("link")) {
                    resolveLinking(commandMessage, event.getChannel(), event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getId());
                } else if (event.getMessageContent().matches("^.*\\sidentify\\s.*")) {
                    getMinecraftLinkInfo(event.getMessageContent(), event.getChannel());
                } else if (commandMessage[1].equalsIgnoreCase("dev")){
                    developmentMessaging(event.getMessageContent(), event.getChannel(), event.getMessageAuthor().asUser().get());
                } else if (commandMessage.length >= 2 && commandMessage[1].equalsIgnoreCase("server")){
                    //Resolve server controls
                    resolveServerCommand(event.getServer().get(), commandMessage, event.getChannel(), event.getMessageAuthor());
                } else if (commandMessage[1].equalsIgnoreCase("config") && commandMessage[2].equalsIgnoreCase("info")){
                    resolveConfigCommand(event);
                }
            }
        });
    }
    
    //========================== Config Commands ==========================
    private void resolveConfigCommand(MessageCreateEvent event) {
        new MessageBuilder()
                .append("Server Info: " + event.getServer().get().getName() + ":" + event.getServer().get().getIdAsString())
                .send(event.getChannel());

        for (ChannelCategory chnlCtg : event.getServer().get().getChannelCategories()) {
            MessageBuilder channels = new MessageBuilder();
            channels.append("Channels for catagory: ")
                    .append(chnlCtg.getName());
            for (Channel chnl : chnlCtg.getChannels()) {
                channels.append("Channel: " + chnl.asServerChannel().get().getName() + " with ID: " + chnl.getIdAsString() + "\r");
            }
            channels.send(event.getChannel());
        }
        MessageBuilder msg = new MessageBuilder();
        msg.append("Roles: \r");
        for (Role role : event.getServer().get().getRoles()) {
                msg.append("Role: " + role.getName() + " with id " + role.getIdAsString() + "\r");
        }
        msg.send(event.getChannel());
        
        
        
    }

    //========================== Server Commands ==========================
    private void resolveServerCommand(Server server, String[] messageContent, TextChannel channel, MessageAuthor user){
        PlayerDataObject pdo = getLinkedUser(user.asUser().get());
        boolean allowedControl = isAllowedServerControl(pdo, user.asUser().get(), server);
        if (allowedControl) {
            
            if (messageContent[2].equalsIgnoreCase("restart")) {
                int delay = 30;

                    if (messageContent.length == 4) {
                        delay = TimeParser.getSeconds(messageContent[3]);
                    }
                    new MessageBuilder()
                            .append(user.asUser().get())
                            .append(" has initiated a server restart. I will go down in " + delay + " seconds and the server will attempt to restart.")
                            .send(channel);
                    Bukkit.getServer().getPluginManager().callEvent(new DiscordRestartEvent(true, delay));
            }
        } else {
            String alertMessage = "";
            if (pdo == null){
                alertMessage = "User " + user.asUser().get().getDiscriminatedName() + " is not linked on discord but has attempted to use a server control command.";
            } else {
                alertMessage = user.asUser().get().getDiscriminatedName() + " has attempted to user a server control command and is not authorized.";
            }
            
            new MessageBuilder()
                            .append(alertMessage)
                            .send(channel);
            LOGGER.log(Level.INFO, alertMessage);
        }
    }
    
    //FIXME this is just bad
    private boolean isAllowedServerControl(PlayerDataObject player, User user, Server server) {
        boolean minecraftAllowed = (player != null) && (player.getRank().equalsIgnoreCase("superadmin") || player.getRank().equalsIgnoreCase("owner")); //FIXME allow this to be permissions based
        boolean discordAllowed = server.getRoles(user).contains(server.getRoleById(202947498342350848L)) || server.getRoles(user).contains(server.getRoleById(306382249136357376L));
        return minecraftAllowed || discordAllowed;
    }
    
    /**
     * This resolves a discord link by isolating the link code, searching the ES
     * Database, and linking the player data, then calling a DiscordLinkEvent to
     * safely take this data back into a synchronous thread. If this fails a
     * message is reported in discord that there was no link code found.
     *
     * @param messageContent
     * @param channel
     * @return
     */
    private void resolveLinking(String[] messageContent, TextChannel channel, String discriminatedName, Long id) {
        //Resolve the link code via regex
        if (messageContent[2].matches("#[0-9]+#") && PlayerManager.codeExists(messageContent[2])) {
            //I don't think this is really needed like I thought it was
            Bukkit.getServer().getPluginManager().callEvent(new DiscordLinkEvent(id, messageContent[2], PlayerManager.getPlayerCodedUUID(messageContent[2]), channel, discriminatedName));
        } else {
            channel.sendMessage("I'm sorry I wasn't able to locate your link code. Please check that it is correct and has not expired.");
        }
    }

    /**
     * Evaluates an identify command and sends the respective messages.
     *
     * @param messageContent
     * @param channel
     */
    private void getMinecraftLinkInfo(String messageContent, TextChannel channel) {
        String user = messageContent.replaceFirst("!(foxebot|FoxelBot)\\sidentify\\s", "");
        String userID = null;
        for (User cachedUser : api.getServerById(93446384836939776L).get().getMembers()) {

            if (cachedUser.getDiscriminatedName().equalsIgnoreCase(user)) {
                userID = cachedUser.getDiscriminatedName();
                break;
            }
        }

        if (userID == null) {
            channel.sendMessage("I'm sorry I don't recogonize that user name. Please use the user's discriminated name. i.e. " + api.getYourself().getDiscriminatedName());
        } else {
            PlayerDataObject target = ESHandler.getPlayerData("players", "discord-data.discord-name", userID);
            if (target == null) {
                channel.sendMessage(user + " does not appear to be linked to Discord");
            } else {
                channel.sendMessage(user + " is linked to Minecraft as " + target.getName());
            }
        }
    }

    public void notifyUser(TextChannel channel, String message) {
        channel.sendMessage(message);
    }

    public void notifyStaff(Player player, String message) {
        PlayerDataObject target = PlayerManager.getPlayerDataObject(player);
        String msg = "User [" + target.getRank() + "]" + target.getPlainNick() + " has pinged staff with message: " + message;
        api.getChannelById("714593157299568742").get().asTextChannel().get().sendMessage(msg);
    }
    
    //========================== Development Commands ==========================
    private void developmentMessaging(String messageContent, TextChannel channel, User user){
        String[] args = messageContent.replaceFirst("!(FoxelBot|foxelbot)\\sdev\\s", "").split("\\s");
        MessageBuilder msg = new MessageBuilder();
        if (messageContent.matches("^.*rolemap.*")) {
            for (Role role : api.getRoles()) {
                msg.append("> Role: " + role.getName() + " with id " + role.getIdAsString() + "\r");
            }
            msg.send(channel);
        } else if (messageContent.matches("^.*at\\sme.*")) {
                    msg.append("I'll at you bro ")
                    .append(user).send(channel);
        } else if (messageContent.matches("^.*colormap.*")){
        }
    }
    
    private void buildRankMap(){
        
        for (String section : discordPermissions.getKeys(false)){
            rankMap.put(section, api.getRoleById(discordPermissions.getConfigurationSection(section).getLong("discordrank")).get());
        }
        
//        if (JawaChat.getConfiguration().contains("discord-configuration") && JawaChat.getConfiguration().getConfigurationSection("discord-configuration").contains("discord-channel-map")) {
//            ConfigurationSection rankSection = JawaChat.getConfiguration().getConfigurationSection("discord-configuration").getConfigurationSection("discord-rank-map");
//            Collection<Role> roles = api.getRoles();
//            
//            for (Role role : roles){
//                if (rankSection.contains(role.getName())){
//                    rankMap.put(role.getName(), role);
//                }
//            }
//            
//            for (String rank : rankSection.getKeys(false)){
//                rankMap.put(rank,  api.getRoleById(rankSection.getLong(rank)).get());
//            }
//        } else {
//            rankMap = new HashMap();
//        }
//        HashMap<String, Long> rankMap = new HashMap();
//        for (Role role : api.getRoles()){
//            
//        }
        
        //For now manually create the rank map
//        rankMap.put("guest", 522538121414443021L);
//        rankMap.put("builder", 306236533751414784L);
//        rankMap.put("advbuilder", 716307249764761612L);
//        rankMap.put("trainee", 310094319849832458L);
//        rankMap.put("admin", 202947541598339072L);
//        rankMap.put("superadmin", 202947498342350848L);
        //Not setting owner roles because that's risky
        //return rankMap;
    }
    
    private void buildChannelMap(){
        channelMap = new HashMap();
        
    }
    
    private static PlayerDataObject getLinkedUser(User user){
        return ESHandler.getPlayerData("players", "discord-data.discord-id", user.getIdAsString());
    }

}
