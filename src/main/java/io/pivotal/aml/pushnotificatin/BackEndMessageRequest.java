package io.pivotal.aml.pushnotificatin;

import com.google.gson.annotations.SerializedName;

public class BackEndMessageRequest {

    @SerializedName("message")
    public BackEndMessageRequestData message;

    @SerializedName("target")
    public BackEndMessageTarget target;

    public BackEndMessageRequest(String messageBody, String platforms, String[] devices) {
        this.message = new BackEndMessageRequestData(messageBody);
        this.target = new BackEndMessageTarget(platforms, devices);
    }
}
