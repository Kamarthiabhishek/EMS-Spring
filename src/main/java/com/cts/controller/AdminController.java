package com.cts.controller;

import java.time.LocalDate;
import java.util.Date;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.cts.models.Admin;
import com.cts.models.Employee;
import com.cts.repo.AdminRepository;
import com.cts.repo.EmployeeRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class AdminController {

	@Autowired
	private AdminRepository adminrepo;
	

	
	@Autowired
	private EmployeeRepository empRepo;

	@GetMapping("/admin")
	public String adminLogin(Model model) {
		model.addAttribute("admin", new Admin());
		return "admin_functions/admin_login";
	}

	@GetMapping("/new/admin")
	public String adminregisterForm(Model model) {
		model.addAttribute("admin", new Admin());
		return "admin_functions/admin_register";
	}

	@PostMapping("/register/admin")
	public String adminregister(@Valid Admin admin, BindingResult result) {
		if (result.hasErrors()) {
			return "admin_functions/admin_register";
		}
		Random r = new Random();
		int num = r.nextInt(9000) + 1000;
		admin.setAdmin_id(num);
		adminrepo.save(admin);
		return "redirect:/admin";
	}

	@GetMapping("/adminhome")
	public String employeeDisplay(Model model) {
		model.addAttribute("employee", empRepo.findAll());
		model.addAttribute("emp",new Employee());
		return "admin_functions/admin_dashboard";
	}

	@PostMapping("/adminhome")
	public String employeesDisplay(Model model) {
		// TODO: process POST request
		model.addAttribute("employee", empRepo.findAll());
		return "admin_functions/admin_dashboard";
	}

	@PostMapping("/admin/login")
	public String loginSubmit(@ModelAttribute Admin admin, BindingResult bindingResult, Model model) {
		boolean success = login(admin);
		String res = "";
		if (bindingResult.hasErrors()) {
			System.out.println(bindingResult);
			String msg = "Invalid Credintials";
			model.addAttribute("message", msg);
			res = "admin_functions/admin_login";

		} else if (success) {
			res = "redirect:/adminhome";
			model.addAttribute("Email", admin.getEmail());
		} else {
			res = "redirect:/admin";
		}
		return res;
	}

	public boolean login(Admin admin) {
		Admin existingUser = adminrepo.findByEmail(admin.getEmail());
		if (existingUser != null && existingUser.getPassword().equals(admin.getPassword())) {
			return true;
		} else {
			return false;
		}
	}

	// getting employees done
	@GetMapping("/new/employee")
	public String registerForm(Model model) {
		Employee emp = new Employee();
		model.addAttribute("employee", emp);
		return "admin_functions/employee_register";
	}


    @PostMapping("/employee/register")
    public String register(@ModelAttribute("employee") @Valid Employee emp,
                           BindingResult result,
                           @RequestParam("imageFile") MultipartFile imageFile,
                           Model model) {
        if (result.hasErrors()) {
            return "admin_functions/employee_register";
        }

        // Generate a random employee ID
        Random r = new Random();
        int num = r.nextInt(9000) + 1000;
        emp.setEmployeeId(num);
        empRepo.save(emp);

        model.addAttribute("employeeId", num);
        return "admin_functions/employee_success";
    }

	@GetMapping("/employee/{employee_id}")
	public String empUpdateForm(@PathVariable("employee_id") int employee_id, Employee emp, Model model) {
		System.out.println("1.");
		Employee employee = empRepo.findById(employee_id).get();
		//model.addAttribute("employee", emp);
		model.addAttribute("employee", employee);
		return "admin_functions/employee_update";
	}

	@PostMapping("employee/update")
	public String updateEmployee(@Valid Employee emp, BindingResult result) {
		if (result.hasErrors()) {
			return "admin_functions/employee_update";
		}
		empRepo.save(emp);
		return "redirect:/adminhome";
	}

	@GetMapping("/employee/delete/{employee_id}")
	public String deleteemployee(@PathVariable("employee_id") int employee_id, Model model) {
		empRepo.deleteById(employee_id);
		Employee emp = new Employee();
		model.addAttribute("employee", emp);
		return "admin_functions/delete_success";
	}

	@PostMapping("/delete/success")
	public String deleteSuccess(Employee emp, Model model) {
		// TODO: process POST request
		int num = emp.getEmployeeId();
		model.addAttribute("employeeId", num);
		return "admin_functions/delete_success";
	}

	@GetMapping("/employee/view/{employee_id}")
	public String Viewemployee(@PathVariable("employee_id") int employee_id, Model model) {
		Employee std = empRepo.findById(employee_id).get();
		model.addAttribute("employee", std);
		return "admin_functions/view_employee";
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate(); // This will invalidate the session
		return "redirect:/admin";

	}	
	
	@PostMapping("/search")
	public String searchStd(@RequestParam("employeeId") int empId, Model model) {
		Employee emp = empRepo.findByEmployeeId(empId);
		
		if(emp != null) {
			model.addAttribute("employee", emp);
			return "admin_functions/show_employee";
		}else {
			model.addAttribute("employeeId", emp);
			return "admin_functions/not_found";
		}
	}
}
