package io.github.lokka30.papichatformatter;

import de.leonhard.storage.LightningBuilder;
import de.leonhard.storage.internal.FlatFile;
import io.github.lokka30.papichatformatter.commands.PCFCommand;
import io.github.lokka30.papichatformatter.listeners.ChatListener;
import io.github.lokka30.papichatformatter.utils.FileCache;
import io.github.lokka30.papichatformatter.utils.Utils;
import io.github.lokka30.phantomlib.PhantomLib;
import io.github.lokka30.phantomlib.classes.CommandRegister;
import io.github.lokka30.phantomlib.classes.PhantomLogger;
import io.github.lokka30.phantomlib.classes.UpdateChecker;
import io.github.lokka30.phantomlib.enums.LogLevel;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class PAPIChatFormatter extends JavaPlugin {

    final String logPrefix = "&b&lPAPIChatFormatter: &7";
    public PhantomLib phantomLib;
    public PhantomLogger phantomLogger;
    public ChatListener chatListener;
    public FileCache fileCache;
    public Utils utils;
    public FlatFile settings;
    public FlatFile messages;
    public PluginManager pluginManager;

    @Override
    public void onLoad() {
        chatListener = new ChatListener(this);
        fileCache = new FileCache(this);
        utils = new Utils();
        pluginManager = getServer().getPluginManager();
    }

    @Override
    public void onEnable() {
        if (isPhantomLibInstalled()) {
            phantomLib = PhantomLib.getInstance();
            phantomLogger = phantomLib.getPhantomLogger();
        } else {
            //PhantomLib is not installed, disable the plugin and don't do anything at all in onEnable.
            getLogger().severe("--!-- WARNING --!--");
            getLogger().severe(" ");
            getLogger().severe("PhantomLib is not installed! PhantomLib is a required dependency of PAPIChatFormatter. Loading cancelled.");
            getLogger().severe(" ");
            getLogger().severe("--!-- WARNING --!--");
            pluginManager.disablePlugin(this);
            return;
        }

        phantomLogger.log(LogLevel.INFO, logPrefix, "&8(&3Startup&8) &8+----+ &f(Enable Started) &8+----+");
        long startTime = System.currentTimeMillis();
        if (checkCompatibility()) {

            loadFiles();
            registerEvents();
            registerCommands();
            registerMetrics();

            long timeTaken = System.currentTimeMillis() - startTime;
            phantomLogger.log(LogLevel.INFO, logPrefix, "&8(&3Startup&8) &8+----+ &f(Enable Complete, took " + timeTaken + "ms) &8+----+");

            checkForUpdates();
        } else {
            //Severe incompatibility/incompatibilities found, disable the plugin and don't load anything.
            pluginManager.disablePlugin(this);
            //return; --- unnecessary as the last statement in a void method
        }
    }

    private boolean isPhantomLibInstalled() {
        return pluginManager.getPlugin("PhantomLib") != null;
    }

    private boolean checkCompatibility() {
        phantomLogger.log(LogLevel.INFO, logPrefix, "&8(&3Startup&8 - &31&8/&35&8) &7Checking compatibility");

        //Check server version.
        final String currentServerVersion = getServer().getVersion();
        boolean isSupported = false;
        for (String supportedVersion : utils.getSupportedServerVersions()) {
            if (currentServerVersion.contains(supportedVersion)) {
                isSupported = true;
                break;
            }
        }
        if (isSupported) {
            phantomLogger.log(LogLevel.INFO, logPrefix, "&8(&3Startup&8 - &31&8/&35&8) &7Detected server version as '&b" + currentServerVersion + "&7' (supported).");
        } else {
            phantomLogger.log(LogLevel.INFO, logPrefix, "&8(&3Startup&8 - &31&8/&35&8) &7Detected server version as '&b" + currentServerVersion + "&7'. Your current version of the plugin does not support your server's version, you will not receive support for any issues you encounter.");
        }

        //Check if PAPI is installed
        if (pluginManager.getPlugin("PlaceholderAPI") == null) {
            phantomLogger.log(LogLevel.SEVERE, logPrefix, "&8(&3Startup&8 - &31&8/&35&8) &7Plugin dependency '&bPlaceholderAPI&7' is not installed, loading cancelled.");
            return false;
        }

        return true;
    }

    private void loadFiles() {
        phantomLogger.log(LogLevel.INFO, logPrefix, "&8(&3Startup&8 - &32&8/&35&8) &7Loading files");

        //Load files.
        settings = LightningBuilder
                .fromFile(new File(getDataFolder() + File.separator + "settings"))
                .addInputStreamFromResource("settings.yml")
                .createYaml();
        messages = LightningBuilder
                .fromFile(new File(getDataFolder() + File.separator + "messages"))
                .addInputStreamFromResource("messages.yml")
                .createYaml();

        //Check if they exist
        final File settingsFile = new File(getDataFolder() + File.separator + "settings.yml");
        final File messagesFile = new File(getDataFolder() + File.separator + "messages.yml");

        if (!(settingsFile.exists() && !settingsFile.isDirectory())) {
            phantomLogger.log(LogLevel.INFO, logPrefix, "&8(&3Startup&8 - &32&8.&35&8) &7File '&bsettings.yml&7' doesn't exist. Creating it now.");
            saveResource("settings.yml", false);
        }

        if (!(messagesFile.exists() && !messagesFile.isDirectory())) {
            phantomLogger.log(LogLevel.INFO, logPrefix, "&8(&3Startup&8 - &32&8.&35&8) &7File '&bmessages.yml&7' doesn't exist. Creating it now.");
            saveResource("messages.yml", false);
        }

        //Check their versions
        if (settings.get("file-version", 0) != utils.getLatestSettingsFileVersion()) {
            phantomLogger.log(LogLevel.SEVERE, logPrefix, "&8(&3Startup&8 - &32&8.&35&8) &7File &bsettings.yml&7 is out of date! Errors are likely to occur! Reset it or merge the old values to the new file.");
        }

        if (messages.get("file-version", 0) != utils.getLatestMessagesFileVersion()) {
            phantomLogger.log(LogLevel.SEVERE, logPrefix, "&8(&3Startup&8 - &32&8.&35&8) &7File &bmessages.yml&7 is out of date! Errors are likely to occur! Reset it or merge the old values to the new file.");
        }

        //Cache values.
        fileCache.cacheLatest();
    }

    private void registerEvents() {
        phantomLogger.log(LogLevel.INFO, logPrefix, "&8(&3Startup&8 - &33&8/&35&8) &7Registering events");
        getServer().getPluginManager().registerEvents(chatListener, this);
    }

    private void registerCommands() {
        phantomLogger.log(LogLevel.INFO, logPrefix, "&8(&3Startup&8 - &34&8/&35&8) &7Registering commands");
        CommandRegister commandRegister = phantomLib.getCommandRegister();
        commandRegister.registerCommand(this, "papichatformatter", new PCFCommand(this));
    }

    private void registerMetrics() {
        phantomLogger.log(LogLevel.INFO, logPrefix, "&8(&3Startup&8 - &35&8/&35&8) &7Registering metrics");
        new Metrics(this, 7469);
    }

    private void checkForUpdates() {
        if (fileCache.SETTINGS_USE_UPDATE_CHECKER) {
            phantomLogger.log(LogLevel.INFO, logPrefix, "&8(&3Update Checker&8) &7Checking for updates...");
            new UpdateChecker(this, 12345).getVersion(version -> { //TODO CHANGE
                final String currentVersion = getDescription().getVersion();

                if (currentVersion.equals(version)) {
                    phantomLogger.log(LogLevel.INFO, logPrefix, "&8(&3Update Checker&8) &7You're running the latest version '&b" + currentVersion + "&7'.");
                } else {
                    phantomLogger.log(LogLevel.WARNING, logPrefix, "&8(&3Update Checker&8) &7There's a new update available: '&b" + version + "&7'. You're running '&b" + currentVersion + "&7'.");
                }
            });
        }
    }

    @Override
    public void onDisable() {
        phantomLogger.log(LogLevel.INFO, logPrefix, "&8(&3Shutdown&8) &8+---+ &f(Disable Started) &8+---+");
        final long startTime = System.currentTimeMillis();

        //If anything needs to be done here then do it.

        final long totalTime = System.currentTimeMillis() - startTime;
        phantomLogger.log(LogLevel.INFO, logPrefix, "&8(&3Shutdown&8) &8+---+ &f(Disable Complete, took " + totalTime + "ms) &8+---+");
    }
}
