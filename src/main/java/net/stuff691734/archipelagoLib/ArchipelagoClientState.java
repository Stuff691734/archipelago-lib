package net.stuff691734.archipelagoLib;

import net.stuff691734.archipelagoLib.interfaces.StorageInterface;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ArchipelagoClientState implements StorageInterface {
    private final Set<String> checks;

    public ArchipelagoClientState() {
        this.checks = new HashSet<>();
    }

    public boolean hasCheck(String checkName) {
        return this.checks.contains(checkName);
    }

    public void addCheck(String checkName) {
        this.checks.add(checkName);
    }

    public void setChecks(String[] checks) {
        this.clear();
        this.checks.addAll(Arrays.asList(checks));
    }

    public void clear() {
        this.checks.clear();
    }
}
