package eu.stamp_project.inspector.test.input;

public class ReturningConstants {

    public int getOne() { return -1; }

    public short getTwo() { return (short)2; }

    public byte getThree() { return 3; }

    public char getA() { return 'A'; }

    public float getFour() { return 4.f; }

    public String getFive() { return "five"; }

    //TODO: It would be nice to detect this kind of method.
    // public int[] getEmptyArray() { return new int[0]; }

}
