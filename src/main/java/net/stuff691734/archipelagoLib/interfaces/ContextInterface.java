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
     * @param args the arguments to add to the message.
     */
    void sendMessageTranslatable(String message, Object... args);
}
