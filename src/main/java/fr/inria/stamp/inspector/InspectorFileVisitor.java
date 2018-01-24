package fr.inria.stamp.inspector;

import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;

public class InspectorFileVisitor implements FileVisitor<Path> {
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

        if(!file.toString().endsWith(".class")) //BEWARE: endsWith method in Path objects does a different thing.
            return FileVisitResult.CONTINUE;

        ClassReader reader = new ClassReader(Files.readAllBytes(file));
        InspectorClassVisitor visitor = new InspectorClassVisitor();
        reader.accept(visitor, 0);
        methods.addAll(visitor.getMethods());

        return FileVisitResult.CONTINUE;
    }


    private List<MethodEntry> methods = new LinkedList<>();

    public List<MethodEntry> getMethods() {
        return methods;
    }

    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
    }
}
