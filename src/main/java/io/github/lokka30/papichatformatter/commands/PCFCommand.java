package io.github.lokka30.papichatformatter.commands;

import io.github.lokka30.papichatformatter.PAPIChatFormatter;
import io.github.lokka30.phantomlib.classes.MessageMethods;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class PCFCommand implements TabExecutor {

    private PAPIChatFormatter instance;
    private MessageMethods messageMethods;
    public PCFCommand(PAPIChatFormatter instance) {
        this.instance = instance;
        this.messageMethods = instance.phantomLib.getMessageMethods();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0) {
            for(String msg : instance.fileCache.MESSAGES_PLUGIN_INFO) {
                commandSender.sendMessage(messageMethods.colorize(msg));
            }
        } else if(args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if(commandSender.hasPermission("papichatformatter.reload")) {
                commandSender.sendMessage(messageMethods.colorize(instance.fileCache.MESSAGES_RELOAD_STARTED));
                instance.fileCache.cacheLatest();
                commandSender.sendMessage(messageMethods.colorize(instance.fileCache.MESSAGES_RELOAD_COMPLETE));
            } else {
                commandSender.sendMessage(messageMethods.colorize(instance.fileCache.MESSAGES_NO_PERMISSION));
            }
        } else {
            commandSender.sendMessage(messageMethods.colorize(instance.fileCache.MESSAGES_USAGE.replaceAll("%label%", label)));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0) {
            return Collections.singletonList("reload");
        } else {
            return null;
        }
    }
}
