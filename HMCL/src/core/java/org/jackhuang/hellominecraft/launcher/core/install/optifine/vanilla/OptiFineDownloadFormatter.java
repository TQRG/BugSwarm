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
package org.jackhuang.hellominecraft.launcher.core.install.optifine.vanilla;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jackhuang.hellominecraft.util.C;
import org.jackhuang.hellominecraft.util.tasks.Task;
import org.jackhuang.hellominecraft.util.tasks.communication.PreviousResult;
import org.jackhuang.hellominecraft.util.NetUtils;

/**
 *
 * @author huangyuhui
 */
public class OptiFineDownloadFormatter extends Task implements PreviousResult<String> {

    String url, result;

    public OptiFineDownloadFormatter(String url) {
        this.url = url;
    }

    @Override
    public void executeTask() throws Exception {
        String content = NetUtils.get(url);
        Pattern p = Pattern.compile("\"downloadx\\?f=OptiFine(.*)\"");
        Matcher m = p.matcher(content);
        while (m.find())
            result = m.group(1);
        result = "http://optifine.net/downloadx?f=OptiFine" + result;
    }

    @Override
    public String getInfo() {
        return C.i18n("install.optifine.get_download_link");
    }

    @Override
    public String getResult() {
        return result;
    }
}
