package com.example.alexandro.myapplication.model;

/**
 * Created by Alexandro on 01.09.2016.
 */
public class User {
    private static User currentUser = new User();

    public static User currentUser() {
        return currentUser;
    }

    private String objectId;
    private String nickname;
    private String deviceId;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
