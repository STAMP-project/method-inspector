package eu.stamp_project.inspector;

import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.LinkedList;

import static java.nio.file.FileVisitResult.*;


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

        MethodCollector collector = new MethodCollector();

        for(MavenProject target : projects) {
            Files.walkFileTree(
                    Paths.get(target.getBuild().getOutputDirectory()),
                    new FileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            if(!file.toString().endsWith(".class")) //BEWARE: endsWith method in Path objects does a different thing.
                                return CONTINUE;

                            collector.collectFrom(Files.readAllBytes(file));
                            return CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                            return CONTINUE;
                        }

                        @Override
                        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                            return CONTINUE;
                        }

                        @Override
                        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                            return CONTINUE;
                        }
                    });
        }
        return collector.getMethods();
    }

}
