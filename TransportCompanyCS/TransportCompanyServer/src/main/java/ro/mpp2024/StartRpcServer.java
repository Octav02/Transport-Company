package ro.mpp2024;

import ro.mpp2024.persistance.BookingRepository;
import ro.mpp2024.persistance.TripRepository;
import ro.mpp2024.persistance.UserRepository;
import ro.mpp2024.persistance.dbrepository.*;
import ro.mpp2024.services.TransportCompanyService;
import ro.mpp2024.utils.AbstractServer;
import ro.mpp2024.utils.RpcConcurrentServer;


import java.io.FileReader;
import java.util.Properties;

public class StartRpcServer {
    private static int defaultPort = 55555;

    public static void main(String[] args) {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader("../properties/db.properties"));
        }
        catch (Exception e) {
            System.out.println("Error reading db properties" + e);
        }

        UserRepository userRepository = new UserDBRepository(properties);
        TripRepository tripRepository = new TripDbRepository(properties);
        BookingRepository bookingRepository = new BookingDBRepository(properties,tripRepository);

        TransportCompanyService serverImpl = new TransportCompanyServerImplementation(userRepository, tripRepository, bookingRepository);
        System.out.println("Starting server on port " + defaultPort);
        AbstractServer server = new RpcConcurrentServer(defaultPort, serverImpl);
        try {
            server.start();
        } catch (Exception e) {
            System.out.println("Error starting server" + e);
        } finally {
            try {
                server.stop();
            } catch (Exception e) {
                System.out.println("Error stopping server" + e);
            }
        }


    }
}
