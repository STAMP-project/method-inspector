package eu.stamp_project.inspector.test;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.pitest.bytecode.analysis.ClassTree;

import java.io.IOException;
import java.io.InputStream;

public class Utils {

    public static ClassTree getTreeFor(Class<?> clazz) throws IOException {
        String name = clazz.getName().replace('.', '/') + ".class";
        InputStream stream = clazz.getClassLoader().getResourceAsStream(name);
        return getTreeFor(new ClassReader(stream));
    }

    public static ClassTree getTreeFor(String className) throws  IOException {
        return getTreeFor(new ClassReader(className));
    }

    private static ClassTree getTreeFor(ClassReader reader) {
        ClassNode node = new ClassNode();
        reader.accept(node, 0);
        return new ClassTree(node);
    }
}
