package com.cg.core;

public enum ImageOperations {
    ADD((a, b) -> a + b),
    SUB((a, b) -> a - b),
    MUL((a, b) -> a * b),
    DIVIDE((a, b) -> b == 0 ? 0 : a / b),
    OR((a, b) -> a | b),
    AND((a, b) -> a & b),
    XOR((a, b) -> a ^ b);

    private final Operator operator;

    ImageOperations(Operator operator) {
        this.operator = operator;
    }

    public int apply(int a, int b) {
        return operator.apply(a, b);
    }

    public interface Operator {
        int apply(int a, int b);
    }
}