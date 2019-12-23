/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package jawamaster.jawachat.handlers;

import jawamaster.jawachat.JawaChat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author Arthur Bulin
 */
public class FormattingHandler {
    
    public static void compilePlayerName(Player target){
        String playerName = "", tag, nickname, star;
        
        //For stars
        if (JawaChat.playerStars.containsKey(target.getUniqueId())) {
            star = JawaChat.playerStars.get(target.getUniqueId());
            playerName = playerName.concat(star + " ");
        }
        
        //For tags
        if (JawaChat.playerTags.containsKey(target.getUniqueId())) {
            tag = JawaChat.playerTags.get(target.getUniqueId());
            playerName = playerName.concat(tag + " ");
        }
        
        //For Nicknames
        if (JawaChat.playerNicks.containsKey(target.getUniqueId())) {
            nickname = JawaChat.playerNicks.get(target.getUniqueId());
            playerName = playerName.concat(nickname + ChatColor.WHITE);
        } else {
            String color = String.valueOf(JawaChat.rankColorMap.get(JawaChat.playerRanks.get(target.getUniqueId())));
            playerName = playerName + ChatColor.getByChar(color) + target.getDisplayName() + ChatColor.WHITE;
        }
        
        //JawaChat.playerCompiledName.put(target.getUniqueId(), playerName.trim());
        target.setDisplayName(playerName.trim());
        String playerRank = JawaChat.playerRanks.get(target.getUniqueId());
        ChatColor rankColor = ChatColor.getByChar(String.valueOf(JawaChat.rankColorMap.get(playerRank)));
        String playerNick = JawaChat.playerNicks.get(target.getUniqueId());
        if (playerNick == null) {
            playerNick = "None";
        }
        
        try{
            target.setPlayerListName( rankColor + "["+ playerRank.trim() + "] " + target.getName().trim() + ChatColor.DARK_GREEN + " > " + playerNick.trim());
        } catch (Exception e) {
            System.out.println("Exception grabbed from line 49 of format handler.");
            System.out.println("PlayerRank: " + playerRank + " rankColor: " + rankColor + " player nick: " + playerNick);
        }
    }
}