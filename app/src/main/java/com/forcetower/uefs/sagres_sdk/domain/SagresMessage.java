package com.forcetower.uefs.sagres_sdk.domain;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by João Paulo on 17/11/2017.
 */

public class SagresMessage {
    //Message Keys
    private static final String SENDER_KEY = "sender";
    private static final String MESSAGE_KEY = "message";
    private static final String RECEIVE_TIME_KEY = "received_at";
    private static final String CLASS_MESSAGE_KEY = "class";

    //Attributes
    private String sender;
    private String message;
    private String received_at;
    private String class_receive;

    public SagresMessage(String sender, String message, String received_at, String class_receive) {
        this.sender = sender;
        this.message = message;
        this.received_at = received_at;
        this.class_receive = class_receive;
    }

    public static SagresMessage fromJSONObject(JSONObject jsonObject) throws JSONException {
        String sender = jsonObject.getString(SENDER_KEY);
        String message = jsonObject.getString(MESSAGE_KEY);
        String received_at = jsonObject.getString(RECEIVE_TIME_KEY);
        String class_receive = jsonObject.getString(CLASS_MESSAGE_KEY);
        return new SagresMessage(sender, message, received_at, class_receive);
    }

    public String getClassName() {
        return class_receive;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String getReceivedTime() {
        return received_at;
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(SENDER_KEY, sender);
        jsonObject.put(MESSAGE_KEY, message);
        jsonObject.put(RECEIVE_TIME_KEY, received_at);
        jsonObject.put(CLASS_MESSAGE_KEY, class_receive);
        return jsonObject;
    }

    @Override
    public String toString() {
        return "Class: " + class_receive + " --- " + "Message: " + message + "\nReceived: " + received_at + ", from: " + sender + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SagresMessage message1 = (SagresMessage) o;

        if (sender != null ? !sender.equals(message1.sender) : message1.sender != null)
            return false;
        if (message != null ? !message.equals(message1.message) : message1.message != null)
            return false;
        return class_receive != null ? class_receive.equals(message1.class_receive) : message1.class_receive == null;
    }

    @Override
    public int hashCode() {
        int result = sender != null ? sender.hashCode() : 0;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (class_receive != null ? class_receive.hashCode() : 0);
        return result;
    }
}
