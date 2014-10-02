package io.pivotal.aml.pushnotificatin;

import com.google.gson.annotations.SerializedName;

public class BackEndMessageTarget {

    @SerializedName("platform")
    public String platform;

    @SerializedName("tags")
    public String[] devices;

    public BackEndMessageTarget(String platform, String[] devices) {
        this.platform = platform;
        this.devices = devices;
    }
}
