package CoreUtils;

import java.util.List;

public class ScapeGoatTree<K extends Comparable<K>, V> extends ScapeGoatTreeInterface<K, V> {
    //root
    protected Node<K, V> root;
    //total number of nodes
    protected int nodeCount = 0;
    /**
     * max node count required to use rebuild in remove
     *
     * @implNote maxNodeCount enables scapegoat trees to be well-balanced over time. They store
     *   this additional value with the tree data structure.  maxNodeCount simply represents
     *   the highest achieved NodeCount.  maxNodeCount is set to nodeCount whenever the entire tree
     *   is rebalanced (i.e. rebuilt is called on root), and after adding a node it is
     *   set to max(maxNodeCount, nodeCount).
     */
    protected int maxNodeCount = 0;

    //alpha parameter defined in abstract parent class ("ALPHA_THRESHOLD")

    /**
     * Constructs an empty scapegoat tree
     */
    public ScapeGoatTree(){
        root = null;
    }

    /**
     * Constructs a scapegoat tree with a root.
     * @param rootKey root key
     * @param rootData root data to store
     */
    public ScapeGoatTree(K rootKey, V rootData) {
        root = new Node<K, V>(rootKey, rootData, null, null, null);
        //nodeCount++;
        nodeCount = 1;
        maxNodeCount = 1;
    }


    /**
     * This might be helpful for your debugging
     */

    public void printTreeInorder(){
        //List<Node<K, V>> nodes = inorder(root);

    }
    /**
     * This might be helpful for your debugging
     */
    public void printTreePreorder(){
        //List<Node<K, V>> nodes = preorder(root);

    }

    /**
     * Retrieves the root of the scapegoat tree, or <code>null</code> if none exists.
     *
     * @return the root of the scapegoat tree, or <code>null</code> if none exists
     */
    @Override
    public Node<K, V> root() {
        //t
        //return null;
        return root;
    }

    /**
     * Finds the first scapegoat node and returns it.  The first scapegoat node is the first node at or above
     * the passed in node which does not satisfy the alpha-weight-balanced property.  This function is meant
     * to be used after you insert a node in the tree.  Since this function is only supposed to be called
     * when an insertion breaks the alpha-weight-balanced property, we know that we must find at least one
     * node that isn't alpha-weight-balanced.
     * <p>
     * Hint: when you add a new node, the possible nodes that do not meet the alpha-weight-balanced property
     * are on the path from the inserted node to the root.
     *
     * @param node newly inserted node to start searching at
     * @return the first scapegoat node
     * @implNote this function is not individually tested, it is for your convenience in implementation.
     */
    @Override
    protected Node<K, V> scapeGoatNode(Node<K, V> node) {
        //
        Node<K, V> curr = node;
        while (curr != null && curr.parent != null) {
            if (sizeOfSubtree(curr) > ALPHA_THRESHOLD * sizeOfSubtree(curr.parent)) {
                return curr.parent;
            }
            curr = curr.parent;
        }
        //another situation
        curr = node;
        while (curr != null && curr.parent != null) {
            if (sizeOfSubtree(curr.parent) > ALPHA_THRESHOLD * sizeOfSubtree(curr)) {
                return curr.parent;
            }
            curr = curr.parent;
        }
//        java.util.Map<Node<K,V>, Integer> cache = new java.util.HashMap<>();
//        for (Node<K,V> n : path) {
//            if (!cache.containsKey(n)) cache.put(n, sizeOfSubtree(n));
//            if (n.parent != null && !cache.containsKey(n.parent)) cache.put(n.parent, sizeOfSubtree(n.parent));
//        }
//        // 找第一个违反平衡的
//        for (Node<K,V> n : path) {
//            if (n.parent == null) continue;
//            int sz = cache.get(n);
//            int parentSz = cache.get(n.parent);
//            if (sz > ALPHA_THRESHOLD * parentSz) {
//                return n.parent;
//            }
//        }
        return null;
    }

    /**
     * Rebuilds the subtree rooted at this node to be a perfectly balanced BST.
     * <p>
     * One approach is to get the subtree elements in some sorted order (for you to think about). Then,
     * we can build a perfectly balanced BST.  The main idea is to rebuild the subtree rooted at this
     * node into a perfectly balanced BST and then return the new root.  You could (but are not required
     * to) use a recursive function.  The middle of a list is defined as floor(size()/2).
     *
     * @param node root of subtree to rebuild
     * @return the new root of the balanced subtree
     * @implNote this function is not individually tested, it is for your convenience in implementation.
     */
    @Override
    protected Node<K, V> rebuild(Node<K, V> node) {
        //
        //return null;
        List<Node<K, V>> list = inorder(node);
        if (list.isEmpty()) return null;
        //return buildBalanced(list, 0, list.size() - 1, null);
        return buildBalancedClean(list, 0, list.size() - 1, null);
    }

    private Node<K,V> buildBalanced(List<Node<K, V>> nodes, int lo, int hi, Node<K, V> parent) {
        if (lo > hi) return null;
        int mid = (lo + hi) / 2;
        Node<K, V> n = nodes.get(mid);
        n.left = buildBalanced(nodes, lo, mid - 1, n);
        n.right = buildBalanced(nodes, mid + 1, hi, n);
        n.parent = parent;
        return n;
    }
    //add cleam
    private Node<K, V> buildBalancedClean(List<Node<K, V>> nodes, int lo, int hi, Node<K, V> parent) {
        if (lo > hi) return null;
        int mid = (lo + hi) / 2;
        Node<K, V> src = nodes.get(mid);
        // 避免旧指针遗留造成cycle
        Node<K, V> n = new Node<>(src.key, src.value, parent, null, null);
        n.left = buildBalancedClean(nodes, lo, mid - 1, n);
        n.right = buildBalancedClean(nodes, mid + 1, hi, n);
        return n;
    }

    /**
     * Adds an element to the scapegoat tree. Passing key=null will not change the state of the tree.
     * Some guidance is provided below:
     * <p>
     * 1. Find the insertion point. Ensure you know the depth you are inserting at, as it is useful later.
     * 2. If that data already exists in the tree, skip inserting that data.
     * 3. Insert the new data
     * 4. Check if the tree is still alpha-weight-balanced. By the theory on the wiki page, we know we can
     * check this by making sure the tree is still alpha-height-balanced.
     * 5. If not, rebalance. You will need to find the scapegoat node.
     * 6. The entire subtree rooted at the scapegoat node will need to be rebuilt using <code>rebuild()</code> above
     * 7. Connect new subtree back to main tree correctly
     * <p>
     * The above steps are based on the wikipedia article provided in the handout and at the top of this file.
     *
     * @param key   key to insert
     * @param value value to associate with key
     */
    @Override
    public void add(K key, V value) {
        //
        if (key == null) return;
        if (root == null) {
            root = new Node<>(key, value, null, null, null);
            nodeCount = 1;
            maxNodeCount = 1;
            return;
        }
        Node<K, V> curr = root;
        Node<K, V> parent = null;
        int cmp = 0;
        int depth = 0;
        while (curr != null) {
            parent = curr;
            cmp = key.compareTo(curr.key);
            if (cmp == 0) return; // duplicate
            depth++;
            if (cmp < 0) curr = curr.left;
            else curr = curr.right;
        }
        Node<K, V> newNode = new Node<>(key, value, parent, null, null);
        if (cmp < 0) parent.left = newNode;
        else parent.right = newNode;
        nodeCount++;
        double threshold = Math.log(nodeCount) / Math.log(1.0 / ALPHA_THRESHOLD);
        if (depth + 1 > threshold) {
            Node<K, V> scapegoat = scapeGoatNode(newNode);
            if (scapegoat != null) {
                Node<K, V> rebuilt = rebuild(scapegoat);
                if (scapegoat.parent == null) {
                    root = rebuilt;
                    if (root != null) root.parent = null;
                } else {
                    if (scapegoat.parent.left == scapegoat) scapegoat.parent.left = rebuilt;
                    else scapegoat.parent.right = rebuilt;
                    if (rebuilt != null) rebuilt.parent = scapegoat.parent;
                }
            }
        }
        if (nodeCount > maxNodeCount) maxNodeCount = nodeCount;

    }

    /**
     * Removes an element from the tree. Does not change the tree if key does not exist in it.
     * Some guidance is provided below:
     * <p>
     * 1. Find the deletion point (if it exists).
     * 2. Deletion is done in the way you would delete a node from a regular BST.  The policy for this should be
     * the same as the one in the professor's slides (there are multiple correct policies, follow the one on
     * the slides).  The one difference is that we will use the successor node instead of the predecessor node
     * (see {@link #succNode})
     * 3. Slight modifications for scapegoat based on your implementation may be required.
     * 4. After deletion, if nodeCount <= alphaweight * MaxNodeCount, then we rebuild the entire tree around the
     * "root" again (i.e. call rebuild and ensure the new root follows the properties of the root).
     *
     * @param key key to remove
     */
    @Override
    public void remove(K key) {
        //
        if (key == null || root == null) return;
        Node<K, V> target = findNode(key);

        if (target == null) return;

        if (target.left != null && target.right != null) {
            Node<K, V> succ = succNode(target);
            target.key = succ.key;
            target.value = succ.value;
            target = succ;
        }
        Node<K, V> child = (target.left != null) ? target.left : target.right;
        if (child != null) child.parent = target.parent;
        if (target.parent == null) {
            root = child;
        } else if (target.parent.left == target) {
            target.parent.left = child;
        } else {
            target.parent.right = child;
        }
        nodeCount--;
        if (nodeCount <= ALPHA_THRESHOLD * maxNodeCount) {
            root = rebuild(root);
            if (root != null) root.parent = null;
            maxNodeCount = nodeCount;
        }

    }

    /**
     * Returns the node associated with the given key.
     *
     * (be careful will null...)
     *
     * @param key key to search for
     * @return node associated with key, or <code>null</code> if item does not exist
     * @apiNote This function is protected because we don't want outside classes to have access to
     * the internal structure of our tree, which is possible through Node's interface.  Thus, we
     * have an internal function to find a Node and an external function which just returns the
     * value, preventing external classes from modifying the tree.
     */
    @Override
    protected Node<K, V> findNode(K key) {
        //
        if (key == null) return null;
        Node<K, V> curr = root;
        while (curr != null) {
            int cmp = key.compareTo(curr.key);
            if (cmp == 0) return curr;
            else if (cmp < 0) curr = curr.left;
            else curr = curr.right;
        }
        return null;
    }

    /**
     * Empties the tree, resetting all pertinent variables.
     */
    @Override
    public void clear() {
        //
        root = null;
        nodeCount = 0;
        maxNodeCount = 0;
    }

    /**
     * Returns the number of nodes this tree contains.
     * @return number of nodes in the tree
     */
    public int size(){
        //return -1;
        return nodeCount;
    }
    private int sizeOfSubtree(Node<K, V> node, java.util.Set<Node<K, V>> seen) {
        if (node == null) return 0;
        if (seen.contains(node)) {
            // 发现环,warning
            return 0;
        }
        seen.add(node);
        return 1 + sizeOfSubtree(node.left, seen) + sizeOfSubtree(node.right, seen);
    }
}

