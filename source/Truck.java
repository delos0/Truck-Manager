public class Truck {
    private long maxCapacity;
    private long load;
    private long id;
    public Truck() {}
    public Truck(long id, long maxCapacity, long load) {
        this.id = id;
        this.maxCapacity = maxCapacity;
        this.load = load;
    }
    public void setId(long id){
        this.id = id;
    }
    public void setCapacity(long capacity) {
        this.maxCapacity = capacity;
    }
    public void setLoad(long load) {
        if (load == maxCapacity) this.load = 0;
        else this.load = load;
    }
    public long getId() {
        return id;
    }
    public long getRemainingCapacity() {
        return maxCapacity - load;
    }
    public long getLoad() { return load; }
    public long getMaxCapacity() { return maxCapacity;}
    private static long getMin(long maxCapacity, long capacity) {
        if (maxCapacity < capacity) return maxCapacity;
        else return capacity;
    }
}
