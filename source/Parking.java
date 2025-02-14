import java.util.ArrayList;

public class Parking implements Comparable<Parking> {
    private long capacityConstraint;
    private long truckLimit;
    private MyQueue<Truck> ready = new MyQueue();
    private MyQueue<Truck> waiting = new MyQueue();

    public Parking() {}
    public Parking(long capacityConstraint, long truckLimit) {
        this.capacityConstraint = capacityConstraint;
        this.truckLimit = truckLimit;
    }
    public boolean addTruck(Truck truck) {
        if (ready.getSize() + waiting.getSize() < truckLimit) {
            waiting.enqueue(truck);
            return true;
        }
        else return false;
    }
    public Truck ready() {
        Truck truckReady = waiting.dequeue();
        if (truckReady != null) {
            ready.enqueue(truckReady);
        }
        return truckReady;
    }
    public MyQueue<Truck> getWaitingTruck() {
        return waiting;
    }
    public MyQueue<Truck> getReadyTruck() {
        return ready;
    }

    public long getWaitingSize() {return waiting.getSize();}
    public long getReadySize() {return ready.getSize();}

    public void setCapacityConstraint(long capacityConstraint) {
        this.capacityConstraint = capacityConstraint;
    }
    public long getCapacityConstraint() {
        return capacityConstraint;
    }

    public void setTruckLimit(long truckLimit) {
        this.truckLimit = truckLimit;
        waiting.setMaxSize(truckLimit);
        ready.setMaxSize(truckLimit);
    }

    @Override
    public int compareTo(Parking otherParking) {
        if((this.capacityConstraint - otherParking.getCapacityConstraint())==0) return 0;
        else if((this.capacityConstraint - otherParking.getCapacityConstraint())>0) return 1;
        else return -1;
    }


    public void printWaitingTruck() {
        for(int i=0; i < waiting.getSize(); i++) {
            System.out.println(waiting.get(i).getId());
        }
    }
    public void printReadyTruck() {
        for(int i=0; i < ready.getSize(); i++) {
            System.out.println(ready.get(i).getId());
        }
    }

}

