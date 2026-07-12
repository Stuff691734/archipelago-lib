package net.stuff691734.archipelagoLib.interfaces;

import net.stuff691734.archipelagoLib.CheckType;

import java.util.List;
import java.util.stream.Stream;

public interface FTBQuestsInterface extends DependencyInterface {
    Stream<List<FTBQuestsInterface>> getDependencies();

    List<String> getAdvancementDependencies();

    int getMinimumDependencies();

    boolean hasSingleDependencyRequirement();

    default CheckType checkType() {
        return CheckType.FTB_QUEST;
    }

    String getChapterName();

    // server side getName.
    String getName(ServerInterface server);

    default String getChapterCheckName() {
        return this.checkType().addPrefix(String.format("%s (%s)", this.getPage(), this.getChapterName()));
    }

    default String getCheckName(ServerInterface server) {
        if (!this.getName(server).isEmpty()) {
            return this.checkType().addPrefix(String.format("%s (%s)", this.getId(), this.getName(server)));
        } else {
            return this.checkType().addPrefix(String.format("%s (%s)", this.getId(), this.getId()));
        }
    }
}
