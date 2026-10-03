package net.stuff691734.archipelagoLib.interfaces;

import java.util.List;

public interface BetterQuestingInterface extends DependencyInterface {
    default String getChapterCheckName() {
        return this.checkType().addPrefix(String.format("%s (%s)", this.getPage(), this.getChapterName()));
    }

    String getChapterName();

    // -1 NAND/NOR/XNOR 0 AND 1 OR/XOR
    int getMinimumDependencies();

    /**
     * Returns a list of this quest's advancement dependencies.
     * @return a list of this quest's advancement dependencies.
     */
    List<String> getAdvancementDependencies();

    /**
     * Returns a list of this quest's dependencies.
     * @return a list of this quest's dependencies.
     */
    List<BetterQuestingInterface> getDependencies();

    /**
     * Returns whether a quest is hidden from the user.
     * @return whether a quest is hidden from the user.
     */
    boolean isHidden();
}
