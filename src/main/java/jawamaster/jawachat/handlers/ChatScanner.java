/*
 * Copyright (C) 2021 Jawamaster (Arthur Bulin)
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

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import jawamaster.jawachat.JawaChat;
import jawamaster.jawachat.crosslink.CrossLinkMessage;
import net.jawasystems.jawacore.utils.FileHandler;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author Jawamaster (Arthur Bulin)
 */
public class ChatScanner {
    
    private static final Queue<CrossLinkMessage> messageQueue = new LinkedBlockingQueue<>();
    private static final HashMap<String,Thread> SCANNERS = new HashMap();
    
    private static FileConfiguration loadFilterDefinitions(){
        return FileHandler.getYMLFile(JawaChat.plugin.getDataFolder()+"/chat-filters.yml", JawaChat.plugin.getResource("chat-filters.yml"));
    }
    
    private static void createChatScanner() {
        
    }
}
