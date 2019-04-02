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
package bammerbom.ultimatecore.sponge.modules.spy.api;

import bammerbom.ultimatecore.sponge.api.data.Key;
import bammerbom.ultimatecore.sponge.api.data.KeyProvider;
import bammerbom.ultimatecore.sponge.api.user.UltimateUser;
import bammerbom.ultimatecore.sponge.config.datafiles.PlayerDataFile;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class SpyKeys {
    public static Key.User<Boolean> COMMANDSPY_ENABLED = new Key.User<>("commandspy", new KeyProvider.User<Boolean>() {
        @Override
        public Boolean load(UltimateUser user) {
            PlayerDataFile loader = new PlayerDataFile(user.getIdentifier());
            CommentedConfigurationNode node = loader.get();
            return node.getNode("commandspy").getBoolean(false);
        }

        @Override
        public void save(UltimateUser user, Boolean data) {
            PlayerDataFile loader = new PlayerDataFile(user.getIdentifier());
            CommentedConfigurationNode node = loader.get();
            node.getNode("commandspy").setValue(data);
            loader.save(node);
        }
    });

    public static Key.User<Boolean> MESSAGESPY_ENABLED = new Key.User<>("messagespy", new KeyProvider.User<Boolean>() {
        @Override
        public Boolean load(UltimateUser user) {
            PlayerDataFile loader = new PlayerDataFile(user.getIdentifier());
            CommentedConfigurationNode node = loader.get();
            return node.getNode("messagespy").getBoolean(false);
        }

        @Override
        public void save(UltimateUser user, Boolean data) {
            PlayerDataFile loader = new PlayerDataFile(user.getIdentifier());
            CommentedConfigurationNode node = loader.get();
            node.getNode("messagespy").setValue(data);
            loader.save(node);
        }
    });
}
