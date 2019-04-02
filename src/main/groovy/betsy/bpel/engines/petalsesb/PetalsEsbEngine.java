package betsy.bpel.engines.petalsesb;

import betsy.bpel.engines.AbstractLocalBPELEngine;
import betsy.bpel.model.BPELProcess;
import betsy.common.config.Configuration;
import betsy.common.tasks.*;
import org.apache.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PetalsEsbEngine extends AbstractLocalBPELEngine {
    @Override
    public String getName() {
        return "petalsesb";
    }

    @Override
    public String getEndpointUrl(final BPELProcess process) {
        return CHECK_URL + "/petals/services/" + process.getName() + "TestInterfaceService";
    }

    public Path getPetalsFolder() {
        return getServerPath().resolve(getPetalsFolderName());
    }

    protected String getPetalsFolderName() {
        return "petals-esb-4.0";
    }

    public Path getPetalsLogsFolder() {
        return getPetalsFolder().resolve("logs");
    }

    public Path getPetalsLogFile() {
        return getPetalsLogsFolder().resolve("petals.log");
    }

    public Path getPetalsBinFolder() {
        return getPetalsFolder().resolve("bin");
    }

    @Override
    public void storeLogs(BPELProcess process) {
        FileTasks.mkdirs(process.getTargetLogsPath());

        for (Path p : getLogs()) {
            FileTasks.copyFileIntoFolder(p, process.getTargetLogsPath());
        }
    }

    @Override
    public List<Path> getLogs() {
        List<Path> result = new LinkedList<>();

        result.add(getPetalsLogFile());

        return result;
    }

    @Override
    public void startup() {
        Path pathToJava7 = Configuration.getJava7Home();
        Map<String,String> environment = new HashMap<>();
        environment.put("JAVA_HOME", pathToJava7.toString());
        ConsoleTasks.executeOnWindows(ConsoleTasks.CliCommand.build(getPetalsBinFolder(), "petals-esb.bat"), environment);

        WaitTasks.waitFor(30 * 1000, 500, () -> FileTasks.hasFileSpecificSubstring(getPetalsLogFile(), "[Petals.Container.Components.petals-bc-soap] : Component started") &&
                FileTasks.hasFileSpecificSubstring(getPetalsLogFile(), "[Petals.Container.Components.petals-se-bpel] : Component started"));

        try {

            if (FileTasks.hasFileSpecificSubstring(getPetalsLogFile(), "[Petals.AutoLoaderService] : Error during the auto- installation of a component")) {
                throw new Exception("SOAP BC not installed correctly");
            }

        } catch (Exception ignore) {
            LOGGER.warn("SOAP BC Installation failed - shutdown, reinstall and start petalsesb again");
            shutdown();
            install();
            startup();
        }
    }

    @Override
    public void shutdown() {
        try {
            ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(getPath(), "taskkill").values("/FI", "WINDOWTITLE eq OW2*"));
        } catch (Exception ignore) {
            LOGGER.info("COULD NOT STOP ENGINE " + getName());
        }

    }

    @Override
    public void install() {
        new PetalsEsbInstaller().install();
    }

    @Override
    public void deploy(BPELProcess process) {
        new PetalsEsbDeployer(getInstallationDir(), getPetalsLogFile()).deploy(process.getTargetPackageCompositeFilePath(), process.getName());
    }

    public Path getInstallationDir() {
        return getPetalsFolder().resolve("install");
    }

    @Override
    public void buildArchives(BPELProcess process) {
        getPackageBuilder().createFolderAndCopyProcessFilesToTarget(process);

        // engine specific steps
        Path metaDir = process.getTargetProcessPath().resolve("META-INF");
        FileTasks.mkdirs(metaDir);

        XSLTTasks.transform(getXsltPath().resolve("create_jbi_from_bpel.xsl"), process.getTargetProcessFilePath(), metaDir.resolve("jbi.xml"));

        FileTasks.replaceTokenInFile(process.getTargetProcessPath().resolve("TestInterface.wsdl"), "TestInterfaceService", process.getName() + "TestInterfaceService");

        Path testPartnerWsdl = process.getTargetProcessPath().resolve("TestPartner.wsdl");
        if (Files.exists(testPartnerWsdl)) {
            FileTasks.replaceTokenInFile(testPartnerWsdl, "TestService", process.getName() + "TestService");
        }


        getPackageBuilder().replaceEndpointTokenWithValue(process);
        getPackageBuilder().replacePartnerTokenWithValue(process);
        getPackageBuilder().bpelFolderToZipFile(process);

        new PetalsEsbCompositePackager(process).build();
    }

    @Override
    public boolean isRunning() {
        return URLTasks.isUrlAvailable(CHECK_URL);
    }

    private static final Logger LOGGER = Logger.getLogger(PetalsEsbEngine.class);
    public static final String CHECK_URL = "http://localhost:8084";
}
