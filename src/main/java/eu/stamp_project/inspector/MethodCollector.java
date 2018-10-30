package eu.stamp_project.inspector;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.bytecode.analysis.MethodTree;
import org.pitest.classinfo.ClassName;

import javax.sound.sampled.Line;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static java.nio.file.FileVisitResult.CONTINUE;

public class MethodCollector {

    private List<MethodEntry> methods = new LinkedList<>();
    private MethodClassifier classifier = new MethodClassifier();

    public void collectFrom(byte[] rawClass) {
        ClassNode classNode = new ClassNode();
        ClassReader reader = new ClassReader(rawClass);
        reader.accept(classNode, 0);
        collectFrom(classNode);
    }

    public void collectFrom(ClassNode classNode) {
        ClassTree classTree = new ClassTree(classNode);

        for (MethodTree methodTree : classTree.methods()) {
            ClassName className = classTree.name();

            MethodEntry entry = new MethodEntry();
            entry.setClassName(className.getNameWithoutPackage().asInternalName());
            entry.setPackageName(className.getPackage().asInternalName());
            MethodNode methodNode = methodTree.rawNode();

            LineCounter counter = new LineCounter();
            methodNode.accept(counter);

            if(counter.hasCounted()) {
                entry.setFirstLine(counter.getFirstLine());
                entry.setLastLine(counter.getLastLine());
            }

            entry.setName(methodNode.name);
            entry.setDescription(methodNode.desc);
            entry.setClassifications(classifier.classify(classTree, methodTree));

            methods.add(entry);
        }
    }

    public Collection<MethodEntry> getMethods() {
        return methods;
    }

    public static Collection<MethodEntry> collectFromFolders(String... paths) throws IOException {
        MethodCollector collector = new MethodCollector();
        for(String path : paths) {
            Files.walkFileTree(
                    Paths.get(path),
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
