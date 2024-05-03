package MatchingEngine;

import java.util.Comparator;

/**
 * Implementation of a Red Black binary tree with additional references to the maximum
 * and minimum node in the tree for update of best bid / ask in O(1) in the order book
 */
class RedBlackTree<T extends RedBlackTreeNode<T>> {
    private int size;
    private T root;
    private T minimum;
    private T maximum;
    private final Comparator<T> comparator;

    public RedBlackTree(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    // Get method
    public T get(T key) {
        T current = root;
        while (current != null) {
            int cmp = comparator.compare(key, current);
            if (cmp == 0) {
                return current;
            } else if (cmp < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return null;
    }

    public void insert(T data) {
        // existing node cannot be updated
        T existingNode = get(data);
        if (existingNode != null) {
            return;
        }

        if (root == null) {
            root = data;
            minimum = data;
            maximum = data;
        } else {
            insertIterative(data);
        }
        fixTreeAfterInsert(data);

        // potentially update minimum and maximum
        if (minimum == null || comparator.compare(data, minimum) < 0){
            minimum = data;
        }
        if (maximum == null || comparator.compare(data, maximum) > 0){
            maximum = data;
        }
        size++;
    }

    private void insertIterative(T n) {
        T parent = null;
        T current = root;

        while (current != null) {
            parent = current;
            int cmp = comparator.compare(n, current);
            if (cmp < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }

        n.parent = parent;
        if (parent == null){
            root = n;
        } else if (comparator.compare(n, parent) < 0){
            parent.left = n;
        } else {
            parent.right = n;
        }
    }


    private void fixTreeAfterInsert(T n) {
        while (n != null && n != root && n.parent != null && n.parent.isRed) {
            if (n.parent.parent == null) {
                break; // Add null checks here
            }

            if (n.parent == n.parent.parent.left) {
                T uncle = (n.parent.parent.right != null) ? n.parent.parent.right : null;
                if (uncle != null && uncle.isRed) {
                    n.parent.isRed = false;
                    uncle.isRed = false;
                    n.parent.parent.isRed = true;
                    n = n.parent.parent;
                } else {
                    if (n == n.parent.right) {
                        n = n.parent;
                        rotateLeft(n);
                    }
                    if (n.parent != null && n.parent.parent != null) {
                        n.parent.isRed = false;
                        n.parent.parent.isRed = true;
                        rotateRight(n.parent.parent);
                    }
                }
            } else {
                T uncle = (n.parent.parent.left != null) ? n.parent.parent.left : null;
                if (uncle != null && uncle.isRed) {
                    n.parent.isRed = false;
                    uncle.isRed = false;
                    n.parent.parent.isRed = true;
                    n = n.parent.parent;
                } else {
                    if (n == n.parent.left) {
                        n = n.parent;
                        rotateRight(n);
                    }
                    if (n.parent != null && n.parent.parent != null) {
                        n.parent.isRed = false;
                        n.parent.parent.isRed = true;
                        rotateLeft(n.parent.parent);
                    }
                }
            }
        }
        if (root != null) {
            root.isRed = false;
        }
    }

    private void rotateLeft(T x) {
        T y = x.right;
        if (y == null)
            return;
        x.right = y.left;
        if (y.left != null) {
            y.left.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == null) {
            root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }
        y.left = x;
        x.parent = y;
    }

    private void rotateRight(T y) {
        T x = y.left;
        if (x == null)
            return;
        y.left = x.right;
        if (x.right != null) {
            x.right.parent = y;
        }
        x.parent = y.parent;
        if (y.parent == null) {
            root = x;
        } else if (y == y.parent.right) {
            y.parent.right = x;
        } else {
            y.parent.left = x;
        }
        x.right = y;
        y.parent = x;
    }

    public void delete(T node) {
        T child;
        if (node.left != null && node.right != null) {
            T predecessor = maximum(node.left);
            node.left.parent = null; // remove link to predecessor
            node.left = null;
            node.right = null;
            node.parent = null;
            node = predecessor;
        }

        if (node.left != null) {
            child = node.left;
        } else {
            child = node.right;
        }

        if (!node.isRed) {
            node.isRed = (child == null || child.isRed);
            deleteFixUp(child);
        }

        if (node.parent == null) {
            root = child;
        } else {
            if (node == node.parent.left) {
                node.parent.left = child;
            } else {
                node.parent.right = child;
            }
        }

        if (child != null) {
            child.parent = node.parent;
        }

        if (node == minimum) {
            minimum = successor(node);
        }

        if (node == maximum) {
            maximum = predecessor(node);
        }
        size--;
    }

    private void deleteFixUp(T x) {
        while (x != null && x != root && !x.isRed) {
            if (x == x.parent.left) {
                T w = x.parent.right;

                if (w != null && w.isRed) {
                    w.isRed = false;
                    x.parent.isRed = true;
                    rotateLeft(x.parent);
                    w = x.parent.right;
                }
                if (w != null && !w.left.isRed && !w.right.isRed) {
                    w.isRed = true;
                    x = x.parent;
                } else {
                    if (w != null && !w.right.isRed) {
                        w.left.isRed = false;
                        w.isRed = true;
                        rotateRight(w);
                        w = x.parent.right;
                    }
                    if (w != null) {
                        w.isRed = x.parent.isRed;
                    }
                    x.parent.isRed = false;
                    if (w != null && w.right != null) {
                        w.right.isRed = false;
                    }
                    rotateLeft(x.parent);
                    x = root;
                }
            } else {
                T w = x.parent.left;
                if (w != null && w.isRed) {
                    w.isRed = false;
                    x.parent.isRed = true;
                    rotateRight(x.parent);
                    w = x.parent.left;
                }
                if (w != null && !w.right.isRed && !w.left.isRed) {
                    w.isRed = true;
                    x = x.parent;
                } else {
                    if (w != null && !w.left.isRed) {
                        w.right.isRed = false;
                        w.isRed = true;
                        rotateLeft(w);
                        w = x.parent.left;
                    }
                    if (w != null) {
                        w.isRed = x.parent.isRed;
                    }
                    x.parent.isRed = false;
                    if (w != null && w.left != null) {
                        w.left.isRed = false;
                    }
                    rotateRight(x.parent);
                    x = root;
                }
            }
        }
        if (x != null) {
            x.isRed = false;
        }
    }

    // Find minimum node in subtree rooted at given node
    private T minimum(T node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    // Find maximum node in subtree rooted at given node
    private T maximum(T node) {
        while (node.right != null) {
            node = node.right;
        }
        return node;
    }

    // Find successor of a given node
    private T successor(T node) {
        if (node.right != null) {
            return minimum(node.right);
        }
        T parent = node.parent;
        while (parent != null && node == parent.right) {
            node = parent;
            parent = parent.parent;
        }
        return parent;
    }

    // Find predecessor of a given node
    public T predecessor(T node) {
        if (node.left != null) {
            return maximum(node.left);
        }
        T parent = node.parent;
        while (parent != null && node == parent.left) {
            node = parent;
            parent = parent.parent;
        }
        return parent;
    }

    // Search method
    private T search(T root, T data) {
        if (root == null || root.equals(data)) {
            return root;
        }
        int cmp = comparator.compare(data, root);
        if (cmp < 0) {
            return search(root.left, data);
        } else {
            return search(root.right, data);
        }
    }

    // getter methods
    public T getMinimum() {return minimum;}
    public T getMaximum() {return maximum;}
    public int size(){return size;}
}
