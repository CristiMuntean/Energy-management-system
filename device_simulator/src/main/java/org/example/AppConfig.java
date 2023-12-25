package org.example;

public class AppConfig {
    private String deviceId;

    public AppConfig() {
    }

    public AppConfig(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
