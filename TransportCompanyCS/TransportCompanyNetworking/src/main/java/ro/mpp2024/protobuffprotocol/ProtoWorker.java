package ro.mpp2024.protobuffprotocol;

import ro.mpp2024.model.Booking;
import ro.mpp2024.model.User;
import ro.mpp2024.services.TransportCompanyObserver;
import ro.mpp2024.services.TransportCompanyService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ProtoWorker implements Runnable, TransportCompanyObserver {
    private TransportCompanyService server;
    private Socket connection;

    private InputStream input;

    private OutputStream output;
    private volatile boolean connected;

    public ProtoWorker(TransportCompanyService server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try {
            output = connection.getOutputStream();
            input = connection.getInputStream();
            connected = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (connected) {
            try {
                ProtoBuff.Request request = ProtoBuff.Request.parseDelimitedFrom(input);
                ProtoBuff.Response response = handleRequest(request);
                if (response != null) {
                    sendResponse(response);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
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

    private void sendResponse(ProtoBuff.Response response) {
        try {
            System.out.println("Sending response ...");
            response.writeDelimitedTo(output);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ProtoBuff.Response handleRequest(ProtoBuff.Request request) {
        ProtoBuff.Response response = null;
        switch (request.getType()) {
            case Login: {
                System.out.println("Login request ...");
                User user = ProtoUtils.getUser(request);
                try {
                    boolean login = server.login(user.getUsername(), user.getPassword(), this);
                    if (login) {
                        return ProtoUtils.createOkResponse();
                    } else {
                        return ProtoUtils.createErrorResponse("Login failed");
                    }
                } catch (Exception e) {
                    connected = false;
                    return ProtoUtils.createErrorResponse(e.getMessage());
                }
            }
            case Logout: {
                System.out.println("Logout request ...");
                User user = ProtoUtils.getUser(request);
                try {
                    server.logout(user, this);
                    connected = false;
                    return ProtoUtils.createOkResponse();
                } catch (Exception e) {
                    return ProtoUtils.createErrorResponse(e.getMessage());
                }
            }
            case GetTrip: {
                System.out.println("GetTrip request ...");
                long id = ProtoUtils.getId(request);
                try {
                    return ProtoUtils.createGetTripResponse(server.getTripById(id));
                } catch (Exception e) {
                    return ProtoUtils.createErrorResponse(e.getMessage());
                }
            }
            case GetTripsWithSeats: {
                System.out.println("GetTripsWithSeats request ...");
                try {
                    return ProtoUtils.createGetTripsWithSeatsResponse(server.getAllTripsWithAvailableSeats());
                } catch (Exception e) {
                    return ProtoUtils.createErrorResponse(e.getMessage());
                }
            }
            case GetBookingsForTrip: {
                System.out.println("GetBookingsForTrip request ...");
                long id = ProtoUtils.getId(request);
                try {
                    return ProtoUtils.createGetBookingsForTripResponse(server.getBookingsSeatsForTrip(id));
                } catch (Exception e) {
                    return ProtoUtils.createErrorResponse(e.getMessage());
                }
            }
            case AddBooking: {
                System.out.println("AddBooking request ...");
                try {
                    Booking booking = ProtoUtils.getBooking(request);
                    server.addBooking(booking.getClientName(), booking.getTrip(), booking.getReservedSeats(), this);
                    return ProtoUtils.createAddBookingResponse();
                } catch (Exception e) {
                    return ProtoUtils.createErrorResponse(e.getMessage());
                }
            }
            case GetUser: {
                System.out.println("GetUser request ...");
                String username = ProtoUtils.getUsername(request);
                try {
                    return ProtoUtils.createGetUserResponse(server.getUserByUsername(username));
                } catch (Exception e) {
                    return ProtoUtils.createErrorResponse(e.getMessage());
                }
            }
        }
        return response;
    }

    @Override
    public void bookingAdded() {
        try {
            System.out.println("Update received Smeker");
            ProtoBuff.Response response = ProtoUtils.createAddBookingResponse();
            sendResponse(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
