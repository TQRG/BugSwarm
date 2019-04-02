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
package bammerbom.ultimatecore.sponge.modules.personalmessage.commands;

import bammerbom.ultimatecore.sponge.UltimateCore;
import bammerbom.ultimatecore.sponge.api.command.Command;
import bammerbom.ultimatecore.sponge.api.module.Module;
import bammerbom.ultimatecore.sponge.api.module.Modules;
import bammerbom.ultimatecore.sponge.api.permission.Permission;
import bammerbom.ultimatecore.sponge.api.user.UltimateUser;
import bammerbom.ultimatecore.sponge.modules.personalmessage.api.PersonalmessageEvent;
import bammerbom.ultimatecore.sponge.modules.personalmessage.api.PersonalmessageKeys;
import bammerbom.ultimatecore.sponge.modules.personalmessage.api.PersonalmessagePermissions;
import bammerbom.ultimatecore.sponge.utils.Messages;
import bammerbom.ultimatecore.sponge.utils.Selector;
import bammerbom.ultimatecore.sponge.utils.StringUtil;
import bammerbom.ultimatecore.sponge.utils.VariableUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.message.MessageEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class PersonalmessageCommand implements Command {
    @Override
    public Module getModule() {
        return Modules.PERSONALMESSAGE.get();
    }

    @Override
    public String getIdentifier() {
        return "personalmessage";
    }

    @Override
    public Permission getPermission() {
        return PersonalmessagePermissions.UC_PERSONALMESSAGE;
    }

    @Override
    public List<Permission> getPermissions() {
        return Arrays.asList(PersonalmessagePermissions.UC_PERSONALMESSAGE);
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("personalmessage", "pm", "dm", "msg", "w", "whisper", "tell");
    }

    @Override
    public CommandResult run(CommandSource sender, String[] args) {
        if (!sender.hasPermission(getPermission().get())) {
            sender.sendMessage(Messages.getFormatted("core.nopermissions"));
            return CommandResult.empty();
        }
        if (args.length < 2) {
            sender.sendMessage(getUsage());
            return CommandResult.empty();
        }
        CommandSource t = Selector.one(sender, args[0]).orElse(null);
        if (t == null) {
            if (args[0].equalsIgnoreCase("server")) {
                t = Sponge.getServer().getConsole();
            } else {
                sender.sendMessage(Messages.getFormatted("core.playernotfound", "%player%", args[0]));
                return CommandResult.empty();
            }
        }
        String message = StringUtil.getFinalArg(args, 1);
        Text fmessage = Messages.getFormatted("personalmessage.command.personalmessage.format.receive", "%player%", VariableUtil.getNameSource(sender), "%message%", message);

        //Event
        Cause cause = Cause.builder().owner(UltimateCore.get()).named("sender", sender).named("target", t).build();
        MessageEvent.MessageFormatter formatter = new MessageEvent.MessageFormatter(fmessage);
        final CommandSource tf = t;
        MessageChannel channel = new MessageChannel() {
            @Override
            public Collection<MessageReceiver> getMembers() {
                return Arrays.asList(tf);
            }
        };
        PersonalmessageEvent event = new PersonalmessageEvent(cause, sender, t, formatter, channel, message, fmessage);
        Sponge.getEventManager().post(event);
        if (!event.isMessageCancelled()) {
            Text received = event.getFormatter().toText();
            t.sendMessage(received);
            //Reply
            UUID uuid_s = sender instanceof Player ? ((Player) sender).getUniqueId() : UUID.fromString("00000000-0000-0000-0000-000000000000");
            UUID uuid_t = t instanceof Player ? ((Player) t).getUniqueId() : UUID.fromString("00000000-0000-0000-0000-000000000000");
            if (t instanceof Player) {
                UltimateUser user = UltimateCore.get().getUserService().getUser((Player) t);
                user.offer(PersonalmessageKeys.REPLY, uuid_s);
            }
            if (sender instanceof Player) {
                UltimateUser user2 = UltimateCore.get().getUserService().getUser((Player) sender);
                user2.offer(PersonalmessageKeys.REPLY, uuid_t);
            }
            //TODO better system for this message?
            Text send = Messages.getFormatted("personalmessage.command.personalmessage.format.send", "%player%", VariableUtil.getNameSource(t), "%message%", message);
            sender.sendMessage(send);
            return CommandResult.success();
        } else {
            t.sendMessage(Messages.getFormatted("personalmessage.command.personalmessage.cancelled"));
            return CommandResult.empty();
        }
    }

    @Override
    public List<String> onTabComplete(CommandSource sender, String[] args, String curs, Integer curn) {
        return null;
    }
}
