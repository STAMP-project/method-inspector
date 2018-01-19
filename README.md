# Method Inspector

**Method Inspector** is a simple Maven plugin that classifies each method in a project according to its metadata and
structure as:

- Empty
    ```Java
        public void method() {}
    ```
- Returns a constant
    ```Java
        public int getThree() { return 3; }
    ```
- Hash Code
    ```Java
        public int hashCode() {
            //Regular hashCode method
            ...
        }
    ```
- Accessible to elements of the same package. (Any public or protected method inside a public or protected class)
- Delegation pattern
    ```
    public class A {

        B b;

        ...

        public int mA(String arg) {
            return b.mB(arg);
        }
        ...
    }
    ```
- Deprecated. (Any method annotated as `@Deprecated` or any method inside a class annotated as `@Deprecated` as well.)
- Simple getter
    ```
    public class Ac {
        int a;
        ...
        public getA() {
            return a;
        }
        ...
    }
    ```
- Simple setter
    ```
    public class Ac {
        int a;
        ...
        public setA(int a) {
            this.a = a;
        }
        ...
    }
    ```

The information gathered is stored in a JSON file that contains an entry for every method inspected in a project.
It works on Maven multi-module projects. Test classes and methods are not considered in the inspection.

To use, simply run the following command in the project's directory.
```
mvn fr.inria.stamp.plugins.inspector:method-inspector:inspect
```

