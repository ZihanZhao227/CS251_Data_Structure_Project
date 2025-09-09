package CoreUtils;

import java.util.ArrayList;
import java.util.List;

public class ScapeGoatIntKey<V> extends ScapeGoatTree<Integer, V> {
    /**
     * Constructs an empty scapegoat tree
     */
    public ScapeGoatIntKey() { super(); }

    /**
     * Constructs a scapegoat tree with a root.
     *
     * @param rootKey  root key
     * @param rootData root data to store
     */
    public ScapeGoatIntKey(Integer rootKey, V rootData) { super(rootKey, rootData); }

    /**
     * Returns the data associated with the given range of keys, inclusive ( [start, end] ).  The data is sorted
     *   by key.
     *
     * It may be helpful to use a recursive function that acts similar to inorder().
     *
     * @param start starting key to retrieve
     * @param end ending key to retrieve
     * @return a sorted list of values associated with the range of keys, or an empty list if no item exists in that range
     */
    public List<V> getRange(int start, int end){
        //
        List<V> res = new ArrayList<>();
        if (root == null) return res;
        collectInRange(root, start, end, res);
        return res;

    }

    private void collectInRange(Node<Integer, V> node, int start, int end, List<V> acc) {
        if (node == null) return;
        if (node.key.compareTo(start) > 0) {
            collectInRange(node.left, start, end, acc);
        }
        if (node.key >= start && node.key <= end) {
            acc.add(node.value);
        }
        if (node.key.compareTo(end) < 0) {
            collectInRange(node.right, start, end, acc);
        }
    }
}