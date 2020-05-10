package io.github.lokka30.papichatformatter.listeners;

import io.github.lokka30.papichatformatter.PAPIChatFormatter;
import io.github.lokka30.phantomlib.classes.MessageMethods;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private PAPIChatFormatter instance;
    public ChatListener(PAPIChatFormatter instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onChat(final AsyncPlayerChatEvent event) {
        if(!event.isCancelled()) {
            final Player player = event.getPlayer();
            final MessageMethods messageMethods = instance.phantomLib.getMessageMethods();

            String format = instance.fileCache.SETTINGS_FORMAT;
            format = PlaceholderAPI.setPlaceholders(event.getPlayer(), format);
            format = messageMethods.colorize(format);

            if(player.hasPermission("papichatformatter.coloredmessages")) {
                format = format.replaceAll("%message%", messageMethods.colorize(event.getMessage()));
            } else {
                format = format.replaceAll("%message%", event.getMessage());
            }


            event.setFormat(format);
        }
    }
}
