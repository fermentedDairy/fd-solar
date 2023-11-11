package org.fermented.dairy.solar.entity.exception;

import java.util.function.Supplier;

/**
 * Exception thrown by Repositories.
 */
public class RepositoryException extends RuntimeException {

    /**
     * Constructor.
     *
     * @param messageSupplier supplier of messages
     * @param causedBy The cause of the exception
     */
    public RepositoryException(final Supplier<String> messageSupplier, final Throwable causedBy) {
        super(messageSupplier.get(), causedBy);
    }

    /**
     * Constructor.
     *
     * @param message supplier of messages
     * @param causedBy The cause of the exception
     */
    public RepositoryException(final String message, final Throwable causedBy) {
        super(message, causedBy);
    }
}
