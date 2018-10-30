package eu.stamp_project.inspector;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.pitest.reloc.asm.Opcodes.ASM5;

public class LineCounter extends MethodVisitor {

    public LineCounter() {
        this(ASM5);
    }

    public LineCounter(int api) {
        super(api);
    }

    private int firstLine = Integer.MAX_VALUE;

    private int lastLine = Integer.MIN_VALUE;

    public int getFirstLine() {
        return firstLine;
    }

    public int getLastLine() {
        return lastLine;
    }

    public boolean hasCounted() {
        return firstLine != Integer.MAX_VALUE && lastLine != Integer.MIN_VALUE;
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        firstLine = Integer.min(firstLine, line);
        lastLine = Integer.max(lastLine, line);

        super.visitLineNumber(line, start);
    }

}
