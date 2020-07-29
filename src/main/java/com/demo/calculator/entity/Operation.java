package com.demo.calculator.entity;

import org.apache.commons.lang3.StringUtils;

public enum Operation {
    INSERT,
    UPDATE,
    CANCEL;

    public static Operation of(String inputOperation) {
        for (Operation operation : Operation.values()) {
            if (StringUtils.equalsIgnoreCase(operation.name(), inputOperation)) {
                return operation;
            }
        }
        return null;
    }
}
