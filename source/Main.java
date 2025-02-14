import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        AVL<Parking> park = new AVL<>();

        File inputFile = new File(args[0]);
        File outputFile = new File(args[1]);
        PrintStream outstream;
        try {
            outstream = new PrintStream(outputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        Scanner reader;
        try {
            reader = new Scanner(inputFile);
        } catch (FileNotFoundException e) {
            System.out.println("Cannot find input file");
            return;
        }

        /* Reading the file and considering each case */
        int count = 0;
        while (reader.hasNextLine()) {
            String line = reader.nextLine();
            String[] lineParts = line.split(" ");
            String command = lineParts[0];
            if (Objects.equals(command, "create_parking_lot")) {
                long capacity = Long.parseLong(lineParts[1]);
                long truckLimit = Long.parseLong(lineParts[2]);
                CreateNewParkingLot(park, capacity, truckLimit);
            }
            else if (Objects.equals(command, "add_truck")) {
                long truckId = Long.parseLong(lineParts[1]);
                long capacity = Long.parseLong(lineParts[2]);
                Truck truck = new Truck(truckId, capacity, 0);
                long result = AddTruck(park, truck);
                //outstream.print("addTruck ");
                outstream.println(result);
            }
            else if(Objects.equals(command, "delete_parking_lot")) {
                long capacity = Long.parseLong(lineParts[1]);
                DeleteParkingLot(park, capacity);
                Node<Parking> temp;
                Parking parking = new Parking();
                parking.setCapacityConstraint(capacity);
                temp = park.search(parking);
            }
            else if(Objects.equals(command, "ready")) {
                long capacity = Long.parseLong(lineParts[1]);
                long[] result = Ready(park, capacity);
                //outstream.print("ready ");
                if(result.length != 0) {
                    outstream.print(result[0] + " ");
                    outstream.print(result[1] + "\n");
                }
                else outstream.println(-1);
            }
            else if(Objects.equals(command, "load")) {
                long capacity = Long.parseLong(lineParts[1]);
                long load = Long.parseLong(lineParts[2]);
                ArrayList<ArrayList<Long>> result = Load(park, capacity, load);
                //outstream.print("load ");
                for(int i=0; i < result.size(); i++) {
                    outstream.print(result.get(i).get(0) + " ");
                    outstream.print(result.get(i).get(1));
                    if(i!=result.size()-1) outstream.print(" - ");
                }
                if(result.size()==0) outstream.print("-1\n");
                else outstream.print("\n");
            }
            else if(Objects.equals(command, "count")) {
                long capacity = Long.parseLong(lineParts[1]);
                long numTrucks = Count(park, capacity);
                outstream.println(numTrucks);
            }
            count += 1;
        }
    }

    // Function that returns number of trucks in parking lots with capacity greater than given value
    private static long Count(AVL<Parking> park, long capacity) {
        Parking parkingTemp = new Parking();
        parkingTemp.setCapacityConstraint(capacity);
        Node<Parking> parkingNode = new Node<>(parkingTemp);
        ArrayList<Node<Parking>> lots = new ArrayList<>();
        park.inorderRight(park.getRoot(), lots, parkingNode); //inorder traversing from right to get parking lots with larger capacity
        long numTrucks = 0;
        for (int i = 0; i < lots.size(); i++) {
            numTrucks += lots.get(i).element.getWaitingTruck().getSize(); //counting waiting trucks
            numTrucks += lots.get(i).element.getReadyTruck().getSize(); //counting ready trucks
        }
        return numTrucks;
    }

    //Function to add a truck to a suitable parking lot, returns capacity of that parking lot or -1
    // if the parking lot with given capacity does not exist, we go through all parking lots with smaller capacity
    private static long AddTruck(AVL<Parking> park, Truck truck) {
        Node<Parking> temp;
        long capacity = truck.getRemainingCapacity();
        Parking parking = new Parking();
        parking.setCapacityConstraint(capacity);
        temp = park.search(parking);
        if ( temp != null && temp.element.addTruck(truck)) return capacity;
        else {
            if(temp == null) temp = new Node<>(parking);
            ArrayList<Node<Parking>> lots = new ArrayList<>();
            park.inorder(park.getRoot(), lots, temp); //inorder traversing to get parking lots with larger capacity
            Parking smallerParking = new Parking();
            for (int i = lots.size() - 1; i >= 0 ; i--) { //traversing through larger parking lots
                smallerParking = lots.get(i).element;
                if (smallerParking.addTruck(truck)) return smallerParking.getCapacityConstraint();
            }
        }
        return -1;
    }

    //Function to distribute load, returns array of id of trucks that got the load and their new parking lot capacity
    //Traverses through parking lots with capacity greater than given value until load is distributed (or no parking left)
    private static ArrayList<ArrayList<Long>> Load(AVL<Parking> park, long capacity, long load) {
        ArrayList<ArrayList<Long>> trucksMoved = new ArrayList<>();
        Node<Parking> temp;
        Parking parking = new Parking();
        parking.setCapacityConstraint(capacity);
        temp = park.search(parking);
        MyQueue<Truck> readyTrucks = null;
        if(temp!=null) readyTrucks = temp.element.getReadyTruck(); // getting ready section of parking lot
        if ( temp != null && readyTrucks.getSize() > 0) load = UnloadTrucks(park, readyTrucks, trucksMoved, capacity, load);
        if (load!=0) {
            if( temp==null ) temp = new Node<>(parking);
            ArrayList<Node<Parking>> lots = new ArrayList<>();
            park.inorderRight(park.getRoot(), lots, temp);
            Parking largerParking = new Parking();
            for (int i = lots.size() - 1; i >= 0 ; i--) { //traversing through larger parking lots
                if(load==0) return trucksMoved;
                largerParking = lots.get(i).element;
                readyTrucks = largerParking.getReadyTruck(); // getting ready section of parking lot
                if (readyTrucks.getSize() > 0 ) load = UnloadTrucks(park, readyTrucks, trucksMoved, largerParking.getCapacityConstraint(), load);
            }
        }
        return trucksMoved;
    }

    //Function to unload trucks in a single parking list's ready section, returns the remaining load
    //Stores trucks' ids and their new parking lot to an array "trucksMoved"
    private static long UnloadTrucks(AVL<Parking> park, MyQueue<Truck> readyTrucks, ArrayList<ArrayList<Long>> trucksMoved, long parkingCapacity, long load) {
        long readyTruckSize = readyTrucks.getSize();
        for(int i=0; i < readyTruckSize; i++) { //traversing through parking lots' ready section
            if(load==0) return 0;
            Truck readyTruck = readyTrucks.dequeue();
            long remainingCapacity = getMin(readyTruck.getRemainingCapacity(), parkingCapacity);
            long realLoad = getMin(load, remainingCapacity);
            load -= realLoad;
            readyTruck.setLoad(realLoad + readyTruck.getLoad());
            ArrayList<Long> singleTruck = new ArrayList<>();
            singleTruck.add(readyTruck.getId());
            singleTruck.add(AddTruck(park, readyTruck)); //replacing the truck to proper parking lot
            trucksMoved.add(singleTruck);
        }
        return load;
    }

    private static long getMin(long maxCapacity, long capacity) {
        if (maxCapacity < capacity) return maxCapacity;
        else return capacity;
    }

    //Function to make a truck ready
    //If waiting list of initial parking lot is empty, goes through all larger parking lots
    //Returns a truck id and capacity of parking lot
    private static long[] Ready(AVL<Parking> park, long capacityConstraint) {
        Node<Parking> temp;
        Parking parking = new Parking();
        parking.setCapacityConstraint(capacityConstraint);
        temp = park.search(parking);
        Truck truck = null;
        if(temp!=null && temp.element.getWaitingSize()!=0) {
            truck = temp.element.ready();
            return new long[]{truck.getId(), capacityConstraint};
        }
        else {
            if(temp == null) temp = new Node<>(parking);
            ArrayList<Node<Parking>> lots = new ArrayList<>();
            park.inorderRight(park.getRoot(), lots, temp); // getting all larger parks
            Parking largerParking = new Parking();
            for (int i = lots.size() - 1; i >= 0 ; i--) { // traversing all larger parks
                largerParking = lots.get(i).element;
                truck = largerParking.ready(); //dequeue the truck from waiting list
                if (truck != null) {
                    return new long[]{truck.getId(), largerParking.getCapacityConstraint()};
                }
            }
        }
        return new long[]{};
    }
    private static void DeleteParkingLot(AVL<Parking> park, long capacityConstraint) {
        Parking parking = new Parking();
        parking.setCapacityConstraint(capacityConstraint);
        park.delete(parking);
    }

    public static void CreateNewParkingLot(AVL<Parking> park, long capacityConstraint, long truckLimit) {
            Parking parking = new Parking(capacityConstraint, truckLimit);
            park.insert(parking);
        }

    }