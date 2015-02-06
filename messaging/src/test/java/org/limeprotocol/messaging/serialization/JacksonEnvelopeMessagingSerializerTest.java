package org.limeprotocol.messaging.serialization;

import org.junit.Before;
import org.junit.Test;
import org.limeprotocol.Command;
import org.limeprotocol.Envelope;
import org.limeprotocol.Message;
import org.limeprotocol.Node;
import org.limeprotocol.messaging.contents.ChatState;
import org.limeprotocol.messaging.contents.ChatState.ChatStateEvent;
import org.limeprotocol.messaging.contents.PlainText;
import org.limeprotocol.messaging.resource.Capability;
import org.limeprotocol.messaging.resource.Receipt;
import org.limeprotocol.serialization.JacksonEnvelopeSerializer;
import org.limeprotocol.testHelpers.JsonConstants;
import org.limeprotocol.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.limeprotocol.Command.CommandMethod.Set;
import static org.limeprotocol.Notification.Event;
import static org.limeprotocol.messaging.testHelpers.MessagingJsonConstants.Capability.CONTENT_TYPES_KEY;
import static org.limeprotocol.messaging.testHelpers.MessagingJsonConstants.Capability.RESOURCE_TYPES_KEY;
import static org.limeprotocol.messaging.testHelpers.MessagingTestDummy.createCapability;
import static org.limeprotocol.messaging.testHelpers.MessagingTestDummy.createTextContent;
import static org.limeprotocol.serialization.JacksonEnvelopeSerializerTest.assertJsonEnvelopeProperties;
import static org.limeprotocol.testHelpers.JsonConstants.Command.*;
import static org.limeprotocol.testHelpers.JsonConstants.Envelope.*;
import static org.limeprotocol.testHelpers.TestDummy.*;

public class JacksonEnvelopeMessagingSerializerTest {

    private JacksonEnvelopeSerializer target;

    @Before
    public void setUp() throws Exception {
        target = new JacksonEnvelopeSerializer();
    }

    //region serialize method

    //region Message

    @Test
    public void serialize_TextMessage_ReturnsValidJsonString()
    {
        PlainText content = createTextContent();
        Message message = createMessage(content);
        message.setPp(createNode());

        message.setMetadata(createRandomMetadata());

        String resultString = target.serialize(message);

        assertJsonEnvelopeProperties(message, resultString, ID_KEY, FROM_KEY, TO_KEY, PP_KEY, METADATA_KEY);

        assertThatJson(resultString).node(JsonConstants.Message.TYPE_KEY).isEqualTo(message.getType().toString());

        assertThatJson(resultString).node(JsonConstants.Message.CONTENT_KEY).isPresent();
        assertThatJson(resultString).node(JsonConstants.PlainText.CONTENT_TEXT_KEY).isEqualTo(content.getText());
    }

    @Test
    public void serialize_FireAndForgetTextMessage_ReturnsValidJsonString()
    {
        PlainText content = createTextContent();
        Message message = createMessage(content);
        message.setId(null);

        String resultString = target.serialize(message);

        assertJsonEnvelopeProperties(message, resultString, FROM_KEY, TO_KEY);

        assertThatJson(resultString).node(JsonConstants.Message.TYPE_KEY).isEqualTo(message.getType().toString());
        assertThatJson(resultString).node(JsonConstants.Message.CONTENT_KEY).isPresent();

        assertThatJson(resultString).node(JsonConstants.PlainText.CONTENT_TEXT_KEY).isEqualTo(content.getText());
    }

    //endregion Message

    //region Command

    @Test
    public void serialize_CapabilityRequestCommand_ReturnsValidJsonString()
    {
        Capability resource = createCapability();
        Command command = createCommand(resource);
        command.setPp(createNode());
        command.setMethod(Command.CommandMethod.Get);

        String metadataKey1 = "randomString1";
        String metadataValue1 = createRandomString(50);
        String metadataKey2 = "randomString2";
        String metadataValue2 = createRandomString(50);
        Map<String, String> metadata = new HashMap<>();
        metadata.put(metadataKey1, metadataValue1);
        metadata.put(metadataKey2, metadataValue2);
        command.setMetadata(metadata);

        String resultString = target.serialize(command);

        assertJsonEnvelopeProperties(command, resultString, ID_KEY, FROM_KEY, PP_KEY, TO_KEY, METADATA_KEY );

        assertThatJson(resultString).node(METHOD_KEY).isEqualTo(command.getMethod().toString().toLowerCase());

        assertThatJson(resultString).node(RESOURCE_KEY).isPresent();
        assertThatJson(resultString).node(TYPE_KEY).isEqualTo(command.getResource().getMediaType().toString());

        assertThatJson(resultString).node(CONTENT_TYPES_KEY).isEqualTo(resource.getContentTypes());
        assertThatJson(resultString).node(RESOURCE_TYPES_KEY).isEqualTo(resource.getResourceTypes());

        assertThatJson(resultString).node(STATUS_KEY).isPresent();
        assertThatJson(resultString).node(REASON_KEY).isPresent();
    }

    //endregion Command

    //endregion serialize method

    //region deserialize method

    //region Message

    @Test
    public void deserialize_TextMessage_ReturnsValidInstance()
    {
        UUID id = UUID.randomUUID();
        Node from = createNode();
        Node pp = createNode();
        Node to = createNode();

        String randomKey1 = "randomString1";
        String randomKey2 = "randomString2";
        String randomString1 = createRandomString(50);
        String randomString2 = createRandomString(50);

        String text = createRandomString(50);

        String json = StringUtils.format(
                "{\"type\":\"text/plain\",\"content\":\"{0}\",\"id\":\"{1}\",\"from\":\"{2}\",\"pp\":\"{3}\",\"to\":\"{4}\",\"metadata\":{\"{5}\":\"{6}\",\"{7}\":\"{8}\"}}",
                text,
                id,
                from,
                pp,
                to,
                randomKey1,
                randomString1,
                randomKey2,
                randomString2
        );

        Envelope envelope = target.deserialize(json);

        assertThat(envelope instanceof Message);

        Message message = (Message)envelope;
        assertEquals(id, message.getId());
        assertEquals(from, message.getFrom());
        assertEquals(pp, message.getPp());
        assertEquals(to, message.getTo());
        assertNotNull(message.getMetadata());
        assertTrue(message.getMetadata().containsKey(randomKey1));
        assertEquals(message.getMetadata().get(randomKey1), randomString1);
        assertTrue(message.getMetadata().containsKey(randomKey2));
        assertEquals(message.getMetadata().get(randomKey2), randomString2);

        assertTrue(message.getContent() instanceof PlainText);

        PlainText textContent = (PlainText)message.getContent();
        assertEquals(text, textContent.getText());
    }

    @Test
    public void deserialize_ChatStateMessage_ReturnsValidInstance()
    {
        UUID id = UUID.randomUUID();
        Node from = createNode();
        Node pp = createNode();
        Node to = createNode();

        String randomKey1 = "randomString1";
        String randomKey2 = "randomString2";
        String randomString1 = createRandomString(50);
        String randomString2 = createRandomString(50);

        ChatStateEvent state = ChatStateEvent.DELETING;

        String json = StringUtils.format(
                "{\"type\":\"application/vnd.lime.chatstate+json\",\"content\":{\"state\":\"{0}\"},\"id\":\"{1}\",\"from\":\"{2}\",\"pp\":\"{3}\",\"to\":\"{4}\",\"metadata\":{\"{5}\":\"{6}\",\"{7}\":\"{8}\"}}",
                state.toString().toLowerCase(),
                id,
                from,
                pp,
                to,
                randomKey1,
                randomString1,
                randomKey2,
                randomString2
        );

        Envelope envelope = target.deserialize(json);

        assertThat(envelope instanceof Message);

        Message message = (Message)envelope;
        assertEquals(id, message.getId());
        assertEquals(from, message.getFrom());
        assertEquals(pp, message.getPp());
        assertEquals(to, message.getTo());
        assertNotNull(message.getMetadata());
        assertTrue(message.getMetadata().containsKey(randomKey1));
        assertEquals(message.getMetadata().get(randomKey1), randomString1);
        assertTrue(message.getMetadata().containsKey(randomKey2));
        assertEquals(message.getMetadata().get(randomKey2), randomString2);


        assertTrue(message.getContent() instanceof ChatState);

        ChatState textContent = (ChatState)message.getContent();
        assertEquals(state, textContent.getState());
    }
//
//    public void Deserialize_FireAndForgetTextMessage_ReturnsValidInstance()
//    {
//        var target = GetTarget();
//
//        var from = DataUtil.CreateNode();
//        var to = DataUtil.CreateNode();
//
//        var text = DataUtil.CreateRandomString(50);
//
//        string json = string.Format(
//                "{{\"type\":\"text/plain\",\"content\":\"{0}\",\"from\":\"{1}\",\"to\":\"{2}\"}}",
//                text,
//                from,
//                to
//        );
//
//        var envelope = target.Deserialize(json);
//
//        Assert.IsTrue(envelope is Message);
//
//        var message = (Message)envelope;
//        Assert.AreEqual(from, message.From);
//        Assert.AreEqual(to, message.To);
//
//        Assert.AreEqual(message.Id, Guid.Empty);
//        Assert.IsNull(message.Pp);
//        Assert.IsNull(message.Metadata);
//
//        Assert.IsTrue(message.Content is PlainText);
//        var textContent = (PlainText)message.Content;
//        Assert.AreEqual(text, textContent.Text);
//    }
//
//    [Test]
//            [Category("Deserialize")]
//    public void Deserialize_FireAndForgetChatStateMessage_ReturnsValidInstance()
//    {
//        var target = GetTarget();
//
//        var from = DataUtil.CreateNode();
//        var to = DataUtil.CreateNode();
//
//        var state = ChatStateEvent.COMPOSING;
//
//        string json = string.Format(
//                "{{\"type\":\"application/vnd.lime.chatstate+json\",\"content\":{{\"state\":\"{0}\"}},\"from\":\"{1}\",\"to\":\"{2}\"}}",
//                state.ToString().ToCamelCase(),
//                from,
//                to
//        );
//
//        var envelope = target.Deserialize(json);
//
//        Assert.IsTrue(envelope is Message);
//
//        var message = (Message)envelope;
//        Assert.AreEqual(from, message.From);
//        Assert.AreEqual(to, message.To);
//
//        Assert.AreEqual(message.Id, Guid.Empty);
//        Assert.IsNull(message.Pp);
//        Assert.IsNull(message.Metadata);
//
//        Assert.IsTrue(message.Content is ChatState);
//        var textContent = (ChatState)message.Content;
//        Assert.AreEqual(state, textContent.State);
//    }

    //endregion Message

    //region Command

    @Test
    public void deserialize_ReceiptRequestCommand_ReturnsValidInstance() {
        // Arrange
        Command.CommandMethod method = Set;
        UUID id = UUID.randomUUID();

        String json = StringUtils.format(
                "{\"type\":\"application/vnd.lime.receipt+json\",\"resource\":{\"events\":[\"dispatched\",\"received\"]},\"method\":\"{0}\",\"id\":\"{1}\"}",
                method.toString(),
                id);

        // Act
        Envelope envelope = target.deserialize(json);

        // Assert
        assertThat(envelope).isInstanceOf(Command.class);

        Command command = (Command)envelope;

        assertThat(command.getId()).isEqualTo(id);
        assertThat(command.getFrom()).isNull();
        assertThat(command.getTo()).isNull();
        assertThat(command.getPp()).isNull();
        assertThat(command.getMetadata()).isNull();

        assertThat(command.getMethod()).isEqualTo(method);

        assertThat(command.getType().toString()).isEqualTo(Receipt.MIME_TYPE);
        assertThat(command.getResource()).isNotNull().isInstanceOf(Receipt.class);
        Receipt receipt = (Receipt) command.getResource();
        assertThat(receipt.getEvents()).containsOnly(new Event[] {Event.Dispatched, Event.Received });

        assertThat(command.getUri()).isNull();
    }

    //endregion Command

    //endregion deserialize method
}