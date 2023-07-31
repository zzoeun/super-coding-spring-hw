package com.github.supercodingspring.repository.payments;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


@Repository
public class PaymentJdbcTemplateDao implements PaymentRepository {

    private JdbcTemplate template;

    public PaymentJdbcTemplateDao(@Qualifier("jdbcTemplate2") JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public Boolean savePayment(Payment paymentNew) {
        Integer rowNums = template.update("INSERT INTO payment(passenger_id, reservation_id, pay_at) VALUES (? ,?, ?)",
                                          paymentNew.getPassengerId(), paymentNew.getReservationId(), paymentNew.getPayTime());
        return rowNums > 0;
    }
}
