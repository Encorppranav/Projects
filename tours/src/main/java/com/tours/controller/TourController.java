package com.tours.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tours.entities.Location;
import com.tours.entities.Lodging;
import com.tours.entities.Tour;
import com.tours.entities.Transport;
import com.tours.repo.LocationRepo;
import com.tours.repo.LodgingRepo;
import com.tours.repo.TransportRepo;
import com.tours.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("admin/tours")
@CrossOrigin(origins = "*")
public class TourController
{
    @Autowired
    private TourService tourService;

    @Autowired
    private CloudinaryImageService cloudinaryImageService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private TransportService transportService;

    @Autowired
    private LodgingService lodgingService;


    @Autowired
    private LocationRepo locationRepo;

    @Autowired
    private LodgingRepo lodgingRepo;

    @Autowired
    private TransportRepo transportRepo;


    @PostMapping
    @PreAuthorize("hasRole('ADMIN)")
    public ResponseEntity<Tour> uploadImagesToTour(
            @RequestParam String tourJSON,
            @RequestParam MultipartFile image1,
            @RequestParam MultipartFile image2
    ) throws JsonProcessingException
    {
        //Parse the tour json data into the Tour object
           Tour tour = new ObjectMapper().readValue(tourJSON,Tour.class);

           //Upload the images from Cloudinary
        String url1 = cloudinaryImageService.uploadImages(image1);
        String url2 = cloudinaryImageService.uploadImages(image2);

        //Set the image URL's in Tour object
           tour.setTourImages(List.of(url1,url2));

           //Update the location,lodging,transport
        Long locationId = locationRepo.findTopByOrderByIdDesc().getId();
        Long lodgingId = lodgingRepo.findTopByOrderByIdDesc().getId();
        Long transportId = transportRepo.findTopByOrderByIdDesc().getId();

        //save the tour object in schema
         Tour savedTour = tourService.saveTour(tour,locationId,lodgingId,transportId);
         return ResponseEntity.ok(savedTour);

    }

    //Api to retrieve the all tours details including location,lodging and transport
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Restricting access to users with ADMIN role
    public ResponseEntity<List<Tour>> getAllTours() {
        List<Tour> tours = tourService.getAllToursWithDetails();
        return ResponseEntity.ok(tours);
    }

    // API to retrieve a tour based on given id along with associated location, lodging, transport details
//    @Operation(
//            summary = "Retrieve a tour by ID",
//            description = "Fetches details of a specific tour by its ID, including associated information about location, lodging, and transport. " +
//                    "If the tour ID does not exist, a 404 Not Found status is returned."
//    )
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Restricting access to users with ADMIN role
    public ResponseEntity<Tour> getTourById(@PathVariable Long id) {
        return tourService.getTourById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<Tour> updateTourWithId(
            @PathVariable Long id,
            @RequestParam("tour") String updatedTourJson,
            @RequestParam(value = "image1" , required = false) MultipartFile image1,
            @RequestParam(value = "image2" , required = false) MultipartFile image2
    )
    {
        try{
           Tour updatedTour =  new ObjectMapper().readValue(updatedTourJson,Tour.class);

          List<String> currentImages = updatedTour.getTourImages();

           if(image1 != null && !currentImages.isEmpty())
           {
              String newImage1 = cloudinaryImageService.updateImage(currentImages.get(0), image1);
              currentImages.set(0,newImage1);
           }


            if(image2 != null && !currentImages.isEmpty())
            {
                String newImage2 = cloudinaryImageService.updateImage(currentImages.get(0), image2);
                currentImages.set(0,newImage2);
            }


               return ResponseEntity.ok(tourService.updateTourWithAssociations(id,updatedTour));


        }catch (Exception e)
        {
            return ResponseEntity.notFound().build();
        }

    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Restricting access to users with ADMIN role
    public ResponseEntity<Void> deleteTour(@PathVariable("id")  Long tourId)
    {
        try {

            Tour tour = tourService.getTourById(tourId).orElseThrow( () ->new RuntimeException("Tour Not Found"));

            for(String image :tour.getTourImages() )
            {
                cloudinaryImageService.deleteImage(image);
            }
            
            tourService.deleteTour(tourId);
            return ResponseEntity.noContent().build();
        }catch (RuntimeException e)
        {
            return ResponseEntity.notFound().build();
        }

    }

}
