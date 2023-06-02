package me.maikkingma.performancetest.forEach;

import lombok.Getter;

public enum Algortihm {
    FOR_LOOP("For loop"),
    FOREACH("ForEach"),
    INDEXED_FOR("Indexed For"),
    ITERATOR("Iterator"),
    PARALLEL_STREAM("Parallel Stream"),
    STREAM_COLLECT("Stream collect"),
    STREAM_MAPTOLONG("Stream mapToLong"),
    STREAM_REDUCE("Stream reduce"),
    WHILE_LOOP("While loop"),
    ;

    @Getter
    private final String text;

    Algortihm(String s) {
        text = s;
    }
}
