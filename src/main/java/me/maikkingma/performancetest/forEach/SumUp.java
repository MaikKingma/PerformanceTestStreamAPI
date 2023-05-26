package me.maikkingma.performancetest.forEach;


import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@Getter
public class SumUp {

    public static final int ARRAY_SIZE = 100000000;

    private final Long arrayTargetSum;
    private final Long linkedTargetSum;
    private final List<Long> arrayTestList;
    private final List<Long> linkedTestList;

    public SumUp() {
        Random random = new Random();
        // Init array list test base
        this.arrayTestList = new ArrayList<>(ARRAY_SIZE);
        for (int i = 0; i < ARRAY_SIZE; i++) {
            arrayTestList.add(i, random.nextLong(10));
        }
        arrayTargetSum = arrayTestList.parallelStream().reduce(0L, Long::sum);

        // init linked list test base
        this.linkedTestList = new LinkedList<>();
        for (int i = 0; i < ARRAY_SIZE; i++) {
            linkedTestList.add(i, random.nextLong(10));
        }
        this.linkedTargetSum = linkedTestList.parallelStream().reduce(0L, Long::sum);
    }
}
