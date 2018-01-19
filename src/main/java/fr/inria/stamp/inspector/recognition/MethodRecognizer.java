package fr.inria.stamp.inspector.recognition;

import org.objectweb.asm.Opcodes;

import static fr.inria.stamp.inspector.recognition.State.SINK;
import static fr.inria.stamp.inspector.recognition.State.finalState;
import static fr.inria.stamp.inspector.recognition.Transition.*;

public class MethodRecognizer {

    public MethodRecognizer(State initialState) {
        current = initial = initialState;
    }

    private State initial;

    public State getInitialState() {
        return initial;
    }

    private State current;

    public State getCurrentState() {
        return current;
    }

    public boolean isInFinalState () {
        return current.isFinal();
    }

    public State advance(int opcode) {
        return current = current.next(opcode);
    }

    public void reset() {
        current = initial;
    }

    public void stop() {
        current = SINK;
    }

    public static MethodRecognizer returnsConstant() {

        State initial = new State();

        initial.moves(withConstantOnStack()).to(new State())
                .that().moves(withXReturn()).to(finalState());

        return new MethodRecognizer(initial);
    }

    public static MethodRecognizer simpleSetter() {

        State initial = new State();

        initial.movesWith(Opcodes.ALOAD).to(new State())
                .that().moves(withXLOAD()).to(new State())
                .that().movesWith(Opcodes.PUTFIELD).to(new State())
                .that().movesWith(Opcodes.RETURN).to(finalState());

        return new MethodRecognizer(initial);
    }

    public static MethodRecognizer simpleGetter() {
        State initial = new State();

        initial.movesWith(Opcodes.ALOAD).to(new State())
                .that().movesWith(Opcodes.GETFIELD).to(new State())
                .that().moves(withXReturn()).to(finalState());

        return new MethodRecognizer(initial);
    }

    public static MethodRecognizer emptyMethod() {

        State initial = new State();

        initial.movesWith(Opcodes.RETURN).to(finalState());

        return new MethodRecognizer(initial);

    }

}
