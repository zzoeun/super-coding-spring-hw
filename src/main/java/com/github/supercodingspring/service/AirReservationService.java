package com.github.supercodingspring.service;

import com.github.supercodingspring.repository.airlineTicket.AirlineTicket;
import com.github.supercodingspring.repository.airlineTicket.AirlineTicketAndFlightInfo;
import com.github.supercodingspring.repository.airlineTicket.AirlineTicketRepository;
import com.github.supercodingspring.repository.passenger.Passenger;
import com.github.supercodingspring.repository.passenger.PassengerReposiotry;
import com.github.supercodingspring.repository.payments.Payment;
import com.github.supercodingspring.repository.payments.PaymentRepository;
import com.github.supercodingspring.repository.reservations.Reservation;
import com.github.supercodingspring.repository.reservations.ReservationRepository;
import com.github.supercodingspring.repository.users.UserEntity;
import com.github.supercodingspring.repository.users.UserRepository;
import com.github.supercodingspring.web.dto.airline.PaymentsRequest;
import com.github.supercodingspring.web.dto.airline.ReservationRequest;
import com.github.supercodingspring.web.dto.airline.ReservationResult;
import com.github.supercodingspring.web.dto.airline.Ticket;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AirReservationService {

    private UserRepository userRepository;
    private AirlineTicketRepository airlineTicketRepository;

    private PassengerReposiotry passengerReposiotry;
    private ReservationRepository reservationRepository;
    private PaymentRepository paymentRepository;

    public AirReservationService(UserRepository userRepository, AirlineTicketRepository airlineTicketRepository, PassengerReposiotry passengerReposiotry, ReservationRepository reservationRepository, PaymentRepository paymentRepository) {
        this.userRepository = userRepository;
        this.airlineTicketRepository = airlineTicketRepository;
        this.passengerReposiotry = passengerReposiotry;
        this.reservationRepository = reservationRepository;
        this.paymentRepository = paymentRepository;
    }

    public List<Ticket> findUserFavoritePlaceTickets(Integer userId, String ticketType) {
        // 1. 유저를 userId 로 가져와서, 선호하는 여행지 도출
        // 2. 선호하는 여행지와 ticketType으로 AirlineTIcket table 질의 해서 필요한 AirlineTicket
        // 3. 이 둘의 정보를 조합해서 Ticket DTO를 만든다.
        UserEntity userEntity = userRepository.findUserById(userId);
        String likePlace = userEntity.getLikeTravelPlace();

        List<AirlineTicket> airlineTickets
                = airlineTicketRepository.findAllAirlineTicketsWithPlaceAndTicketType(likePlace, ticketType);

        List<Ticket> tickets = airlineTickets.stream().map(Ticket::new).collect(Collectors.toList());
        return tickets;
    }

    @Transactional(transactionManager = "tm2")
    public ReservationResult makeReservation(ReservationRequest reservationRequest) {
        // 1. Reservation Repository, Passenger Repository, Join table ( flight/airline_ticket ),

        // 0. userId,airline_ticke_id
        Integer userId = reservationRequest.getUserId();
        Integer airlineTicketId= reservationRequest.getAirlineTicketId();

        // 1. Passenger I
        Passenger passenger = passengerReposiotry.findPassengerByUserId(userId);
        Integer passengerId= passenger.getPassengerId();

        // 2. price 등의 정보 불러오기
        List<AirlineTicketAndFlightInfo> airlineTicketAndFlightInfos
                = airlineTicketRepository.findAllAirLineTicketAndFlightInfo(airlineTicketId);

        // 3. reservation 생성
        Reservation reservation = new Reservation(passengerId, airlineTicketId);
        Boolean isSuccess = reservationRepository.saveReservation(reservation);

        // TODO: ReservationResult DTO 만들기
        List<Integer> prices = airlineTicketAndFlightInfos.stream().map(AirlineTicketAndFlightInfo::getPrice).collect(Collectors.toList());
        List<Integer> charges = airlineTicketAndFlightInfos.stream().map(AirlineTicketAndFlightInfo::getCharge).collect(Collectors.toList());
        Integer tax = airlineTicketAndFlightInfos.stream().map(AirlineTicketAndFlightInfo::getTax).findFirst().get();
        Integer totalPrice = airlineTicketAndFlightInfos.stream().map(AirlineTicketAndFlightInfo::getTotalPrice).findFirst().get();

        return new ReservationResult(prices, charges, tax, totalPrice, isSuccess);
    }

    @Transactional(transactionManager = "tm2")
    public Integer makePayments(PaymentsRequest paymentsRequest) {
        // TODO: 아래 로직 으로 잔행
        // 0. userIds, airlineTicketIds 추출 및 정상 Input 조건 확인
        // 1. 각 userIds 에 해당하는 passengers 검색 및 id 추출
        // 2. 각 passenger 와 airline_ticket 에 해당하는 reservation 검색
        //      2-1. 만약 userId 와 airline_ticket 해당하는 Reservation이 2개 이상이면 실패
        //      2-2. 만약 UserId 와 airline_ticket 해당하는 Reservation이 0개면 실패
        // 3. Reservation 을 찾았는데, 해당 reservation이 이미 "확정"상태이면 결제 실패
        // 4. 모든 조건 만족하는 Reservation 를 찾고 passengerId 와 ReservationId로 Payment 생성 후 count++
        // 5. Reservation 상태 "대기" -> "확정"으로 변경.

        List<Integer> userIds = paymentsRequest.getUserIds();
        List<Integer> airlineTicketIds = paymentsRequest.getAirlineTicketIds();

        if (userIds.size() != airlineTicketIds.size() )
            throw new RuntimeException("userIds 와 airlineTicketIds의 길이는 항상 같아야 합니다.");

        List<Integer> passengerIds = userIds.stream()
                                            .map((userId) -> passengerReposiotry.findPassengerByUserId(userId)) // passenger 검색
                                            .map(Passenger::getPassengerId)
                                            .collect(Collectors.toList());

        List<Reservation> reservationCandidateList = new ArrayList<>();
        int successCount = 0;

        for (int i = 0; i < userIds.size(); i++){
            Integer passengerId = passengerIds.get(i);
            Integer airlineTicketId = airlineTicketIds.get(i);

            Reservation reservation = reservationRepository.findReservationWithPassengerIdAndAirLineTicketId(passengerId, airlineTicketId);
            reservationCandidateList.add(reservation);
        }

        for (Reservation reservation: reservationCandidateList){
            if (reservation == null ) continue;
            if (reservation.getReservationStatus().equals("확정")) continue;

            Payment paymentNew = new Payment(reservation.getReservationId(), reservation.getPassengerId());
            Boolean success = paymentRepository.savePayment(paymentNew);

            if (success) {
                successCount++;
                reservationRepository.updateReservationStatus(reservation.getReservationId(), "확정");
            }
        }
        return successCount;
    }
}
