package olyamba.utills;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;

import javax.jms.*;
import java.util.HashMap;
import java.util.Map;

public class Producer {

    public static void sendMessage(String text) throws JMSException {
        AWSCredentials awsCredentials = new AWSCredentials() {
            @Override
            public String getAWSAccessKeyId() {
                return "21C8B1C79A1C28DCBE90";
            }

            @Override
            public String getAWSSecretKey() {
                return "FCF5C8513673653D6B46EE42ED54A64203A40DAD";
            }
        };
        AmazonSQS sqs = AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion("ru-msk")
                .build();
        Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
        messageAttributes.put("AttributeOne", new MessageAttributeValue()
                .withStringValue("This is an attribute")
                .withDataType("String"));

        SendMessageRequest sendMessageStandardQueue = new SendMessageRequest()
                .withQueueUrl("https://sqs.mcs.mail.ru/mcs4092377196/very-nice-shop-mail")
                .withMessageBody(text)
                .withDelaySeconds(30)
                .withMessageAttributes(messageAttributes);

        sqs.sendMessage(sendMessageStandardQueue);
    }
}
