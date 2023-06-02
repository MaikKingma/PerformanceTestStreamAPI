package me.maikkingma.performancetest.forEach;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Result {

    @Getter
    private final Map<TestEnvironment, ResultSet> resultsPerTestEnvironment;

    public Result(int outlierCroppingCount) {
        HashMap<TestEnvironment, ResultSet> map = new HashMap<>();
        Arrays.stream(TestEnvironment.values()).forEach(value -> map.put(value, new ResultSet(outlierCroppingCount)));
        this.resultsPerTestEnvironment = map;
    }

    public static class ResultSet {

        @Getter
        private final Map<Algortihm, List<Long>> measurementsPerAlgorithm;

        @Getter
        private final Map<Algortihm, Double> standardDeviationPerAlgorithm = new HashMap<>();

        @Getter
        private final Map<Algortihm, Double> averagePerAlgorithm = new HashMap<>();

        @Getter
        private final Map<Algortihm, Map<Deviation, List<Long>>> classifiedMeasurementsPerDeviationPerAlgorithm = new HashMap<>();

        private final int OUTLIER_CROPPING_COUNT;

        public ResultSet(int outlierCroppingCount) {
            Map<Algortihm, List<Long>> measurementsPerAlgorithm = new HashMap<>();
            Arrays.stream(Algortihm.values()).forEach(val -> measurementsPerAlgorithm.put(val, new ArrayList<>()));
            this.measurementsPerAlgorithm = measurementsPerAlgorithm;
            this.OUTLIER_CROPPING_COUNT = outlierCroppingCount;
        }

        public void addMeasurement(Algortihm algortihm, Long measurement) {
            measurementsPerAlgorithm.get(algortihm).add(measurement);
        }

        public void calculateStandardDeviation() {
            measurementsPerAlgorithm.forEach((key, values) -> {
                var sortedList = values.stream()
                        .mapToLong(value -> value)
                        .sorted()
                        .boxed()
                        .toList();
                var listCleanedFromOutliers = sortedList.subList(OUTLIER_CROPPING_COUNT, sortedList.size() - OUTLIER_CROPPING_COUNT);

                // Calculate mean
                var mean = listCleanedFromOutliers.stream()
                        .mapToLong(val -> val)
                        .average()
                        .orElse(0.0);

                // Calculate variance
                var variance = listCleanedFromOutliers.stream()
                        .mapToDouble(val -> Math.pow(val - mean, 2))
                        .average()
                        .orElse(0.0);

                // Calculate standard deviation
                var standardDeviation = Math.sqrt(variance);
                standardDeviationPerAlgorithm.put(key, standardDeviation);

                // Classify elements based on standard deviation
                classifiedMeasurementsPerDeviationPerAlgorithm.put(key, values.stream()
                        .collect(Collectors.groupingBy(val -> classify(val, mean, standardDeviation))));
            });
        }

        private static Deviation classify(long value, double mean, double standardDeviation) {
            double difference = Math.abs(value - mean);
            if (difference <= standardDeviation) {
                return Deviation.LESS_THAN_ONE_STANDARD_DEVIATION;
            } else if (difference <= 2 * standardDeviation) {
                return Deviation.MORE_THAN_ONE_STANDARD_DEVIATION;
            } else {
                return Deviation.MORE_THAN_TWO_STANDARD_DEVIATIONS;
            }
        }

        public void calculateAverage() {
            measurementsPerAlgorithm.forEach((key, value1) -> {
                var sortedArray = value1.stream()
                        .mapToLong(value -> value)
                        .sorted()
                        .boxed()
                        .toList();
                var result = sortedArray.subList(1, sortedArray.size() - 1).stream()
                        .mapToLong(value -> value)
                        .average()
                        .orElse(0.0);
                averagePerAlgorithm.put(key, result);
            });

        }
    }
}
