package com.cts.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cts.models.Employee;
import java.util.List;


public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
	Employee findByEmail(String email);
	
	Employee findByEmployeeId(int employeeId);
	
}
