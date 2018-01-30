package fr.inria.stamp.inspector;

import fr.inria.stamp.inspector.recognition.MethodSignatureClassifier;
import fr.inria.stamp.inspector.recognition.MethodSignatureRecognizer;
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

        namePattern = Pattern.compile("(?<package>([^/]+/)*)(?<name>[^/]+)");
        signatureClassifier = new MethodSignatureClassifier();

    }

    private String packageName;

    private String className;

    private boolean isClassAccessible; // Whether the class is accessible to package elements or not.

    private boolean isClassDeprecated;

    private Pattern namePattern;

    private MethodSignatureClassifier signatureClassifier;

    @Override
    public void visit(int version, int access, String name, String signature, String supername, String[] interfaces) {

        Matcher matcher = namePattern.matcher(name);
        matcher.matches(); // O_o
        packageName = matcher.group("package");
        className = matcher.group("name");
        isClassAccessible = isAccessibleToPackage(access);
        isClassDeprecated = isDeprecated(access);

        signatureClassifier.set(MethodClassification.ACCESSIBLE,
                (mName, mDesc, mAcc) -> isClassAccessible && Utils.isAccessibleToPackage(mAcc));

        signatureClassifier.set(MethodClassification.DEPRECATED,
                (mName, mDesc, mAcc) -> isClassDeprecated || Utils.isDeprecated(mAcc) );

    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodEntry entry = new MethodEntry();

        methods.add(entry);

        entry.setPackageName(packageName);
        entry.setClassName(className);
        entry.setName(name);
        entry.setDescription(desc);

        entry.getClassifications().addAll(signatureClassifier.classify(name, desc, access));

        return new InspectorMethodVisitor(entry);
    }

    private List<MethodEntry> methods = new LinkedList<>();

    public List<MethodEntry> getMethods() {
        return methods;
    }

}
