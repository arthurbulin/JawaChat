/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
 */
package jawamaster.jawachat.handlers;

import java.util.HashMap;
import java.util.UUID;
import net.jawasystems.jawacore.PlayerManager;
import net.jawasystems.jawacore.dataobjects.PlayerDataObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author Arthur Bulin
 */
public class FormattingHandler {
    
    private static final HashMap<UUID, Integer> DISPLAYNAMEHASHES = new HashMap();
    private static final HashMap<UUID, Integer> LISTNAMEHASHES = new HashMap();
    
    public static void clearHashs(UUID target){
        DISPLAYNAMEHASHES.remove(target);
        LISTNAMEHASHES.remove(target);
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
    
    /** creates a player's list and chat formatted names on user join. This is logically the same as 
     * {@link #recompilePlayerName(org.bukkit.entity.Player) recompilePlayerName} but leverages faster lookups using
     * already cached player data objects.
     * @param target
     * @param pdObject 
     */
    public static void compilePlayerNameOnJoin(UUID target, PlayerDataObject pdObject) {
        compilePlayerName(target, pdObject);
        compileListName(pdObject);
    }
    
    /** Compiles the player's display name and sets it. This also creates a hash of the name so that 
     * changes to names can be tracked and used to trigger an update.
     * @param target UUID of the player who's display name is being compiled
     * @param pdObject the player's data object
     */
    public static void compilePlayerName(UUID target, PlayerDataObject pdObject){
        //Player displayname compilation
        TextComponent displayName = Component.empty();

        displayName.append(pdObject.getStarComponent());
        displayName.append(pdObject.getTagComponent());
        displayName.append(pdObject.getNickNameComponent());

        DISPLAYNAMEHASHES.put(target, displayName.hashCode());
        pdObject.getPlayer().displayName(displayName);
    }
    
    /** Compiles the player listname from the given information.
     * @param pdObject 
     */
    public static void compileListName(PlayerDataObject pdObject){
        TextComponent listName = Component.empty();

        if (pdObject.hasNickName()) {
            listName.append(pdObject.getFriendlyName());
            listName.append(Component.text(" > ", NamedTextColor.WHITE));
            listName.append(pdObject.getColoredName());
        } else {
            listName.append(pdObject.getColoredName());
        }
        LISTNAMEHASHES.put(pdObject.getUniqueID(), listName.hashCode());
        pdObject.getPlayer().playerListName(listName);
    }
    

}
