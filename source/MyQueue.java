import java.util.ArrayList;

public class MyQueue<E> {
    private ArrayList<E> queue = new ArrayList<E>();
    private long size = 0;
    private long maxSize = 0;

    public MyQueue() {}

    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
    }
    public void enqueue(E element) {
        queue.add(element);
        size += 1;
    }
    public E front() {return queue.get(0);}
    public E get(int i) {return queue.get(i);}
    public E dequeue() {
        if (size > 0) {
            E removed = queue.remove(0);
            size -= 1;
            return removed;
        }
        else return null;
    }
    public void print() {
        for(int i = 0; i < size; i++) {
            System.out.println(queue.get(i));
        }
    }
    public long getSize() {
        return size;
    }
}
