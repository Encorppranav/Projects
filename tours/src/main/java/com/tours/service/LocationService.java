package com.tours.service;

import com.tours.entities.Location;
import com.tours.exception.LocationNotFoundException;
import com.tours.repo.LocationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class LocationService
{
    private static final java.util.logging.Logger logger = Logger.getLogger(LodgingService.class.getName());

    @Autowired
    private LocationRepo locationRepo;

    @CachePut(value = "locations" , key = "#location.id")
    public Location addLocation(Location location)
    {
        logger.info("Adding the Location Details with id: " + location.getId());
         Location addedLocation = locationRepo.save(location);

         logger.info("Location added successfully with id: " + addedLocation.getId());

         return addedLocation;
    }


    @Cacheable(value = "locations", key = "#id")
    public Optional<Location> getLocation(Long id)
    {
        logger.info("Fetching the location having id: " + id);

         Optional location = Optional.ofNullable(locationRepo.findById(id))
                 .orElseThrow(() -> new LocationNotFoundException("Location with given id do not exist"));

         logger.info("Succesfully fetched the Location having id: " + id);

         return location;

    }

    @Cacheable(value = "locations", key = "AllLocations")
    public List<Location> getALLLocation()
    {
        logger.info("Fetching the locations ");

        List<Location> locations =  locationRepo.findAll();


        logger.info("Succesfully fetched the Locations");

        return locations;

    }




    @Transactional
    @Cacheable(value = "locations" , key = "#id")
    public Location updateLocation(Long id , Location locationDetails)
    {
        logger.info("Fetching the Location details to be updated for id: " + id);
         Location location = locationRepo.findById(id)
                .orElseThrow(() -> new LocationNotFoundException("Location with given id do not exist"));

        // Update location details
        location.setFromLocation(locationDetails.getFromLocation());
        location.setToLocation(locationDetails.getToLocation());
        location.setCountry(locationDetails.getCountry());
        location.setDistance(locationDetails.getDistance());
        location.setLocationDescription(locationDetails.getLocationDescription());
        location.setEstimatedTravelTime(locationDetails.getEstimatedTravelTime());

        Location updatedLocation = locationRepo.save(location);

        logger.info("Location with id " + id + " updated successfully");

        return updatedLocation;

    }

    @CacheEvict(value = "locations" , key = "#id")
    public void deleteLocation(Long id)
    {
        logger.info("Deleting the location with id: " + id);

        locationRepo.findById(id)
                .orElseThrow(() -> new LocationNotFoundException("Location with given id do not exist"));

        logger.info("Location removed successfully with id" + id);

        locationRepo.deleteById(id);
    }





}
