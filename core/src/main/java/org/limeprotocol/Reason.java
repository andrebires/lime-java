package org.limeprotocol;

import org.limeprotocol.util.StringUtils;

public class Reason {
    private int code;
    private String description;

    @SuppressWarnings("unused")
    public Reason(){}

    public Reason(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("%s (Code %d)", description, code);
    }
}
