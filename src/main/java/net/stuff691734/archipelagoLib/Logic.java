package net.stuff691734.archipelagoLib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.stuff691734.archipelagoLib.interfaces.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Logic {
    private final StorageInterface state;
    private final SlotData slotData;

    public Logic(StorageInterface state, SlotData slotData) {
        this.state = state;
        this.slotData = slotData;
    }

    /**
     * Returns whether an advancement should get shown in a gui.
     * @param advancement the advancement to check against.
     * @return whether an advancement should get shown in a gui.
     */
    public boolean shouldShowAdvancement(AdvancementInterface advancement) {
        if (advancement.hasDisplay()) {
            if (!this.slotData.isInitiated) {
                return true;
            }
            if (
                !this.slotData.activated_modules.contains("Advancements") ||
                !this.slotData.advancement_difficulty.contains(advancement.getDifficulty())
            ) {
                return false;
            }
            if (this.slotData.roots_unlocked && advancement.isRoot()) {
                return true;
            }
            if (Objects.equals(this.slotData.unlock_type, "tab")) {
                return this.state.hasCheck(advancement.checkType().addPrefix(advancement.getPage()));
            }
            else if (Objects.equals(this.slotData.unlock_type, "tree")) {
                if (advancement.isRoot()) {
                    return this.state.hasCheck(advancement.checkType().addPrefix(advancement.getId()));
                } else {
                    AdvancementInterface checkAdvancement = advancement.getParent();
                    while (!checkAdvancement.isNull()) {
                        String checkAdvancementName = checkAdvancement.getId();
                        if (!this.state.hasCheck(advancement.checkType().addPrefix(checkAdvancementName))) {
                            return false;
                        }
                        checkAdvancement = checkAdvancement.getParent();
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns whether the player should be able to complete this advancement.
     * @param advancement the advancement to determine if the player can complete.
     * @return whether the player should be able to complete this advancement.
     */
    public boolean isAdvancementCompletable(AdvancementInterface advancement) {
        if (this.slotData.isInitiated) {
            if (
                !this.slotData.activated_modules.contains("Advancements") ||
                !this.slotData.advancement_difficulty.contains(advancement.getDifficulty())
            ) {
                return true;
            }

            if (this.slotData.roots_unlocked && advancement.isRoot()) {
                return true;
            }
            if (Objects.equals(this.slotData.unlock_type, "tab")) {
                return this.state.hasCheck(advancement.checkType().addPrefix(advancement.getPage()));
            }
            else if (Objects.equals(this.slotData.unlock_type, "tree")) {
                if (advancement.isRoot()) {
                    return this.state.hasCheck(advancement.checkType().addPrefix(advancement.getId()));
                } else {
                    AdvancementInterface checkAdvancement = advancement.getParent();
                    while (!checkAdvancement.isNull()) {
                        String checkAdvancementName = checkAdvancement.getId();
                        if (!this.state.hasCheck(advancement.checkType().addPrefix(checkAdvancementName))) {
                            return false;
                        }
                        checkAdvancement = checkAdvancement.getParent();
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Return whether a connection should be drawn between two advancements.
     * @param parent the advancement gui element at the beginning of the connection, for preventing connections to non-visible advancements.
     * @param advancement the advancement at the end of the connection.
     * @return null if no connection should be drawn otherwise returns parent.
     */
    public <T> T isDependencyDrawn(T parent, AdvancementInterface advancement) {
        if (parent == null) return null;
        if (!this.slotData.isInitiated) {
            return parent;
        }
        if (
            this.slotData.activated_modules.contains("Advancements") &&
            !this.slotData.advancement_difficulty.contains(advancement.getDifficulty())
        ) {
            return null;
        }
        else if (!this.slotData.activated_modules.contains("Advancements")) {
            if (advancement.isHidden()) {
                return parent;
            }
            return null;
        }
        if (this.shouldShowAdvancement(advancement)) {
            return parent;
        }
        return null;
    }

    /**
     * Returns whether a tab should be drawn.
     * @param tab the tab to prevent trying to show a non-existent tab.
     * @param advancement the root advancement of the tab.
     * @return null if the tab should not be drawn otherwise returns tab.
     */
    public <T> T isTabDrawn(T tab, AdvancementInterface advancement) {
        if (tab == null) return null;
        if (!this.slotData.isInitiated) {
            return tab;
        }
        if (!this.slotData.activated_modules.contains("Advancements") || this.slotData.roots_unlocked) {
            return tab;
        }
        if (this.state.hasCheck(advancement.checkType().addPrefix(advancement.getId()))) {
            return tab;
        }
        return null;
    }

    /**
     * Returns whether the quest should be completable.
     * @param quest the quest to check.
     * @param original whether the quest is completable before checking against these restrictions.
     * @return original when this logic allows it to be completed and false otherwise.
     */
    public boolean isFTBQuestCompletable(FTBQuestsInterface quest, boolean original) {
        if (this.slotData.isInitiated) {
            if (
                !this.slotData.activated_modules.contains("FTBQuests") ||
                !this.slotData.ftb_quest_shape.contains(quest.getDifficulty())
            ) {
                return original;
            }

            if (this.slotData.roots_unlocked && quest.isRoot()) {
                return original;
            }

            if (Objects.equals(this.slotData.unlock_type, "tab")) {
                if (state.hasCheck(quest.checkType().addPrefix(quest.getPage()))) {
                    return original;
                }
            } else if (Objects.equals(this.slotData.unlock_type, "tree")) {
                if (quest.isRoot()) {
                    if (this.hasRequiredChecks(Collections.singletonList(quest))) {
                        return original;
                    }
                } else {
                    if (quest.getMinimumDependencies() != 0) {
                        if (quest.getDependencies().filter(this::hasRequiredChecks).count() >= quest.getMinimumDependencies()) {
                            return original;
                        }
                    } else if (quest.hasSingleDependencyRequirement()) {
                        if (quest.getDependencies().anyMatch(this::hasRequiredChecks)) {
                            return original;
                        }
                    } else {
                        if (quest.getDependencies().allMatch(this::hasRequiredChecks)) {
                            return original;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns whether the player has all of a list of ftb quest dependencies.
     * @param requiredChecks a list of ftb quest dependencies to check against.
     * @return whether the player has all of a list of ftb quest dependencies.
     */
    private boolean hasRequiredChecks(List<FTBQuestsInterface> requiredChecks) {
        for (DependencyInterface requiredCheck : requiredChecks) {
            if (!this.state.hasCheck(requiredCheck.checkType().addPrefix(requiredCheck.getId()))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether the player should be able to claim rewards for this check.
     * @param quest the quest to check if they can claim rewards for.
     * @param original whether the rewards are claimable before checking this logic.
     * @return whether the player should be able to claim rewards for this check.
     */
    public boolean isFTBQuestRewardObtained(FTBQuestsInterface quest, boolean original) {
        if (this.slotData.isInitiated) {
            if (
                this.slotData.activated_modules.contains("FTBQuests") &&
                this.slotData.ftb_quest_shape.contains(quest.getDifficulty()) &&
                this.slotData.quest_checks_give_rewards
            ) {
                return state.hasCheck(quest.checkType().addPrefix(quest.getId()));
            }
            return original;
        }
        return false;
    }

    /**
     * Returns a map of all advancements to their details.
     * @param server the server to get advancements from.
     * @param removeHidden whether to remove hidden advancements (unused)
     * @return a map of all advancements to their details.
     */
    private Map<String, Check> generateAdvancementChecks(ServerInterface server, boolean removeHidden) {
        Map<String, Check> checks = new HashMap<>();

        for (AdvancementInterface advancement : server.getAllAdvancements()) {

            if (advancement.hasDisplay()) {
                AdvancementInterface parent = advancement.getParent();
                String parent_id = null;
                if (!parent.isNull() && parent.hasDisplay()) {
                    parent_id = parent.getCheckName();
                }

                if (parent_id == null || !parent_id.equals("minecraft:recipes/root")) {
                    checks.put(advancement.getCheckName(), new Check(
                            advancement.getDifficulty(),
                            new DependencyNotation(parent_id),
                            advancement.getRoot().getCheckName()
                    ));
                }
            }
        }
        return checks.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, // use first instance when dealing with conflicts
                        LinkedHashMap::new
                ));
    }

    /**
     * Returns a map of all ftb quests to their details.
     * @param server the server to get quests from.
     * @param removeHidden whether to remove hidden quests.
     * @return a map of all ftb quests to their details.
     */
    private Map<String, Check> generateFTBQuestChecks(ServerInterface server, boolean removeHidden) {
        Map<String, Check> ftbQuestsChecks = new HashMap<>();

        for (FTBQuestsInterface quest : server.getAllFTBQuests()) {
            DependencyNotation fullDependency = new DependencyNotation();
            DependencyNotation questDependencies = new DependencyNotation();
            if (quest.getMinimumDependencies() > 0) {
                questDependencies.setMinimum(quest.getMinimumDependencies());
            } else if (quest.hasSingleDependencyRequirement()) {
                questDependencies.setMinimum(1);
            } else {
                questDependencies.setMinimum(0);
            }
            Stream<List<FTBQuestsInterface>> dependencies = quest.getDependencies();

            List<String> advancementDependencyStrings = quest.getAdvancementDependencies();
            List<AdvancementInterface> advancementDependencies = new ArrayList<>();

            advancementDependencyStrings.forEach((advancement) -> advancementDependencies.add(server.getAdvancement(advancement)));

            dependencies.forEach((dependencyList) -> {
                if (dependencyList.size() == 1) {
                    questDependencies.addCheck(dependencyList.get(0).getCheckName(server));
                } else {
                    questDependencies.addNested(new DependencyNotation(
                        dependencyList.stream().map((dependency) -> dependency.getCheckName(server))
                    ));
                }
            });

            fullDependency.addNested(questDependencies);
            fullDependency.addNested(new DependencyNotation(advancementDependencies.stream().map(AdvancementInterface::getCheckName)));

            ftbQuestsChecks.put(
                    quest.getCheckName(server),
                    new Check(
                            quest.getDifficulty(),
                            fullDependency,
                            quest.getChapterCheckName()
                    )
            );
        }

        return ftbQuestsChecks.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (e1, e2) -> e1, // use first instance when dealing with conflicts
                                LinkedHashMap::new
                        )
                );
    }


    /**
     * Writes a file with all check details to ./output/archipelago_data.json.
     * @param server the server to get check details from.
     * @param context the context to use send progress messages.
     * @param singleLine whether to remove extra spacing from the JSON file.
     * @param removeHidden whether to remove hidden checks.
     * @return 0 on success, or 1 when if it fails to write a file.
     */
    public int generateChecks(ServerInterface server, ContextInterface context, boolean singleLine, boolean removeHidden) {
        context.sendMessage("Started writing to file.");

        Map<String, Check> checks = new LinkedHashMap<>(this.generateAdvancementChecks(server, removeHidden));

        if (server.isModLoaded("ftbquests")) {
            checks.putAll(this.generateFTBQuestChecks(server, removeHidden));
        }

        try {
            new File("output").mkdir();
            new File("output/archipelago_data.json").createNewFile();

            Writer writer = new FileWriter("output/archipelago_data.json");
            GsonBuilder builder = new GsonBuilder()
                    .disableHtmlEscaping()
                    .registerTypeAdapter(DependencyNotation.class, new DependencyNotationSerializer())
                    .serializeNulls();
            if (!singleLine) {
                builder.setPrettyPrinting();
            }
            Gson gson = builder.create();
            gson.toJson(checks, writer);
            writer.close();
        } catch (IOException e) {
            context.sendMessage(e.getMessage());
            return 1;
        }
        context.sendMessage("Finished writing to file.");
        return 0;
    }

    /**
     * Returns a list of dependencies that a ftb quest has.
     * @param advancementGetter a function to get an instance of an advancement from its id.
     *                          This should be client sided.
     * @param quest the quest to get dependencies from.
     * @return a list of dependencies that a ftb quest has.
     */
    public List<String> addDependencies(Function<String, AdvancementInterface> advancementGetter, FTBQuestsInterface quest) {
        List<String> items = new ArrayList<>();
        if (
            this.slotData.isInitiated &&
            (
                !this.slotData.activated_modules.contains("FTBQuests") ||
                !this.slotData.ftb_quest_shape.contains(quest.getDifficulty())
            )
        ) {
            return new ArrayList<>();
        }

        // only do this for dependencies
        if (Objects.equals(this.slotData.unlock_type, "tab")) {
            if (!(!quest.getDependencies().findAny().isPresent() && this.slotData.roots_unlocked)) {
                addToMenu(items, "Archipelago Item: " + quest.getChapterCheckName(), 0);
            }
        }
        else if (Objects.equals(this.slotData.unlock_type, "tree")) {
            List<String> advancementDependencies = quest.getAdvancementDependencies();

            final int[] indent = {0};

            if (!advancementDependencies.isEmpty()) {
                addToMenu(items, "All of: {", indent[0]++);
                for (String advancement : advancementDependencies) {
                    AdvancementInterface advancementDetails = advancementGetter.apply(advancement);
                    if (advancementDetails != null && advancementDetails.hasDisplay()) {
                        addToMenu(items, advancementDetails.getCheckName(), indent[0]);
                    }
                }
            }
            if (!quest.getDependencies().findAny().isPresent()) {
                if (!this.slotData.roots_unlocked) {
                    addToMenu(items, CheckType.FTB_QUEST.addPrefix(String.format("%s (%s)", quest.getId(), quest.getName())), indent[0]);
                } else {
                    if (!advancementDependencies.isEmpty()) {
                        addToMenu(items, "}", --indent[0]);
                    }
                    return items;
                }
            } else if (quest.getMinimumDependencies() != 0) {
                addToMenu(items, "At Least " + quest.getMinimumDependencies() + " of: {", indent[0]++);
            } else if (quest.hasSingleDependencyRequirement()) {
                addToMenu(items, "One of: {", indent[0]++);
            } else {
                if (advancementDependencies.isEmpty()) {
                    addToMenu(items, "All of: {", indent[0]++);
                }
            }
            quest.getDependencies().forEach((dependency) -> {
                if (dependency.size() == 1) {
                    addToMenu(items, dependency.get(0).getCheckName(), indent[0]);
                } else {
                    addToMenu(items, "All of: {", indent[0]++);
                    for (FTBQuestsInterface dep : dependency) {
                        addToMenu(items, dep.getCheckName(), indent[0]);
                    }
                    addToMenu(items, "}", --indent[0]);
                }
            });
            addToMenu(items, "}", --indent[0]);
            if (!advancementDependencies.isEmpty() && (quest.getMinimumDependencies() != 0 || quest.hasSingleDependencyRequirement())) {
                addToMenu(items, "}", --indent[0]);
            }
        }
        else {
            addToMenu(items, "Archipelago Item: " + quest.getCheckName(), 0);
        }

        return items;
    }

    /**
     * Adds a text element to a list with a specified indent.
     * @param itemList the list to add the text to.
     * @param text the text to add to the list.
     * @param indent the amount to indent the text.
     */
    private void addToMenu(List<String> itemList, String text, int indent) {
        String indentText = indent >= 0 ? new String(new char[indent]).replace("\0", "  ") : "";
        itemList.add(indentText + text);
    }
}
