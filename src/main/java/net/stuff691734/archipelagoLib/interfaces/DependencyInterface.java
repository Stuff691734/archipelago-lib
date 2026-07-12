package net.stuff691734.archipelagoLib.interfaces;

import net.stuff691734.archipelagoLib.CheckType;

public interface DependencyInterface {
    /**
     * Returns the page for this check.
     * @return the page for this check.
     */
    String getPage();

    /**
     * Returns the id of this check.
     * @return the id of this check.
     */
    String getId();

    /**
     * Returns the difficulty of this check.
     * @return the difficulty of this check.
     */
    String getDifficulty();

    /**
     * Returns whether this check has any dependencies.
     * @return whether this check has any dependencies.
     */
    boolean isRoot();

    /**
     * Returns the type of this check.
     * @return the type of this check.
     */
    CheckType checkType();

    /**
     * Returns this checks name.
     * @return this checks name.
     */
    String getName();

    /**
     * Returns this checks full name.
     * Formatted as 'type id (name)'
     * @return this checks full name.
     */
    default String getCheckName() {
        if (!this.getName().isEmpty()) {
            return this.checkType().addPrefix(String.format("%s (%s)", this.getId(), this.getName()));
        } else {
            return this.checkType().addPrefix(String.format("%s (%s)", this.getId(), this.getId()));
        }
    }
}
