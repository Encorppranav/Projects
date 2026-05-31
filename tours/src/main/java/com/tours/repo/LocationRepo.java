package com.tours.repo;

import com.tours.entities.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepo extends JpaRepository<Location,Long> {
    Location findTopByOrderByIdDesc();
}
