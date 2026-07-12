package net.stuff691734.archipelagoLib.interfaces;

public interface ContextInterface {
    /**
     * Sends a message to the player issuing the command.
     * @param message the message to send.
     */
    void sendMessage(String message);

    /**
     * Sends a translation message to the player issuing the command.
     * @param message the message to translate and send.
     */
    void sendMessageTranslatable(String message);
}
