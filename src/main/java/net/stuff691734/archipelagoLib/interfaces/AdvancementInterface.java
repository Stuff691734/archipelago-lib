package net.stuff691734.archipelagoLib.interfaces;

import net.stuff691734.archipelagoLib.CheckType;

public interface AdvancementInterface extends DependencyInterface {
    /**
     * Returns the advancement instance used to create this object.
     * If possible try to avoid using this.
     * @return the advancement instance used to create this object.
     */
    Object getAdvancement();

    /**
     * Traces dependencies of this advancement and returns the root advancement.
     * @return the root advancement.
     */
    AdvancementInterface getRoot();

    /**
     * Returns the parent advancement to this advancement.
     * @return the parent advancement.
     */
    AdvancementInterface getParent();

    /**
     * Returns whether this advancement has a display element.
     * @return whether this advancement has a display element.
     */
    boolean hasDisplay();

    /**
     * Returns the type of this check.
     * @return {@link CheckType#ADVANCEMENT}
     */
    default CheckType checkType() {
        return CheckType.ADVANCEMENT;
    }

    /**
     * Returns whether this advancement should be hidden before access logic.
     * @return whether this advancement should be hidden before access logic.
     */
    boolean isHidden();

    /**
     * Updates the visibility of this advancement to prevent instances where the advancement is in logic but not shown.
     */
    void updateVisibility();

    /**
     * Returns whether this advancement is null.
     * @return whether this advancement is null.
     */
    boolean isNull();
}
