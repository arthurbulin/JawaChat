/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package jawamaster.jawachat.handlers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import jawamaster.jawachat.JawaChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

/**
 *
 * @author Arthur Bulin
 */
public class ESDataHandler {
    public static JawaChat plugin;
    public static RestHighLevelClient restClient;
    public static SearchRequest searchRequest;
    public static SearchSourceBuilder searchSourceBuilder;
    public static SearchHits searchHits;
    public static SearchHit searchHit;
    public static IndexRequest indexRequest;
    public static UpdateRequest updateRequest;
    
    public static String handlerSlug = "[ESHandler] ";

    
    public ESDataHandler(JawaChat plugin) {
        this.plugin = plugin;
        ESDataHandler.restClient = JawaChat.restClient;
    }
    
    public static void setNickName(Player target, CommandSender commandSender, String nickName) throws IOException {
        Map<String, String> nickNameData = new HashMap();

        if(nickName == null) nickNameData.put("nick", "$$");
        else nickNameData.put("nick", ChatColor.translateAlternateColorCodes('&', nickName.trim()));
        
        
        updateRequest = new UpdateRequest("mc", "players", target.getUniqueId().toString()).doc(nickNameData);
        restClient.updateAsync(updateRequest, new ActionListener<UpdateResponse>() {
            @Override
            public void onFailure(Exception e) {
                System.out.println(JawaChat.pluginSlug + handlerSlug + "Please check the ElasticSearch server. Something went wrong with output.");
                e.printStackTrace();
            }

            @Override
            public void onResponse(UpdateResponse response) {
                if (nickName == null) {
                    JawaChat.playerNicks.remove(target.getUniqueId());
                    if (commandSender instanceof Player) ((Player) commandSender).sendMessage(ChatColor.WHITE + target.getName().trim() + "'s nick name has been removed.");
                    else System.out.println(target.getDisplayName() + "'s nick name has been removed.");
                } else {
                    JawaChat.playerNicks.put(target.getUniqueId(), nickName);
                    if (commandSender instanceof Player) ((Player) commandSender).sendMessage(nickName.trim() + ChatColor.WHITE + " has been nicknamed.");
                    else System.out.println(nickName + " has been nicknamed.");
                }
                
                //Call to rebuild the player's pretty name
                FormattingHandler.compilePlayerName(target);
                
            }
        });
    }
    
    public static void setTag(Player target, CommandSender commandSender, String tag){
        Map<String, String> tagData = new HashMap();
        
        //TODO move this little bit into command execution
        if (tag == null) tagData.put("tag", "$$");
        else if (tag.contains("&")) tagData.put("tag", ChatColor.translateAlternateColorCodes('&', tag));
        else tagData.put("tag", tag);
        
        updateRequest = new UpdateRequest("mc", "players", target.getUniqueId().toString()).doc(tagData);
        restClient.updateAsync(updateRequest, new ActionListener<UpdateResponse>() {
            @Override
            public void onResponse(UpdateResponse response) {
                if (tag == null) {
                    JawaChat.playerTags.remove(target.getUniqueId());
                    if (commandSender instanceof Player) ((Player) commandSender).sendMessage(JawaChat.playerNicks.get(target.getUniqueId()) + "'s tag has been removed.");
                    else System.out.println(JawaChat.playerNicks.get(target.getUniqueId()) + "'s tag has been removed.");
                }
                else {
                    JawaChat.playerTags.put(target.getUniqueId(), ChatColor.translateAlternateColorCodes('&', tag).trim());
                    if (commandSender instanceof Player) ((Player) commandSender).sendMessage(JawaChat.playerNicks.get(target.getUniqueId()) + " has been tagged with " + ChatColor.translateAlternateColorCodes('&', tag).trim());
                    else System.out.println(JawaChat.playerNicks.get(target.getUniqueId()) + " has been tagged with " + ChatColor.translateAlternateColorCodes('&', tag).trim());
                }
                
                //Call to rebuild the player's pretty name
                FormattingHandler.compilePlayerName(target);
                
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
        
    }
    
    public static void setStar(Player target, CommandSender commandSender, String star) {
        Map<String, Object> starData = new HashMap();
        
        starData.put("star", star);
        
        updateRequest = new UpdateRequest("mc", "players", target.getUniqueId().toString()).doc(starData);
        
        restClient.updateAsync(updateRequest, new ActionListener<UpdateResponse>() {
            @Override
            public void onResponse(UpdateResponse response) {
                
                if (!"$$".equals(star)) {
                    JawaChat.playerStars.put(target.getUniqueId(), star);
                    if (commandSender instanceof Player) ((Player) commandSender).sendMessage(target.getDisplayName() + " starred as: " + star);
                    else System.out.println(target.getDisplayName() + " starred as: " + star);
                }
                else {
                    JawaChat.playerStars.remove(target.getUniqueId());
                    if (commandSender instanceof Player) ((Player) commandSender).sendMessage(target.getDisplayName() + " has been unstarred.");
                    else System.out.println(target.getDisplayName() + " has been unstarred.");
                }
                
                FormattingHandler.compilePlayerName(target);

            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
        
    }
    
    public static void compilePlayerNameJoin(Player target) {
        System.out.println("Compile player name called.");
        searchRequest = new SearchRequest("mc");
        searchSourceBuilder = new SearchSourceBuilder();
        
        searchSourceBuilder.query(QueryBuilders.matchQuery("_id", target.getUniqueId().toString()));
        searchRequest.source(searchSourceBuilder);
        
        restClient.searchAsync(searchRequest, new ActionListener<SearchResponse>() {
            @Override
            public void onResponse(SearchResponse searchResponse) {
                searchHits = searchResponse.getHits();
                SearchHit[] hits = searchHits.getHits();
                long totalHits = searchHits.totalHits;
                
                if (totalHits == 0){ //If not found in Index
                    JawaChat.playerRanks.put(target.getUniqueId(), "guest");
                    FormattingHandler.compilePlayerName(target);
                }else {
                    Map<String, Object> playerData = hits[0].getSourceAsMap();

                    //System.out.println("player join playerData: " + playerData);
                    
                    //Get the response data
                    String nick = (String) playerData.get("nick");
                    String tag = (String) playerData.get("tag");
                    String star = (String) playerData.get("star");
                    
                    //Once the Data is retrieved check if it exists and put it in the respective maps
                    if (!"$$".equals(nick)) JawaChat.playerNicks.put(target.getUniqueId(), nick );
                    if (!"$$".equals(tag)) JawaChat.playerTags.put(target.getUniqueId(), tag);
                    if (!"$$".equals(star)) JawaChat.playerStars.put(target.getUniqueId(), star);
                    
                    //there will always be a rank, if not then something is wrong
                    JawaChat.playerRanks.put(target.getUniqueId(), (String) playerData.get("rank"));
                    
                    
                    //Call to compile the player's pretty name
                    FormattingHandler.compilePlayerName(target);
                    
                }
            }
            
            @Override
            public void onFailure(Exception e) {
                System.out.println(JawaChat.pluginSlug + handlerSlug + "Please check the ElasticSearch server. Something went wrong with output.");
                e.printStackTrace();
            }
            
        });
    }
    

}
