/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package jawamaster.jawachat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import jawamaster.jawachat.commands.PrivateMessage;
import jawamaster.jawachat.commands.SetNick;
import jawamaster.jawachat.commands.SetStar;
import jawamaster.jawachat.commands.SetTag;
import jawamaster.jawachat.handlers.ESDataHandler;
import jawamaster.jawachat.listeners.OnBukkitMe;
import jawamaster.jawachat.listeners.OnOpChat;
import jawamaster.jawachat.listeners.OnPlayerRankChange;
import jawamaster.jawachat.listeners.PlayerChat;
import jawamaster.jawachat.listeners.PlayerJoin;
import jawamaster.jawachat.listeners.PlayerQuit;
import jawamaster.jawachat.listeners.OnPluginMessage;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author Arthur Bulin
 */
public class JawaChat extends JavaPlugin {
//    //Plugin and handler declaration
    public static JawaChat plugin;
    public static ESDataHandler eshandler;
//    //Variable declarations
    public static Configuration config;
    public static boolean debug;
    public static String eshost;
    public static int esport;
    public static RestHighLevelClient restClient;
    public static String ServerName;
    
    //HashMaps for player controls
    public static Map<UUID, String> playerRanks;
    public static Map<UUID, String> playerTags;
    public static Map<UUID, String> playerNicks;
    public static Map<UUID, String> playerStars;
    //public static Map<UUID, String> playerCompiledName;
    public static Map<String, String> rankColorMap;
    public static Set<Player> opsOnline;
    
    public static String pluginSlug = "[JawaChat] ";
    
    public static JawaChat getPlugin(){
        return plugin;
    }
    
//    public static RestHighLevelClient getESClient(){
//        return restClient;
//    }
    
    @Override
    public void onEnable(){
        try {
            loadConfig();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JawaChat.class.getName()).log(Level.SEVERE, null, ex);
        }
        startESHandler();
        //restClient = jawamaster.jawapermissions.JawaPermissions.restClient;
        //eshandler = new ESHandler(this);
        
        
        //Initiate maps
        playerRanks = new HashMap();
        playerTags = new HashMap();
        playerNicks = new HashMap();
        playerStars = new HashMap();
        //playerCompiledName = new HashMap();
        opsOnline = new HashSet();
        //rankColorMap = new HashMap();
        
        //Attempt BungeeIntegration
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "JawaChat");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "JawaChat", new OnPluginMessage());
        
        //Declear and intiate Commands
        this.getCommand("setnick").setExecutor(new SetNick());
        this.getCommand("settag").setExecutor(new SetTag());
        this.getCommand("setstar").setExecutor(new SetStar());
        //this.getCommand("me").setExecutor(new OnMe());
        this.getCommand("pm").setExecutor(new PrivateMessage());
        
        //Declare and initiate Listeners
        getServer().getPluginManager().registerEvents(new OnPlayerRankChange(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuit(), this);
        getServer().getPluginManager().registerEvents(new PlayerChat(), this);
        getServer().getPluginManager().registerEvents(new OnBukkitMe(), this);
        getServer().getPluginManager().registerEvents(new OnOpChat(), this);

    }
    
    @Override
    public void onDisable(){
        playerRanks.clear();
        playerTags.clear();
        playerNicks.clear();
        playerStars.clear();
        //playerCompiledName.clear();
        opsOnline.clear();
    }
    
    public void startESHandler(){
        
        //Initialize the restClient for global use
        restClient = new RestHighLevelClient(RestClient.builder(new HttpHost(eshost, esport, "http")).setRequestConfigCallback((RequestConfig.Builder requestConfigBuilder) -> requestConfigBuilder.setConnectTimeout(5000).setSocketTimeout(60000)).setMaxRetryTimeoutMillis(60000));
        
        //Long annoying debug line for restClient connection
        if (debug){
            System.out.println(pluginSlug + "High Level Rest Client initialized at: " + restClient.toString());
            System.out.println(pluginSlug + "With host: " + eshost);
            System.out.println(pluginSlug + "on port: " + esport);
            boolean restPing = false;
            try {
                restPing = restClient.ping();
            } catch (IOException ex) {
                Logger.getLogger(JawaChat.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println(pluginSlug + "ElasticSearch DB pings as: " + restPing );
        }
        
        eshandler = new ESDataHandler(this);
        
    }
    
    public void loadConfig() throws FileNotFoundException{
        //Handle the config generation and loading
        this.saveDefaultConfig();
        config = this.getConfig();
        debug = (Boolean) config.get("debug");
        eshost = (String) config.get("eshost");
        esport = (int) config.get("esport");
        ServerName = (String) config.get("servername");
        
        final File rankColors =  new File(this.getDataFolder() + "/rankcolors.yml");
        Yaml yaml = new Yaml();
        
        BufferedReader reader = new BufferedReader(new FileReader(rankColors));
        rankColorMap = (Map<String,String>) yaml.load(reader);
        //System.out.println(JawaChat.pluginSlug + "rankColorMap: " + rankColorMap);
    }
}
