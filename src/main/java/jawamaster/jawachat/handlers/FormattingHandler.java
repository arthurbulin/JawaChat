/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
 */
package jawamaster.jawachat.handlers;

import java.util.UUID;
import net.jawasystems.jawacore.PlayerManager;
import net.jawasystems.jawacore.dataobjects.PlayerDataObject;
import net.jawasystems.jawacore.handlers.ESHandler;
import net.jawasystems.jawacore.utils.ESRequestBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.JSONObject;

/**
 *
 * @author Arthur Bulin
 */
public class FormattingHandler {

    public static void updatePlayerName(Player target) {
        PlayerDataObject pdObject = new PlayerDataObject(target.getUniqueId());
        pdObject = ESHandler.runMultiIndexSearch(ESRequestBuilder.buildSingleMultiSearchRequest("players", "_id", target.getUniqueId().toString()), pdObject);
        compilePlayerNameOnJoin(target.getUniqueId(), pdObject);
    }

    public static void recompilePlayerName(UUID uuid){
        recompilePlayerName(Bukkit.getServer().getPlayer(uuid));
    }
    
    /** Recreates the player's display and list names using the map stored player
     * information.
     * @param target 
     */
    public static void recompilePlayerName(Player target){
        compilePlayerName(target.getUniqueId(), PlayerManager.getPlayerDataObject(target));
        compileListName(PlayerManager.getPlayerDataObject(target));
    }
    
    public static void compilePlayerNameOnJoin(UUID target, PlayerDataObject pdObject) {
        compilePlayerName(target, pdObject);
        compileListName(pdObject);
    }
    
    public static void compilePlayerName(UUID target, PlayerDataObject pdObject){
        //Player displayname compilation
        String displayName = "";

        if (!pdObject.getStar().equals("")) displayName += pdObject.getStar();
        if (!pdObject.getTag().equals("")) displayName += ChatColor.translateAlternateColorCodes('&', pdObject.getTag()) + " ";
        
        if (!pdObject.getNickName().equals("")) displayName += ChatColor.translateAlternateColorCodes('&', pdObject.getNickName());
        else displayName += pdObject.getRankColor() + pdObject.getName();
        pdObject.getPlayer().setDisplayName(displayName);
        //pdObject.setCompiledName(displayName);

    }
    
    /** Compiles the player listname from the given information.
     * @param pdObject
     * @param target 
     */
    public static void compileListName(PlayerDataObject pdObject){
        //Player listname compilation
        String listName = "";
        if (pdObject.getNickName().equals("")){
            listName = pdObject.getRankColor() + pdObject.getName();
        } else {
            listName =
                    pdObject.getRankColor() + 
                    pdObject.getFriendlyName() + 
                    ChatColor.WHITE + "> " + 
                    pdObject.getRankColor() + 
                    pdObject.getName();
        }

        pdObject.getPlayer().setPlayerListName(listName);
    }
    
    /** Compile the string containing stars for the user's name.
     * @param starData
     * @return 
     */
    public static String buildStar(JSONObject starData){
        String star = "";
        if (starData.getBoolean("promote")) {
            star += ChatColor.GREEN + "*";
        }
        if (starData.getBoolean("probation")) {
            star += ChatColor.RED + "*";
        }
        if (starData.getBoolean("consult")) {
            star += ChatColor.YELLOW + "*";
        }
        
        return star;
    }
    
}
