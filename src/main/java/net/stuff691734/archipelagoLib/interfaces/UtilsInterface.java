package net.stuff691734.archipelagoLib.interfaces;

public interface UtilsInterface extends LoggerInterface {
    /**
     * Returns whether the item is valid.
     * @param itemName the item to check if is valid.
     * @return whether the item is valid.
     */
    boolean isItemId(String itemName);

    /**
     * Returns whether the advancement is valid.
     * @param advancementName the advancement to check if is valid.
     * @return whether the advancement is valid.
     */
    boolean isAdvancementId(String advancementName);

    /**
     * Returns whether the quest is valid.
     * @param questName the quest to check if is valid.
     * @return whether the quest is valid.
     */
    boolean isQuestId(String questName);

    /**
     * Sends a message to the server console as well as all players.
     * Prefer using {@link UtilsInterface#sendMessageTranslatable(String message)}.
     * @param message the message to send.
     */
    void sendMessage(String message);

    /**
     * Sends a message to the server console as well as all players with a translated string.
     * @param message the message to translate and send.
     */
    void sendMessageTranslatable(String message);

    /**
     * Gives all players an item based on the advancement's icon.
     * @param server the server to send items with.
     * @param advancement the advancement to base the item off of.
     * @param index a nullable value where if it is less than the latest item received should not grant the item.
     */
    void giveItem(ServerInterface server, AdvancementInterface advancement, Long index);

    /**
     * Gives all players an item.
     * @param server the server to send items with.
     * @param itemName the item to give the players.
     * @param index a nullable value where if it is less than the latest item received should not grant the item.
     */
    void giveItem(ServerInterface server, String itemName, Long index);
}
