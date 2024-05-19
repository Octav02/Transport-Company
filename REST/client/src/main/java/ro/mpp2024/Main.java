package ro.mpp2024;


import ro.mpp2024.model.Trip;

import java.time.LocalDateTime;

public class Main {
    private final static ClientTest clientTest = new ClientTest();
    public static void main(String[] args) {
        try {
            Trip trip1 = new Trip("Dest1", LocalDateTime.now(),12457);
            Trip trip2 = new Trip("Dest2", LocalDateTime.now(), 98768);

            printSeparator();
            System.out.println("Adding trips:");
            show(() -> System.out.println(clientTest.createTrip(trip1)));
            show(() -> System.out.println(clientTest.createTrip(trip2)));

            printSeparator();
            System.out.println("Printing all trips:");
            show(() -> clientTest.getAllTrips().forEach(System.out::println));

            printSeparator();
            System.out.println("Get by id:");
            show(() -> System.out.println(clientTest.getTripById(4L)));
            printSeparator();
            System.out.println("Update trip:");
            show(() -> System.out.println(clientTest.updateTrip(new Trip("Dest3", LocalDateTime.now(), 12345), 12L)));
            printSeparator();
            System.out.println("Delete trip:");
            show(() -> System.out.println(clientTest.deleteTrip(22L)));
            printSeparator();
            System.out.println("Get destination of trip:");
            show(() -> System.out.println(clientTest.getDestinationOfTrip(5L)));
            printSeparator();
        }
        catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    private static void show(Runnable task){
        try {
            task.run();
        } catch (Exception e){
            System.out.println("Error: " + e);
        }
    }
    private static void printSeparator(){
        System.out.println("----------------------------------------------------------------------------------");
    }
}