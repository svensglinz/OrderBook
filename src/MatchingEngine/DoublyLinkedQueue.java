package MatchingEngine;
import MatchingEngine.OrderTypes.Order;

import java.util.Iterator;
/**
 * Custom implementation of a queue using a doubly linked list
 * and an internal hash map which holdes <T, Node> pairs to find
 * and remove and element inside the queue in constant time
 * <br>
 * The implementation implements the iterable interface and an iterator
 * can be used to iterate over the nodes in order of entry as well as to delete
 * nodes in the queue
 * @param <T>
 */

class DoublyLinkedQueue<T extends DoublyLinkedQueueNode<T>> implements Iterable<T> {
    private T head;
    private T tail;

    /**
     * Implements an Iterator for the DoublyLinkedQueue that iterates over the elements in
     * the queue in FIFO order
     */
    class QueueIterator implements Iterator<T>{
        T prev = null;
        T cur = head;

        @Override
        public boolean hasNext() {
            return cur != null;
        }

        @Override
        public T next() {
            prev = cur;
            cur = cur.next;
            return prev;
        }

        @Override
        public void remove() {

            if (prev == head){
                head = cur;
            }
            if (prev == tail){
                tail = prev.prev;
            }
            if (prev.prev != null){
                prev.prev.next = prev.next;
            }
            if (prev.next != null){
                prev.next.prev = prev.prev;
            }
        }
    }

    public void add(T item){
        if(head == null){
            head = item;
            tail = item;
        } else {
            tail.next = item;
            item.prev = tail;
            tail = item;
        }
    }

    public void remove(T item){
        if(item == null)
            return;

        if (item == head)
            head = head.next;
        if (item == tail)
            tail = tail.prev;
        if (item.prev != null)
            item.prev.next = item.next;
        if (item.next != null)
            item.next.prev = item.prev;
    }

    public T removeFirst(){
        T n = head;
        head = head.next;
        head.prev = null;

        return n;
    }

    public void addLast(T data){
        if (head == null){
            head = data;
            tail = data;
        } else {
            tail.next = data;
            data.prev = tail;
            tail = data;
        }
    }

    public boolean isEmpty(){
        return head == null;
    }

    @Override
    public Iterator<T> iterator() {
        return new QueueIterator();
    }
}

