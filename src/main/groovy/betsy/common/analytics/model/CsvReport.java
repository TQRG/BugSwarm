package betsy.common.analytics.model;

import java.nio.file.Path;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

public class CsvReport {
    public Group getGroup(String name) {
        if (nameToGroup.containsKey(name)) {
            return nameToGroup.get(name);
        } else {
            Group group = new Group(name);
            nameToGroup.put(name, group);

            return group;
        }

    }

    public Collection<Engine> getEngines() {
        return nameToEngine.values();
    }

    public Collection<Group> getGroups() {
        return nameToGroup.values();
    }

    public Collection<Test> getTests() {
        return nameToTest.values();
    }

    public Engine getEngine(String name) {
        if (nameToEngine.containsKey(name)) {
            return nameToEngine.get(name);
        } else {
            Engine engine = new Engine(name);
            nameToEngine.put(name, engine);

            return engine;
        }

    }

    public Test getTest(String name) {
        if (nameToTest.containsKey(name)) {
            return nameToTest.get(name);
        } else {
            Test test = new Test();

            test.setName(name);
            nameToTest.put(name, test);

            return test;
        }

    }

    public int getNumberOfSuccessfulTestsPer(Engine engine) {
        int successfulTests = 0;
        for (Test test : getTests()) {
            Result result = test.getEngineToResult().get(engine);
            if (result.isSuccessful()) {
                successfulTests++;
            }
        }
        return successfulTests;
    }

    public String getRelativePath(Group group, Engine engine, Test test) {
        return "#";
    }

    public Path getFile() {
        return file;
    }

    public void setFile(Path file) {
        this.file = file;
    }

    public final SortedMap<String, Test> getNameToTest() {
        return nameToTest;
    }

    public final SortedMap<String, Group> getNameToGroup() {
        return nameToGroup;
    }

    public final SortedMap<String, Engine> getNameToEngine() {
        return nameToEngine;
    }

    private Path file;
    private final SortedMap<String, Test> nameToTest = new TreeMap<>();
    private final SortedMap<String, Group> nameToGroup = new TreeMap<>();
    private final SortedMap<String, Engine> nameToEngine = new TreeMap<>();
}
