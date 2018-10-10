# Method Inspector

**Method Inspector** is a simple Maven plugin that classifies each method in a project according to its metadata and
structure as:

- Empty
    ```Java
        public void method() {}
    ```
- Native
    ```Java
    public static native void nativeMethod();
    ```
- Abstract (Any abstract method in abstract classes or methods with no default implementation in interfaces.)
- Hash Code
    ```Java
        public int hashCode() {
            //Regular hashCode method
            ...
        }
    ```
- Synthetic (Compiler generated methods marked as such)
- To string methods
    ```
    @Override
    public String toString() {...}
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
- Constructor
- Enum methods. (Compiler generated methods `values` and `valueOf` to support enum types. These are not marked as synthetic.)
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
- Returns a constant
    ```Java
        public int getThree() { return 3; }
    ```
- Returns null (As a specialization of the previous class)
    ```Java
    public Object getNull() { return null; }
    ```
- Static initializers
    ```Java
    class A {
        static {
            ...
        }
    }
    ```
The information gathered is stored in a JSON file that contains an entry for every method inspected in a project.
It works on Maven multi-module projects. Test classes and methods are not considered in the inspection.

To use, simply run the following command in the project's directory.
```
mvn fr.inria.stamp.plugins.inspector:method-inspector:inspect
```

The main purpose of this project is to support the experimentation with [Descartes](https://github.com/STAMP-project/pitest-descartes)
Most of these categories are used to discover methods which are not generally targeted by developers in their test suites.
