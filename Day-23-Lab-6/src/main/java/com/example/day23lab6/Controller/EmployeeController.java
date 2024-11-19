package com.example.day23lab6.Controller;

import com.example.day23lab6.ApiResponse.ApiResponse;
import com.example.day23lab6.Model.Employee;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/employee")

public class EmployeeController {


    ArrayList<Employee> employees = new ArrayList<>();

    // endpoint to get all the avilable employees
    @GetMapping("/get/all")
    public ResponseEntity getAll() {
        return ResponseEntity.status(200).body(employees);
    }

    //endpoint to add an employee
    @PostMapping("/add")
    public ResponseEntity addEmp(@RequestBody @Valid Employee employee, Errors errors) {

        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }
        employees.add(employee);
        return ResponseEntity.status(200).body(new ApiResponse("employee added successfully"));
    }

    // endpoint to update an employee
    @PutMapping("/update/{id}")
    public ResponseEntity updateEmployee(@PathVariable String id, @RequestBody @Valid Employee employee, Errors errors) {
        for (Employee e : employees) {
            if (e.getId().equals(id)) {
                if (errors.hasErrors()) {
                    String message = errors.getFieldError().getDefaultMessage();
                    return ResponseEntity.status(400).body(new ApiResponse(message));
                }
                employees.set(employees.indexOf(e), employee);
                return ResponseEntity.status(200).body(new ApiResponse("Employee updated successfully"));
            }
        }
        return ResponseEntity.status(400).body(new ApiResponse("Employee does not exist"));
    }

    // endpoint to delete an employee
    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteEmp(@PathVariable String id) {
        for (Employee e : employees) {
            if (e.getId().equals(id)) {
                employees.remove(employees.indexOf(e));
                return ResponseEntity.status(200).body(new ApiResponse("employee deleted successfully"));
            }
        }
        return ResponseEntity.status(400).body(new ApiResponse("Employee does not exist"));
    }

    // endpoint to search employee by position

    @Validated
    @GetMapping("/get/position")
    public ResponseEntity groupByPosition(@RequestParam
                                          @NotNull(message = "position must not be empty")
                                          @Pattern(regexp = "^(supervisor|coordinator)$", message = "positon should be eathier supervisor or coordinator ")
                                          String position) {

        ArrayList<Employee> supervisorEmployee = new ArrayList<>();
        ArrayList<Employee> coordinatorEmployee = new ArrayList<>();
        if (position.equals("supervisor")) {
            for (Employee e : employees) {
                if (e.getPosition().equals("supervisor")) {
                    supervisorEmployee.add(e);
                }
            }
            return ResponseEntity.status(200).body(supervisorEmployee);
        } else if (position.equals("coordinator")) {

            for (Employee e : employees) {
                if (e.getPosition().equals("coordinator")) {
                    coordinatorEmployee.add(e);
                }
            }
            return ResponseEntity.status(200).body(coordinatorEmployee);
        }

        return ResponseEntity.status(400).body(new ApiResponse("No employees found.."));

    }

    // endpoint to get employees by age range ...

    @Validated
    @GetMapping("get/age-range")
    public ResponseEntity getEmployeeByAgeRange(@RequestParam @NotNull @Min(26) @Max(125) int minAge,
                                                @RequestParam @NotNull @Min(26) @Max(125) int maxAge) {

        if (minAge > maxAge) {
            return ResponseEntity.status(400).body(new ApiResponse("minAge must be greater than minAge"));
        }
        ArrayList<Employee> ageEmployee = new ArrayList<>();
        for (Employee e : employees) {
            if (e.getAge() >= minAge && e.getAge() <= maxAge) {
                ageEmployee.add(e);
            }
        }
        return ResponseEntity.status(200).body(ageEmployee);
    }

    // endpoint to apply for annual leave
    @PutMapping("/update/apply-leave")
    public ResponseEntity applyForAnnualLeave(@RequestParam String id) {

        for (Employee e : employees) {
            if (e.getId().equals(id)) {
                if (e.isOnLeave() || e.getAnnualLeave() < 1) {
                    return ResponseEntity.status(400).body(new ApiResponse("employee cannot have annual leave"));
                }
                e.setOnLeave(true);
                e.setAnnualLeave(e.getAnnualLeave() - 1);
                return ResponseEntity.status(200).body(new ApiResponse("employee successfully applied on annual leave!"));
            }
        }

        return ResponseEntity.status(400).body(new ApiResponse("Employee Not Found! "));
    }

    // endpoint to get all employees with no annual leave

    @GetMapping("/get/no-leave")
    public ResponseEntity getEmpWithNoAnnualLeave() {
        ArrayList<Employee> noAnnualLeaveEmployees = new ArrayList<>();
        for (Employee e : employees) {

            if (e.getAnnualLeave() < 1) {
                noAnnualLeaveEmployees.add(e);
            }


        }
        return ResponseEntity.status(200).body(noAnnualLeaveEmployees);
    }

    // endpoint to promote an employee to a supervisor
    @PutMapping("/update/promote-employee")
    public ResponseEntity promoteEmpToSupervisor(@RequestParam String requesterId, @RequestParam String employeeId) {
        for (Employee e : employees) {
            if (e.getId().equals(employeeId)) { // employee exist in the db (list)

                for (Employee emp : employees) {// look for the requester
                    if (emp.getId().equals(requesterId)) {
                        if (emp.getPosition().equals("coordinator")) {// only supervisor are allowed to promote
                            return ResponseEntity.status(400).body(new ApiResponse("requester must be supervisor"));
                        } else {
                            // promotion conditions
                            boolean ageCondition = e.getAge() >= 30;
                            boolean employeeOnLeave = e.isOnLeave();
                            if (ageCondition && !employeeOnLeave) {
                                e.setPosition("supervisor");
                                return ResponseEntity.status(200).body(new ApiResponse("employee has been promoted to a supervisor"));
                            }

                        }

                    }
                }


            }
        }
        return ResponseEntity.status(400).body(new ApiResponse("employee promotion failed"));
    }

}
