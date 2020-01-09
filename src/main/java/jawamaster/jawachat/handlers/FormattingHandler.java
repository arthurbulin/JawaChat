/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
 */
package jawamaster.jawachat.handlers;

import jawamaster.jawachat.JawaChat;
import jawamaster.jawapermissions.JawaPermissions;
import jawamaster.jawapermissions.PlayerDataObject;
import jawamaster.jawapermissions.handlers.ESHandler;
import jawamaster.jawapermissions.utils.ESRequestBuilder;
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

        compilePlayerNameOnJoin(target, pdObject);
    }

    /** Recreates the player's display and list names using the map stored player
     * information.
     * @param target 
     */
    public static void recompilePlayerName(Player target){
        String star = "";
        String tag = "";
        String nick = "";
        
        if (JawaChat.playerStars.containsKey(target.getUniqueId())) star = JawaChat.playerStars.get(target.getUniqueId());
        if (JawaChat.playerTags.containsKey(target.getUniqueId())) tag = JawaChat.playerTags.get(target.getUniqueId());
        if (JawaChat.playerNicks.containsKey(target.getUniqueId())) nick = JawaChat.playerNicks.get(target.getUniqueId());
        
        compilePlayerName(target, star, tag, nick);
        compileListName(target);
    }
    
    public static void compilePlayerNameOnJoin(Player target, PlayerDataObject pdObject) {
        //Store player data
        //pdObject.spillData();
        storePlayerNameData(target, pdObject);
        compilePlayerName(target, pdObject);
        compileListName(target);
    }
    
    /** Commit all values needed to build a player's in-game name to maps. This is
     * backed by storePlayerNameData(Player target, JSONObject starData, String tag, String nickName)
     * @param target
     * @param pdObject
     */
    public static void storePlayerNameData(Player target, PlayerDataObject pdObject){
        storePlayerNameData(target, pdObject.getStar(), pdObject.getTag(), pdObject.getNickName());
    }
    
    /** Commit all values needed to build a player's in-game name to maps, or if
     * they are "" remove them from the maps.
     * @param target
     * @param star
     * @param tag
     * @param nickName 
     */
    public static void storePlayerNameData(Player target, String star, String tag, String nickName) {
        //If != to "" put it in there, else if it == "" remove it if it exists
        if (!star.equals("")) JawaChat.playerStars.put(target.getUniqueId(), star);
        else if (star.equals("") && JawaChat.playerStars.containsKey(target.getUniqueId())) JawaChat.playerStars.remove(target.getUniqueId());
        
        if (!tag.equals("")) JawaChat.playerTags.put(target.getUniqueId(), tag);
        else if (tag.equals("") && JawaChat.playerTags.containsKey(target.getUniqueId())) JawaChat.playerTags.remove(target.getUniqueId());
        
        if (!nickName.equals("")) JawaChat.playerNicks.put(target.getUniqueId(), nickName);
        else if (nickName.equals("") && JawaChat.playerNicks.containsKey(target.getUniqueId())) JawaChat.playerNicks.remove(target.getUniqueId());
    }
    
    public static void compilePlayerName(Player target, PlayerDataObject pdObject){
        compilePlayerName(target, pdObject.getStar(), pdObject.getTag(), pdObject.getNickName());
    }
    
    /** Compile a player's display name and commit it to store
     * @param target
     * @param star
     * @param tag
     * @param nickName 
     */
    public static void compilePlayerName(Player target, String star, String tag, String nickName){
        //Player displayname compilation
        String displayName = "";
        
        System.out.println("Star: "+ star + "tag: " + tag + "nick: "+nickName);

        if (!star.equals("")) displayName += star;
        if (!tag.equals("")) displayName += ChatColor.translateAlternateColorCodes('$', tag) + " ";
        
        if (!nickName.equals("")) displayName += ChatColor.translateAlternateColorCodes('$', nickName);
        else displayName += ChatColor.getByChar(String.valueOf(JawaChat.rankColorMap.get(JawaPermissions.playerRank.get(target.getUniqueId()))).charAt(0)) + target.getName();
//        else displayName += ChatColor.getByChar(JawaChat.rankColorMap.get(JawaChat.playerRanks.get(target.getUniqueId()))) + target.getName();
        
        target.setDisplayName(displayName);
        
        JawaChat.playerCompiledName.put(target.getUniqueId(),displayName);
    }
    
    /** Compiles the player listname from the given information.
     * @param target 
     */
    public static void compileListName(Player target){
        //Player listname compilation
        String listName = "";
        if (!JawaChat.playerNicks.containsKey(target.getUniqueId())){
            listName = ChatColor.getByChar(
                    String.valueOf(JawaChat.rankColorMap.get(JawaPermissions.playerRank.get(target.getUniqueId())))) + target.getName();
        } else {
            listName = ChatColor.getByChar(
                    String.valueOf(JawaChat.rankColorMap.get(JawaPermissions.playerRank.get(target.getUniqueId())))) + 
                    ChatColor.translateAlternateColorCodes('$', JawaChat.playerNicks.get(target.getUniqueId())) + 
                    ChatColor.WHITE + "> " + 
                    ChatColor.getByChar(String.valueOf(JawaChat.rankColorMap.get(JawaPermissions.playerRank.get(target.getUniqueId())))) + 
                    target.getName();
        }

        target.setPlayerListName(listName);
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
