package com.tours.service;

import com.tours.entities.Booking;
import com.tours.entities.Tour;
import com.tours.entities.Users;
import com.tours.exception.BookingNotFoundException;
import com.tours.exception.InsufficientTicketsException;
import com.tours.exception.PaymentFailedException;
import com.tours.repo.BookingRepo;
import com.tours.repo.TourRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
import java.util.*;

@Service
public class BookingService
{
    private static final Logger logger = LoggerFactory.getLogger(BookingService.class.getName());

    @Autowired
   private BookingRepo bookingRepo;

    @Autowired
    private TourRepo tourRepo;


    public Booking createBooking(Users customer , Long tourID , int numberOfTickets)
    {

        logger.info("Searching for tour with id:" + tourID);
        Optional<Tour> optionalTour  =  tourRepo.findById(tourID);

        if(optionalTour.isPresent())
        {
            Tour tour = optionalTour.get();

            if(tour.getTicketsAvailable() >= numberOfTickets)
            {
                Double totalPrice = tour.getPrice() * numberOfTickets;

             Booking booking = Booking.builder()
                        .customer(customer)
                        .bookingDate(new Date())
                        .totalPrice(totalPrice)
                        .numberOfTickets(numberOfTickets)
                        .paymentStatus(Booking.PaymentStatus.PENDING)
                        .tour(tour)
                        .isBookingConfirmed(false)
                        .build();

             logger.debug("Booking Successful with details: {}", booking);

             return bookingRepo.save(booking);

            }

            else {
                logger.info("No Sufficient Tickets Avaialble for Booking for tourId: " + tourID);

                throw new InsufficientTicketsException("Not enough tickets available.");

            }
        }

        else {
            logger.error("Tour not found for ID: {}", tourID);
            throw new BookingNotFoundException("Tour not found.");
        }

    }


    //Confirm the booking

    public Booking confirmBooking(Long bookingId , String paymentIntent)
    {
        logger.info("Confirming the Booking with id: {}" , bookingId);

         Optional<Booking> optionalBooking  = bookingRepo.findById(bookingId);

         if(optionalBooking.isPresent())
         {
              Booking booking = optionalBooking.get();
                Tour tour = booking.getTour();

                if(booking.getPaymentStatus() == Booking.PaymentStatus.PENDING)
                {
                    booking.setPaymentStatus(Booking.PaymentStatus.SUCCESS);
                    booking.confirmBooking();
                    booking.setPaymentTransactionId(paymentIntent);

                    logger.info("Booking confirmed for id {}" , bookingId);

                   Integer availableTickets = tour.getTicketsAvailable();
                    if(availableTickets >= booking.getNumberOfTickets())
                    {
                        tourRepo.save(tour);
                    }
                    else {
                        logger.error("Tickets are unavailable for bookingId:{}" , bookingId);
                        throw new InsufficientTicketsException("Not Enough Tickets are Available");
                    }
                    return bookingRepo.save(booking);
                }
                else
                {
                    logger.error("Booking failed due to payment error");

                    throw new PaymentFailedException("Payment Failed or Already Processed");
                }
         }

         else {
             logger.error("Booking with id {} is not found",bookingId);
             throw new BookingNotFoundException("Booking with id " + bookingId + " is not found");
         }

    }


    //Get the summary of each tour booked succesfully
    public List<Map<String,Object>> getSummaryOfTicketsPerTour()
    {          logger.info("Generating ticket summary per tour");
             List<Tour> tours = tourRepo.findAll();

            List<Map<String,Object>> summary = tours.stream().map((tour) ->{

               int ticketsSold = Optional.ofNullable(bookingRepo.countTicketsSoldForTourWithSuccessfulPayment(tour.getId(), Booking.PaymentStatus.SUCCESS))
                       .orElse(0);

                 Map<String, Object> tourSummary = new HashMap<>();
                 tourSummary.put("tourId", tour.getId());
                 tourSummary.put("tourName", tour.getTourName());
                 tourSummary.put("ticketsSold", ticketsSold);
                 tourSummary.put("ticketsAvailable", tour.getTicketsAvailable());
                 tourSummary.put("totalRevenue", ticketsSold * tour.getPrice());

                 logger.debug("Tour id: {} having the summary: {}", tour.getId() , tourSummary);

                 return tourSummary;

             }).toList();

        logger.info("Ticket summary generation completed");
        return summary;


    }

    public Map<String,Object> getTourDetailsWithBookings(Long id)
    {
        logger.info("Fetching the tour and booking details regarding the id: {}" , id);

         Optional<Tour> updateTour = tourRepo.findById(id);

         if(updateTour.isPresent())
         {
              Tour tour = updateTour.get();

            List<Booking> bookings = bookingRepo.findByTourIdAndPaymentStatus(id,Booking.PaymentStatus.SUCCESS);

            int tickets = Optional.ofNullable(bookingRepo.countTicketsSoldForTourWithSuccessfulPayment(id, Booking.PaymentStatus.SUCCESS))
                        .orElse(0);

          List<Map<String,Object>> bookingDetails =  bookings.stream().map((booking) ->{

                Map<String,Object> bookingInfo = new HashMap<>();
                bookingInfo.put("bookingId", booking.getBookingId());
                bookingInfo.put("customerName", booking.getCustomer().getName());
                bookingInfo.put("customerEmail", booking.getCustomer().getEmail());
                bookingInfo.put("numberOfTickets", booking.getNumberOfTickets());
                bookingInfo.put("totalPrice", booking.getTotalPrice());
                bookingInfo.put("bookingDate", booking.getBookingDate());
                bookingInfo.put("paymentStatus", booking.getPaymentStatus());
                return bookingInfo;

            }).toList();


             Map<String, Object> tourDetails = new HashMap<>();
             tourDetails.put("tourId", tour.getId());
             tourDetails.put("tourName", tour.getTourName());
             tourDetails.put("tourDescription", tour.getTourDescription());
             tourDetails.put("ticketsSold", tickets);
             tourDetails.put("bookings", bookingDetails);

             logger.debug("Tour details with bookings for Tour ID {}: {}", id, tourDetails);
             return tourDetails;



         }

         else {
             logger.info("Tour is not found with id:{}" , id);
            return null;
         }
    }


    // Filter tours based on criteria
    public List<Tour> filterTours(String country, String lodgingType, String transportType, Double minPrice, Double maxPrice) {
        logger.info("Filtering tours with criteria - Country: {}, Lodging: {}, Transport: {}, Min Price: {}, Max Price: {}",
                country, lodgingType, transportType, minPrice, maxPrice);

        return bookingRepo.filterTours(country, lodgingType, transportType, minPrice, maxPrice);
    }

    //To delete associated bookings, if any tour gets delete
    public void deleteBookingsByTourId(Long tourId) {
        logger.info("Deleting all bookings for Tour ID: {}", tourId);
        List<Booking> bookings = bookingRepo.findByTourId(tourId);
        bookingRepo.deleteAll(bookings);
        logger.info("Successfully deleted {} bookings for Tour ID: {}", bookings.size(), tourId);
    }

}
