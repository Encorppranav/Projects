package com.tours.repo;

import com.tours.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<Users,Integer>
{
    @Query("Select u from Users u where u.email = :email")
    public Users getUserByEmail(@Param("email")String email);

    public Boolean existsByEmail(String email);

    public Optional<Users> findByEmail(String email);

    @Query("Select u.role from Users u where u.email = :email")
    public String findRoleByUsername(String email);

}
