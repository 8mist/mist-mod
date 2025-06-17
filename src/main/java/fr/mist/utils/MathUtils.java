package fr.mist.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalDouble;

public class MathUtils {
    /**
     * Computes the median value of a list of Double numbers.
     *
     * @param list the list of numbers
     * @return an OptionalDouble containing the median, or empty if the list is empty
     */
    public static OptionalDouble median(List<Double> list) {
        if (list.isEmpty()) return OptionalDouble.empty();

        List<Double> sorted = new ArrayList<>(list);
        sorted.sort(Comparator.naturalOrder());

        int size = sorted.size();
        int mid = size / 2;

        if (size % 2 == 0) {
            double median = (sorted.get(mid - 1) + sorted.get(mid)) / 2.0;
            return OptionalDouble.of(median);
        } else {
            return OptionalDouble.of(sorted.get(mid));
        }
    }

    /**
     * Computes the given percentile (e.g. 25th, 75th) from a list of Double numbers.
     *
     * @param list the list of numbers
     * @param p    the percentile to calculate (0 to 100)
     * @return an OptionalDouble containing the percentile, or empty if the list is empty or p is invalid
     */
    public static OptionalDouble percentile(List<Double> list, double p) {
        if (list.isEmpty() || p < 0 || p > 100) return OptionalDouble.empty();

        List<Double> sorted = new ArrayList<>(list);
        sorted.sort(Comparator.naturalOrder());

        double index = (p / 100.0) * (sorted.size() - 1);
        int lower = (int) Math.floor(index);
        int upper = (int) Math.ceil(index);
        double weight = index - lower;

        if (upper >= sorted.size()) return OptionalDouble.of(sorted.get(lower));

        double interpolated = sorted.get(lower) * (1 - weight) + sorted.get(upper) * weight;
        return OptionalDouble.of(interpolated);
    }
}
