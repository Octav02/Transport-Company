package ro.mpp2024.dto;

import ro.mpp2024.model.Booking;
import ro.mpp2024.model.User;

import java.util.List;

public  class DTOUtils {
    public static User getFromDTO(UserDTO userDTO) {
        return new User(userDTO.getUsername(), userDTO.getPassword());
    }

    public UserDTO getDTO(User user) {
        return new UserDTO(user.getUsername(), user.getPassword());
    }

    public static Booking getFromDTO(BookingDTO bookingDTO) {
        return new Booking(bookingDTO.getClientName(),  bookingDTO.getSeats(),bookingDTO.getTrip());
    }

    public BookingDTO getDTO(Booking booking) {
        return new BookingDTO(booking.getClientName(), booking.getReservedSeats(),booking.getTrip());
    }
}
