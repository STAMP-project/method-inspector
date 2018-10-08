package eu.stamp_project.inspector.test.input;

public enum ComplexEnum {

    VALUE1(0),
    VALUE2(1),
    VALUE3(2);

    private int value;

    ComplexEnum(int value) {
        this.value = value;
    }

    public int getValue() { return value; }

}
