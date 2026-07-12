package net.stuff691734.archipelagoLib.interfaces;

import net.stuff691734.archipelagoLib.SlotData;

import java.util.List;
import java.util.Map;

public interface ServerInterface {
    /**
     * Returns the server instance used to create this object.
     * If possible try to avoid using this.
     * @return the server instance used to create this object.
     */
    Object getServer();

    /**
     * Executes a method on the main server thread.
     * @param method the method to run.
     */
    void execute(Runnable method);

    /**
     * Sends a packet to all players with the slotData.
     * @param data the slotData to send.
     */
    void sendSlotDataPacket(Map<String, String> data);

    /**
     * Sends a packet to all players with a check that was collected.
     * @param check the check that got collected.
     */
    void sendCheckPacket(String check);

    /**
     *
     */
    void sendChecksDataPacket(List<String> checks);

    /**
     * Sets the servers slotData.
     * @param data the slotData to set on the server.
     */
    void setSlotData(SlotData data);

    /**
     * Returns whether a mod is loaded.
     * @param mod the mod to check if is loaded.
     * @return whether a mod is loaded.
     */
    boolean isModLoaded(String mod);

    /**
     * Returns an advancement based on the advancements name.
     * @param advancementName the name of the advancement to get.
     * @return an advancement based on the advancements name.
     */
    AdvancementInterface getAdvancement(String advancementName);

    /**
     * Returns a list of all advancements.
     * @return a list of all advancements.
     */
    List<AdvancementInterface> getAllAdvancements();

    /**
     * Returns a list of all ftb quests.
     * @return a list of all ftb quests.
     */
    List<FTBQuestsInterface> getAllFTBQuests();

    /**
     * Returns the slotData from the server.
     * @return ths slotData from the server.
     */
    SlotData getSlotData();

    /**
     * Kills all players from a deathLink event.
     */
    void killPlayers();

    /**
     * Overrides the {@link net.stuff691734.archipelagoLib.Logic} instance.
     * Used in cases where slotData is updated.
     */
    void updateLogic();
}
