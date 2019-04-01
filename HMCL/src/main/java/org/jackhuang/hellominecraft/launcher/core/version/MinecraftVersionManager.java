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

import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import org.jackhuang.hellominecraft.util.C;
import org.jackhuang.hellominecraft.util.logging.HMCLog;
import org.jackhuang.hellominecraft.launcher.core.GameException;
import org.jackhuang.hellominecraft.launcher.core.service.IMinecraftProvider;
import org.jackhuang.hellominecraft.launcher.core.service.IMinecraftService;
import org.jackhuang.hellominecraft.util.system.FileUtils;
import org.jackhuang.hellominecraft.launcher.core.MCUtils;
import org.jackhuang.hellominecraft.util.tasks.TaskWindow;
import org.jackhuang.hellominecraft.util.system.IOUtils;
import org.jackhuang.hellominecraft.util.MessageBox;
import org.jackhuang.hellominecraft.util.StrUtils;
import org.jackhuang.hellominecraft.util.func.Consumer;
import org.jackhuang.hellominecraft.util.func.Predicate;
import org.jackhuang.hellominecraft.util.ui.SwingUtils;

/**
 *
 * @author huangyuhui
 */
public class MinecraftVersionManager extends IMinecraftProvider {

    final Map<String, MinecraftVersion> versions = new TreeMap();

    /**
     *
     * @param p
     */
    public MinecraftVersionManager(IMinecraftService p) {
        super(p);
    }

    @Override
    public Collection<MinecraftVersion> getVersions() {
        return versions.values();
    }

    @Override
    public int getVersionCount() {
        return versions.size();
    }

    @Override
    public synchronized void refreshVersions() {
        onRefreshingVersions.execute(service);

        try {
            MCUtils.tryWriteProfile(service.baseDirectory());
        } catch (IOException ex) {
            HMCLog.warn("Failed to create launcher_profiles.json, Forge/LiteLoader installer will not work.", ex);
        }

        versions.clear();
        File oldDir = new File(service.baseDirectory(), "bin");
        if (oldDir.exists()) {
            MinecraftClassicVersion v = new MinecraftClassicVersion();
            versions.put(v.id, v);
        }

        File version = new File(service.baseDirectory(), "versions");
        File[] files = version.listFiles();
        if (files != null && files.length > 0)
            for (File dir : files) {
                String id = dir.getName();
                File jsonFile = new File(dir, id + ".json");

                if (!dir.isDirectory())
                    continue;
                boolean ask = false;
                File[] jsons = null;
                if (!jsonFile.exists()) {
                    jsons = FileUtils.searchSuffix(dir, "json");
                    if (jsons.length == 1)
                        ask = true;
                }
                if (ask) {
                    HMCLog.warn("Found not matched filenames version: " + id + ", json: " + jsons[0].getName());
                    if (MessageBox.Show(String.format(C.i18n("launcher.versions_json_not_matched"), id, jsons[0].getName()), MessageBox.YES_NO_OPTION) == MessageBox.YES_OPTION)
                        if (!jsons[0].renameTo(new File(jsons[0].getParent(), id + ".json")))
                            HMCLog.warn("Failed to rename version json " + jsons[0]);
                }
                if (!jsonFile.exists()) {
                    if (MessageBox.Show(C.i18n("launcher.versions_json_not_matched_cannot_auto_completion", id), MessageBox.YES_NO_OPTION) == MessageBox.YES_OPTION)
                        FileUtils.deleteDirectoryQuietly(dir);
                    continue;
                }
                MinecraftVersion mcVersion;
                try {
                    mcVersion = C.GSON.fromJson(FileUtils.readFileToString(jsonFile), MinecraftVersion.class);
                    if (mcVersion == null)
                        throw new GameException("Wrong json format, got null.");
                } catch (Exception e) {
                    HMCLog.warn("Found wrong format json, try to fix it.", e);
                    if (MessageBox.Show(C.i18n("launcher.versions_json_not_formatted", id), MessageBox.YES_NO_OPTION) == MessageBox.YES_OPTION) {
                        service.download().downloadMinecraftVersionJson(id);
                        try {
                            mcVersion = C.GSON.fromJson(FileUtils.readFileToString(jsonFile), MinecraftVersion.class);
                            if (mcVersion == null)
                                throw new GameException("Wrong json format, got null.");
                        } catch (IOException | GameException | JsonSyntaxException ex) {
                            HMCLog.warn("Ignoring: " + dir + ", the json of this Minecraft is malformed.", ex);
                            continue;
                        }
                    } else
                        continue;
                }
                try {
                    if (!id.equals(mcVersion.id)) {
                        HMCLog.warn("Found: " + dir + ", it contains id: " + mcVersion.id + ", expected: " + id + ", this app will fix this problem.");
                        mcVersion.id = id;
                        FileUtils.writeQuietly(jsonFile, C.GSON.toJson(mcVersion));
                    }

                    versions.put(id, mcVersion);
                    onLoadedVersion.execute(id);
                } catch (Exception e) {
                    HMCLog.warn("Ignoring: " + dir + ", the json of this Minecraft is malformed.", e);
                }
            }
        onRefreshedVersions.execute(service);
    }

    @Override
    public File versionRoot(String id) {
        return new File(service.baseDirectory(), "versions/" + id);
    }

    @Override
    public boolean removeVersionFromDisk(String name) {
        File version = versionRoot(name);
        if (!version.exists())
            return true;

        versions.remove(name);
        return FileUtils.deleteDirectoryQuietly(version);
    }

    @Override
    public boolean renameVersion(String from, String to) {
        try {
            File fromJson = new File(versionRoot(from), from + ".json");
            MinecraftVersion mcVersion = C.GSON.fromJson(FileUtils.readFileToString(fromJson), MinecraftVersion.class);
            mcVersion.id = to;
            FileUtils.writeQuietly(fromJson, C.GSON.toJson(mcVersion));
            File toDir = versionRoot(to);
            if (!versionRoot(from).renameTo(toDir))
                HMCLog.warn("MinecraftVersionManager.RenameVersion: Failed to rename version root " + from + " to " + to);
            File toJson = new File(toDir, to + ".json");
            File toJar = new File(toDir, to + ".jar");
            if (new File(toDir, from + ".json").renameTo(toJson))
                HMCLog.warn("MinecraftVersionManager.RenameVersion: Failed to rename json");
            File newJar = new File(toDir, from + ".jar");
            if (newJar.exists() && !newJar.renameTo(toJar))
                HMCLog.warn("Failed to rename pre jar " + newJar + " to new jar " + toJar);
            return true;
        } catch (IOException | JsonSyntaxException e) {
            HMCLog.warn("Failed to rename " + from + " to " + to + ", the json of this Minecraft is malformed.", e);
            return false;
        }
    }

    @Override
    public File getRunDirectory(String id) {
        if (getVersionById(id) != null)
            if ("version".equals(getVersionById(id).runDir))
                return versionRoot(id);
        return baseDirectory();
    }

    @Override
    public boolean install(String id, Consumer<MinecraftVersion> callback) {
        if (!TaskWindow.factory().append(service.download().downloadMinecraft(id)).create())
            return false;
        if (callback != null) {
            File mvt = new File(versionRoot(id), id + ".json");
            MinecraftVersion v = C.GSON.fromJson(FileUtils.readFileToStringQuietly(mvt), MinecraftVersion.class);
            if (v == null)
                return false;
            callback.accept(v);
            FileUtils.writeQuietly(mvt, C.GSON.toJson(v));
        }
        refreshVersions();
        return true;
    }

    @Override
    public void open(String mv, String name) {
        SwingUtils.openFolder((name == null) ? getRunDirectory(mv) : new File(getRunDirectory(mv), name));
    }

    @Override
    public DecompressLibraryJob getDecompressLibraries(MinecraftVersion v) throws GameException {
        if (v.libraries == null)
            throw new GameException("Wrong format: minecraft.json");
        ArrayList<File> unzippings = new ArrayList<>();
        ArrayList<Extract> extractRules = new ArrayList<>();
        for (IMinecraftLibrary l : v.libraries)
            if (l.isRequiredToUnzip() && v.isAllowedToUnpackNatives()) {
                unzippings.add(IOUtils.tryGetCanonicalFile(l.getFilePath(service.baseDirectory())));
                extractRules.add(l.getDecompressExtractRules());
            }
        return new DecompressLibraryJob(unzippings.toArray(new File[unzippings.size()]), extractRules.toArray(new Extract[extractRules.size()]), getDecompressNativesToLocation(v));
    }

    @Override
    public File getDecompressNativesToLocation(MinecraftVersion v) {
        return v == null ? null : v.getNatives(service.baseDirectory());
    }

    @Override
    public File getMinecraftJar(String id) {
        if (versions.containsKey(id))
            return versions.get(id).getJar(service.baseDirectory());
        else
            return null;
    }

    @Override
    public MinecraftVersion getOneVersion(Predicate<MinecraftVersion> pred) {
        for (MinecraftVersion v : versions.values())
            if (pred == null || pred.apply(v))
                return v;
        return null;
    }

    @Override
    public MinecraftVersion getVersionById(String id) {
        return StrUtils.isBlank(id) ? null : versions.get(id);
    }

    @Override
    public File getResourcePacks() {
        return new File(service.baseDirectory(), "resourcepacks");
    }

    @Override
    public boolean onLaunch() {
        File resourcePacks = getResourcePacks();
        if (!resourcePacks.exists() && !resourcePacks.mkdirs())
            HMCLog.warn("Failed to make resourcePacks: " + resourcePacks);
        return true;
    }

    @Override
    public void cleanFolder() {
        for (MinecraftVersion s : getVersions()) {
            FileUtils.deleteDirectoryQuietly(new File(versionRoot(s.id), s.id + "-natives"));
            File f = getRunDirectory(s.id);
            String[] dir = { "natives", "native", "$native", "AMD", "crash-reports", "logs", "asm", "NVIDIA", "server-resource-packs", "natives", "native" };
            for (String str : dir)
                FileUtils.deleteDirectoryQuietly(new File(f, str));
            String[] files = { "output-client.log", "usercache.json", "usernamecache.json", "hmclmc.log" };
            for (String str : files)
                if (!new File(f, str).delete())
                    HMCLog.warn("Failed to delete " + str);
        }
    }

    @Override
    public void initializeMiencraft() {

    }
}
