package com.spotlight.platform.userprofile.api.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.spotlight.platform.userprofile.api.web.validation.ValidCommandType;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class UserCommandDto {
    @JsonProperty("userId")
    @NotNull
    private String userId;


    @JsonProperty("type")
    @ValidCommandType(message = "value is not correct Command")
    private String type;

    @JsonProperty("properties")
    private Map<String, Object> properties;

    public UserCommandDto() {
    }

    public UserCommandDto(String userId, String type, Map<String, Object> properties) {
        this.userId = userId;
        this.type = type;
        this.properties = properties;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }



    public enum CommandType {
        replace,
        increment,
        collect
    }

}
