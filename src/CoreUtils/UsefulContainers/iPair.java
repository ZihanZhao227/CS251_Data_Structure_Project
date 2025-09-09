//package CoreUtils.UsefulContainers;
//
///**
// * int pair container
// */
//public class iPair extends Pair<Integer, Integer> {
//    //simple constructor
//    public iPair(int a, int b) { super(a, b);
//    }
//}
package CoreUtils.UsefulContainers;

public class iPair {
    public final int a;
    public final int b;

    public iPair(int a, int b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof iPair)) return false;
        iPair that = (iPair) o;
        return this.a == that.a && this.b == that.b;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(a) * 31 + Integer.hashCode(b);
    }

    @Override
    public String toString() {
        return "(" + a + "," + b + ")";
    }
}

//package CoreUtils.UsefulContainers;
//
//public class iPair {
//    public final int first;
//    public final int second;
//
//    public iPair(int first, int second) {
//        this.first = first;
//        this.second = second;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof iPair)) return false;
//        iPair that = (iPair) o;
//        return this.first == that.first && this.second == that.second;
//    }
//
//    @Override
//    public int hashCode() {
//        return Integer.hashCode(first) * 31 + Integer.hashCode(second);
//    }
//
//    @Override
//    public String toString() {
//        return "(" + first + "," + second + ")";
//    }
//}
