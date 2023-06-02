package me.maikkingma.performancetest.forEach;


import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@Getter
public class SumUp {

    private final TestEnvironment testEnvironment;
    private final Long arrayTargetSum;
    private final Long linkedTargetSum;
    private final List<Long> arrayTestList;
    private final List<Long> linkedTestList;

    public SumUp(TestEnvironment testEnvironment) {
        this.testEnvironment = testEnvironment;
        Random random = new Random();
        // Init array list test base
        int testSize = testEnvironment.getValue();
        this.arrayTestList = new ArrayList<>(testSize);
        for (int i = 0; i < testSize; i++) {
            arrayTestList.add(i, random.nextLong(10));
        }
        arrayTargetSum = arrayTestList.parallelStream().reduce(0L, Long::sum);

        // init linked list test base
        this.linkedTestList = new LinkedList<>();
        for (int i = 0; i < testSize; i++) {
            linkedTestList.add(i, random.nextLong(10));
        }
        this.linkedTargetSum = linkedTestList.parallelStream().reduce(0L, Long::sum);
    }
}
