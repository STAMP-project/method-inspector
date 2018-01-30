package fr.inria.stamp.inspector.recognition;

import org.objectweb.asm.Opcodes;

import static fr.inria.stamp.inspector.recognition.State.SINK;
import static fr.inria.stamp.inspector.recognition.State.finalState;
import static fr.inria.stamp.inspector.recognition.Transition.*;

public class MethodBodyRecognizer {

    public MethodBodyRecognizer(State initialState) {
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

    public static MethodBodyRecognizer returnsConstant() {

        State initial = new State();

        initial.moves(withConstantOnStack()).to(new State())
                .that().moves(withXReturn()).to(finalState());

        return new MethodBodyRecognizer(initial);
    }

    public static MethodBodyRecognizer simpleSetter() {

        State initial = new State();

        initial.movesWith(Opcodes.ALOAD).to(new State())
                .that().moves(withXLOAD()).to(new State())
                .that().movesWith(Opcodes.PUTFIELD).to(new State())
                .that().movesWith(Opcodes.RETURN).to(finalState());

        return new MethodBodyRecognizer(initial);
    }

    public static MethodBodyRecognizer simpleGetter() {
        State initial = new State();

        initial.movesWith(Opcodes.ALOAD).to(new State())
                .that().movesWith(Opcodes.GETFIELD).to(new State())
                .that().moves(withXReturn()).to(finalState());

        return new MethodBodyRecognizer(initial);
    }

    public static MethodBodyRecognizer emptyMethod() {

        State initial = new State();

        initial.movesWith(Opcodes.RETURN).to(finalState());

        return new MethodBodyRecognizer(initial);

    }

    /*
        Recognizes methods delegating to a method of an object in a local or static variable
     */
    public static MethodBodyRecognizer instanceDelegation() {
        State initial = new State();
        State thisOnStack = new State();
        State targetLoaded = new State();
        State paramLoop = new State();
        State methodInvoked = new State();
        State end = State.finalState();


        initial.movesWith(Opcodes.ALOAD).to(thisOnStack); //For some reason, here we get ALOAD instead of ALOAD_0

        initial.movesWith(Opcodes.GETSTATIC).to(targetLoaded);


        thisOnStack.movesWith(Opcodes.GETFIELD).to(targetLoaded);
        thisOnStack.moves(withLocalVariable()).to(paramLoop);
        thisOnStack.moves(withInstanceMethodInvocation()).to(methodInvoked);


        targetLoaded.moves(withLocalVariable()).to(paramLoop);
        targetLoaded.moves(withInstanceMethodInvocation()).to(methodInvoked);

        paramLoop.moves(withLocalVariable()).to(paramLoop);
        paramLoop.moves(withInstanceMethodInvocation()).to(methodInvoked);

        methodInvoked.moves(withXReturn()).to(end);

        return new MethodBodyRecognizer(initial);
    }

    public static MethodBodyRecognizer staticDelegation() {

        State initial = new State();
        State paramLoop = new State();
        State methodInvoked = new State();
        State end = State.finalState();

        initial.moves(withLocalVariable()).to(paramLoop);
        initial.movesWith(Opcodes.INVOKESTATIC).to(methodInvoked);
        paramLoop.moves(withLocalVariable()).to(paramLoop);
        paramLoop.movesWith(Opcodes.INVOKESTATIC).to(methodInvoked);
        methodInvoked.moves(withXReturn()).to(end);

        return new MethodBodyRecognizer(initial);

    }



}
