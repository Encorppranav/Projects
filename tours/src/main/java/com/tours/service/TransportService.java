package com.tours.service;

import com.tours.entities.Transport;
import com.tours.exception.TransportNotFoundException;
import com.tours.repo.TransportRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class TransportService
{
    private static final Logger logger = Logger.getLogger(TransportService.class.getName());

    @Autowired
    private TransportRepo transportRepository;

    //Add a new transport
    @CachePut(value= "transports" , key = "#transport.id")
    public Transport addTransport(Transport transport)
    {
        logger.info("Adding a transport with details : " + transport );
        Transport savedTransport = transportRepository.save((transport));
        logger.info("Transport added successfully with id: " + transport.getId());

        return savedTransport ;
    }

    //Get Transport by id
    @Cacheable(value = "transports" , key = "#id")
    public Transport getTransportById(Long id)
    {
        logger.info("Searching initialized fot transport with id:" + id);

        Transport transport = transportRepository.findById(id)
                .orElseThrow(()-> new TransportNotFoundException("Transport not found with id: " + id));

        logger.info("Transport fetched successfully");

        return transport;
    }

    //Fetch all transports
    @Cacheable(value = "TransportCache" , key = "AllTransports")
    public List<Transport> getAllTransports()
    {
        logger.info("Fetching all transports");

        List<Transport> transports = transportRepository.findAll();

        logger.info("Fetched " + transports.size() + " Transports");

        return transports;
    }


    //Update Transport
    @Cacheable(value = "transports" , key = "#id")
    public Transport updateTransport(Long id , Transport transportDetails)
    {
        logger.info("Updating the transport with id: " + id);
       Transport transport =  transportRepository.findById(id)
                .orElseThrow(()-> new TransportNotFoundException("Transport with given id is not present"));

       //Update Transport Details
       transport.setTransportName(transportDetails.getTransportName());
       transport.setTransportDescription(transportDetails.getTransportDescription());
       transport.setTransportType(transportDetails.getTransportType());
       transport.setEstimatedTravelTime(transportDetails.getEstimatedTravelTime());

       Transport updatedTransport = transportRepository.save(transport);

       logger.info("Transport updated successfully: " + updatedTransport);

       return updatedTransport;
    }


    //Delete Transport
    @CacheEvict(value = "transports" , key = "#id")
    public void deleteTransport(Long id)
    {
        logger.info("Deleting the Transport wit ID: " +id);

        transportRepository.findById(id)
                .orElseThrow(() -> new TransportNotFoundException("Transport not found with id " + id));

        transportRepository.deleteById(id);

        logger.info("Transport with ID: " +id + " deleted successfully");
    }




}
