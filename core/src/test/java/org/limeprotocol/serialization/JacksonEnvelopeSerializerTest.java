package org.limeprotocol.serialization;

import org.junit.Before;
import org.junit.Test;
import org.limeprotocol.Envelope;
import org.limeprotocol.Node;
import org.limeprotocol.Session;
import org.limeprotocol.Session.SessionState;
import org.limeprotocol.security.PlainAuthentication;
import org.limeprotocol.testHelpers.JsonConstants;
import org.limeprotocol.util.StringUtils;

import java.util.HashMap;
import java.util.UUID;

import static org.limeprotocol.security.Authentication.*;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.limeprotocol.testHelpers.TestDummy.*;

public class JacksonEnvelopeSerializerTest {

    private JacksonEnvelopeSerializer target;

    @Before
    public void setUp() throws Exception {
        target = new JacksonEnvelopeSerializer();
    }

    //region serialize method

    //region Session

    @Test
    public void serialize_AuthenticatingSession_ReturnsValidJsonString() {
        // Arrange
        Session session = createSession(SessionState.Authenticating);
        PlainAuthentication plainAuthentication = createPlainAuthentication();
        session.setAuthentication(plainAuthentication);

        String metadataKey1 = "randomString1";
        String metadataValue1 = createRandomString(50);
        String metadataKey2 = "randomString2";
        String metadataValue2 = createRandomString(50);
        session.setMetadata(new HashMap<String, String>());
        session.getMetadata().put(metadataKey1, metadataValue1);
        session.getMetadata().put(metadataKey2, metadataValue2);

        // Act
        String resultString = target.serialize(session);

        // Assert
        assertThatJson(resultString).node(JsonConstants.Envelope.ID_KEY).isEqualTo(session.getId());
        assertThatJson(resultString).node(JsonConstants.Envelope.FROM_KEY).isEqualTo(session.getFrom().toString());
        assertThatJson(resultString).node(JsonConstants.Envelope.TO_KEY).isEqualTo(session.getTo().toString());
        assertThatJson(resultString).node(JsonConstants.Session.STATE_KEY).isEqualTo(session.getState().toString().toLowerCase());
        assertThatJson(resultString).node(JsonConstants.Envelope.getMetadataKeyFromRoot(metadataKey1)).isEqualTo(metadataValue1);
        assertThatJson(resultString).node(JsonConstants.Envelope.getMetadataKeyFromRoot(metadataKey2)).isEqualTo(metadataValue2);
        assertThatJson(resultString).node(JsonConstants.Session.AUTHENTICATION_KEY).isPresent();
        assertThatJson(resultString).node(JsonConstants.PlainAuthentication.PASSWORK_KEY_FROM_ROOT).isEqualTo(plainAuthentication.getPassword());

        assertThatJson(resultString).node(JsonConstants.Envelope.PP_KEY).isAbsent();
        assertThatJson(resultString).node(JsonConstants.Session.REASON_KEY).isAbsent();
    }

    @Test
    public void serialize_FailedSession_ReturnsValidJsonString() {
        // Arrange
        Session session = createSession();
        session.setState(SessionState.Failed);
        session.setReason(createReason());

        // Act
        String resultString = target.serialize(session);

        // Assert
        assertThatJson(resultString).node(JsonConstants.Envelope.ID_KEY).isEqualTo(session.getId());
        assertThatJson(resultString).node(JsonConstants.Envelope.FROM_KEY).isEqualTo(session.getFrom().toString());
        assertThatJson(resultString).node(JsonConstants.Envelope.TO_KEY).isEqualTo(session.getTo().toString());
        assertThatJson(resultString).node(JsonConstants.Session.STATE_KEY).isEqualTo(session.getState().toString().toLowerCase());
        assertThatJson(resultString).node(JsonConstants.Session.REASON_KEY).isPresent();
        assertThatJson(resultString).node(JsonConstants.Reason.CODE_KEY_FROM_ROOT).isEqualTo(session.getReason().getCode());
        assertThatJson(resultString).node(JsonConstants.Reason.DESCRIPTION_KEY_FROM_ROOT).isEqualTo(session.getReason().getDescription());

        assertThatJson(resultString).node(JsonConstants.Envelope.PP_KEY).isAbsent();
        assertThatJson(resultString).node(JsonConstants.Envelope.METADATA_KEY).isAbsent();
        assertThatJson(resultString).node(JsonConstants.Session.AUTHENTICATION_KEY).isAbsent();
    }

    //endregion Session

    //region Message

//    public void Serialize_TextMessage_ReturnsValidJsonString()
//    {
//        var content = DataUtil.CreateTextContent();
//        var message = DataUtil.CreateMessage(content);
//        message.Pp = DataUtil.CreateNode();
//
//        var metadataKey1 = "randomString1";
//        var metadataValue1 = DataUtil.CreateRandomString(50);
//        var metadataKey2 = "randomString2";
//        var metadataValue2 = DataUtil.CreateRandomString(50);
//        message.Metadata = new Dictionary<string, string>();
//        message.Metadata.Add(metadataKey1, metadataValue1);
//        message.Metadata.Add(metadataKey2, metadataValue2);
//
//        var resultString = target.Serialize(message);
//        Assert.IsTrue(resultString.HasValidJsonStackedBrackets());
//        Assert.IsTrue(resultString.ContainsJsonProperty(Envelope.ID_KEY, message.Id));
//        Assert.IsTrue(resultString.ContainsJsonProperty(Envelope.FROM_KEY, message.From));
//        Assert.IsTrue(resultString.ContainsJsonProperty(Envelope.PP_KEY, message.Pp));
//        Assert.IsTrue(resultString.ContainsJsonProperty(Envelope.TO_KEY, message.To));
//        Assert.IsTrue(resultString.ContainsJsonProperty(Message.TYPE_KEY, message.Content.GetMediaType()));
//        Assert.IsTrue(resultString.ContainsJsonKey(Message.CONTENT_KEY));
//        Assert.IsTrue(resultString.ContainsJsonProperty(Message.CONTENT_KEY, content.Text));
//        Assert.IsTrue(resultString.ContainsJsonProperty(metadataKey1, metadataValue1));
//        Assert.IsTrue(resultString.ContainsJsonProperty(metadataKey2, metadataValue2));
//    }
//
//    public void Serialize_UnknownJsonContentMessage_ReturnsValidJsonString()
//    {
//        var target = GetTarget();
//
//        var content = DataUtil.CreateJsonDocument();
//        var message = DataUtil.CreateMessage(content);
//        message.Pp = DataUtil.CreateNode();
//
//        var metadataKey1 = "randomString1";
//        var metadataValue1 = DataUtil.CreateRandomString(50);
//        var metadataKey2 = "randomString2";
//        var metadataValue2 = DataUtil.CreateRandomString(50);
//        message.Metadata = new Dictionary<string, string>();
//        message.Metadata.Add(metadataKey1, metadataValue1);
//        message.Metadata.Add(metadataKey2, metadataValue2);
//
//        var resultString = target.Serialize(message);
//
//        Assert.IsTrue(resultString.HasValidJsonStackedBrackets());
//        Assert.IsTrue(resultString.ContainsJsonProperty(Envelope.ID_KEY, message.Id));
//        Assert.IsTrue(resultString.ContainsJsonProperty(Envelope.FROM_KEY, message.From));
//        Assert.IsTrue(resultString.ContainsJsonProperty(Envelope.PP_KEY, message.Pp));
//        Assert.IsTrue(resultString.ContainsJsonProperty(Envelope.TO_KEY, message.To));
//        Assert.IsTrue(resultString.ContainsJsonProperty(Message.TYPE_KEY, message.Content.GetMediaType()));
//        Assert.IsTrue(resultString.ContainsJsonKey(Message.CONTENT_KEY));
//
//        foreach (var keyValuePair in content)
//        {
//            Assert.IsTrue(resultString.ContainsJsonProperty(keyValuePair.Key, keyValuePair.Value));
//        }
//
//        Assert.IsTrue(resultString.ContainsJsonProperty(metadataKey1, metadataValue1));
//        Assert.IsTrue(resultString.ContainsJsonProperty(metadataKey2, metadataValue2));
//    }
//
//    public void Serialize_UnknownPlainContentMessage_ReturnsValidJsonString()
//    {
//        var target = GetTarget();
//
//        var content = DataUtil.CreatePlainDocument();
//        var message = DataUtil.CreateMessage(content);
//        message.Pp = DataUtil.CreateNode();
//
//        var metadataKey1 = "randomString1";
//        var metadataValue1 = DataUtil.CreateRandomString(50);
//        var metadataKey2 = "randomString2";
//        var metadataValue2 = DataUtil.CreateRandomString(50);
//        message.Metadata = new Dictionary<string, string>();
//        message.Metadata.Add(metadataKey1, metadataValue1);
//        message.Metadata.Add(metadataKey2, metadataValue2);
//
//        var resultString = target.Serialize(message);
//
//        Assert.IsTrue(resultString.HasValidJsonStackedBrackets());
//        Assert.IsTrue(resultString.ContainsJsonProperty(Envelope.ID_KEY, message.Id));
//        Assert.IsTrue(resultString.ContainsJsonProperty(Envelope.FROM_KEY, message.From));
//        Assert.IsTrue(resultString.ContainsJsonProperty(Envelope.PP_KEY, message.Pp));
//        Assert.IsTrue(resultString.ContainsJsonProperty(Envelope.TO_KEY, message.To));
//        Assert.IsTrue(resultString.ContainsJsonProperty(Message.TYPE_KEY, message.Content.GetMediaType()));
//        Assert.IsTrue(resultString.ContainsJsonKey(Message.CONTENT_KEY));
//        Assert.IsTrue(resultString.ContainsJsonProperty(Message.CONTENT_KEY, content.Value));
//        Assert.IsTrue(resultString.ContainsJsonProperty(metadataKey1, metadataValue1));
//        Assert.IsTrue(resultString.ContainsJsonProperty(metadataKey2, metadataValue2));
//    }
//
//    public void Serialize_FireAndForgetTextMessage_ReturnsValidJsonString()
//    {
//        var target = GetTarget();
//
//        var content = DataUtil.CreateTextContent();
//        var message = DataUtil.CreateMessage(content);
//        message.Id = Guid.Empty;
//
//        var resultString = target.Serialize(message);
//
//        Assert.IsTrue(resultString.HasValidJsonStackedBrackets());
//
//        Assert.IsTrue(resultString.ContainsJsonProperty(Envelope.FROM_KEY, message.From));
//        Assert.IsTrue(resultString.ContainsJsonProperty(Envelope.TO_KEY, message.To));
//        Assert.IsTrue(resultString.ContainsJsonProperty(Message.TYPE_KEY, message.Content.GetMediaType()));
//        Assert.IsTrue(resultString.ContainsJsonKey(Message.CONTENT_KEY));
//        Assert.IsTrue(resultString.ContainsJsonProperty(Message.CONTENT_KEY, content.Text));
//
//        Assert.IsFalse(resultString.ContainsJsonKey(Envelope.ID_KEY));
//        Assert.IsFalse(resultString.ContainsJsonKey(Envelope.PP_KEY));
//        Assert.IsFalse(resultString.ContainsJsonKey(Envelope.METADATA_KEY));
//    }

    //endregion Message


    //endregion serialize

    //region deserialize method

    //region Session

    @Test
    public void deserialize_AuthenticatingSession_ReturnsValidInstance() {
        // Arrange
        UUID id = UUID.randomUUID();
        Node from = createNode();
        Node pp = createNode();
        Node to = createNode();

        String password = StringUtils.toBase64(createRandomString(10));

        String randomKey1 = "randomString1";
        String randomKey2 = "randomString2";
        String randomString1 = createRandomString(50);
        String randomString2 = createRandomString(50);

        SessionState state = SessionState.Authenticating;

        AuthenticationScheme scheme = AuthenticationScheme.Plain;

        String json = StringUtils.format("{\"state\":\"{0}\",\"scheme\":\"{9}\",\"authentication\":{\"password\":\"{1}\"},\"id\":\"{2}\",\"from\":\"{3}\",\"to\":\"{4}\",\"metadata\":{\"{5}\":\"{6}\",\"{7}\":\"{8}\"}}",
                state.toString().toLowerCase(),
                password,
                id,
                from,
                to,
                randomKey1,
                randomString1,
                randomKey2,
                randomString2,
                scheme.toString().toLowerCase()
        );
        // Act
        Envelope envelope = target.deserialize(json);

        // Assert
        assertThat(envelope).isInstanceOf(Session.class);

        Session session = (Session)envelope;
        assertThat(session.getId()).isEqualTo(id);
        assertThat(session.getFrom()).isEqualTo(from);
        assertThat(session.getTo()).isEqualTo(to);
        assertThat(session.getMetadata()).isNotNull();
        assertThat(session.getMetadata()).containsKey(randomKey1);
        assertThat(session.getMetadata().get(randomKey1)).isEqualTo(randomString1);
        assertThat(session.getMetadata()).containsKey(randomKey2);
        assertThat(session.getMetadata().get(randomKey2)).isEqualTo(randomString2);

        assertThat(session.getState()).isEqualTo(state);

        assertThat(session.getPp()).isNull();
        assertThat(session.getReason()).isNull();

        assertThat(session.getScheme()).isEqualTo(AuthenticationScheme.Plain);
    }

    //endregion Session

    //endregion deserialize

}