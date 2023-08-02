package com.github.supercodingspring.service.mapper;

import com.github.supercodingspring.repository.flight.Flight;
import com.github.supercodingspring.web.dto.airline.FlightInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FlightMapper {

    FlightMapper INSTANCE = Mappers.getMapper(FlightMapper.class);

    FlightInfo flightToFlightInfo(Flight flight);
}
