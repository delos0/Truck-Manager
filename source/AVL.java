import java.util.ArrayList;

public class AVL <E extends Comparable<E>>{
    private Node<E> root;
    private long size = 0;

    public AVL() {}

    public void insert(E element) {
        root = insert(element, root);

    }
    private Node<E> insert(E element, Node<E> current) {
        if(current == null) {
            return newNode(element);
        }
        if (element.compareTo(current.element) < 0) {
            current.left = insert(element, current.left);
        }
        else if (element.compareTo(current.element) > 0) {
            current.right = insert(element, current.right);
        }

        size++;
        return balance(current);
    }

    public void delete(E element) {
        root = delete(element, root);
    }

    private Node<E> delete(E element, Node<E> current) {
        if (current == null) {
            return null;
        }
        if (element.compareTo(current.element) == 0) {
            if (current.left == null && current.right == null) return null;
            if (current.left == null) return current.right;
            if (current.right == null) return current.left;
            E min = findMin(current.right);
            current.element = min;
            current.right = delete(min, current.right);
            return current;
        }
        if (element.compareTo(current.element) < 0) {
            current.left = delete(element, current.left);
            return current;
        }
        current.right = delete(element, current.right);

        return balance(current);
    }
    private E findMin(Node<E> root) {
        if(root.left == null)  return root.element;
        else return findMin(root.left);
    }
    protected Node<E> newNode(E e) {
        return new Node<>(e);
    }

    public Node<E> getRoot() {
        return root;
    }

    public Node<E> search(E element) {
        Node<E> current = root;
        while (current != null) {
            if(element.compareTo(current.element) < 0) {
                current = current.left;
            }
            else if (element.compareTo(current.element) > 0) {
                current = current.right;
            }
            else {
                return current;
            }
        }
        return null;
    }

    public void inorder(Node<E> root, ArrayList<Node<E>> nodes, Node<E> target) {
        if(root==null) return;
        inorder(root.left, nodes, target);
        if(root.element.compareTo(target.element) > 0) return;
        if(root.element.compareTo(target.element) == 0) return;
        nodes.add(root);
        inorder(root.right, nodes, target);
    }

    public void inorderRight(Node<E> root, ArrayList<Node<E>> nodes, Node<E> target) {
        if(root==null) return;
        inorderRight(root.right, nodes, target);
        if(root.element.compareTo(target.element) < 0) return;
        if(root.element.compareTo(target.element) == 0) return;
        nodes.add(root);
        inorderRight(root.left, nodes, target);
    }

    public int getHeight(Node<E> node) {
        if(node==null) return -1;
        else return node.height;
    }
    public int getMax(int first, int second) {
        if (first > second) return first;
        else return second;
    }

    private Node<E> balance(Node<E> node) {
        updateHeight(node);
        int balanceFactor = getBalanceFactor(node);

        if(balanceFactor > 1) {
            if(getBalanceFactor(node.right) < 0) {
                node.right = rotateRight(node.right);
            }
            return rotateLeft(node);
        }
        else if(balanceFactor < -1) {
            if(getBalanceFactor(node.left) > 0) {
                node.left = rotateLeft(node.left);
            }
            return rotateRight(node);
        }
        return node;
    }
    private Node<E> rotateLeft(Node<E> node) {
        Node<E> newRoot = node.right;
        node.right = newRoot.left;
        newRoot.left = node;
        updateHeight(node);
        updateHeight(newRoot);

        return newRoot;
    }
    private Node<E> rotateRight(Node<E> node) {
        Node<E> newRoot = node.left;
        node.left = newRoot.right;
        newRoot.right = node;
        updateHeight(node);
        updateHeight(newRoot);

        return newRoot;
    }
    private void updateHeight(Node<E> node) {
        node.height = 1 + getMax(getHeight(node.left), getHeight(node.right));
    }
    private int getBalanceFactor(Node<E> node) {
        if(node==null) return 0;
        else return getHeight(node.right) - getHeight(node.left);
    }
}

