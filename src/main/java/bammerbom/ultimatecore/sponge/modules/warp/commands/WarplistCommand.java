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
package bammerbom.ultimatecore.sponge.modules.warp.commands;

import bammerbom.ultimatecore.sponge.api.command.Command;
import bammerbom.ultimatecore.sponge.api.data.GlobalData;
import bammerbom.ultimatecore.sponge.api.module.Module;
import bammerbom.ultimatecore.sponge.api.module.Modules;
import bammerbom.ultimatecore.sponge.api.permission.Permission;
import bammerbom.ultimatecore.sponge.modules.warp.api.Warp;
import bammerbom.ultimatecore.sponge.modules.warp.api.WarpKeys;
import bammerbom.ultimatecore.sponge.modules.warp.api.WarpPermissions;
import bammerbom.ultimatecore.sponge.utils.Messages;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class WarplistCommand implements Command {
    @Override
    public Module getModule() {
        return Modules.WARP.get();
    }

    @Override
    public String getIdentifier() {
        return "warplist";
    }

    @Override
    public Permission getPermission() {
        return WarpPermissions.UC_WARPLIST;
    }

    @Override
    public List<Permission> getPermissions() {
        return Arrays.asList(WarpPermissions.UC_WARPLIST);
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("warplist", "warps");
    }

    @Override
    public CommandResult run(CommandSource sender, String[] args) {
        if (!sender.hasPermission(WarpPermissions.UC_WARPLIST.get())) {
            sender.sendMessage(Messages.getFormatted("core.nopermissions"));
            return CommandResult.empty();
        }
        //Send the player a paginated list of all warps
        List<Warp> warps = GlobalData.get(WarpKeys.WARPS).get();
        List<Text> texts = new ArrayList<>();
        //Add entry to texts for every warp
        for (Warp warp : warps) {
            if (!sender.hasPermission(WarpPermissions.UC_WARP.get()) && !sender.hasPermission("uc.warp." + warp.getName().toLowerCase())) {
                continue;
            }
            texts.add(Messages.getFormatted("warp.command.warplist.entry", "%warp%", warp.getName(), "%description%", warp.getDescription()).toBuilder().onHover(TextActions.showText
                    (Messages.getFormatted("warp.command.warplist.hoverentry", "%warp%", warp.getName()))).onClick(TextActions.runCommand("/warp " + warp.getName())).build());
        }
        //If empty send message
        if (texts.isEmpty()) {
            sender.sendMessage(Messages.getFormatted("warp.command.warplist.empty"));
            return CommandResult.empty();
        }
        //Sort alphabetically
        Collections.sort(texts);
        //Send page
        PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
        PaginationList paginationList = paginationService.builder().contents(texts).title(Messages.getFormatted("warp.command.warplist.header").toBuilder().color(TextColors.DARK_GREEN)
                .build()).build();
        paginationList.sendTo(sender);
        return CommandResult.success();
    }

    @Override
    public List<String> onTabComplete(CommandSource sender, String[] args, String curs, Integer curn) {
        return GlobalData.get(WarpKeys.WARPS).get().stream().map(Warp::getName).collect(Collectors.toList());
    }
}
