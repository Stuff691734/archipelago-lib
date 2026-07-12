package net.stuff691734.archipelagoLib.interfaces;

import net.stuff691734.archipelagoLib.SlotData;

import java.util.List;
import java.util.Map;

public interface ServerStorageInterface extends StorageInterface {
    /**
     * Saves changes to permanent storage, should be called after any write operation.
     */
    void setDirty();

    /**
     * Returns a list of all checks that are waiting to be sent to the archipelago server.
     * @return a list of all checks that are waiting to be sent to the archipelago server.
     */
    List<String> getPendingChecks();

    /**
     * Adds a check to the list of checks that are waiting to be sent.
     * @param check the check to add.
     */
    void addPendingCheck(String check);

    /**
     * Returns a map of the slotData from storage, this should not be used for checking values use {@link SlotData} instead
     * @return a map of the slotData from storage.
     */
    Map<String, String> getSlotData();

    /**
     * Returns a map of collected checks.
     * @return a map of collected checks.
     */
    Map<String, Boolean> getChecks();

    /**
     * Saves a check that the player has collected into storage.
     * @param checkName the check to save.
     */
    void addCheck(String checkName);

    /**
     * Updates the users lastest check to prevent recollecting checks.
     */
    void updateLastCheck(Long index);


}
