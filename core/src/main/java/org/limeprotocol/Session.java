package org.limeprotocol;

import org.limeprotocol.security.Authentication;
import org.limeprotocol.security.Authentication.AuthenticationScheme;

/**
 * Represents a client session with the
 * server and its current state
 */
public class Session extends Envelope {
    public Session() {
        // The session id is generated by the server
        super(null);
    }

    /**
     * Informs or changes the state of a session.
     * Only the server can change the session state,
     * but the client can request the state tra
     */
    private SessionState state;

    /**
     * encryption options provided by
     * the server during the session negotiation.
     */
    private SessionEncryption[] encryptionOptions;

    /**
     * The encryption option selected for the session.
     * This property is provided by the client in the
     * negotiation and by the server in the confirmation
     * after that.
     */
    private SessionEncryption encryption;

    /**
     * Compression options provided by the
     * server during the session negotiation.
     */
    private SessionCompression[] compressionOptions;

    /**
     * The compression option selected for the session.
     * This property is provided by the client in the
     * negotiation and by the server in the confirmation
     * after that.
     */
    private SessionCompression compression;

    /**
     * List of available authentication schemas
     * for session authentication provided by the server.
     */
    private AuthenticationScheme[] schemeOptions;

    /**
     * The authentication scheme option selected
     * for the session. This property must be present
     * if the property authentication is defined.
     */
    private AuthenticationScheme scheme;

    /**
     * authentication data, related to the selected schema.
     * Information like password sent by the client or
     * roundtrip data sent by the server.
     */
    private Authentication authentication;

    /**
     * In cases where the client receives a session with
     * failed state, this property should provide more
     * details about the problem.
     */
    private Reason reason;

    public SessionState getState() {
        return state;
    }

    public void setState(SessionState state) {
        this.state = state;
    }

    public Reason getReason() {
        return reason;
    }

    public void setReason(Reason reason) {
        this.reason = reason;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public SessionEncryption[] getEncryptionOptions() {
        return encryptionOptions;
    }

    public void setEncryptionOptions(SessionEncryption[] encryptionOptions) {
        this.encryptionOptions = encryptionOptions;
    }

    public SessionEncryption getEncryption() {
        return encryption;
    }

    public void setEncryption(SessionEncryption encryption) {
        this.encryption = encryption;
    }

    public SessionCompression getCompression() {
        return compression;
    }

    public void setCompression(SessionCompression compression) {
        this.compression = compression;
    }

    public AuthenticationScheme[] getSchemeOptions() {
        return schemeOptions;
    }

    public void setSchemeOptions(AuthenticationScheme[] schemeOptions) {
        this.schemeOptions = schemeOptions;
    }

    public AuthenticationScheme getScheme() {
        if (this.authentication != null) {
            return this.authentication.getAuthenticationScheme();
        }

        return null;
    }

    /**
     * Defines the supported session states
     */
    public enum SessionState {
        /**
         * The session is new and doesn't exists an
         * established context. It is sent by a client
         * node to start a session with a server.
         */
        NEW,

        /**
         * The server and the client are negotiating the
         * session options, like cryptography and compression.
         * The server sends to the client the options (if available)
         * and the client chooses the desired options.
         * If there's no options (for instance, if the connection
         * is already encrypted or the transport protocol doesn't
         * support these options), the server SHOULD skip the negotiation.
         */
        NEGOTIATING,

        /**
         * The session is being authenticated. The server sends to
         * the client the available authentication schemes list and
         * the client must choose one and send the specific authentication
         * data. The authentication can occurs in multiple roundtrips,
         * according to the selected schema.
         */
        AUTHENTICATING,

        /**
         * The session is active and is possible to send and receive
         * messages and commands. The server sends this state
         * after the session was authenticated.
         */
        ESTABLISHED,

        /**
         * The client node is requesting to
         * the server to finish the session.
         */
        FINISHING,

        /**
         * The session was gracefully
         * finished by the server.
         */
        FINISHED,

        /**
         * A problem occurred while the session was established, under
         * negotiation or authentication and it was closed by the server.
         * In this case, the property reason MUST be present to provide
         * more details about the problem.
         */
        FAILED
    }
}
