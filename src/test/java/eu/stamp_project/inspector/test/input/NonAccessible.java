package eu.stamp_project.inspector.test.input;

public class NonAccessible {

    private NonAccessible() {}

    private void privateMethod() {}

    public class NestedPublic {

        private NestedPublic() {}

        private void anotherPrivateMethod () {}
    }

    protected class NestedProtected {

        private NestedProtected() {}

        private void yetAnother() {}
    }

    private static class NestedPrivate {

        public NestedPrivate() {}

        public void atLastAPublicOne() {}

    }
}
