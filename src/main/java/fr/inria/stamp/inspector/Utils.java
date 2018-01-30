package fr.inria.stamp.inspector;

import org.objectweb.asm.Opcodes;

public class Utils {

    public static boolean isAccessibleToPackage(int access) {
        return ( access & ( Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED ) ) != 0;
    }

    public static boolean isDeprecated(int access) {
        return ( access & Opcodes.ACC_DEPRECATED ) != 0;
    }

    //TODO: hashCode and toString are very similar. If a simimlar third category appears, refactor the two methods into a class
    // that classifies methods according to their name, description and access.

    public static boolean isHashCode(String name, String desc) {
        return name.equals("hashCode") && desc.equals("()I");
    }

    public static boolean isToString(String name, String desc) {
        return name.equals("toString") && desc.equals("()Ljava/lang/String;");
    }

    private Utils () {}

}
