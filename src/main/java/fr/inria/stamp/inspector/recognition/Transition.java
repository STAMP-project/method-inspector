package fr.inria.stamp.inspector.recognition;


import jdk.nashorn.internal.runtime.regexp.joni.constants.OPCode;
import org.objectweb.asm.Opcodes;

public abstract class Transition {

    public Transition () { }

    public Transition(State target) {
        to(target);
    }

    public Transition(State origin, State target) {
        from(origin); to(target);
    }

    public abstract boolean accepts(int opcode);

    private State origin = State.SINK;

    public State getOrigin() { return origin; }

    public State from(State state) {
        return origin = state;
    }

    private State target = State.SINK;

    public State getTarget() { return target; }

    public State to(State state) {
        return target = state;
    }

    public static Transition with(int opcode) {
        return new Transition() {
            @Override
            public boolean accepts(int op) {
                return opcode == op;
            }
        };
    }

    public static Transition withOpcodeBetween(int lower, int upper) {

        return new Transition() {
            @Override
            public boolean accepts(int opcode) {
                return opcode >= lower && opcode <= upper;
            }
        };

    }

    public static Transition withConstantOnStack() {
        return withOpcodeBetween(1, 20);
    }

    public static Transition withXLOAD() {
        return withOpcodeBetween(Opcodes.ILOAD, Opcodes.ALOAD);
    }

    public static Transition withXReturn() {
        return withOpcodeBetween(Opcodes.IRETURN, Opcodes.RETURN);
    }

    public static Transition withLocalVariable() {
        return withOpcodeBetween(21, 45);
    }

    public static Transition withInstanceMethodInvocation() {

        return new Transition() {
            @Override
            public boolean accepts(int opcode) {
                return opcode == Opcodes.INVOKEVIRTUAL ||
                        opcode == Opcodes.INVOKESPECIAL ||
                        opcode == Opcodes.INVOKEINTERFACE;
            }
        };

    }

}
