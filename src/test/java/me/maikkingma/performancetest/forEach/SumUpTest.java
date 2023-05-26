package me.maikkingma.performancetest.forEach;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SumUpTest {

    private final SumUp sumUp = new SumUp();
    private static final Map<String, Long> arrayResults = new HashMap<>();
    private static final Map<String, Long> linkedResults = new HashMap<>();

    @AfterAll
    static void logResult() {
        System.out.println("""
                Array List Results:
                given size:""" + SumUp.ARRAY_SIZE +
                """
                
                ----------------------------------------""");
        arrayResults.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> System.out.println(entry.getKey() + ": time in ms: " + entry.getValue()));
        System.out.println("""
                ----------------------------------------

                Linked List Results:
                given size:""" + SumUp.ARRAY_SIZE +
                """
                
                ----------------------------------------""");
        linkedResults.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> System.out.println(entry.getKey() + ": time in ms: " + entry.getValue()));
    }

    @Nested
    class ArrayListTestSuite {
        @Test
        void testForIndexedLoop() {
            Long sum = 0L;
            var testList = sumUp.getArrayTestList();
            var start = System.currentTimeMillis();
            for (int i = 0, testListSize = testList.size(); i < testListSize; i++) {
                Long current = testList.get(i);
                sum = Long.sum(sum, current);
            }
            var finish = System.currentTimeMillis();
            assertEquals(sumUp.getArrayTargetSum(), sum);
            arrayResults.put("Indexed For", (finish-start));
        }

        @Test
        void testForLoop() {
            Long sum = 0L;
            var start = System.currentTimeMillis();
            for (Long current : sumUp.getArrayTestList()) {
                sum = Long.sum(sum, current);
            }
            var finish = System.currentTimeMillis();
            assertEquals(sumUp.getArrayTargetSum(), sum);
            arrayResults.put("For loop", (finish-start));
        }

        @Test
        void testWhileLoop() {
            Long sum = 0L;
            var testList = sumUp.getArrayTestList();
            int i = 0;
            var start = System.currentTimeMillis();
            while (i < testList.size()) {
                sum += testList.get(i);
                i++;
            }
            var finish = System.currentTimeMillis();
            assertEquals(sumUp.getArrayTargetSum(), sum);
            arrayResults.put("While loop", (finish-start));
        }

        @Test
        void testForEach() {
            AtomicReference<Long> sum = new AtomicReference<>(0L);
            var start = System.currentTimeMillis();
            sumUp.getArrayTestList().forEach(current -> sum.set(Long.sum(sum.get(), current)));
            var finish = System.currentTimeMillis();
            assertEquals(sumUp.getArrayTargetSum(), sum.get());
            arrayResults.put("ForEach" , (finish-start));
        }

        @Test
        void testStreamReduce() {
            var start = System.currentTimeMillis();
            Long reduce = sumUp.getArrayTestList().stream().reduce(0L, Long::sum);
            var finish = System.currentTimeMillis();
            assertEquals(sumUp.getArrayTargetSum(), reduce);
            arrayResults.put("Stream reduce" , (finish-start));
        }

        @Test
        void testParallelStream() {
            var list = sumUp.getArrayTestList();
            var start = System.currentTimeMillis();
            var sum = list.parallelStream().reduce(0L, Long::sum);
            var finish = System.currentTimeMillis();
            assertEquals(sumUp.getArrayTargetSum(), sum);
            arrayResults.put("Parallel Stream" , (finish - start));
        }

        @Test
        void testStreamCollect() {
            var start = System.currentTimeMillis();
            Long sum = sumUp.getArrayTestList().stream().collect(Collectors.summingLong(Long::longValue));
            var finish = System.currentTimeMillis();
            assertEquals(sumUp.getArrayTargetSum(), sum);
            arrayResults.put("Stream collect", (finish-start));
        }

        @Test
        void testStreamMapToLong() {
            var start = System.currentTimeMillis();
            var sum = sumUp.getArrayTestList().stream().mapToLong(Long::longValue).sum();
            var finish = System.currentTimeMillis();
            assertEquals(sumUp.getArrayTargetSum(), sum);
            arrayResults.put("Stream mapToLong", (finish-start));
        }

        @Test
        void testIterator() {
            Long sum = 0L;
            Iterator<Long> iterator = sumUp.getArrayTestList().iterator();
            var start = System.currentTimeMillis();
            while (iterator.hasNext()) {
                sum += iterator.next();
            }
            var finish = System.currentTimeMillis();
            assertEquals(sumUp.getArrayTargetSum(), sum);
            arrayResults.put("Iterator", (finish-start));
        }
    }

    @Nested
    class LinkedListTestSuite {
//        @Test
//        void testForIndexedLoop() {
//            Long sum = 0L;
//            var testList = sumUp.getLinkedTestList();
//            var start = System.currentTimeMillis();
//            for (int i = 0, testListSize = testList.size(); i < testListSize; i++) {
//                Long current = testList.get(i);
//                sum = Long.sum(sum, current);
//            }
//            var finish = System.currentTimeMillis();
//            assertEquals(sumUp.getLinkedTargetSum(), sum);
//            linkedResults.put("Indexed For", (finish-start));
//        }

        @Test
        void testForLoop() {
            Long sum = 0L;
            var start = System.currentTimeMillis();
            for (Long current : sumUp.getLinkedTestList()) {
                sum = Long.sum(sum, current);
            }
            var finish = System.currentTimeMillis();
            assertEquals(sumUp.getLinkedTargetSum(), sum);
            linkedResults.put("For loop", (finish-start));
        }

//        @Test
//        void testWhileLoop() {
//            Long sum = 0L;
//            var testList = sumUp.getLinkedTestList();
//            int i = 0;
//            var start = System.currentTimeMillis();
//            while (i < testList.size()) {
//                sum += testList.get(i);
//                i++;
//            }
//            var finish = System.currentTimeMillis();
//            assertEquals(sumUp.getLinkedTargetSum(), sum);
//            linkedResults.put("While loop", (finish-start));
//        }

        @Test
        void testForEach() {
            AtomicReference<Long> sum = new AtomicReference<>(0L);
            var start = System.currentTimeMillis();
            sumUp.getLinkedTestList().forEach(current -> sum.set(Long.sum(sum.get(), current)));
            var finish = System.currentTimeMillis();
            assertEquals(sumUp.getLinkedTargetSum(), sum.get());
            linkedResults.put("ForEach" , (finish-start));
        }

        @Test
        void testStreamReduce() {
            var start = System.currentTimeMillis();
            Long reduce = sumUp.getLinkedTestList().stream().reduce(0L, Long::sum);
            var finish = System.currentTimeMillis();
            assertEquals(sumUp.getLinkedTargetSum(), reduce);
            linkedResults.put("Stream reduce" , (finish-start));
        }

        @Test
        void testParallelStream() {
            var list = sumUp.getLinkedTestList();
            var start = System.currentTimeMillis();
            var sum = list.parallelStream().reduce(0L, Long::sum);
            var finish = System.currentTimeMillis();
            assertEquals(sumUp.getLinkedTargetSum(), sum);
            linkedResults.put("Parallel Stream" , (finish - start));
        }

        @Test
        void testStreamCollect() {
            var start = System.currentTimeMillis();
            Long sum = sumUp.getLinkedTestList().stream().collect(Collectors.summingLong(Long::longValue));
            var finish = System.currentTimeMillis();
            assertEquals(sumUp.getLinkedTargetSum(), sum);
            linkedResults.put("Stream collect", (finish-start));
        }

        @Test
        void testStreamMapToLong() {
            var start = System.currentTimeMillis();
            var sum = sumUp.getLinkedTestList().stream().mapToLong(Long::longValue).sum();
            var finish = System.currentTimeMillis();
            assertEquals(sumUp.getLinkedTargetSum(), sum);
            linkedResults.put("Stream mapToLong", (finish-start));
        }

        @Test
        void testIterator() {
            Long sum = 0L;
            Iterator<Long> iterator = sumUp.getLinkedTestList().iterator();
            var start = System.currentTimeMillis();
            while (iterator.hasNext()) {
                sum += iterator.next();
            }
            var finish = System.currentTimeMillis();
            assertEquals(sumUp.getLinkedTargetSum(), sum);
            linkedResults.put("Iterator", (finish-start));
        }
    }
}
