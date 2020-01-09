package sqlparser.common.util;

import java.io.Serializable;

public class Pair<T1, T2> implements Serializable {
    private static final long serialVersionUID = -1;

    protected T1 first = null;
    protected T2 second = null;

    public Pair() {
    }

    public Pair(T1 a, T2 b) {
        this.first = a;
        this.second = b;
    }

    public static <T1, T2> Pair<T1, T2> newPair(T1 a, T2 b) {
        return new Pair<T1, T2>(a, b);
    }

    private static boolean equals(Object x, Object y) {
        return (x == null && y == null) || (x != null && x.equals(y));
    }

    public T1 getFirst() {
        return first;
    }

    public T1 getKey() {
        return first;
    }

    public void setFirst(T1 a) {
        this.first = a;
    }

    public T2 getSecond() {
        return second;
    }

    public T2 getValue() {
        return second;
    }

    public void setSecond(T2 b) {
        this.second = b;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean equals(Object other) {
        return other instanceof Pair && equals(first, ((Pair) other).first) && equals(second, ((Pair) other).second);
    }

    @Override
    public int hashCode() {
        if (first == null)
            return (second == null) ? 0 : second.hashCode() + 1;
        else if (second == null)
            return first.hashCode() + 2;
        else
            return first.hashCode() * 17 + second.hashCode();
    }

    @Override
    public String toString() {
        return "{" + getFirst() + "," + getSecond() + "}";
    }

}