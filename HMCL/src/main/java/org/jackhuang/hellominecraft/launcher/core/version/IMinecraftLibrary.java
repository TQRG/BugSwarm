/*
 * Hello Minecraft! Launcher.
 * Copyright (C) 2013  huangyuhui <huanghongxun2008@126.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see {http://www.gnu.org/licenses/}.
 */
package org.jackhuang.hellominecraft.launcher.core.version;

import java.io.File;

/**
 *
 * @author huangyuhui
 */
public abstract class IMinecraftLibrary implements Cloneable {

    public String name;

    public IMinecraftLibrary(String name) {
        this.name = name;
    }

    public abstract boolean isRequiredToUnzip();

    public abstract Extract getDecompressExtractRules();

    public abstract LibraryDownloadInfo getDownloadInfo();

    public abstract boolean allow();

    public abstract File getFilePath(File gameDir);

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MinecraftLibrary)
            return ((MinecraftLibrary) obj).name.equals(name);
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    @Override
    @SuppressWarnings("CloneDeclaresCloneNotSupported")
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new InternalError(ex);
        }
    }
}
