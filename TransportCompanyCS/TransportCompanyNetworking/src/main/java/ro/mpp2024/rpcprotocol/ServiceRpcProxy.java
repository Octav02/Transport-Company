package ro.mpp2024.rpcprotocol;

import ro.mpp2024.dto.BookingSeatDTO;
import ro.mpp2024.dto.TripSeatsDTO;
import ro.mpp2024.model.Booking;
import ro.mpp2024.model.Trip;
import ro.mpp2024.model.User;
import ro.mpp2024.services.TransportCompanyObserver;
import ro.mpp2024.services.TransportCompanyService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServiceRpcProxy implements TransportCompanyService {
    private final String host;
    private final int port;

    private TransportCompanyObserver client;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;
    private final BlockingQueue<Response> qresponses;
    private volatile boolean finished;

    public ServiceRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingQueue<>();
    }

    @Override
    public boolean login(String username, String password, TransportCompanyObserver client) {
        initializeConnection();
        User user = new User(username, password);
        Request request = new Request.Builder().type(RequestType.LOGIN).data(user).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.OK) {
            this.client = client;
            return true;
        }
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            closeConnection();
            throw new IllegalArgumentException(err);
        }
        return false;
    }

    private void closeConnection() {
        finished = true;
        try {
            input.close();
            output.close();
            connection.close();
            client = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendRequest(Request request) {
        try {
            output.writeObject(request);
            output.flush();
        } catch (Exception e) {
            throw new IllegalArgumentException("Error sending object " + e);
        }
    }

    private Response readResponse() {
        Response response = null;
        try {
            response = qresponses.take();
            System.out.println("ReadResponse from qresponses" + response);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    private void initializeConnection() {
        try {
            connection = new Socket(host, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startReader() {
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }

    @Override
    public void logout(User user, TransportCompanyObserver client) {
        Request request = new Request.Builder().type(RequestType.LOGOUT).data(user).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new IllegalArgumentException(err);
        }
    }

    @Override
    public List<TripSeatsDTO> getAllTripsWithAvailableSeats() {
        Request request = new Request.Builder().type(RequestType.GET_TRIPS_WITH_SEATS).build();
        sendRequest(request);
        Response response = readResponse();
        System.out.println(response.data() + "Smecherie");
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new IllegalArgumentException(err);
        }
        System.out.println(response.data() + "Smecherie");

        return (List<TripSeatsDTO>) response.data();
    }

    @Override
    public Trip getTripById(Long id) {
        Request request = new Request.Builder().type(RequestType.GET_TRIP).data(id).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new IllegalArgumentException(err);
        }
        return (Trip) response.data();
    }

    @Override
    public List<BookingSeatDTO> getBookingsSeatsForTrip(Long id) {
        Request request = new Request.Builder().type(RequestType.GET_BOOKINGS_FOR_TRIP).data(id).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new IllegalArgumentException(err);
        }
        System.out.println(response.data() + " Smecherie getBookingTripsDTO");
        return (List<BookingSeatDTO>) response.data();
    }

    @Override
    public void addBooking(String clientName, Trip trip, List<Integer> seatsToBook, TransportCompanyObserver client) {
        Request request = new Request.Builder().type(RequestType.ADD_BOOKING).data(new Booking(clientName, seatsToBook, trip)).build();
        sendRequest(request);
    }

    @Override
    public User getUserByUsername(String username) {
        Request request = new Request.Builder().type(RequestType.GET_USER).data(username).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new IllegalArgumentException(err);
        }
        return (User) response.data();
    }

    private void handleUpdate(Response response) {
        try {
            System.out.println("Updatam tot - ServiceProxy");
            client.bookingAdded();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isUpdate(Response response) {
        return response.type() == ResponseType.SEATS_UPDATED || response.type() == ResponseType.NEW_BOOKING;
    }

    private class ReaderThread implements Runnable {
        public void run() {
            while (!finished) {
                try {
                    Object response = input.readObject();
                    System.out.println("response received " + response);
                    if (isUpdate((Response) response)) {
                        handleUpdate((Response) response);
                    } else {

                        try {
                            qresponses.put((Response) response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Reading error " + e);
                }
            }
        }
    }
}
