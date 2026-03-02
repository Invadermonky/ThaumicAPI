package com.invadermonky.thaumicapi.commands.subcommands;

import com.invadermonky.thaumicapi.api.command.ISubCommand;
import com.invadermonky.thaumicapi.api.warpevent.IWarpEvent;
import com.invadermonky.thaumicapi.utils.helpers.PlayerHelper;
import com.invadermonky.thaumicapi.warpevents.WarpEventHandler;
import com.invadermonky.thaumicapi.warpevents.WarpEventRegistry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SubCommandWarpEvent implements ISubCommand {
    @Override
    public String getSubCommand() {
        return "warpevent";
    }

    @Override
    public int getMinCommandLength() {
        return 3;
    }

    @Override
    public int getMaxCommandLength() {
        return 4;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if(args.length == 2) {
            return new ArrayList<>(CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()));
        } else if(args.length == 3) {
            List<String> eventNames = WarpEventRegistry.WARP_EVENTS.keySet().stream().map(ResourceLocation::toString).collect(Collectors.toList());
            return new ArrayList<>(CommandBase.getListOfStringsMatchingLastWord(args, eventNames));
        } else if(args.length == 4) {
            try {
                EntityPlayer player = CommandBase.getPlayer(server, sender, args[1]);
                return Collections.singletonList(String.valueOf(this.getPlayerTotalWarp(player)));
            } catch (Exception ignored) {}
        }
        return Collections.emptyList();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        // /tapi warpevent @p thaumcraft.spiders_fake
        // /tapi warpevent <player> <warpevent> [playerwarp]
        EntityPlayer player = CommandBase.getPlayer(server, sender, args[1]);
        IWarpEvent warpEvent = WarpEventRegistry.getWarpEvent(args[2]);
        if(warpEvent == null) {
            throw new CommandException(new TextComponentTranslation("command.thaumicapi:warpevent.invalid").getFormattedText());
        }
        int warp = args.length == 4 ? CommandBase.parseInt(args[3]) : this.getPlayerTotalWarp(player);
        WarpEventHandler.performWarpEvent(player, warp, warpEvent);
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 2;
    }

    private int getPlayerTotalWarp(EntityPlayer player) {
        IPlayerWarp playerWarp = ThaumcraftCapabilities.getWarp(player);
        int temp = playerWarp.get(IPlayerWarp.EnumWarpType.TEMPORARY);
        int norm = playerWarp.get(IPlayerWarp.EnumWarpType.NORMAL);
        int perm = playerWarp.get(IPlayerWarp.EnumWarpType.PERMANENT);
        int gearWarp = PlayerHelper.getWarpFromGear(player);
        return temp + norm + perm + gearWarp;
    }
}
