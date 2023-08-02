package com.github.supercodingspring.web.dto.airline;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class FlightInfo {
    private Integer flightId;
    private LocalDateTime departAt;
    private LocalDateTime arrivalAt;
    private String departureLocation;
    private String arrivalLocation;
}
