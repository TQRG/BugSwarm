/*
 * Copyright (c) 2017, Joyent, Inc. All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.joyent.manta.client;

import com.joyent.manta.http.MantaHttpHeaders;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import static com.joyent.manta.client.MantaClient.SEPARATOR;

/**
 * Directory creation strategy which creates all non-system directories every time. Used if timestamp updates are
 * requested or probing is unnecessary (i.e. 3 PUTs in the worst-case). This is the original implementation of
 * recursive putDirectory.
 *
 * @author <a href="https://github.com/tjcelaya">Tomas Celayac</a>
 * @since 3.1.7
 */
class PessimisticDirectoryCreationStrategy extends RecursiveDirectoryCreationStrategy {

    PessimisticDirectoryCreationStrategy(final MantaClient client) {
        super(client);
    }

    public void create(final String rawPath, final MantaHttpHeaders headers) throws IOException {
        final String[] parts = rawPath.split(SEPARATOR);
        final Iterator<Path> itr = Paths.get("", parts).iterator();
        final StringBuilder sb = new StringBuilder(SEPARATOR);

        for (int i = 0; itr.hasNext(); i++) {
            final String part = itr.next().toString();
            sb.append(part);

            // This means we aren't in the home nor in the reserved
            // directory path (stor, public, jobs, etc)
            if (i > 1) {

                getClient().putDirectory(sb.toString(), headers);
                incrementOperations();
            }

            if (itr.hasNext()) {
                sb.append(SEPARATOR);
            }
        }
    }
}
