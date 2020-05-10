package io.github.lokka30.papichatformatter.utils;

import io.github.lokka30.papichatformatter.PAPIChatFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileCache {

    private PAPIChatFormatter instance;
    public FileCache(PAPIChatFormatter instance) {
        this.instance = instance;
    }

    public String SETTINGS_FORMAT;
    public boolean SETTINGS_USE_UPDATE_CHECKER;
    public String MESSAGES_PREFIX;
    public List<String> MESSAGES_PLUGIN_INFO = new ArrayList<>();
    public String MESSAGES_RELOAD_STARTED;
    public String MESSAGES_RELOAD_COMPLETE;
    public String MESSAGES_NO_PERMISSION;
    public String MESSAGES_USAGE;

    public void cacheLatest() {
        SETTINGS_FORMAT = instance.settings.get("format", "&c&n(Invalid PAPIChatFormatter Format Setting)&r %vault_prefix% %player_name%&8:&7 %message%");
        SETTINGS_USE_UPDATE_CHECKER = instance.settings.get("use-update-checker", true);
        MESSAGES_PREFIX = instance.messages.get("prefix", "PCF");

        MESSAGES_PLUGIN_INFO.clear();
        for(String line : instance.messages.get("plugin-info", Collections.singletonList("Server is running PAPIChatFormatter."))) {
            MESSAGES_PLUGIN_INFO.add(line
                    .replaceAll("%prefix%", MESSAGES_PREFIX)
                    .replaceAll("%version%", instance.getDescription().getVersion()));
        }

        MESSAGES_RELOAD_STARTED = instance.messages.get("reload-started", "reload started")
            .replaceAll("%prefix%", MESSAGES_PREFIX);
        MESSAGES_RELOAD_COMPLETE = instance.messages.get("reload-complete", "reload complete")
                .replaceAll("%prefix%", MESSAGES_PREFIX);
        MESSAGES_NO_PERMISSION = instance.messages.get("no-permission", "no permission")
                .replaceAll("%prefix%", MESSAGES_PREFIX);
        MESSAGES_USAGE = instance.messages.get("usage", "/pcf [reload]")
                .replaceAll("%prefix%", MESSAGES_PREFIX);
    }
}
