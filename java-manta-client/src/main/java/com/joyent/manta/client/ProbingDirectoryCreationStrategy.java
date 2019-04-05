/*
 * Copyright (c) 2017, Joyent, Inc. All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.joyent.manta.client;

import com.joyent.manta.http.MantaHttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.joyent.manta.util.MantaUtils.writeablePrefixPaths;

/**
 * A strategy for creating directories similar to binary/interpolation search. The general approach is
 * as follows:
 *
 * <ol>
 *     <li>Build prefix paths to desired directory</li>
 *     <li>Drop system paths (/USER and /USER/SYSTEM_DIR)</li>
 *     <li>Set up:
 *     <li>
 *         <ol>
 *             <li><code>idx</code> is the index of the current directory to probe</li>
 *             <li><code>min</code> is the index of the root-most directory we need to check</li>
 *             <li><code>max</code> is the index of the deepest directory we need to check</li>
 *             <li><code>descending</code> whether or not the next probe should be deeper in the hierarchy</li>
 *         </ol>
 *     </li>
 *     <li>Loop the probing phase, updating the above variables along the way:</li>
 *     <li>
 *         <ol>
 *             <li>Choose the directory halfway between the root-most directory that might need to be created
 *             and the desired directory</li>
 *             <li>If the directory creation was redundant, update <code>min</code>
 *             and
 *             <li>Otherwise, if the directory creation failed, update max to point to the failed probe
 *             and set <code>descending</code> to true</li>
 *             <li>Calculate the jump length based on the distance between <code>min</code> and <code>max</code></li>
 *             <li>Add (or subtract, if descending = false) the jump distance from the current index</li>
 *         </ol>
 *     </li>
 *     <li>Exit the above loop if any of the following conditions are met</li>
 *     <li>
 *         <ol>
 *             <li>The leaf directory was created (this is unlikely)</li>
 *             <li>The last PUT attempted actually created a new directory (it can have no children)</li>
 *             <li>The jump distance was zero, i.e. floor((max - min) / 2) == 0 since
 *             <code>min</code> and <code>max</code> are too close together</li>
 *         </ol>
 *     </li>
 * </ol>
 *
 * @author <a href="https://github.com/tjcelaya">Tomas Celayac</a>
 * @since 3.1.7
 */
class ProbingDirectoryCreationStrategy extends RecursiveDirectoryCreationStrategy {

    @SuppressWarnings("checkstyle:JavadocVariable")
    private static final Logger LOG = LoggerFactory.getLogger(ProbingDirectoryCreationStrategy.class);

    /**
     * The minimum directory depth beyond {@code /$USER/$SYSTEM_DIR} at which probing is used.
     */
    private static final int MIN_PROBE_DEPTH = 3;

    ProbingDirectoryCreationStrategy(final MantaClient client) {
        super(client);
    }

    public void create(final String rawPath, final MantaHttpHeaders headers) throws IOException {
        LOG.info("START  " + rawPath);
        final String[] paths = writeablePrefixPaths(rawPath);

        if (paths.length < MIN_PROBE_DEPTH) {
            new PessimisticDirectoryCreationStrategy(getClient()).create(rawPath, headers);
            return;
        }

        int min = 0;
        int max = paths.length - 1;
        int idx = Math.floorDiv(paths.length, 2);

        while (true) {
            final String currentPath = paths[idx];
            final Boolean requiredPut = createNewDirectory(currentPath, headers);
            incrementOperations();

            // we successfully created the requested directory, who cares if it's redundant?
            if (requiredPut != null && rawPath.equals(currentPath)) {
                return;
            }

            final boolean descending;
            if (requiredPut == null) {
                LOG.info("FAILED " + currentPath);
                descending = false;
                max = idx;
            } else if (requiredPut) {
                LOG.info("REQD   " + currentPath);
                // stop skipping around and create the remaining directories normally
                break;
            } else {
                LOG.info("REDUN  " + currentPath);
                descending = true;
                min = idx;
            }

            final int jumpDistance = Math.max(1, Math.floorDiv(max - min, 2));
            LOG.info("JUMP   : " + jumpDistance + " ( min: " + min + ", max: " + max + ", diff: " + (max - min) + " )");

            if (jumpDistance == 0) {
                break;
            }

            if (descending) {
                idx = idx + jumpDistance;
            } else {
                idx = idx - jumpDistance;
            }
        }

        LOG.info("PROBE FINISH");

        // create remaining directories normally
        for (; idx < paths.length; idx++) {
            getClient().putDirectory(paths[idx], headers);
            incrementOperations();
        }
    }
}
