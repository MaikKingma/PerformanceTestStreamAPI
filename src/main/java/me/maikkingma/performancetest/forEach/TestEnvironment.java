package me.maikkingma.performancetest.forEach;

import lombok.Getter;

public enum TestEnvironment {
    Size_10000(10000),
    Size_100000(100000),
    Size_1000000(1000000),
    Size_10000000(10000000),
    Size_100000000(100000000),
    ;

    @Getter
    private final int value;

    TestEnvironment(int i) {
        value=i;
    }
}
