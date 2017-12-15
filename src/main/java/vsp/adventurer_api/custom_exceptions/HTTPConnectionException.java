package vsp.adventurer_api.custom_exceptions;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 *
 */
public class HTTPConnectionException extends IOException {

    private final int errorCode;

    public HTTPConnectionException(final int errorCode,
                                   @Nonnull final String codeDetails,
                                   @Nonnull final String message) {
        super(errorCode + " " + codeDetails + "\n" + message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

}