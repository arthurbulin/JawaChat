/*
 * Copyright (C) 2024 Arthur Bulin
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
package jawamaster.jawachat.handlers;

import net.jawasystems.jawacore.dataobjects.PlayerDataObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

/**
 *
 * @author Arthur Bulin
 */
public class MessageHandler {

    /** Return a TextComponent for a URL.
     * @param urlPart
     * @return
     */
    public static TextComponent assembleURLComponent(String urlPart) {
        TextComponent urlComp = Component.text(urlPart)
        .decorate(TextDecoration.BOLD)
        .clickEvent(ClickEvent.openUrl(urlPart))
        .hoverEvent(HoverEvent.showText(Component.text("Open Link")));
        return urlComp;
    }

    /** Create the formatted player predicate for all player sent messages.
     * @param PDO
     * @param predicatePrefix
     * @param dividerString
     * @return
     */
    public static TextComponent createNamePredicate(PlayerDataObject PDO, TextComponent predicatePrefix, TextComponent dividerString) {
        TextComponent predicate = Component.empty()
                .append(predicatePrefix)
                .append(PDO.getFriendlyName())
                .append(dividerString)
                .clickEvent(ClickEvent.suggestCommand("/pm ".concat(PDO.getName())))
                .hoverEvent(HoverEvent.showText(Component.text("/pm ".concat(PDO.getName()))));
        return predicate;
//        //Generate the name portion of the predicate and affix any prefixes i.e. [op]
//        TextComponent form = new TextComponent(predicatePrefix + playerDisplayName);
//        form.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.SUGGEST_COMMAND, "/pm " + playerName + " "));
//        form.setHoverEvent(new net.md_5.bungee.api.chat.HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new Text("/pm " + playerName)));
//        //Generate the divider component. The color specified will also be the base color for the rest of the message
//        TextComponent divider = new TextComponent(dividerString);
//        divider.setColor(dividerColor);
//        //Append all of the parts into a single ComponentBuilder for other additions
//        ComponentBuilder baseComp = new ComponentBuilder().append(form).append(divider).append(" ").reset();
//        return baseComp;
    }
    
    public TextComponent assembleGeneralMessage(TextComponent predicate, TextComponent divider, TextComponent message){
        //<predicate><divider><message>
        return Component.empty()
                .append(predicate)
                .append(divider)
                .append(message);
    }
    
    /** Assembles a private message that goes TO a player
     * 
     * @param PredicateOne
     * @param predicateTwo
     * @param PDO
     * @param messageComponent
     * @return 
     */
    public static TextComponent assemblePrivateMessage(String PredicateOne, String predicateTwo, PlayerDataObject PDO, TextComponent messageComponent) {
        //TODO need to pull in message customization from a config file at some point
        TextComponent messagePredicate = Component.text(PredicateOne, NamedTextColor.DARK_GRAY)
                .append(PDO.getFriendlyName())
                .append(Component.text(predicateTwo, NamedTextColor.DARK_GRAY))
                .append(Component.text(": ", NamedTextColor.WHITE));
        
        TextComponent message = messagePredicate.append(messageComponent);
        return message;
    }

    public static TextComponent prepareMessageComponent(String[] messageText) {
        //TODO this is where we should add dirty word searching
        TextComponent message = Component.empty();
//        String[] messageWords = messageText.split(" ");
        for (String part : messageText) {
            if (part.matches("^.*(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|].*$")) {
                message.append(assembleURLComponent(part)).appendSpace();
            } else {
                message.append(Component.text(part)).appendSpace();
            }
        }
        return message;
    }
}
