package com.tours.repo;

import com.tours.entities.Booking;
import com.tours.entities.Lodging;
import com.tours.entities.Tour;
import com.tours.entities.Transport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepo extends JpaRepository<Booking , Long>
{

    @Query("SELECT SUM(b.numberOfTickets) FROM Booking b WHERE b.tour.id = :tourId " )
    public Integer countTicketsSoldForTour(@Param("tourId") int tourId);


    //Fetch the Booking details by tour id
    List<Booking> findByTourId(Long tourId);

    //Count number of tickets sold after successfull payment
    @Query("SELECT SUM(b.numberOfTickets) FROM Booking b WHERE b.tour.id = :tourId AND b.paymentStatus = :status ")
    Integer countTicketsSoldForTourWithSuccessfulPayment
    (
            @Param("tourId") Long tourId,
            @Param("status") Booking.PaymentStatus status
    );

    //Fetch Bookings For a specific tour with successful payment status
    List<Booking> findByTourIdAndPaymentStatus(Long tourId , Booking.PaymentStatus paymentStatus );

    //Filter the tours

    @Query(
            "SELECT t FROM Tour t WHERE" +
             ":country is NULL OR t.location.country = :country AND" +
              ":lodgingType is NULL OR t.lodging.lodgingType = :lodgingType AND" +
                    "(:transportType IS NULL OR t.transport.transportType = :transportType) AND " +
              ":minPrice is NULL OR t.price >= :minPrice AND " +
              ":maxPrice is NULL OR t.price <= :maxPrice"
    )

    List<Tour> filterTours(
            @Param("country") String location,
            @Param("lodgingType") String lodgingType,
            @Param("transportType")String transportType,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice
            );



}
