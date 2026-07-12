package net.stuff691734.archipelagoLib;

import java.util.ArrayList;
import java.util.stream.Stream;

public class DependencyNotation {
    // all values at same layer are treated the same if no minimum specified assume and
    public int minimum;
    public ArrayList<DependencyNotation> nested;
    public ArrayList<String> checks;

    public DependencyNotation() {
        this.minimum = 0;
        this.nested = new ArrayList<>();
        this.checks = new ArrayList<>();
    }

    public DependencyNotation(String check) {
        this();
        if (check != null) {
            this.checks.add(check);
        }
    }

    public DependencyNotation(Stream<String> checks) {
        this();
        checks.forEach(this.checks::add);
    }

    public void addCheck(String check) {
        this.checks.add(check);
    }

    public void addNested(DependencyNotation dependency) {
        this.nested.add(dependency);
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }

    public boolean isEmpty() {
        return this.checks.isEmpty() && this.nested.isEmpty();
    }
}
