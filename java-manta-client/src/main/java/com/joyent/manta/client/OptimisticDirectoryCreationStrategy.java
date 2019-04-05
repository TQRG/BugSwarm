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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.joyent.manta.util.MantaUtils.prefixPaths;

/**
 * Directory creation strategy which attempts to create the deepest directories first. In the best case this
 * would create a single directory when its direct parent exists. In the worst case it will perform twice as many
 * operations by failing every step from the desired directory to the root, and then working back down to the
 * desired directory.
 *
 * @author <a href="https://github.com/tjcelaya">Tomas Celayac</a>
 * @since 3.1.7
 */
class OptimisticDirectoryCreationStrategy extends RecursiveDirectoryCreationStrategy {

    OptimisticDirectoryCreationStrategy(final MantaClient client) {
        super(client);
    }

    public void create(final String rawPath, final MantaHttpHeaders headers) throws IOException {
        final List<String> ascendingDirectories = Arrays.asList(prefixPaths(rawPath));

        // reverse the list so the deepest directories are attempted first
        Collections.reverse(ascendingDirectories);
        final ArrayList<String> failedPuts = new ArrayList<>();

        for (final String intermediateDirectory : ascendingDirectories) {
            final Boolean createResult = createNewDirectory(intermediateDirectory, headers);
            if (createResult == null) {
                failedPuts.add(intermediateDirectory);
            }
        }

        // failed puts will have the deepest directories first, reversing it will allow us to work downwards towards
        // the requested directory
        Collections.reverse(failedPuts);

        for (final String descendingDirectory : failedPuts) {
            getClient().putDirectory(descendingDirectory, headers);
        }
    }
}
