package net.stuff691734.archipelagoLib;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SlotData {
    public String unlock_type;
    public String final_goal;
    public List<String> activated_modules;
    public List<String> advancement_difficulty;
    public List<String> ftb_quest_shape;
    public List<String> better_questing_shape;
    public boolean advancement_checks_give_items;
    public boolean quest_checks_give_rewards;
    public boolean better_questing_gives_rewards;
    public boolean death_link;
    public boolean roots_unlocked;

    public boolean isInitiated = false;

    private SlotData(
            String unlock_type,
            String final_goal,
            String activated_modules,
            String advancement_difficulty,
            String ftb_quest_shape,
            String better_questing_shape,
            String advancement_checks_give_items,
            String quest_checks_give_rewards,
            String better_questing_gives_rewards,
            String death_link,
            String roots_unlocked
    ) {
        this.unlock_type = unlock_type;
        this.final_goal = final_goal;
        this.activated_modules = Arrays.stream(activated_modules.split("\\|")).collect(Collectors.toList());
        this.advancement_difficulty = Arrays.stream(advancement_difficulty.split("\\|")).collect(Collectors.toList());
        this.ftb_quest_shape = Arrays.stream(ftb_quest_shape.split("\\|")).collect(Collectors.toList());
        this.better_questing_shape = Arrays.stream(better_questing_shape.split("\\|")).collect(Collectors.toList());
        this.advancement_checks_give_items = advancement_checks_give_items.equals("1");
        this.quest_checks_give_rewards = quest_checks_give_rewards.equals("1");
        this.better_questing_gives_rewards = better_questing_gives_rewards.equals("1");
        this.death_link = death_link.equals("1");
        this.roots_unlocked = roots_unlocked.equals("1");

        this.isInitiated = true;
    }

    public SlotData(Map<String, String> slotData) {
        this(
                slotData.get("unlock_type"),
                slotData.get("final_goal"),
                slotData.get("activated_modules"),
                slotData.get("advancement_check_difficulty"),
                slotData.get("ftb_quest_check_shape"),
                slotData.get("better_questing_shape"),
                slotData.get("advancement_checks_give_items"),
                slotData.get("quest_checks_give_rewards"),
                slotData.get("better_questing_gives_rewards"),
                slotData.get("death_link"),
                slotData.get("roots_unlocked")
        );
    }

    public SlotData() {

    }

    public boolean isCheckFinalGoal(String checkName) {
        return this.isInitiated && checkName.equals(String.format("%s %s", (Object[]) this.final_goal.split(" ")));
    }
}
