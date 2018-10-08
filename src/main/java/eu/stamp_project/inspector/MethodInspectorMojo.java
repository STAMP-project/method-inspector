package eu.stamp_project.inspector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Collection;

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

            Collection<MethodEntry> methods = ProjectInspector.getMethods(_project);

            try (Writer writer = new FileWriter(_output)) {
                Gson gson = new GsonBuilder().create();
                gson.toJson(methods, writer);
            }
        }
        catch (Throwable exc) {
            getLog().error(exc);
        }
    }

}
