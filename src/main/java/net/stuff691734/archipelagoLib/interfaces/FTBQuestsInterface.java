package net.stuff691734.archipelagoLib.interfaces;

import net.stuff691734.archipelagoLib.CheckType;

import java.util.List;
import java.util.stream.Stream;

public interface FTBQuestsInterface extends DependencyInterface {
    /**
     * Returns a stream of this quest's dependencies.
     * Normalizing various dependencies to their quest representations.
     * @return a stream of this quest's dependencies.
     */
    Stream<List<FTBQuestsInterface>> getDependencies();

    /**
     * Returns a list of this quests advancement dependencies.
     * @return a list of this quests advancement dependencies.
     */
    List<String> getAdvancementDependencies();

    /**
     * Returns the minimum dependencies completed to finish this quest, 0 defaults to using single dependency or all required.
     * @return the minimum dependencies completed to finish this quest.
     */
    int getMinimumDependencies();

    /**
     * Returns whether this quest needs all dependencies completed or only one.<br>
     * To be used after checking {@link FTBQuestsInterface#getMinimumDependencies()}
     * @return whether this quest needs all dependencies completed or only one.
     */
    boolean hasSingleDependencyRequirement();

    /**
     * Returns the type of this check.
     * @return {@link CheckType#FTB_QUEST}
     */
    default CheckType checkType() {
        return CheckType.FTB_QUEST;
    }

    /**
     * Returns the name of this quest's chapter.
     * @return the name of this quest's chapter.
     */
    String getChapterName();

    /**
     * A client sided implentation of the {@link FTBQuestsInterface#getName(ServerInterface)} method.
     * Returns the name of the quest.
     * @return the name of the quest.
     */
    String getName();

    /**
     * A server sided implementation of the {@link FTBQuestsInterface#getName()} method.
     * Returns the name of the quest.
     * @param server the server used to get information about certain tasks.
     * @return the name of the quest.
     */
    String getName(ServerInterface server);

    /**
     * Returns this quest chapter's full name.
     * Formatted as 'type id (name)'
     * @return this quest chapter's full name.
     */
    default String getChapterCheckName() {
        return this.checkType().addPrefix(String.format("%s (%s)", this.getPage(), this.getChapterName()));
    }

    /**
     * A client sided implementation of the {@link FTBQuestsInterface#getCheckName(ServerInterface)} method.
     * Returns the full name of the quest.
     * Formatted as 'type id (name)'
     * @return the full name of the quest.
     */
    default String getCheckName() {
        if (!this.getName().isEmpty()) {
            return this.checkType().addPrefix(String.format("%s (%s)", this.getId(), this.getName()));
        } else {
            return this.checkType().addPrefix(String.format("%s (%s)", this.getId(), this.getId()));
        }
    }

    /**
     * A server sided implementation of the {@link FTBQuestsInterface#getCheckName()} method.
     * Returns the full name of the quest.
     * Formatted as 'type id (name)'
     * @param server the server used to get information about certain tasks.
     * @return the full name of the quest.
     */
    default String getCheckName(ServerInterface server) {
        if (!this.getName(server).isEmpty()) {
            return this.checkType().addPrefix(String.format("%s (%s)", this.getId(), this.getName(server)));
        } else {
            return this.checkType().addPrefix(String.format("%s (%s)", this.getId(), this.getId()));
        }
    }
}
