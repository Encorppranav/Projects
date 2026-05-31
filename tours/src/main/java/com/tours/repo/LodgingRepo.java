package com.tours.repo;

import com.tours.entities.Lodging;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LodgingRepo extends JpaRepository<Lodging,Long>
{
    Lodging findTopByOrderByIdDesc();
}
