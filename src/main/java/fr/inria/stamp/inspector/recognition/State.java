package fr.inria.stamp.inspector.recognition;

import java.util.LinkedList;
import java.util.List;

public class State {

    public static State SINK = new State();

    public State() {
        transitions = new LinkedList<Transition>();
    }

    public State next(int opcode) {
        for (Transition tr : transitions) {
            if (tr.accepts(opcode))
                return tr.getTarget();
        }

        return SINK;
    }

    public Transition moves(Transition tr) {
        transitions.add(tr);
        return tr;
    }

    public Transition movesWith(int opcode) {
        return moves(Transition.with(opcode));
    }

    private List<Transition> transitions;

    private boolean isFinal = false;

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public State that() { return this; }

    public static State finalState() {
        State state = new State();
        state.setFinal(true);
        return state;
    }
}
