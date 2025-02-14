public class Node<E> {
    protected E element;
    protected Node<E> right;
    protected Node<E> left;
    public int height;

    public Node() {}
    public Node(E element) {
        this.element = element;
    }
}
