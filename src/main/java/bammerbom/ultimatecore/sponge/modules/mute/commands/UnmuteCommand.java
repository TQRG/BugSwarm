/*
 * This file is part of UltimateCore, licensed under the MIT License (MIT).
 *
 * Copyright (c) Bammerbom
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package bammerbom.ultimatecore.sponge.modules.mute.commands;

import bammerbom.ultimatecore.sponge.UltimateCore;
import bammerbom.ultimatecore.sponge.api.command.Command;
import bammerbom.ultimatecore.sponge.api.module.Module;
import bammerbom.ultimatecore.sponge.api.module.Modules;
import bammerbom.ultimatecore.sponge.api.permission.Permission;
import bammerbom.ultimatecore.sponge.api.user.UltimateUser;
import bammerbom.ultimatecore.sponge.modules.mute.api.MuteKeys;
import bammerbom.ultimatecore.sponge.modules.mute.api.MutePermissions;
import bammerbom.ultimatecore.sponge.utils.Messages;
import bammerbom.ultimatecore.sponge.utils.Selector;
import bammerbom.ultimatecore.sponge.utils.VariableUtil;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UnmuteCommand implements Command {
    @Override
    public Module getModule() {
        return Modules.MUTE.get();
    }

    @Override
    public String getIdentifier() {
        return "mute";
    }

    @Override
    public Permission getPermission() {
        return MutePermissions.UC_UNMUTE;
    }

    @Override
    public List<Permission> getPermissions() {
        return Arrays.asList(MutePermissions.UC_UNMUTE);
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("unmute");
    }

    //mute <Player> [Time] [Reason]
    @Override
    public CommandResult run(CommandSource sender, String[] args) {
        if (!sender.hasPermission(MutePermissions.UC_UNMUTE.get())) {
            sender.sendMessage(Messages.getFormatted("core.nopermissions"));
            return CommandResult.empty();
        }
        if (args.length == 0) {
            sender.sendMessage(getUsage());
            return CommandResult.empty();
        }
        Player t = Selector.one(sender, args[0]).orElse(null);
        if (t == null) {
            sender.sendMessage(Messages.getFormatted("core.playernotfound", "%player%", args[0]));
            return CommandResult.empty();
        }

        UltimateUser ut = UltimateCore.get().getUserService().getUser(t);
        if (!ut.get(MuteKeys.MUTE).isPresent()) {
            sender.sendMessage(Messages.getFormatted("mute.command.unmute.notmuted", "%player%", VariableUtil.getNameEntity(t)));
            return CommandResult.empty();
        }
        ut.offer(MuteKeys.MUTE, null);

        sender.sendMessage(Messages.getFormatted("mute.command.unmute.success", "%player%", VariableUtil.getNameEntity(t)));
        return CommandResult.success();
    }

    @Override
    public List<String> onTabComplete(CommandSource sender, String[] args, String curs, Integer curn) {
        if (curn == 0) return null;
        return new ArrayList<>();
    }
}
