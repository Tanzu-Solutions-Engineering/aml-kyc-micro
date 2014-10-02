package io.pivotal.aml.pushnotificatin;

import com.google.gson.annotations.SerializedName;

public class BackEndMessageRequestData {

    @SerializedName("body")
    public String body;

    public BackEndMessageRequestData(String body) {
        this.body = body;
    }
}