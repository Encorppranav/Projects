package com.tours.service;

import com.tours.entities.*;
import com.tours.exception.LocationNotFoundException;
import com.tours.exception.LodgingNotFoundException;
import com.tours.exception.TourNotFoundException;
import com.tours.exception.TransportNotFoundException;
import com.tours.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class TourService
{
    private static final Logger logger = Logger.getLogger(TourService.class.getName());

    @Autowired
    private TourRepo tourRepo;

    @Autowired
    private LocationRepo locationRepo;

    @Autowired
    private LodgingRepo lodgingRepo;

    @Autowired
    private TransportRepo transportRepo;

    @Autowired
    private LocationService locationService;

    @Autowired
    private LodgingService lodgingService;

    @Autowired
    private TransportService transportService;

    @Autowired
    private BookingService bookingService;
    @Autowired
    private BookingRepo bookingRepo;


    //save the tour

    public Tour saveTour(Tour tour , Long locationId, Long lodgingId, Long transportId)
    {
      Location location = locationRepo.findById(locationId)
                .orElseThrow(() -> new LocationNotFoundException("Location not found with id " + locationId));

        Lodging lodging = lodgingRepo.findById(locationId)
                .orElseThrow(() -> new LodgingNotFoundException("Lodgingnot found with id " + lodgingId));

        Transport transport = transportRepo.findById(transportId)
                .orElseThrow(() -> new TransportNotFoundException("Transport not found with id " + transportId));

        tour.setLodging(lodging);
        tour.setLocation(location);
        tour.setTransport(transport);

         Tour savedTour = tourRepo.save(tour);

         logger.info("Tour saved successfully with ID: " + savedTour.getId());

         return savedTour;
    }

    // Get all tours with their details
    @Transactional
    public List<Tour> getAllToursWithDetails() {
        logger.info("Fetching all tours with details.");
        List<Tour> tours = tourRepo.findAllToursWithDetails();
        return tourRepo.findAllToursWithDetails();
    }

    //Get a tour by Id
    @Transactional
    public Optional<Tour> getTourById(Long id)
    {
        logger.info("Fetching the tour details with id: " + id);

        return Optional.ofNullable(tourRepo.findById(id)
                .orElseThrow(() ->
                        new TourNotFoundException("Tour not found with id " + id)));
    }

    //Delete the Tour
    @Transactional
    public void deleteTour(Long id)
    {
        logger.info("Deleting tour with ID: " + id);

       Tour tour = tourRepo.findById(id).
                orElseThrow(() ->new TourNotFoundException("Tour not found with id " + id));

        bookingService.deleteBookingsByTourId(id);
        logger.info("Associated bookings deleted for Tour ID: " + id);

        // Delete associated entities if they exist
//        if (tour.getLocation() != null) {
//            Long locationId = tour.getLocation().getId();
//            locationService.deleteLocation(locationId);
//            logger.info("Associated location deleted with ID: " + locationId);
//        }
//
//        if (tour.getLodging() != null) {
//            Long lodgingId = tour.getLodging().getId();
//            lodgingService.deleteLodging(lodgingId);
//            logger.info("Associated lodging deleted with ID: " + lodgingId);
//        }
//
//        if (tour.getTransport() != null) {
//            Long transportId = tour.getTransport().getId();
//            transportService.deleteTransport(transportId);
//            logger.info("Associated transport deleted with ID: " + transportId);
//        }

        tourRepo.delete(tour);
        logger.info("Tour deleted successfully with ID: " + id);


    }


    // Update a tour with associations
    @Transactional
    public Tour updateTourWithAssociations(Long tourId, Tour updatedTour) {
        logger.info("Updating tour with ID: " + tourId);
        Tour existingTour = tourRepo.findById(tourId)
                .orElseThrow(() -> new TourNotFoundException("Tour not found with id " + tourId));

        // Update Location if necessary
        if (updatedTour.getLocation() != null) {
            locationService.updateLocation(existingTour.getLocation().getId(), updatedTour.getLocation());
        }

        // Update Lodging if necessary
        if (updatedTour.getLodging() != null) {
            lodgingService.updateLodging(existingTour.getLodging().getId(), updatedTour.getLodging());
        }

        // Update Transport if necessary
        if (updatedTour.getTransport() != null) {
            transportService.updateTransport(existingTour.getTransport().getId(), updatedTour.getTransport());
        }

        // Update other tour details
        existingTour.setTourName(updatedTour.getTourName());
        existingTour.setTourDescription(updatedTour.getTourDescription());
        existingTour.setTourGuide(updatedTour.getTourGuide());
        existingTour.setStartDate(updatedTour.getStartDate());
        existingTour.setEndDate(updatedTour.getEndDate());
        existingTour.setMeals(updatedTour.getMeals());
        existingTour.setActivities(updatedTour.getActivities());
        existingTour.setPrice(updatedTour.getPrice());
        existingTour.setTicketsAvailable(updatedTour.getTicketsAvailable());
        existingTour.setTourImages(updatedTour.getTourImages());

        Tour savedTour = tourRepo.save(existingTour);
        logger.info("Tour updated successfully with ID: " + savedTour.getId());
        return savedTour;
    }




}
