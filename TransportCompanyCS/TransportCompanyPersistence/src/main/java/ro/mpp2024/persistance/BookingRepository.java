package ro.mpp2024.persistance;


import ro.mpp2024.model.Booking;

import java.util.List;
import java.util.Map;

public interface BookingRepository extends Repository<Booking,Long> {
    List<Booking> getBookingsForAClientOnATrip(String clientName, Long tripId);

    int getNumberOfBookingsForTrip(Long id);

    Map<Integer, String> getBookingSeatDTOsForTrip(Long id);
}