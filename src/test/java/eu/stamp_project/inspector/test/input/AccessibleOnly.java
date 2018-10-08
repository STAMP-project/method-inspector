package eu.stamp_project.inspector.test.input;

public class AccessibleOnly {

    public void nonEmptyMethod() {
        int a = 3;
        System.out.println(3);
    }

    public int[] getVector() {
        return new int[3];
    }

    public int follow(int lookahed) {
        switch (lookahed) {
            case 0:
                return 1;
            case 1:
                return 2;
            case 2:
                return 3;
            case 3:
                return 4;
            case 4:
                return 5;
            default:
                return 0;

        }
    }

    public int digits(int number) {
        switch (number) {
            case 1:
                return 0;
            case 10:
                return 1;
            case 100:
                return 2;
            case 1000:
                return 3;
            default:
                return -1;
        }
    }

    public int sum(int count) {
        int result = 0;
        for(int i = 0; i < count; i++, result += i);
        return result;
    }

    protected boolean isString(Object obj) { return obj instanceof String; }


    public int[][] eye(int dimension) {
        int[][] result = new int[dimension][dimension];
        for(int i = 0; i< dimension; i++)
            result[i][i] = 0;
        return result;
    }

    protected void fireAndCatch() {
        try {
            throw new Exception();
        }
        catch (Throwable exc) {

        }
    }

}
