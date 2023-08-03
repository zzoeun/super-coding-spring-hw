package com.github.supercodingspring.service;

import com.github.supercodingspring.repository.airlineTicket.AirlineTicket;
import com.github.supercodingspring.repository.airlineTicket.AirlineTicketJpaRepository;
import com.github.supercodingspring.repository.flight.Flight;
import com.github.supercodingspring.repository.passenger.Passenger;
import com.github.supercodingspring.repository.passenger.PassengerJpaRepository;
import com.github.supercodingspring.repository.reservations.ReservationJpaRepository;
import com.github.supercodingspring.repository.users.UserEntity;
import com.github.supercodingspring.service.exceptions.NotFoundException;
import com.github.supercodingspring.web.dto.airline.ReservationRequest;
import com.github.supercodingspring.web.dto.airline.ReservationResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
class AirReservationServiceMakeReservationUnitTest {

    @Mock
    private AirlineTicketJpaRepository airlineTicketJpaRepository;
    @Mock
    private PassengerJpaRepository passengerJpaRepository;
    @Mock
    private ReservationJpaRepository reservationJpaRepository;
    @InjectMocks
    private AirReservationService airReservationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @DisplayName("정상적으로 MakeReservation 동작하는 경우")
    @Test
    void MakeReservation() {
        // given
        Integer userId = 5;
        Integer airlineTicketId = 10;
        ReservationRequest reservationRequest = new ReservationRequest(userId, airlineTicketId);

        AirlineTicket airlineTicket = AirlineTicket.builder()
                                                   .ticketType("왕복")
                                                   .arrivalLocation("파리")
                                                   .departureLocation("서울")
                                                   .departureAt(LocalDateTime.now())
                                                   .returnAt(LocalDateTime.now())
                                                   .ticketId(airlineTicketId)
                                                   .tax(1234.0)
                                                   .totalPrice(15000.0)
                                                   .build();

        List<Flight> flightList = Arrays.asList(
                new Flight(1, airlineTicket, LocalDateTime.now(), LocalDateTime.now(), "서울", "파리", 20000.0, 5000.0),
                new Flight(2, airlineTicket, LocalDateTime.now(), LocalDateTime.now(), "서울", "파리", 20000.0, 5000.0),
                new Flight(3, airlineTicket, LocalDateTime.now(), LocalDateTime.now(), "서울", "파리", 20000.0, 5000.0),
                new Flight(4, airlineTicket, LocalDateTime.now(), LocalDateTime.now(), "서울", "파리", 20000.0, 5000.0),
                new Flight(5, airlineTicket, LocalDateTime.now(), LocalDateTime.now(), "서울", "파리", 20000.0, 5000.0),
                new Flight(6, airlineTicket, LocalDateTime.now(), LocalDateTime.now(), "서울", "파리", 20000.0, 5000.0)
        );

        airlineTicket.setFlightList(flightList);


        Passenger passenger = Passenger.builder()
                                       .passengerId(1234)
                                       .passportNum("1234")
                                       .user(new UserEntity())
                                       .build();

        // when
        when(airlineTicketJpaRepository.findById(any())).thenReturn(Optional.ofNullable(airlineTicket));
        when(passengerJpaRepository.findPassengerByUserUserId(userId)).thenReturn(Optional.ofNullable(passenger));
        when(reservationJpaRepository.save(any())).thenReturn(null);

        // then
        ReservationResult reservation = airReservationService.makeReservation(reservationRequest);
        log.info("tickets: " + reservation);
    }

    @DisplayName("airlineTicket 못찾을 경우, NotFoundException 발생")
    @Test
    void MakeReservation2() {
        // given
        Integer userId = 5;
        Integer airlineTicketId = 10;
        ReservationRequest reservationRequest = new ReservationRequest(userId, airlineTicketId);

        AirlineTicket airlineTicket = null;

        List<Flight> flightList = Arrays.asList(
                new Flight(1, airlineTicket, LocalDateTime.now(), LocalDateTime.now(), "서울", "파리", 20000.0, 5000.0),
                new Flight(2, airlineTicket, LocalDateTime.now(), LocalDateTime.now(), "서울", "파리", 20000.0, 5000.0),
                new Flight(3, airlineTicket, LocalDateTime.now(), LocalDateTime.now(), "서울", "파리", 20000.0, 5000.0),
                new Flight(4, airlineTicket, LocalDateTime.now(), LocalDateTime.now(), "서울", "파리", 20000.0, 5000.0),
                new Flight(5, airlineTicket, LocalDateTime.now(), LocalDateTime.now(), "서울", "파리", 20000.0, 5000.0),
                new Flight(6, airlineTicket, LocalDateTime.now(), LocalDateTime.now(), "서울", "파리", 20000.0, 5000.0)
        );

        Passenger passenger = Passenger.builder()
                                       .passengerId(1234)
                                       .passportNum("1234")
                                       .user(new UserEntity())
                                       .build();

        // when
        when(airlineTicketJpaRepository.findById(any())).thenReturn(Optional.ofNullable(airlineTicket));
        when(passengerJpaRepository.findPassengerByUserUserId(userId)).thenReturn(Optional.ofNullable(passenger));
        when(reservationJpaRepository.save(any())).thenReturn(null);

        // then
        assertThrows(NotFoundException.class, () ->
                airReservationService.makeReservation(reservationRequest)
        );
    }


    @DisplayName("passenger를 못찾는경우 에러")
    @Test
    void MakeReservation3() {
        // given
        Integer userId = 5;
        Integer airlineTicketId = 10;
        ReservationRequest reservationRequest = new ReservationRequest(userId, airlineTicketId);

        AirlineTicket airlineTicket = AirlineTicket.builder()
                                                   .ticketType("왕복")
                                                   .arrivalLocation("파리")
                                                   .departureLocation("서울")
                                                   .departureAt(LocalDateTime.now())
                                                   .returnAt(LocalDateTime.now())
                                                   .ticketId(airlineTicketId)
                                                   .tax(1234.0)
                                                   .totalPrice(15000.0)
                                                   .build();

        List<Flight> flightList = Arrays.asList(
                new Flight(1, airlineTicket, LocalDateTime.now(), LocalDateTime.now(), "서울", "파리", 20000.0, 5000.0),
                new Flight(2, airlineTicket, LocalDateTime.now(), LocalDateTime.now(), "서울", "파리", 20000.0, 5000.0),
                new Flight(3, airlineTicket, LocalDateTime.now(), LocalDateTime.now(), "서울", "파리", 20000.0, 5000.0),
                new Flight(4, airlineTicket, LocalDateTime.now(), LocalDateTime.now(), "서울", "파리", 20000.0, 5000.0),
                new Flight(5, airlineTicket, LocalDateTime.now(), LocalDateTime.now(), "서울", "파리", 20000.0, 5000.0),
                new Flight(6, airlineTicket, LocalDateTime.now(), LocalDateTime.now(), "서울", "파리", 20000.0, 5000.0)
        );

        airlineTicket.setFlightList(flightList);


        Passenger passenger = null;

        // when
        when(airlineTicketJpaRepository.findById(any())).thenReturn(Optional.ofNullable(airlineTicket));
        when(passengerJpaRepository.findPassengerByUserUserId(userId)).thenReturn(Optional.ofNullable(passenger));
        when(reservationJpaRepository.save(any())).thenReturn(null);

        // then
        assertThrows(NotFoundException.class, () ->
                airReservationService.makeReservation(reservationRequest)
        );
    }


    @DisplayName("airlineTicket에 flights 가 없으면 에러")
    @Test
    void MakeReservation4() {
        // given
        Integer userId = 5;
        Integer airlineTicketId = 10;
        ReservationRequest reservationRequest = new ReservationRequest(userId, airlineTicketId);

        AirlineTicket airlineTicket = AirlineTicket.builder()
                                                   .ticketType("왕복")
                                                   .arrivalLocation("파리")
                                                   .departureLocation("서울")
                                                   .departureAt(LocalDateTime.now())
                                                   .returnAt(LocalDateTime.now())
                                                   .ticketId(airlineTicketId)
                                                   .flightList(new ArrayList<>())  // Here, no flights are added
                                                   .tax(1234.0)
                                                   .totalPrice(15000.0)
                                                   .build();

        Passenger passenger = Passenger.builder()
                                       .passengerId(1234)
                                       .passportNum("1234")
                                       .user(new UserEntity())
                                       .build();

        // when
        when(airlineTicketJpaRepository.findById(any())).thenReturn(Optional.ofNullable(airlineTicket));
        when(passengerJpaRepository.findPassengerByUserUserId(userId)).thenReturn(Optional.ofNullable(passenger));
        when(reservationJpaRepository.save(any())).thenReturn(null);

        // then
        assertThrows(NotFoundException.class, () ->
                airReservationService.makeReservation(reservationRequest)
        );
    }

}