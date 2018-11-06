package eu.stamp_project.inspector;

import org.objectweb.asm.Opcodes;
import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.bytecode.analysis.MethodTree;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import eu.stamp_project.mutationtest.descartes.stopmethods.StopMethodMatcher;
import static eu.stamp_project.mutationtest.descartes.stopmethods.StopMethodMatchers.*;
import static eu.stamp_project.mutationtest.descartes.stopmethods.StopMethodMatcher.forAccess;
import static eu.stamp_project.utils.Utils.isConstructor;
import static eu.stamp_project.inspector.MethodClassification.*;

public class MethodClassifier {

    private Map<MethodClassification, StopMethodMatcher> matchers;

    public MethodClassifier() {

        matchers = new HashMap<>();
        matchers.put(EMPTY, isEmptyVoid());
        matchers.put(HASH_CODE, isHashCode());
        matchers.put(SYNTHETIC, isSynthetic());
        matchers.put(TO_STRING, isToString());
        matchers.put(DELEGATION, isDelegate());
        matchers.put(DEPRECATED, isDeprecated());
        matchers.put(ENUM_METHOD, isEnumGenerated());
        matchers.put(SIMPLE_GETTER, isSimpleGetter());
        matchers.put(SIMPLE_SETTER, isSimpleSetter());
        matchers.put(RETURNS_NULL, returnsNull());
        matchers.put(RETURNS_CONSTANT, returnsAConstant());
        matchers.put(STATIC_INITIALIZER, isStaticInitializer());

        matchers.put(NATIVE, forAccess(Opcodes.ACC_NATIVE));
        matchers.put(ABSTRACT, forAccess(Opcodes.ACC_ABSTRACT));
        matchers.put(CONSTRUCTOR, (classTree, methodTree) -> isConstructor(methodTree.rawNode().name));
        matchers.put(ACCESSIBLE, (classTree, methodTree) ->
            isAccessibleToPackage(classTree.rawNode().access) &&
            isAccessibleToPackage(methodTree.rawNode().access)
        );
        matchers.put(ACCESSIBLE_CLASS, ((classTree, methodTree) -> isAccessibleToPackage(classTree.rawNode().access)));
    }

    public EnumSet<MethodClassification> classify(ClassTree classTree, MethodTree classNode) {
        EnumSet<MethodClassification> result = EnumSet.noneOf(MethodClassification.class);
        matchers.forEach( (classification, matcher) -> {
            if(matcher.matches(classTree, classNode))
                result.add(classification);
        });
        return result;
    }

    private static boolean isAccessibleToPackage(int access) {
        return ( access & ( Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED ) ) != 0;
    }


}
