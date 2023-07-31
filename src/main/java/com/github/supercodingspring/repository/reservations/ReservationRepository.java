package com.github.supercodingspring.repository.reservations;

public interface ReservationRepository {
    Boolean saveReservation(Reservation reservation);

    Reservation findReservationWithPassengerIdAndAirLineTicketId(Integer userId, Integer airlineTicketId);

    void updateReservationStatus(Integer reservationId, String status);
}
