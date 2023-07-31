package com.github.supercodingspring.repository.payments;

import java.time.LocalDateTime;

public class Payment {

    private Integer paymentId;
    private Integer passengerId;
    private Integer reservationId;

    private LocalDateTime payTime;

    public Payment(Integer reservationId, Integer passengerId) {
        this.passengerId = passengerId;
        this.reservationId = reservationId;
        this.payTime = LocalDateTime.now();
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public Integer getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Integer passengerId) {
        this.passengerId = passengerId;
    }

    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public LocalDateTime getPayTime() {
        return payTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Payment)) {
            return false;
        }

        Payment payment = (Payment) o;

        return paymentId.equals(payment.paymentId);
    }

    @Override
    public int hashCode() {
        return paymentId.hashCode();
    }
}
