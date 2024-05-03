package MatchingEngine;

abstract class RedBlackTreeNode<T> {
    T left;
    T right;
    T parent;
    boolean isRed;

    RedBlackTreeNode() {
        this.isRed = true; // new nodes are always red
    }
}