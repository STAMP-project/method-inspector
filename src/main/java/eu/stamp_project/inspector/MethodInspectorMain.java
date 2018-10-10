package eu.stamp_project.inspector;

import picocli.CommandLine;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.ParameterException;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import static eu.stamp_project.inspector.MethodCollector.collectFromFolders;
import static eu.stamp_project.inspector.MethodEntry.saveToFile;


@Command(name = "method-inspector", mixinStandardHelpOptions = true, version = "2.0")
public class MethodInspectorMain implements Callable<Void> {

    @Spec private CommandSpec spec;

    private File inputfolder;

    @Parameters(index = "0",
            paramLabel = "FOLDER",
            description = "Path to the folder containing all .class files")
    public void setInputfolder(File inputFolder) {
        if(!inputFolder.exists()) {
            throw new ParameterException(spec.commandLine(), "Input folder must exist.");
        }
        if(!inputFolder.isDirectory()) {
            throw new ParameterException(spec.commandLine(), "Input path must be a folder path");
        }
        if(!inputFolder.canRead()) {
            throw new ParameterException(spec.commandLine(), "Input folder must be readable");
        }
        this.inputfolder = inputFolder;
    }

    private File outputFile;

    @Parameters(index = "1",
            paramLabel = "FILE",
            arity = "0..1",
            defaultValue = "methods.json",
            description = "Path to the JSON file output. Defaults to ${DEFAULT-VALUE}")
    public void setOutputFile(File outputFile) {
        try {
            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }
        }
        catch (IOException exc) {
            throw new ParameterException(spec.commandLine(),
                    "A problem occurred while creating the output file. Details: " + exc.toString());
        }
        if(!outputFile.canRead()) {
            throw new ParameterException(spec.commandLine(), "Output file must be writable");
        }
        this.outputFile = outputFile;
    }


    @Override
    public Void call() throws Exception {
        saveToFile(collectFromFolders(inputfolder.getAbsolutePath()), outputFile);
        return null; //ARGHHHH
    }

    public static void main(String... args) {
        CommandLine.call(new MethodInspectorMain(), args);
    }

}
