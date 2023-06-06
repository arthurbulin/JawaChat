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
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import jawamaster.jawachat.commands.DiscordLink;
import jawamaster.jawachat.commands.Mute;
import jawamaster.jawachat.commands.PrivateMessage;
import jawamaster.jawachat.commands.SetNick;
import jawamaster.jawachat.commands.SetStar;
import jawamaster.jawachat.commands.SetTag;
import jawamaster.jawachat.crosslink.CrossLinkMessageHandler;
import jawamaster.jawachat.listeners.DiscordLinkListener;
import jawamaster.jawachat.listeners.DiscordRestartListener;
import jawamaster.jawachat.listeners.OnBukkitMe;
import jawamaster.jawachat.listeners.OnPlayerRankChange;
import jawamaster.jawachat.listeners.OnPlayerChat;
import jawamaster.jawachat.listeners.OnPlayerJoin;
import jawamaster.jawachat.listeners.PlayerInfoLoadedListener;
import jawamaster.jawachat.listeners.PlayerQuit;
import net.jawasystems.jawacore.JawaCore;
import net.jawasystems.jawacore.handlers.ESHandler;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This is the main operational class of the JawaChat plugin. This plugin is
 * intended to create a few nicer features for the Minecraft chat, however, it
 * does render all Minecraft chat synchronous instead of asynchronous.
 *
 * @author Jawamaster (Arthur Bulin)
 */
public class JawaChat extends JavaPlugin {

    //Plugin and handler declaration
    public static JawaChat plugin;
    public static ESHandler eshandler;

    //Variable declarations
    public static FileConfiguration config;
    public static boolean debug;
    private static String serverName;
    public static boolean discordConnected;

    //Variables for crosslink server
    public static boolean crosslinkEnabled;
    private static UUID serverUUID;
    private static String serverRole;
    private static int serverPort;
    private static String serverHost;

    //HashMaps for player controls
//    public static Map<UUID, String> playerTags;
//    public static Map<UUID, String> playerNicks;
//    public static Map<UUID, String> playerStars;
//    public static Map<UUID, String> playerCompiledName;
    public static Map<UUID, Player> opsOnline;

    public static String pluginSlug = "[JawaChat] ";

    private static DiscordBot foxelBot;

    private static JawaJanitor jawaJanitor;

    //private static final Logger CHATLOGGER = Logger.getLogger("ServerChat");
    //private static volatile FileHandler fileHandler;
    public static JawaChat getPlugin() {
        return plugin;
    }

    /**
     * Standard onEnable method to initiate the plugin's functions
     */
    @Override
    public void onEnable() {
        loadConfig();

        //intiateChatLogging();
        plugin = this;
        JawaJanitor.JawaJanitor();
        //Initiate maps
//        playerTags = new HashMap();
//        playerNicks = new HashMap();
//        playerStars = new HashMap();
//        playerCompiledName = new HashMap();
        opsOnline = new HashMap();

        discordConnected = initiateDiscordLink();
        //Attempt BungeeIntegration
        //this.getServer().getMessenger().registerOutgoingPluginChannel(this, "JawaChat");
        //this.getServer().getMessenger().registerIncomingPluginChannel(this, "JawaChat", new OnPluginMessage());

        //Declear and intiate Commands
        this.getCommand("setnick").setExecutor(new SetNick());
        this.getCommand("settag").setExecutor(new SetTag());
        this.getCommand("setstar").setExecutor(new SetStar());
        this.getCommand("pm").setExecutor(new PrivateMessage());
        this.getCommand("mute").setExecutor(new Mute());
        this.getCommand("discordlink").setExecutor(new DiscordLink());

        //Declare and initiate Listeners
        getServer().getPluginManager().registerEvents(new PlayerInfoLoadedListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuit(), this);
        getServer().getPluginManager().registerEvents(new OnPlayerChat(), this);
        getServer().getPluginManager().registerEvents(new OnBukkitMe(), this);
        getServer().getPluginManager().registerEvents(new OnPlayerRankChange(), this);
        getServer().getPluginManager().registerEvents(new OnPlayerJoin(), this);

        getServer().getPluginManager().registerEvents(new DiscordLinkListener(), this);
        getServer().getPluginManager().registerEvents(new DiscordRestartListener(), this);

        initiateServerCrossLink();

    }

    /**
     * Standard onDisable method to close out the program gracefully. I should
     * really do more things here, but fuck it.
     */
    @Override
    public void onDisable() {
//        //playerRanks.clear();
//        playerTags.clear();
//        playerNicks.clear();
//        playerStars.clear();
        //playerCompiledName.clear();
        if (crosslinkEnabled) CrossLinkMessageHandler.terminate();
        opsOnline.clear();
    }

    /**
     * This method handles loading of the plugin's config and setting of key
     * variables.
     */
    public void loadConfig() {
        //Handle the config generation and loading
        this.saveDefaultConfig();
        config = this.getConfig();
        debug = (Boolean) config.get("debug");
        serverName = JawaCore.getServerName();
        JawaCore.receiveConfigurations(this.getClass().getName(), config);
    }

    /**
     * Returns the Configuration file for the plugin
     * @return
     */
    public static Configuration getConfiguration() {
        return config;
    }
    
    /** Returns the Server's friendly chat name from config.
     * @return 
     */
    public static String getServerName(){
        return serverName;
    }
    
    /** Returns the Server's name identifier from the JawaCore config.
     * @return 
     */
    public static String getIdentityServerName(){
        return JawaCore.getServerName();
    }

    /**
     * Initiates a Discord link. This may change heavily as Discord integration
     * is in active early development.
     *
     * @return
     */
    private static boolean initiateDiscordLink() {
        
        if (getConfiguration().getConfigurationSection("discord-configuration").getBoolean("discord-link", false)) {
            if (getConfiguration().getConfigurationSection("discord-configuration").contains("discord-token")) {
                foxelBot = new DiscordBot("FoxelBot", getConfiguration().getConfigurationSection("discord-configuration").getString("discord-token"));
                foxelBot.initiateListener();
                return true;
            } else {
                Logger.getLogger("DiscordLink initialization").warning("In order to use the discord link you must include the discord token for your bot with the configuration key \'discord-token\'");
                return false;
            }
        } else {
            Logger.getLogger("DiscordLink initialization").info("DiscordLink is not configured for this server.");
            return false;
        }
    }

    /**
     * Return the discord bot. This will need to be changed later to support
     * more bot integration.
     *
     * @return
     */
    public static DiscordBot getFoxelBot() {
        return foxelBot;
    }

    /** Initialize the CrossLink if configured to do so. This will load all CrossLink configs
     * and initialize Controller/Node threads.
     */
    public static void initiateServerCrossLink() {
        if (config.getConfigurationSection("crosslink").getBoolean("enable-crosslink", false)) {
            crosslinkEnabled = true;
            serverRole = config.getConfigurationSection("crosslink").getString("server-role", "controller");
            serverPort = config.getConfigurationSection("crosslink").getInt("server-port", 4445);
            serverHost = config.getConfigurationSection("crosslink").getString("server-host", "localhost");

            if (config.getConfigurationSection("crosslink").contains("server-uuid")) {
                serverUUID = UUID.fromString(config.getConfigurationSection("crosslink").getString("server-uuid", UUID.randomUUID().toString()));
            } else {
                serverUUID = UUID.randomUUID();
                config.getConfigurationSection("crosslink").set("server-uuid", serverUUID.toString());
                plugin.saveConfig();
            }
            //System.out.println(serverRole + ":" + serverPort + ":" + serverHost + ":" + serverUUID);
            CrossLinkMessageHandler.initializeCrossLinkMessageHandler(serverRole, serverUUID, serverHost, serverPort);

            //CrossLinkServer
        } else {
            crosslinkEnabled = false;
        }
    }
    
    /** Returns true is crosslink is enabled.
     * @return 
     */
    public static boolean isCrossLinkEnabled(){
        return crosslinkEnabled;
    }
    
    /** Get the crosslink server role.
     * @return 
     */
    public static String getCrossLinkRole(){
        return serverRole;
    }
    
    /** Get the crosslink UUID
     * @return 
     */
    public static UUID getCrossLinkUUID(){
        return serverUUID;
    }
    
    /** Get the crosslink host.
     * @return 
     */
    public static String getCrossLinkHost(){
        return serverHost;
    }
    
    /** Get the crosslink port
     * @return 
     */
    public static int getCrossLinkPort(){
        return serverPort;
    }
    
    /** Is chat logging enabled in JawaCore. This is backed by JawaCore.isChatLoggingEnabled()
     * JawaCore handles all chat logging.
     * @return true if chat logging is enabled in JawaCore. false if it is not enabled.
     */
    public static boolean isChatLoggingEnabled(){
        return JawaCore.isChatLoggingEnabled();
    }
}
