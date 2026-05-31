package com.tours.controller;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.tours.ToursApplication;
import com.tours.entities.Booking;
import com.tours.entities.Tour;
import com.tours.entities.Users;
import com.tours.repo.TourRepo;
import com.tours.repo.UserRepo;
import com.tours.service.BookingService;
import com.tours.service.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import java.util.List;
import java.util.Map;

@RestController
public class BookingController
{
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private TourRepo tourRepo;

    @Autowired
    private TourService tourService;

    @Autowired
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }


    public Users getLoggedInUser()
    {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(userDetails == null)
        {
            return  null;
        }

    return   userRepo.getUserByEmail(userDetails.getUsername());

    }

    @GetMapping("customer/allTours")
    @PreAuthorize("hasRole('CUSTOMER'")
    public ResponseEntity<?> getAllTours()
    {

        Users loggedInUser = getLoggedInUser();

        if(loggedInUser == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

         List<Tour> tours = tourService.getAllToursWithDetails();

         return ResponseEntity.ok(Map.of(
                 "message" , "User with " + loggedInUser.getEmail() + " is viewing the tour ",
                 "authorised" , tours
                 )

         );

    }

    @GetMapping("customer/tour/{id}")
    @PreAuthorize("hasRole('CUSTOMER'")
    public ResponseEntity<?> getTourById(@PathVariable("id") Long id)
    {
        Users loggedInUser = getLoggedInUser();

        if(loggedInUser == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        return tourService.getTourById(id).map((tour) ->

            ResponseEntity.ok(Map.of(
                   "message" , "User: " + loggedInUser.getEmail() + " is viewing the tour.",
                    "tour-details" , tour
            )))
                .orElse( ResponseEntity.status(HttpStatus.UNAUTHORIZED).
                        body(Map.of("message" , "Tour not found with id " + id)));



    }

    @PostMapping("customer/create-payment-intent/{tourId}")
    @PreAuthorize("hasRole('CUSTOMER'")
    public ResponseEntity<?> getPaymentIntent(
            @PathVariable Long tourId,
            @RequestParam int numberOfTickets
    )
    {
        Users loggedInUser = getLoggedInUser();

        if(loggedInUser == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

    try
    {
        //create the preliminary booking for the user
       Booking preliminaryBooking = bookingService.createBooking(loggedInUser,tourId,numberOfTickets);

       //Build Stripe Payment Intent Parameters
        PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
                .setCurrency("usd")
                .setAmount(preliminaryBooking.getTotalPrice().longValue() * 100) // amount in cents
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(createParams);//create PaymentIntent

        //Build Stripe Checkout Session Parameters

         SessionCreateParams checkoutParams = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:5173/success?paymentIntentId=" + paymentIntent.getId() + "&bookingId=" + preliminaryBooking.getBookingId())
                .setCancelUrl("http://localhost:5173/cancel")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity((long) numberOfTickets)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount((long) (preliminaryBooking.getTour().getPrice() * 100)) // Unit price in cents
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Tour Booking") // Product name
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();



        // Create a Checkout Session in Stripe
        Session checkoutSession = Session.create(checkoutParams);

        return ResponseEntity.ok(Map.of(
                "paymentIntentId", paymentIntent.getId(),
                "checkoutSessionId", checkoutSession.getId(),
                "bookingId", preliminaryBooking.getBookingId(),
                "totalAmount", preliminaryBooking.getTotalPrice(),
                "checkoutUrl", checkoutSession.getUrl() // URL for completing payment
        ));
    }
    catch (Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
    }
    }


    @PostMapping("customer/confirm-payment/{bookingId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> confirmPayment(
            @PathVariable Long bookingId,
            @RequestParam String paymentIntentId
    )
    {
        Users loggedInUser = getLoggedInUser();

        if(loggedInUser == null)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User is not logged in.");
        }

         Booking confirmedBooking = bookingService.confirmBooking(bookingId,paymentIntentId);

         try{
             return ResponseEntity.ok(Map.of(
                     "message","Booking Confirmed Successfully",
                     "Details" , confirmedBooking
             ));
         }
         catch (Exception e)
         {
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                     "error" , e.getMessage()
             ));

         }

    }


    @GetMapping("customer/filterTours")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> filterTours(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String lodgingType,
            @RequestParam(required = false) String transportType,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        Users loggedInUser = getLoggedInUser();
        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not logged in.");
        }

        List<Tour> tours = bookingService.filterTours(country, lodgingType, transportType, minPrice, maxPrice);

        if (tours.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message", "Tours not found under the applied filters"
                    ));
        }

        return ResponseEntity
                .ok(Map.of(
                        "message", "Tours are fecthed successfully",
                        "tour-details", tours
                ));
    }

    @GetMapping("admin/tourTicketSummary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getTicketSummaryPerTour() {
        Users loggedInUser = getLoggedInUser();
        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not logged in.");
        }

        try {
            List<Map<String, Object>> ticketSummary = bookingService.getSummaryOfTicketsPerTour();
            return ResponseEntity.ok(Map.of(
                    "message", "Admin: " + loggedInUser.getEmail() + " is viewing ticket summary.",
                    "summary", ticketSummary
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("admin/tourDetails/{tourId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getTourDetailsWithBookings(@PathVariable Long tourId) {
        Users loggedInUser = getLoggedInUser();
        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not logged in.");
        }

        try {
            Map<String, Object> tourDetails = bookingService.getTourDetailsWithBookings(tourId);
            if (tourDetails != null) {
                return ResponseEntity.ok(Map.of(
                        "message", "Admin: " + loggedInUser.getEmail() + " is viewing tour details.",
                        "details", tourDetails
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "message", "Tour not found with ID: " + tourId
                ));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

}
