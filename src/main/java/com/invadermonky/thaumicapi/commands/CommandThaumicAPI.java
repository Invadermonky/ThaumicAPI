package com.invadermonky.thaumicapi.commands;

import com.google.common.collect.Lists;
import com.invadermonky.thaumicapi.api.command.ISubCommand;
import com.invadermonky.thaumicapi.commands.subcommands.SubCommandWarpEvent;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandThaumicAPI extends CommandBase {
    private final Map<String, ISubCommand> subCommands = new HashMap<>();

    public CommandThaumicAPI() {
        this.addSubCommand(new SubCommandWarpEvent());
    }

    public void addSubCommand(ISubCommand subCommand) {
        subCommands.put(subCommand.getSubCommand(), subCommand);
    }

    @Override
    public @NotNull String getName() {
        return "thaumicapi";
    }

    @Override
    public @NotNull List<String> getAliases() {
        return Collections.singletonList("tapi");
    }

    @Override
    public @NotNull String getUsage(@NotNull ICommandSender sender) {
        StringBuilder usage = new StringBuilder("/tapi");
        for(String subCommand : subCommands.keySet()) {
            usage.append(" ").append(subCommand);
        }
        return usage.toString();
    }

    @Override
    public @NotNull List<String> getTabCompletions(@NotNull MinecraftServer server, @NotNull ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if(args.length == 1) {
            return Lists.newArrayList(subCommands.keySet());
        } else if(subCommands.containsKey(args[0])) {
            return subCommands.get(args[0]).getTabCompletions(server, sender, args, targetPos);
        }
        return Collections.emptyList();
    }

    @Override
    public void execute(@NotNull MinecraftServer server, @NotNull ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 1) {
            throw new CommandException(new TextComponentTranslation("command.thaumicapi:invalid").getFormattedText());
        }else if(args.length >= 1) {
            if(!subCommands.containsKey(args[0])) {
                throw new CommandException(new TextComponentTranslation("command.thaumicapi:invalid").getFormattedText());
            } else {
                ISubCommand subCommand = subCommands.get(args[0]);
                if(args.length < subCommand.getMinCommandLength() || args.length > subCommand.getMaxCommandLength()) {
                    throw new CommandException(new TextComponentTranslation("command.thaumicapi:" + subCommand.getSubCommand() + ".invalid").getFormattedText());
                } else {
                    subCommand.execute(server, sender, args);
                }
            }
        }
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        if(args.length > 1 && subCommands.containsKey(args[0])) {
            return subCommands.get(args[0]).isUsernameIndex(args, index);
        }
        return super.isUsernameIndex(args, index);
    }
}
