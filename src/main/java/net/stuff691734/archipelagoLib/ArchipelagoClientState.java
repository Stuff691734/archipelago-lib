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

    @Override
    public boolean hasCheck(String checkName) {
        return this.checks.contains(checkName);
    }

    /**
     * Adds a check to the list of received checks.
     * @param checkName the name of the check to add.
     */
    public void addCheck(String checkName) {
        this.checks.add(checkName);
    }

    /**
     * Sets the list of received checks.
     * @param checks the checks for the new list to have
     */
    public void setChecks(String[] checks) {
        this.clear();
        this.checks.addAll(Arrays.asList(checks));
    }

    /**
     * Clears the list of received checks.
     */
    public void clear() {
        this.checks.clear();
    }
}
