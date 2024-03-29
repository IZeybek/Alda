package Task01;

import java.util.Iterator;

public class BinaryTreeDictionary<K extends Comparable<? super K>, V> implements Dictionary<K, V> {

    private V oldValue;
    private Node<K, V> root = null;
    private int size = 0;

    @Override
    public V insert(K key, V value) {
        root = insertR(key, value, root);
        if (root != null)
            root.parent = null;
        return oldValue;
    }

    private Node<K, V> insertR(K key, V value, Node<K, V> p) {
        if (p == null) {
            p = new Node(key, value);
            size++;
            oldValue = null;
        } else if (key.compareTo(p.key) < 0) {
            p.left = insertR(key, value, p.left);
            if (p.left != null) p.left.parent = p;//&& p.right.parent == null

        } else if (key.compareTo(p.key) > 0) {
            p.right = insertR(key, value, p.right);
            if (p.right != null) p.right.parent = p;//&& p.right.parent == null

        } else { // Schlüssel bereits vorhanden:
            oldValue = p.value;
            p.value = value;
        }
        p = balance(p);
        return p;
    }

    private Node<K, V> balance(Node<K, V> p) {
        if (p == null)
            return null;
        p.height = Math.max(getHeight(p.left), getHeight(p.right)) + 1;
        if (getBalance(p) == -2) {
            if (getBalance(p.left) <= 0)
                p = rotateRight(p);
            else
                p = rotateLeftRight(p);
        } else if (getBalance(p) == +2) {
            if (getBalance(p.right) >= 0)
                p = rotateLeft(p);
            else
                p = rotateRightLeft(p);
        }
        return p;
    }

    private Node<K, V> rotateRight(Node<K, V> p) {
        assert p.left != null;
        Node<K, V> q = p.left;
        p.left = q.right;
        if (p.left != null)
            p.left.parent = p;
        q.right = p;
        if (q.right != null)
            q.right.parent = q;
        p.height = Math.max(getHeight(p.left), getHeight(p.right)) + 1;
        q.height = Math.max(getHeight(q.left), getHeight(q.right)) + 1;
        return q;
    }

    private Node<K, V> rotateLeft(Node<K, V> p) {
        assert p.right != null;
        Node<K, V> q = p.right;
        p.right = q.left;
        if (p.right != null)
            p.right.parent = p;
        q.left = p;
        if (q.left != null)
            q.left.parent = q;
        p.height = Math.max(getHeight(p.right), getHeight(p.left)) + 1;
        q.height = Math.max(getHeight(q.right), getHeight(q.left)) + 1;
        return q;
    }

    private Node<K, V> rotateLeftRight(Node<K, V> p) {
        assert p.left != null;
        p.left = rotateLeft(p.left);
        return rotateRight(p);
    }

    private Node<K, V> rotateRightLeft(Node<K, V> p) {
        assert p.right != null;
        p.right = rotateRight(p.right);
        return rotateLeft(p);
    }

    @Override
    public V search(K key) {
        return searchR(key, root);
    }

    private V searchR(K key, Node<K, V> p) {

        if (p == null)
            return null;
        else if (key.compareTo(p.key) < 0)
            return searchR(key, p.left);
        else if (key.compareTo(p.key) > 0)
            return searchR(key, p.right);
        else
            return p.value;
    }

    @Override
    public V remove(K key) {
        root = removeR(key, root);
        if (root != null)
            root.parent = null;
        return oldValue;
    }

    private Node<K, V> removeR(K key, Node<K, V> p) {
        if (p == null) oldValue = null;
        else if (key.compareTo(p.key) < 0) {
            p.left = removeR(key, p.left);
            if (p.left != null)
                p.left.parent = p;
        } else if (key.compareTo(p.key) > 0) {
            p.right = removeR(key, p.right);
            if (p.right != null)
                p.right.parent = p;
        } else if (p.left == null || p.right == null) {
            oldValue = p.value;
            p = (p.left != null) ? p.left : p.right;
            size--;
        } else {
            // p muss gelöscht werden und hat zwei Kinder:
            MinEntry<K, V> min = new MinEntry<K, V>();
            p.right = getRemMinR(p.right, min);
            oldValue = p.value;
            p.key = min.key;
            p.value = min.value;
            size--;
        }
        p = balance(p);
        return p;
    }

    private Node<K, V> getRemMinR(Node<K, V> p, MinEntry<K, V> min) {
        assert p != null;
        if (p.left == null) {
            min.key = p.key;
            min.value = p.value;
            p = p.right;
        } else
            p.left = getRemMinR(p.left, min);
        p = balance(p);
        return p;
    }

    private static class MinEntry<K, V> {
        private K key;
        private V value;
    }

    @Override
    public int size() {
        return this.size;
    }

    private Node<K, V> leftMostDescendant(Node<K, V> p) {
        assert p != null;
        while (p.left != null)
            p = p.left;
        return p;
    }

    private Node<K, V> parentOfLeftMostAncestor(Node<K, V> p) {
        assert p != null;
        while (p.parent != null && p.parent.right == p)
            p = p.parent;
        return p.parent; // kann auch null sein
    }

    private int getHeight(Node<K, V> p) {
        if (p == null)
            return -1;
        else
            return p.height;
    }

    private int getBalance(Node<K, V> p) {
        if (p == null)
            return 0;
        else
            return getHeight(p.right) - getHeight(p.left);
    }

    static private class Node<K, V> {
        int height;
        K key;
        V value;
        private Node<K, V> parent;
        Node<K, V> left;
        Node<K, V> right;

        private Node(K k, V v) {
            height = 0;
            key = k;
            value = v;
            parent = null;
            left = null;
            right = null;
        }
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new Iterator<>() {

            Node<K, V> head;

            @Override
            public boolean hasNext() {
                if (head == null && root != null) {
                    head = leftMostDescendant(root);

                    assert head != null;
                } else if (head.right != null)
                    head = leftMostDescendant(head.right);
                else
                    head = parentOfLeftMostAncestor(head);
                return head != null;
            }

            @Override
            public Entry<K, V> next() {
                return new Entry<>(head.key, head.value);
            }
        };
    }


    public void prettyPrint() {
        prettyPrintR(root, 0);
    }

    private void prettyPrintR(Node<K, V> head, int height) {
        if (head == null)
            return;

        System.out.printf("%s%s head.parent: %s", "-".repeat(Math.max(0, height)), head.key, head.parent != null ? head.parent.key: "#");

        System.out.print("\n");
        prettyPrintR(head.left, height + 2);
        prettyPrintR(head.right, height + 2);
    }
}
