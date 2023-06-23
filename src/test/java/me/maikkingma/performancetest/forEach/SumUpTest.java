package me.maikkingma.performancetest.forEach;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SumUpTest {

    static List<SumUp> testBases;
    static Result arrayListResults = new Result(1);
    static Result getLinkedListResults = new Result(1);

    @BeforeAll
    static void beforeAll() {
        testBases = new ArrayList<>();
        Arrays.stream(TestEnvironment.values()).forEach(val -> testBases.add(new SumUp(val)));
    }

    @AfterAll
    static void afterAll() {
        Map<TestEnvironment, Result.ResultSet> resultsPerTestEnvironment = arrayListResults.getResultsPerTestEnvironment();
        resultsPerTestEnvironment.values().forEach(resultSet -> {
            resultSet.calculateAverage();
            resultSet.calculateStandardDeviation();
        });
        // Write to file
        try(BufferedWriter writer = new BufferedWriter(new FileWriter("results-" + new Date() + ".txt"))) {
            writer.write("Results:\n");
            resultsPerTestEnvironment.forEach((key, resultSet) -> {
                try {
                    writer.write("\n\nResultSet for list size: " + key.getValue() + "\n");
                    writer.write("\nAverage per algorithm:\n");
                    for (Map.Entry<Algortihm, Double> entry : resultSet.getAveragePerAlgorithm().entrySet()) {
                        writer.write(entry.getKey().toString() + ": " + entry.getValue() + "\n");
                    }
                    writer.write("\nStandard deviation per algorithm:\n");
                    for (Map.Entry<Algortihm, Double> entry : resultSet.getStandardDeviationPerAlgorithm().entrySet()) {
                        writer.write(entry.getKey().toString() + ": " + entry.getValue() + "\n");
                    }
                    writer.write("\nMeasurements per algorithm:\n");
                    for (Map.Entry<Algortihm, List<Long>> entry : resultSet.getMeasurementsPerAlgorithm().entrySet()) {
                        writer.write(entry.getKey().toString() + ": " + entry.getValue() + "\n");
                    }
                    writer.write("\nClassified measurements per deviation per algorithm:\n");
                    for (Map.Entry<Algortihm, Map<Deviation, List<Long>>> entry : resultSet.getClassifiedMeasurementsPerDeviationPerAlgorithm().entrySet()) {
                        writer.write(entry.getKey().toString() + ": " + entry.getValue() + "\n");
                    }
                    writer.write("\n\n---------------------------------------------------\n---------------------------------------------------");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nested
    class ArrayListTestSuite {
        @RepeatedTest(10000)
        void testForIndexedLoop() {
            testBases.parallelStream().forEach(testBase -> {
                Long sum = 0L;
                var testList = testBase.getArrayTestList();
                var start = System.currentTimeMillis();
                for (int i = 0, testListSize = testList.size(); i < testListSize; i++) {
                    Long current = testList.get(i);
                    sum = Long.sum(sum, current);
                }
                var finish = System.currentTimeMillis();
                assertEquals(testBase.getArrayTargetSum(), sum);
                arrayListResults.getResultsPerTestEnvironment()
                        .get(testBase.getTestEnvironment())
                        .addMeasurement(Algortihm.INDEXED_FOR, finish-start);
            });
        }

        @RepeatedTest(10000)
        void testForLoop() {
            testBases.parallelStream().forEach(testBase -> {
                Long sum = 0L;
                var testList = testBase.getArrayTestList();
                var start = System.currentTimeMillis();
                for (Long current : testBase.getArrayTestList()) {
                    sum = Long.sum(sum, current);
                }
                var finish = System.currentTimeMillis();
                assertEquals(testBase.getArrayTargetSum(), sum);
                arrayListResults.getResultsPerTestEnvironment()
                        .get(testBase.getTestEnvironment())
                        .addMeasurement(Algortihm.FOR_LOOP, finish-start);
            });
        }

        @RepeatedTest(10000)
        void testWhileLoop() {
            testBases.parallelStream().forEach(testBase -> {
                Long sum = 0L;
                var testList = testBase.getArrayTestList();
                int i = 0;
                var start = System.currentTimeMillis();
                while (i < testList.size()) {
                    sum += testList.get(i);
                    i++;
                }
                var finish = System.currentTimeMillis();
                assertEquals(testBase.getArrayTargetSum(), sum);
                arrayListResults.getResultsPerTestEnvironment()
                        .get(testBase.getTestEnvironment())
                        .addMeasurement(Algortihm.WHILE_LOOP, finish-start);
            });
        }

        @RepeatedTest(10000)
        void testForEach() {
            testBases.parallelStream().forEach(testBase -> {
                AtomicReference<Long> sum = new AtomicReference<>(0L);
                var start = System.currentTimeMillis();
                testBase.getArrayTestList().forEach(current -> sum.set(Long.sum(sum.get(), current)));
                var finish = System.currentTimeMillis();
                assertEquals(testBase.getArrayTargetSum(), sum.get());
                arrayListResults.getResultsPerTestEnvironment()
                        .get(testBase.getTestEnvironment())
                        .addMeasurement(Algortihm.FOREACH, finish-start);
            });
        }

        @RepeatedTest(10000)
        void testStreamReduce() {
            testBases.parallelStream().forEach(testBase -> {
                var start = System.currentTimeMillis();
                Long reduce = testBase.getArrayTestList().stream().reduce(0L, Long::sum);
                var finish = System.currentTimeMillis();
                assertEquals(testBase.getArrayTargetSum(), reduce);
                arrayListResults.getResultsPerTestEnvironment()
                        .get(testBase.getTestEnvironment())
                        .addMeasurement(Algortihm.STREAM_REDUCE, finish-start);
            });
        }

        @RepeatedTest(10000)
        void testParallelStream() {
            testBases.parallelStream().forEach(testBase -> {
                var list = testBase.getArrayTestList();
                var start = System.currentTimeMillis();
                var sum = list.parallelStream().reduce(0L, Long::sum);
                var finish = System.currentTimeMillis();
                assertEquals(testBase.getArrayTargetSum(), sum);
                arrayListResults.getResultsPerTestEnvironment()
                        .get(testBase.getTestEnvironment())
                        .addMeasurement(Algortihm.PARALLEL_STREAM, finish-start);
            });
        }

        @RepeatedTest(10000)
        void testStreamCollect() {
            testBases.parallelStream().forEach(testBase -> {
                var start = System.currentTimeMillis();
                Long sum = testBase.getArrayTestList().stream().collect(Collectors.summingLong(Long::longValue));
                var finish = System.currentTimeMillis();
                assertEquals(testBase.getArrayTargetSum(), sum);
                arrayListResults.getResultsPerTestEnvironment()
                        .get(testBase.getTestEnvironment())
                        .addMeasurement(Algortihm.STREAM_COLLECT, finish-start);
            });
        }

        @RepeatedTest(10000)
        void testStreamMapToLong() {
            testBases.parallelStream().forEach(testBase -> {
                var start = System.currentTimeMillis();
                var sum = testBase.getArrayTestList().stream().mapToLong(Long::longValue).sum();
                var finish = System.currentTimeMillis();
                assertEquals(testBase.getArrayTargetSum(), sum);
                arrayListResults.getResultsPerTestEnvironment()
                        .get(testBase.getTestEnvironment())
                        .addMeasurement(Algortihm.STREAM_MAPTOLONG, finish-start);
            });
        }

        @RepeatedTest(10000)
        void testIterator() {
            testBases.parallelStream().forEach(testBase -> {
                Long sum = 0L;
                Iterator<Long> iterator = testBase.getArrayTestList().iterator();
                var start = System.currentTimeMillis();
                while (iterator.hasNext()) {
                    sum += iterator.next();
                }
                var finish = System.currentTimeMillis();
                assertEquals(testBase.getArrayTargetSum(), sum);
                arrayListResults.getResultsPerTestEnvironment()
                        .get(testBase.getTestEnvironment())
                        .addMeasurement(Algortihm.ITERATOR, finish-start);
            });
        }
    }

//    @Nested
//    class LinkedListTestSuite {
//        @RepeatedTest(10000)
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
//            linkedResults.get("Indexed For).add((finish-start);
//        }
//
//        @RepeatedTest(10000)
//        void testForLoop() {
//            Long sum = 0L;
//            var start = System.currentTimeMillis();
//            for (Long current : sumUp.getLinkedTestList()) {
//                sum = Long.sum(sum, current);
//            }
//            var finish = System.currentTimeMillis();
//            assertEquals(sumUp.getLinkedTargetSum(), sum);
//            linkedResults.get("For loop").add(finish - start);
//        }
//
//        @RepeatedTest(10000)
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
//            linkedResults.get("While loop).add((finish-start);
//        }
//
//        @RepeatedTest(10000)
//        void testForEach() {
//            AtomicReference<Long> sum = new AtomicReference<>(0L);
//            var start = System.currentTimeMillis();
//            sumUp.getLinkedTestList().forEach(current -> sum.set(Long.sum(sum.get(), current)));
//            var finish = System.currentTimeMillis();
//            assertEquals(sumUp.getLinkedTargetSum(), sum.get());
//            linkedResults.get("ForEach").add(finish - start);
//        }
//
//        @RepeatedTest(10000)
//        void testStreamReduce() {
//            var start = System.currentTimeMillis();
//            Long reduce = sumUp.getLinkedTestList().stream().reduce(0L, Long::sum);
//            var finish = System.currentTimeMillis();
//            assertEquals(sumUp.getLinkedTargetSum(), reduce);
//            linkedResults.get("Stream reduce").add(finish - start);
//        }
//
//        @RepeatedTest(10000)
//        void testParallelStream() {
//            var list = sumUp.getLinkedTestList();
//            var start = System.currentTimeMillis();
//            var sum = list.parallelStream().reduce(0L, Long::sum);
//            var finish = System.currentTimeMillis();
//            assertEquals(sumUp.getLinkedTargetSum(), sum);
//            linkedResults.get("Parallel Stream").add(finish - start);
//        }
//
//        @RepeatedTest(10000)
//        void testStreamCollect() {
//            var start = System.currentTimeMillis();
//            Long sum = sumUp.getLinkedTestList().stream().collect(Collectors.summingLong(Long::longValue));
//            var finish = System.currentTimeMillis();
//            assertEquals(sumUp.getLinkedTargetSum(), sum);
//            linkedResults.get("Stream collect").add(finish - start);
//        }
//
//        @RepeatedTest(10000)
//        void testStreamMapToLong() {
//            var start = System.currentTimeMillis();
//            var sum = sumUp.getLinkedTestList().stream().mapToLong(Long::longValue).sum();
//            var finish = System.currentTimeMillis();
//            assertEquals(sumUp.getLinkedTargetSum(), sum);
//            linkedResults.get("Stream mapToLong").add(finish - start);
//        }
//
//        @RepeatedTest(10000)
//        void testIterator() {
//            Long sum = 0L;
//            Iterator<Long> iterator = sumUp.getLinkedTestList().iterator();
//            var start = System.currentTimeMillis();
//            while (iterator.hasNext()) {
//                sum += iterator.next();
//            }
//            var finish = System.currentTimeMillis();
//            assertEquals(sumUp.getLinkedTargetSum(), sum);
//            linkedResults.get("Iterator").add(finish - start);
//        }
//    }
}
