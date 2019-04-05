/*
 * Capsule
 * Copyright (c) 2014, Parallel Universe Software Co. and Contributors. All rights reserved.
 * 
 * This program and the accompanying materials are licensed under the terms 
 * of the Eclipse Public License v1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package co.paralleluniverse.capsule.build;

import java.util.Collection;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.Exclusion;

import capsule.DependencyManager;
import org.eclipse.aether.repository.RemoteRepository;

/**
 * @author circlespainter
 */
public class Dependencies {

    /**
     * Builds the Capsule string representation of an Aether RemoteRepository object
     *
     * @param rr The remote repository
     */
    public static String toCapsuleRepositoryString(RemoteRepository rr) {
        return DependencyManager.WELL_KNOWN_REPOS.keySet().contains(rr.getId())
                ? rr.getId() : rr.getUrl();
    }

    /**
     * Builds the Capsule string representation of an Aether Dependency object
     *
     * @param d The dependency
     */
    public static String toCapsuleDependencyString(Dependency d) {
        return toCapsuleArtifactString(d.getArtifact())
                + toCapsuleExclusionsString(d.getExclusions());
    }

    private static String toCapsuleArtifactString(Artifact a) {
        return a.getGroupId() + ":" + a.getArtifactId() + ":" + a.getBaseVersion();
    }

    private static String toCapsuleExclusionsString(Collection<Exclusion> exclusions) {
        final StringBuilder res = new StringBuilder();

        if (!exclusions.isEmpty()) {
            res.append("(");

            boolean starting = true;

            for (Exclusion e : exclusions) {
                if (!starting)
                    res.append(",");
                else
                    starting = false;

                res.append(e.getGroupId());
                res.append(":");

                if (e.getArtifactId() != null && e.getArtifactId().length() > 0)
                    res.append(e.getArtifactId());
                else
                    res.append("*");
            }

            res.append(")");
        }

        return res.toString();
    }
}
