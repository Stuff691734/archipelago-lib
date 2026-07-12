package net.stuff691734.archipelagoLib.interfaces;

import net.stuff691734.archipelagoLib.CheckType;

public interface DependencyInterface {
    String getPage();

    String getId();

    String getDifficulty();

    boolean isRoot();

    CheckType checkType();

    String getName();

    default String getCheckName() {
        if (!this.getName().isEmpty()) {
            return this.checkType().addPrefix(String.format("%s (%s)", this.getId(), this.getName()));
        } else {
            return this.checkType().addPrefix(String.format("%s (%s)", this.getId(), this.getId()));
        }
    }
}
