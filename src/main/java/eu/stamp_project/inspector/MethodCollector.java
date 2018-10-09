package eu.stamp_project.inspector;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.bytecode.analysis.MethodTree;
import org.pitest.classinfo.ClassName;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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
            entry.setClassName(className.getNameWithoutPackage().asInternalName()); //TODO: Check this
            entry.setPackageName(className.getPackage().asInternalName());
            MethodNode methodNode = methodTree.rawNode();
            entry.setName(methodNode.name);
            entry.setName(methodNode.desc);
            entry.setClassifications(classifier.classify(classTree, methodTree));

            methods.add(entry);
        }
    }


    public Collection<MethodEntry> getMethods() {
        return methods;
    }

}
