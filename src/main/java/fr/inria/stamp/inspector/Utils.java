package fr.inria.stamp.inspector;

import org.objectweb.asm.Opcodes;

public class Utils {

    public static boolean isAccessibleToPackage(int access) {
        return ( access & ( Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED ) ) != 0;
    }

    public static boolean isDeprecated(int access) {
        return ( access & Opcodes.ACC_DEPRECATED ) != 0;
    }

    public static boolean isHashCode(String name, String desc) {
        return name.equals("hashCode") && desc.equals("()I");
    }

    private Utils () {}

}
