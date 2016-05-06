package eu.firen.checkoutsimulator.loader;

/**
 * Created by Adam on 06.05.2016.
 */
public class PriceRulesFileReadException extends RuntimeException {
    public PriceRulesFileReadException() {
    }

    public PriceRulesFileReadException(String message) {
        super(message);
    }

    public PriceRulesFileReadException(String message, Throwable cause) {
        super(message, cause);
    }

    public PriceRulesFileReadException(Throwable cause) {
        super(cause);
    }

    public PriceRulesFileReadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
