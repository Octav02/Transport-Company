package ro.mpp2024.dto;

import java.io.Serializable;

public class BookingSeatDTO implements Serializable {
    private int seatNumber;
    private String reservedFor;

    public BookingSeatDTO(int seatNumber, String reservedFor) {
        this.seatNumber = seatNumber;
        this.reservedFor = reservedFor;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public String getReservedFor() {
        return reservedFor;
    }
}
