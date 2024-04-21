package ro.mpp2024.protobuffprotocol;

import ro.mpp2024.dto.BookingSeatDTO;
import ro.mpp2024.dto.TripSeatsDTO;
import ro.mpp2024.model.Booking;
import ro.mpp2024.model.Trip;
import ro.mpp2024.model.User;
import ro.mpp2024.services.TransportCompanyObserver;
import ro.mpp2024.services.TransportCompanyService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ProtoProxy implements TransportCompanyService {

    private String host;
    private int port;
    private TransportCompanyObserver client;
    private InputStream input;
    private OutputStream output;
    private Socket connection;
    private BlockingQueue<ProtoBuff.Response> qresponses;
    private volatile boolean finished;

    public ProtoProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingQueue<>();
    }

    @Override
    public boolean login(String username, String password, TransportCompanyObserver client) {
        initializeConnection();
        ProtoBuff.Request request=ProtoUtils.createLoginRequest(new User(username,password));
        sendRequest(request);
        ProtoBuff.Response response=readResponse();
        if (response.getType()==ProtoBuff.Response.Type.Ok){
            this.client=client;
            return true;
        }
        if (response.getType()==ProtoBuff.Response.Type.Error){
            String err=response.getError();
            closeConnection();
            throw new IllegalArgumentException(err);
        }
        return false;
    }

    private void closeConnection() {
        finished=true;
        try {
            input.close();
            output.close();
            connection.close();
            client=null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeConnection() {
        try {
            connection=new Socket(host,port);
            output=connection.getOutputStream();
            //output.flush();
            input=connection.getInputStream();     //new ObjectInputStream(connection.getInputStream());
            finished=false;
            startReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startReader(){
        Thread tw=new Thread(new ReaderThread());
        tw.start();
    }

    private void sendRequest(ProtoBuff.Request request){
        try {
            System.out.println("Sending request "+request);
            request.writeDelimitedTo(output);
            output.flush();
            System.out.println("Request sent "+request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ProtoBuff.Response readResponse(){
        ProtoBuff.Response response=null;
        try {
            response=qresponses.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }


    @Override
    public void logout(User user, TransportCompanyObserver client) {
        ProtoBuff.Request request=ProtoUtils.createLogoutRequest(user);
        sendRequest(request);
        ProtoBuff.Response response=readResponse();
        closeConnection();
        if (response.getType()==ProtoBuff.Response.Type.Error){
            String err=response.getError();
            throw new IllegalArgumentException(err);
        }
    }

    @Override
    public List<TripSeatsDTO> getAllTripsWithAvailableSeats() {
        ProtoBuff.Request request=ProtoUtils.createGetTripsWithSeatsRequest();
        sendRequest(request);
        ProtoBuff.Response response=readResponse();
        if (response.getType()==ProtoBuff.Response.Type.Error){
            String err=response.getError();
            throw new IllegalArgumentException(err);
        }
        return ProtoUtils.getTripsWithSeats(response);
    }

    @Override
    public Trip getTripById(Long id) {
        ProtoBuff.Request request=ProtoUtils.createGetTripRequest(id);
        sendRequest(request);
        ProtoBuff.Response response=readResponse();
        if (response.getType()==ProtoBuff.Response.Type.Error){
            String err=response.getError();
            throw new IllegalArgumentException(err);
        }
        return ProtoUtils.getTrip(response);
    }

    @Override
    public List<BookingSeatDTO> getBookingsSeatsForTrip(Long id) {
        ProtoBuff.Request request=ProtoUtils.createGetBookingsForTripRequest(id);
        sendRequest(request);
        ProtoBuff.Response response=readResponse();
        if (response.getType()==ProtoBuff.Response.Type.Error){
            String err=response.getError();
            throw new IllegalArgumentException(err);
        }
        return ProtoUtils.getBookingsForTrip(response);
    }

    @Override
    public void addBooking(String clientName, Trip trip, List<Integer> seatsToBook, TransportCompanyObserver client) {
        ProtoBuff.Request request=ProtoUtils.createAddBookingRequest(new Booking(clientName,seatsToBook,trip));
        sendRequest(request);
    }

    @Override
    public User getUserByUsername(String username) {
        ProtoBuff.Request request=ProtoUtils.createGetUserRequest(username);
        sendRequest(request);
        ProtoBuff.Response response=readResponse();
        if (response.getType()==ProtoBuff.Response.Type.Error){
            String err=response.getError();
            throw new IllegalArgumentException(err);
        }
        return ProtoUtils.getUser(response);
    }

    private void handleUpdate(ProtoBuff.Response response){
        try {
            System.out.println("Update received "+response);
            client.bookingAdded();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ReaderThread implements Runnable{
        public void run() {
            while(!finished){
                try {
                    ProtoBuff.Response response=ProtoBuff.Response.parseDelimitedFrom(input);
                    System.out.println("response received "+response);

                    if (isUpdateResponse(response.getType())){
                        handleUpdate(response);
                    }else{
                        try {
                            qresponses.put(response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Reading error "+e);
                }
            }
        }
    }

    private boolean isUpdateResponse(ProtoBuff.Response.Type type){
        return type==ProtoBuff.Response.Type.SeatsUpdated;
    }
}
