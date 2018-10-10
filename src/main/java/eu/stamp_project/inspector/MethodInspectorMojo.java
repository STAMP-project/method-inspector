package eu.stamp_project.inspector;


import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Stream;

import static eu.stamp_project.inspector.MethodCollector.collectFromFolders;
import static eu.stamp_project.inspector.MethodEntry.saveToFile;

@Mojo(name = "inspect", aggregator = true)
@Execute(phase = LifecyclePhase.COMPILE)
public class MethodInspectorMojo extends AbstractMojo {

    @Parameter(property="output", defaultValue="${project.build.directory}/methods.json")
    private File _output;

    public File getOutput() {
        return _output;
    }

    public void setOutput(File output) {
        _output = output;
    }

    @Parameter(defaultValue = "${project}")
    private MavenProject _project;

    public MavenProject getProject() {
        return _project;
    }

    public void setProject(MavenProject project) {
        _project = project;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        try {
            saveToFile(collectFromProject(), _output);
        }
        catch (Throwable exc) {
            getLog().error(exc);
        }
    }

    @SuppressWarnings("unchecked")
    private Collection<MethodEntry> collectFromProject() throws IOException {
        if(_project.getPackaging() != "pom")
            return collectFromFolders(_project.getBuild().getOutputDirectory());

        Stream<String> paths = _project.getCollectedProjects()
                .stream()
                .map(p -> ((MavenProject)p).getBuild().getOutputDirectory());

        return collectFromFolders(paths.toArray(String[]::new));
    }

}


