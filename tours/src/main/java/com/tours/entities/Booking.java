package com.tours.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "booking")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Booking
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id" , referencedColumnName = "id" , nullable = false)
    private Users customer;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tour_id" , referencedColumnName = "id" , nullable = false)
    private Tour tour;

    private int numberOfTickets;
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Temporal(TemporalType.TIMESTAMP)
    private Date bookingDate;

    private boolean isBookingConfirmed;

    private String paymentTransactionId;

    public enum PaymentStatus{
        SUCCESS,
        PENDING,
        FAILED
    }

    //Method to check the availability of tickets
    public boolean checkAvailibility(){return tour.getTicketsAvailable() > 0 ;}

    //Method to confirm the booking
    public void confirmBooking()
    {
        if(paymentStatus == PaymentStatus.SUCCESS && checkAvailibility())
        {
            tour.setTicketsAvailable(tour.getTicketsAvailable() - numberOfTickets);
            this.isBookingConfirmed = true;
        }
        else{
            this.isBookingConfirmed = false;
        }
    }

    //Method to handle payment failure
    public void handlePaymentFailure(String reason)
    {
        this.paymentStatus = PaymentStatus.FAILED;
        this.isBookingConfirmed = false;
    }


}
