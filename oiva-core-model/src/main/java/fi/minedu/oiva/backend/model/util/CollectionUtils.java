package fi.minedu.oiva.backend.model.util;

import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static org.jooq.lambda.Seq.seq;

public class CollectionUtils {

    static public <E> String mkString(final List<E> list, final Function<E, String> stringify, final String delimiter) {
        return list.stream().map(stringify).collect(Collectors.joining(delimiter));
    }

    static public String mkString(final String[] arr, final String delimiter) {
        return mkString(Arrays.asList(arr), (String x) -> x, delimiter);
    }

    public static <K, V> Map<K, V> mapOf(final Tuple2<K, V>... values) {
        return arr2seq(values).collect(tuples2Map());
    }

    public static <K, V> Collector<Tuple2<K, V>, ?, Map<K, V>> tuples2Map() {
        final Function<Tuple2<K, V>, K> keyMapper = x -> x.v1;
        final Function<Tuple2<K, V>, V> valueMapper = x -> x.v2;
        return Collectors.toMap(keyMapper, valueMapper);
    }

    public static <T> Seq<T> arr2seq(final T[] arr) {
        return Seq.seq(Arrays.asList(arr));
    }

    public static <T> List<T> union(final List<T> list1, final List<T> list2) {
        final ArrayList<T> result = new ArrayList<>(list1);
        result.addAll(list2);
        return result;
    }
}
