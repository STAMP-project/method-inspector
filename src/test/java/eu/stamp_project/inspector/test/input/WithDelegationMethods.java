package eu.stamp_project.inspector.test.input;

import java.io.PrintStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class WithDelegationMethods {


    Integer integer = 3;
    Integer other = 4;

    MethodHandle mh;

    public WithDelegationMethods() {

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            mh = lookup.findVirtual(PrintStream.class, "println", MethodType.methodType(String.class));
        }catch(Throwable exc) {

        }
    }

    public void delegateToStatic(String name) {
        System.out.println(name);
    }

    public void delegateToStatic() {
        System.out.println();
    }


    public String delegateToFieldMethod() {
        return integer.toString();
    }


    //TODO: To fix this we'll have to account for every type transformation
    public int failsDueToConversionMethod(int x) {
        return other.compareTo(x);
    }


    public int delegateToFieldMethod2(Integer x) {
        return integer.compareTo(x);
    }



    public static void target() {
        String a = "";
        String b = "1";
        System.out.println(a);

    }

    public static void target(String c) {
        String a = "";
        String b = "1";
        System.out.println(a + b + c);
    }


    public void delegateWithoutParameters() {
        target();
    }


    public String delegateWithMoreArguments(String format, Object[] params) {
        return String.format(format, params);
    }


    //TODO: Is this delegation?
    public int delegateApply(Integer a, Integer b) {
        return a.compareTo(b);
    }


    //TODO: Is this delegation?
    public int getLength() { return "a".length(); }



    public void delegateToHandle(PrintStream out, String message) throws Throwable {
        mh.invoke(out, message);
    }

}
