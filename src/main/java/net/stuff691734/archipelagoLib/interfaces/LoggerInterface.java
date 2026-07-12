package net.stuff691734.archipelagoLib.interfaces;

public interface LoggerInterface {
    /**
     * Logs a message.
     * @param info the message to log.
     */
    void logInfo(String info);

    /**
     * Logs an error.
     * @param error the error to log.
     */
    void logError(String error);
}
