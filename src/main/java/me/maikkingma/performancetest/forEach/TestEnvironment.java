package me.maikkingma.performancetest.forEach;

import lombok.Getter;

public enum TestEnvironment {

    Size_10000000(10000000),
    Size_20000000(20000000),
    Size_30000000(30000000),
    Size_40000000(40000000),

    ;

    @Getter
    private final int value;

    TestEnvironment(int i) {
        value=i;
    }
}
