/*
 * Capsule
 * Copyright (c) 2014, Parallel Universe Software Co. All rights reserved.
 * 
 * This program and the accompanying materials are licensed under the terms 
 * of the Eclipse Public License v1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
import java.nio.file.Path;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * A custom capsule example
 */
public class MyCapsule extends TestCapsule {
    public MyCapsule(Path jarFile) {
        super(jarFile);
    }

    public MyCapsule(Capsule pred) {
        super(pred);
    }

    @Override
    protected List<Path> buildBootClassPathA() {
        return super.buildBootClassPathA();
    }

    @Override
    protected List<Path> buildBootClassPathP() {
        return super.buildBootClassPathP();
    }

    @Override
    protected List<Path> buildBootClassPath() {
        return super.buildBootClassPath();
    }

    @Override
    protected List<Path> buildClassPath() {
        return super.buildClassPath();
    }

    @Override
    protected List<String> buildJVMArgs() {
        List<String> args = super.buildJVMArgs();
        for (ListIterator<String> it = args.listIterator(); it.hasNext();) {
            String arg = it.next();
            if (arg.startsWith("-Xmx"))
                it.set("-Xmx3000");
            else if (arg.startsWith("-Xms"))
                it.set("-Xms3");
        }
        return args;
    }

    @Override
    protected List<String> getNativeDependencies() {
        return super.getNativeDependencies();
    }

    @Override
    protected Map<String, String> buildSystemProperties() {
        Map<String, String> props = super.buildSystemProperties();
        props.put("foo", "z");
        props.put("baz", "44");
        return props;
    }

    @Override
    protected List<String> getDependencies() {
        return super.getDependencies();
    }

    @Override
    protected String[] buildAppId() {
        return super.buildAppId();
    }

    @Override
    protected List<String> buildArgs(List<String> args) {
        return super.buildArgs(args);
    }
}
