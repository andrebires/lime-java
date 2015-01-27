package org.limeprotocol;

/**
 * Defines the valid session encryption values.
 */
public enum SessionEncryption {
    /**
     * The session is not encrypted.
     */
    none,
    /**
     * The session is encrypted by TLS (Transport Layer Security).
     */
    tls
}
