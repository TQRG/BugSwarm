/*
 * Hello Minecraft!.
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
package org.jackhuang.hellominecraft.util.system;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jackhuang.hellominecraft.util.logging.HMCLog;
import org.jackhuang.hellominecraft.util.StrUtils;

/**
 *
 * @author huangyuhui
 */
public final class JdkVersion implements Cloneable {

    private String ver;

    public String getVersion() {
        return ver;
    }

    public Platform getPlatform() {
        return Platform.values()[platform];
    }

    public String getLocation() {
        return location;
    }

    public int getParsedVersion() {
        return parseVersion(getVersion());
    }
    /**
     * -1 - unkown 0 - 32Bit 1 - 64Bit
     */
    private int platform;

    private String location;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof JdkVersion))
            return false;
        JdkVersion b = (JdkVersion) obj;
        if (b.location == null || location == null)
            return b.location == location;
        return new File(b.location).equals(new File(location));
    }

    @Override
    public int hashCode() {
        return location == null ? 0 : new File(location).hashCode();
    }

    @Override
    protected Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new Error(ex);
        }
    }

    public JdkVersion(String location) {
        File f = new File(location);
        if (f.exists() && f.isFile())
            f = f.getParentFile();
        this.location = f.getAbsolutePath();
    }

    public JdkVersion(String location, String ver, Platform platform) {
        this(location);
        this.ver = ver;
        this.platform = platform.ordinal();
    }

    /**
     * Constant identifying the 1.5 JVM (Java 5).
     */
    public static final int UNKOWN = 2;
    /**
     * Constant identifying the 1.6 JVM (Java 6).
     */
    public static final int JAVA_16 = 3;
    /**
     * Constant identifying the 1.7 JVM (Java 7).
     */
    public static final int JAVA_17 = 4;
    /**
     * Constant identifying the 1.8 JVM (Java 8).
     */
    public static final int JAVA_18 = 5;
    /**
     * Constant identifying the 1.9 JVM (Java 9).
     */
    public static final int JAVA_19 = 6;

    private static final String JAVA_VER;
    private static final int MAJOR_JAVA_VER;

    static {
        JAVA_VER = System.getProperty("java.version");
        // version String should look like "1.4.2_10"
        MAJOR_JAVA_VER = parseVersion(JAVA_VER);
    }

    private static int parseVersion(String javaVersion) {
        if (StrUtils.isBlank(javaVersion))
            return UNKOWN;
        int a = UNKOWN;
        if (javaVersion.contains("1.9.") || javaVersion.startsWith("9"))
            a = JAVA_19;
        else if (javaVersion.contains("1.8."))
            a = JAVA_18;
        else if (javaVersion.contains("1.7."))
            a = JAVA_17;
        else if (javaVersion.contains("1.6."))
            a = JAVA_16;
        return a;
    }

    /**
     * Return the full Java version string, as returned by
     * <code>System.getProperty("java.version")</code>.
     *
     * @return the full Java version string
     *
     * @see System#getProperty(String)
     */
    public static String getJavaVersion() {
        return JAVA_VER;
    }

    /**
     * Get the major version code. This means we can do things like
     * <code>if (getMajorJavaVersion() < JAVA_14)</code>. @retu
     *
     *
     * rn a code comparable to the JAVA_XX codes in this class
     *
     * @return
     *
     * @see #JAVA_13
     * @see #JAVA_14
     * @see #JAVA_15
     * @see #JAVA_16
     * @see #JAVA_17
     */
    public static int getMajorJavaVersion() {
        return MAJOR_JAVA_VER;
    }

    public static boolean isJava64Bit() {
        String jdkBit = System.getProperty("sun.arch.data.model");
        return jdkBit.contains("64");
    }

    private static final Pattern p = Pattern.compile("java version \"[1-9]*\\.[1-9]*\\.[0-9]*(.*?)\"");

    public static JdkVersion getJavaVersionFromExecutable(String file) throws IOException {
        String[] str = new String[] { file, "-version" };
        Platform platform = Platform.BIT_32;
        String ver = null;
        try {
            for (String line : IOUtils.readProcessByErrorStream(str)) {
                Matcher m = p.matcher(line);
                if (m.find()) {
                    ver = m.group();
                    ver = ver.substring("java version \"".length(), ver.length() - 1);
                }
                if (line.contains("64-Bit"))
                    platform = Platform.BIT_64;
            }
        } catch (InterruptedException | IOException e) {
            HMCLog.warn("Failed to get java version", e);
        }
        return new JdkVersion(file, ver, platform);
    }

    public void write(File f) throws IOException {
        if (ver != null && getPlatform() != Platform.UNKNOWN)
            FileUtils.write(f, ver + "\n" + platform);
    }

    public boolean isEarlyAccess() {
        return getVersion() != null && getVersion().endsWith("-ea");
    }
}
