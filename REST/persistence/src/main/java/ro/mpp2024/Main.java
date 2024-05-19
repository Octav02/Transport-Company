package ro.mpp2024;

public class Main {
    public static void main(String[] args) {
        HibernateTripRepository tripRepository = new HibernateTripRepository();
        tripRepository.findAll().forEach(System.out::println);
    }
}