package ro.mpp2024.rpcprotocol;

import ro.mpp2024.dto.BookingDTO;
import ro.mpp2024.dto.DTOUtils;
import ro.mpp2024.dto.UserDTO;
import ro.mpp2024.model.Booking;
import ro.mpp2024.model.User;
import ro.mpp2024.services.TransportCompanyObserver;
import ro.mpp2024.services.TransportCompanyService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientRpcWorker implements Runnable, TransportCompanyObserver {
    private final TransportCompanyService server;
    private final Socket connection;

    private ObjectInputStream input;

    private ObjectOutputStream output;
    private volatile boolean connected;

    public ClientRpcWorker(TransportCompanyService server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try {
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            connected = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (connected) {
            try {
                Object request = input.readObject();
                Response response = handleRequest((Request) request);
                if (response != null) {
                    sendResponse(response);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            System.out.println("Error " + e);
        }
    }

    private void sendResponse(Response response) {
        try {
            output.writeObject(response);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Response handleRequest(Request request) {
        Response response = null;
        if (request.type() == RequestType.LOGIN) {
            System.out.println("Login request ...");
            User user = (User) DTOUtils.getFromDTO((UserDTO) request.data());
            try {
                boolean login = server.login(user.getUsername(), user.getPassword(), this);
                if (login) {
                    return new Response.Builder().type(ResponseType.OK).build();
                }
                else {
                    return new Response.Builder().type(ResponseType.ERROR).data("Login failed").build();
                }
            } catch (Exception e) {
                connected = false;
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.type() == RequestType.LOGOUT) {
            System.out.println("Logout request");
            User user = (User) DTOUtils.getFromDTO((UserDTO) request.data());
            try {
                server.logout(user, this);
                connected = false;
                return new Response.Builder().type(ResponseType.OK).build();
            } catch (Exception e) {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.type() == RequestType.GET_TRIP) {
            System.out.println("Get trip request");
            Long id = (Long) request.data();
            try {
                return new Response.Builder().type(ResponseType.OK).data(server.getTripById(id)).build();
            } catch (Exception e) {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }

        if (request.type() == RequestType.GET_TRIPS_WITH_SEATS) {
            System.out.println("Get trips with seats request");
            try {
                return new Response.Builder().type(ResponseType.OK).data(server.getAllTripsWithAvailableSeats()).build();
            } catch (Exception e) {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }

        if (request.type() == RequestType.GET_BOOKINGS_FOR_TRIP) {
            System.out.println("Get bookings for trip request");
            Long id = (Long) request.data();
            try {
                return new Response.Builder().type(ResponseType.OK).data(server.getBookingsSeatsForTrip(id)).build();
            } catch (Exception e) {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }

        if (request.type() == RequestType.ADD_BOOKING) {
            System.out.println("Add  booking request");
            Booking booking = DTOUtils.getFromDTO((BookingDTO) request.data());
            try {
                server.addBooking(booking.getClientName(), booking.getTrip(), booking.getReservedSeats(), this);
                return new Response.Builder().type(ResponseType.SEATS_UPDATED).build();
            } catch (Exception e) {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }

        if (request.type() == RequestType.GET_USER) {
            System.out.println("Get user request");
            String username = (String) request.data();
            try {
                return new Response.Builder().type(ResponseType.OK).data(server.getUserByUsername(username)).build();
            } catch (Exception e) {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }

        return response;
    }


    @Override
    public void bookingAdded() {
        try {
            System.out.println("Booking added worker");
            Response response = new Response.Builder().type(ResponseType.SEATS_UPDATED).build();
            sendResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
