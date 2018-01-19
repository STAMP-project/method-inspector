package fr.inria.stamp.inspector;

import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedList;

public class ProjectInspector {


    @SuppressWarnings("unchecked")
    public static Collection<MethodEntry> getMethods(MavenProject project) throws IOException {
        Collection<MavenProject> projects = new LinkedList<>();
        if (project.getPackaging().equals("pom"))

            projects.addAll(project.getCollectedProjects());
        else
            projects.add(project);
        return getMethods(projects);
    }

    public static Collection<MethodEntry> getMethods(Collection<MavenProject> projects) throws IOException {
        InspectorFileVisitor visitor = new InspectorFileVisitor();
        for(MavenProject target : projects) {
            Files.walkFileTree(Paths.get(target.getBuild().getOutputDirectory()), visitor);
        }
        return visitor.getMethods();
    }

}
