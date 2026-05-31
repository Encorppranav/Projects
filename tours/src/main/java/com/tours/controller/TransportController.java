package com.tours.controller;

import com.tours.entities.Transport;
import com.tours.service.TransportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/transports")
@CrossOrigin(origins = "*")
public class TransportController
{
    @Autowired
    private TransportService transportService;

    //Add Transport
    @PostMapping("/addTransport")
    @PreAuthorize("hasRole('ADMIN')")//Only Accessible to Admin
    public ResponseEntity<Transport> addTransport(@RequestBody Transport transport)
    {

         Transport addedTransport = transportService.addTransport(transport);

         return new ResponseEntity<>(addedTransport, HttpStatus.OK);
    }

    //Get transport by id -- Only Accessible by Admin
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Transport> getTransport(@PathVariable Long id)
    {
        Optional<Transport> optional = Optional.ofNullable(transportService.getTransportById(id));

        return optional.map(ResponseEntity::ok).orElseGet(()-> ResponseEntity.notFound().build());
    }


    // Get all transports - Only accessible by ADMIN role
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Ensuring only ADMIN can access this endpoint
    public ResponseEntity<List<Transport>> getAllTransports() {
        return ResponseEntity.ok(transportService.getAllTransports());
    }


    //Update the transports -- Only Accessible by Admin
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Transport> updateTransport(@PathVariable Long id ,@RequestBody Transport transport)
    {

        return  ResponseEntity.ok( transportService.updateTransport(id,transport));
    }

    //Delete the transport -- Only Accessible by Admin
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTransport(@PathVariable Long id)
    {
        transportService.deleteTransport(id);
        return  ResponseEntity.noContent().build();
    }



}
