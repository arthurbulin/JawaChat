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
import java.util.Map;
import java.util.UUID;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import jawamaster.jawachat.commands.Mute;
import jawamaster.jawachat.commands.PrivateMessage;
import jawamaster.jawachat.commands.SetNick;
import jawamaster.jawachat.commands.SetStar;
import jawamaster.jawachat.commands.SetTag;
import jawamaster.jawachat.listeners.OnBukkitMe;
import jawamaster.jawachat.listeners.OnPlayerRankChange;
import jawamaster.jawachat.listeners.OnPlayerChat;
import jawamaster.jawachat.listeners.OnPlayerJoin;
import jawamaster.jawachat.listeners.PlayerInfoLoadedListener;
import jawamaster.jawachat.listeners.PlayerQuit;
import net.jawasystems.jawacore.handlers.ESHandler;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author Arthur Bulin
 */
public class JawaChat extends JavaPlugin {
//    //Plugin and handler declaration
    public static JawaChat plugin;
    public static ESHandler eshandler;
//    //Variable declarations
    public static Configuration config;
    public static boolean debug;
    public static String ServerName;
    
    //HashMaps for player controls
    public static Map<UUID, String> playerTags;
    public static Map<UUID, String> playerNicks;
    public static Map<UUID, String> playerStars;
    public static Map<UUID, String> playerCompiledName;
    public static Map<String, String> rankColorMap;
    
    public static Map<UUID, Player> opsOnline;
    
    public static String pluginSlug = "[JawaChat] ";
    
    private static JawaJanitor jawaJanitor;
    
    private static final Logger CHATLOGGER = Logger.getLogger("ServerChat");
    private static volatile FileHandler fileHandler;
    
    
    public static JawaChat getPlugin(){
        return plugin;
    }

    @Override
    public void onEnable(){
        try {
            loadConfig();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JawaChat.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //intiateChatLogging();
        
        plugin = this;
        jawaJanitor = new JawaJanitor(plugin);
        //Initiate maps
        playerTags = new HashMap();
        playerNicks = new HashMap();
        playerStars = new HashMap();
        playerCompiledName = new HashMap();
        opsOnline = new HashMap();
        
        //Attempt BungeeIntegration
        //this.getServer().getMessenger().registerOutgoingPluginChannel(this, "JawaChat");
        //this.getServer().getMessenger().registerIncomingPluginChannel(this, "JawaChat", new OnPluginMessage());
        
        //Declear and intiate Commands
        this.getCommand("setnick").setExecutor(new SetNick());
        this.getCommand("settag").setExecutor(new SetTag());
        this.getCommand("setstar").setExecutor(new SetStar());
        this.getCommand("pm").setExecutor(new PrivateMessage());
        this.getCommand("mute").setExecutor(new Mute());
        
        //Declare and initiate Listeners
        getServer().getPluginManager().registerEvents(new PlayerInfoLoadedListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuit(), this);
        getServer().getPluginManager().registerEvents(new OnPlayerChat(), this);
        getServer().getPluginManager().registerEvents(new OnBukkitMe(), this);
//        getServer().getPluginManager().registerEvents(new OnOpChat(), this);
        getServer().getPluginManager().registerEvents(new OnPlayerRankChange(), this);
        getServer().getPluginManager().registerEvents(new OnPlayerJoin(), this);
        
        //Bukkit.getServer().getP

    }
    
    @Override
    public void onDisable(){
        //playerRanks.clear();
        playerTags.clear();
        playerNicks.clear();
        playerStars.clear();
        //playerCompiledName.clear();
        opsOnline.clear();
    }
    
    public void loadConfig() throws FileNotFoundException{
        //Handle the config generation and loading
        this.saveDefaultConfig();
        config = this.getConfig();
        debug = (Boolean) config.get("debug");
        //eshost = (String) config.get("eshost");
        //esport = (int) config.get("esport");
        ServerName = (String) config.get("servername");
        
        final File rankColors =  new File(this.getDataFolder() + "/rankcolors.yml");
        Yaml yaml = new Yaml();
        
        BufferedReader reader = new BufferedReader(new FileReader(rankColors));
        rankColorMap = (Map<String,String>) yaml.load(reader);
        //System.out.println(rankColorMap);
    }
    
    public static Configuration getConfiguration(){
        return config;
    }
    
//    private void intiateChatLogging(){
//        FileHandler fh = fileHandler;
//        if (fh != null){
//            CHATLOGGER.removeHandler(fh);
//            fh.close();
//        }
//        
//        fileHandler = rotate(this.getDataFolder() + "/aioinfo%g.log", 1048576, 100, true);
//    }
//    
//    private static FileHandler rotate(String pattern, int limit, int count, boolean append){
//        try {
//            LogManager m = LogManager.getLogManager();
//            String p = FileHandler.class.getName();
//            pattern = m.getProperty(p + ".pattern");
//            if (pattern == null) {
//                pattern = "%h/java%u.log";
//            }
//            new FileHandler(pattern, 0, count, false).close();
//            return new FileHandler(pattern, limit, count, append);
//        } catch (IOException ex) {
//            Logger.getLogger(JawaChat.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (SecurityException ex) {
//            Logger.getLogger(JawaChat.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }
//    
//    public static Logger getChatLogger(){
//        return CHATLOGGER;
//    }
}
