package com.tours.repo;

import com.tours.entities.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TourRepo extends JpaRepository<Tour,Long>
{

    @Query("Select t from Tour t " +
            "JOIN FETCH t.location " +
            "JOIN FETCH t.lodging " +
            "JOIN FETCH t.transport"
    )
    List<Tour> findAllToursWithDetails();


    @Query("Select t from Tour t " +
            "JOIN FETCH t.location " +
            "JOIN FETCH t.lodging " +
            "JOIN FETCH t.transport " +
            "WHERE t.id = :tourId "
    )
    Tour findTourByIdWithDetails(@Param("tourId") Long tourId);


}
