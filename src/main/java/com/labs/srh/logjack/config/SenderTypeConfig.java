package com.labs.srh.logjack.config;

import java.util.Objects;

public class SenderTypeConfig {

    private String type;

    public SenderTypeConfig() {
    }

    public SenderTypeConfig(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SenderTypeConfig that = (SenderTypeConfig) o;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
