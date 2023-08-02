package com.github.supercodingspring.repository.flight;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FlightJpaRepository extends JpaRepository<Flight, Integer> {


    Page<Flight> findAllByAirlineTicket_TicketType(String ticketType, Pageable pageable);
}
