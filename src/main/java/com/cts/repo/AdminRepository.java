package com.cts.repo;

//import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cts.models.Admin;


public interface AdminRepository extends JpaRepository<Admin,Integer> {
	
	Admin findByEmail(String email);	
}
