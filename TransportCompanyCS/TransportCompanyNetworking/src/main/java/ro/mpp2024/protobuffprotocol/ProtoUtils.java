package ro.mpp2024.protobuffprotocol;

import com.google.protobuf.Timestamp;
import ro.mpp2024.dto.BookingSeatDTO;
import ro.mpp2024.dto.TripSeatsDTO;
import ro.mpp2024.model.Booking;
import ro.mpp2024.model.Trip;
import ro.mpp2024.model.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;


public class ProtoUtils {
    protected static Timestamp toGoogleTimestampUTC(final LocalDateTime localDateTime) {
        return Timestamp.newBuilder()
                .setSeconds(localDateTime.toEpochSecond(ZoneOffset.UTC))
                .setNanos(localDateTime.getNano())
                .build();
    }

    protected static LocalDateTime fromGoogleTimestampUTC(final Timestamp googleTimestamp) {
        return Instant.ofEpochSecond(googleTimestamp.getSeconds(), googleTimestamp.getNanos())
                .atOffset(ZoneOffset.UTC)
                .toLocalDateTime();
    }


    public static ProtoBuff.Request createLoginRequest(User user) {
        ProtoBuff.User userDTO = ProtoBuff.User.newBuilder()
                .setUsername(user.getUsername())
                .setPassword(user.getPassword())
                .build();
        ProtoBuff.Request request = ProtoBuff.Request.newBuilder()
                .setType(ProtoBuff.Request.Type.Login)
                .setUser(userDTO)
                .build();
        return request;
    }

    public static ProtoBuff.Request createLogoutRequest(User user) {
        ProtoBuff.User userDTO = ProtoBuff.User.newBuilder()
                .setUsername(user.getUsername())
                .setPassword(user.getPassword())
                .build();
        ProtoBuff.Request request = ProtoBuff.Request.newBuilder()
                .setType(ProtoBuff.Request.Type.Logout)
                .setUser(userDTO)
                .build();
        return request;
    }

    public static ProtoBuff.Request createGetTripsWithSeatsRequest() {
        ProtoBuff.Request request = ProtoBuff.Request.newBuilder()
                .setType(ProtoBuff.Request.Type.GetTripsWithSeats)
                .build();
        return request;
    }

    public static ProtoBuff.Request createGetTripRequest(long id) {
        ProtoBuff.Request request = ProtoBuff.Request.newBuilder()
                .setType(ProtoBuff.Request.Type.GetTrip)
                .setId(id)
                .build();
        return request;
    }

    public static ProtoBuff.Request createGetBookingsForTripRequest(long id) {
        ProtoBuff.Request request = ProtoBuff.Request.newBuilder()
                .setType(ProtoBuff.Request.Type.GetBookingsForTrip)
                .setId(id)
                .build();
        return request;
    }

    public static ProtoBuff.Request createGetUserRequest(String username) {
        ProtoBuff.Request request = ProtoBuff.Request.newBuilder()
                .setType(ProtoBuff.Request.Type.GetUser)
                .setUsername(username)
                .build();
        return request;
    }

    public static ProtoBuff.Request createAddBookingRequest(Booking booking) {

        ProtoBuff.Booking bookingDTO = ProtoBuff.Booking.newBuilder()
                .setClientName(booking.getClientName())
                .setTrip(ProtoBuff.Trip.newBuilder()
                        .setId(booking.getTrip().getId())
                        .setDepartureTime(toGoogleTimestampUTC(booking.getTrip().getDepartureTime()))
                        .setNumberOfSeats(booking.getTrip().getTotalNumberOfSeats())
                        .build())
                .addAllSeats(booking.getReservedSeats())
                .build();
        ProtoBuff.Request request = ProtoBuff.Request.newBuilder()
                .setType(ProtoBuff.Request.Type.AddBooking)
                .setBooking(bookingDTO)
                .build();
        return request;
    }

    public static ProtoBuff.Response createOkResponse() {
        ProtoBuff.Response response = ProtoBuff.Response.newBuilder()
                .setType(ProtoBuff.Response.Type.Ok)
                .build();
        return response;
    }

    public static ProtoBuff.Response createErrorResponse(String message) {
        ProtoBuff.Response response = ProtoBuff.Response.newBuilder()
                .setType(ProtoBuff.Response.Type.Error)
                .setError(message)
                .build();
        return response;
    }


    public static ProtoBuff.Response createGetTripResponse(Trip trip) {
        ProtoBuff.Trip tripDTO = ProtoBuff.Trip.newBuilder()
                .setId(trip.getId())
                .setDepartureTime(toGoogleTimestampUTC(trip.getDepartureTime()))
                .setNumberOfSeats(trip.getTotalNumberOfSeats())
                .setDestination(trip.getDestination())
                .build();
        ProtoBuff.Response response = ProtoBuff.Response.newBuilder()
                .setType(ProtoBuff.Response.Type.Ok)
                .setTrip(tripDTO)
                .build();
        return response;
    }

    public static ProtoBuff.Response createGetTripsWithSeatsResponse(List<TripSeatsDTO> trips) {
        ProtoBuff.Response.Builder responseBuilder = ProtoBuff.Response.newBuilder()
                .setType(ProtoBuff.Response.Type.Ok);
        for (TripSeatsDTO trip : trips) {
            ProtoBuff.TripSeats tripDTO = ProtoBuff.TripSeats.newBuilder()
                    .setTripId(trip.getId())
                    .setDepartureTime(toGoogleTimestampUTC(trip.getDepartureTime()))
                    .setAvailableSeats(trip.getAvailableSeats())
                    .setDestination(trip.getDestination())
                    .build();
            responseBuilder.addTrips(tripDTO);
        }
        return responseBuilder.build();
    }

    public static ProtoBuff.Response createGetBookingsForTripResponse(List<BookingSeatDTO> bookings) {
        ProtoBuff.Response.Builder responseBuilder = ProtoBuff.Response.newBuilder()
                .setType(ProtoBuff.Response.Type.Ok);
        for (BookingSeatDTO booking : bookings) {
            ProtoBuff.BookingSeat bookingDTO = ProtoBuff.BookingSeat.newBuilder()
                    .setClientName(booking.getReservedFor())
                    .setSeatNumber(booking.getSeatNumber())
                    .build();
            responseBuilder.addBookings(bookingDTO);
        }
        return responseBuilder.build();
    }

    public static ProtoBuff.Response createGetUserResponse(User user) {
        ProtoBuff.User userDTO = ProtoBuff.User.newBuilder()
                .setUsername(user.getUsername())
                .setPassword(user.getPassword())
                .build();
        ProtoBuff.Response response = ProtoBuff.Response.newBuilder()
                .setType(ProtoBuff.Response.Type.Ok)
                .setUser(userDTO)
                .build();
        return response;
    }

    public static ProtoBuff.Response createAddBookingResponse() {
        ProtoBuff.Response response = ProtoBuff.Response.newBuilder()
                .setType(ProtoBuff.Response.Type.SeatsUpdated)
                .build();
        return response;
    }

    public static String getError(ProtoBuff.Response response) {
        return response.getError();
    }

    public static User getUser(ProtoBuff.Response response) {
        ProtoBuff.User userDTO = response.getUser();
        return new User(userDTO.getUsername(), userDTO.getPassword());
    }

    public static Trip getTrip(ProtoBuff.Response response) {
        ProtoBuff.Trip tripDTO = response.getTrip();
        return new Trip(tripDTO.getId(),tripDTO.getDestination(), fromGoogleTimestampUTC(tripDTO.getDepartureTime()), tripDTO.getNumberOfSeats());
    }

    public static List<TripSeatsDTO> getTripsWithSeats(ProtoBuff.Response response) {
        List<TripSeatsDTO> trips = new ArrayList<>();
        for (ProtoBuff.TripSeats tripDTO : response.getTripsList()) {
            trips.add(new TripSeatsDTO(tripDTO.getTripId(),tripDTO.getDestination(), fromGoogleTimestampUTC(tripDTO.getDepartureTime()), tripDTO.getAvailableSeats()));
        }
        return trips;
    }

    public static List<BookingSeatDTO> getBookingsForTrip(ProtoBuff.Response response) {
        List<BookingSeatDTO> bookings = new ArrayList<>();
        for (ProtoBuff.BookingSeat bookingDTO : response.getBookingsList()) {
            bookings.add(new BookingSeatDTO(bookingDTO.getSeatNumber(), bookingDTO.getClientName()));
        }
        return bookings;
    }


    public static User getUser(ProtoBuff.Request request) {
        ProtoBuff.User userDTO = request.getUser();
        return new User(userDTO.getUsername(), userDTO.getPassword());
    }

    public static long getId(ProtoBuff.Request request) {
        return request.getId();
    }

    public static Booking getBooking(ProtoBuff.Request request) {
        ProtoBuff.Booking bookingDTO = request.getBooking();
        return new Booking(bookingDTO.getClientName(), bookingDTO.getSeatsList(), new Trip(bookingDTO.getTrip().getId(), bookingDTO.getTrip().getDestination(), fromGoogleTimestampUTC(bookingDTO.getTrip().getDepartureTime()), bookingDTO.getTrip().getNumberOfSeats()));
    }

    public static String getUsername(ProtoBuff.Request request) {
        return request.getUsername();
    }
}
