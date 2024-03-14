package ro.mpp2024.repository;

import ro.mpp2024.model.Booking;

import java.util.List;

public interface BookingRepository extends Repository<Booking,Long>{
    List<Booking> getBookingsForAClientOnATrip(String clientName, Long tripId);
}
