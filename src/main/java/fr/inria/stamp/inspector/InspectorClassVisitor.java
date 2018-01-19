package fr.inria.stamp.inspector;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.inria.stamp.inspector.Utils.*;


public class InspectorClassVisitor extends ClassVisitor {

    //TODO: We could probably use only one instance of this class

    public InspectorClassVisitor() {
        super(Opcodes.ASM5);
    }

    private String packageName;

    private String className;

    private boolean isClassAccessible; // Whether the class is accessible to package elements or not.

    private boolean isClassDeprecated;

    private Pattern namePattern = Pattern.compile("(?<package>([^/]+/)*)(?<name>[^/]+)");

    @Override
    public void visit(int version, int access, String name, String signature, String supername, String[] interfaces) {

        Matcher matcher = namePattern.matcher(name);
        matcher.matches(); // O_o
        packageName = matcher.group("package");
        className = matcher.group("name");
        isClassAccessible = isAccessibleToPackage(access);
        isClassDeprecated = isDeprecated(access);

    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodEntry entry = new MethodEntry();

        methods.add(entry);

        entry.setPackageName(packageName);
        entry.setClassName(className);
        entry.setName(name);
        entry.setDescription(desc);

        if(isClassAccessible && isAccessibleToPackage(access))
            entry.addClassification(MethodClassification.ACCESSIBLE);

        if(isClassDeprecated || isDeprecated(access))
            entry.addClassification(MethodClassification.DEPRECATED);

        if(isHashCode(name, desc))
            entry.addClassification(MethodClassification.HASH_CODE);

        return new InspectorMethodVisitor(entry);
    }

    private List<MethodEntry> methods = new LinkedList<>();

    public List<MethodEntry> getMethods() {
        return methods;
    }

}
