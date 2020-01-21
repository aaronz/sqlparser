package sqlparser.common.util;

public class StringUtil {
    public static String[] subArray(String[] array, int start, int endExclusive) {
        if (start < 0 || start > endExclusive || endExclusive > array.length)
            throw new IllegalArgumentException();
        String[] result = new String[endExclusive - start];
        System.arraycopy(array, start, result, 0, endExclusive - start);
        return result;
    }
}