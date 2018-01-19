package fr.inria.stamp.inspector.recognition;


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

    public static Transition withConstantOnStack() {
        return new Transition() {
            @Override
            public boolean accepts(int opcode) {
                return opcode > 0 && opcode <= 20;
            }
        };
    }

    public static Transition withXLOAD() {
        return new Transition() {
            @Override
            public boolean accepts(int opcode) {
                return opcode >= Opcodes.ILOAD && opcode <= Opcodes.ALOAD;
            }
        };
    }

    public static Transition withXReturn() {
        return new Transition() {
            @Override
            public boolean accepts(int opcode) {
                return opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN;
            }
        };
    }

}
