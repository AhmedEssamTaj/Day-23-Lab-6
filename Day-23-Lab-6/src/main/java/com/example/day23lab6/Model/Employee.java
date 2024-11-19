package com.example.day23lab6.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Employee {

    @NotEmpty(message = "id MUST not be empty")
    @Size(min = 3,message = "id must be more then 2 characters") // 3 because it is the smallest possible id value
    private String id;

    @NotEmpty(message = "name MUST not be empty")
    @Size(min = 5,message = "name must be more than 4 characters")
    @Pattern(regexp = "^[A-Za-z]+$",message ="name must only contain characters")
    private String name;

    @Email(message = "must be a valid Email format")
    private String email;

    @Pattern(regexp = "^05[0-9]*$", message = "number must start with 05 and only contain numbers ")
    @Size(min = 10,max = 10,message = "number must be 10 digits")
    private String phoneNumber;

    @NotNull(message = "age must not be null")
    @Min(value = 26,message = "Age must be more than 25")
    @Max(value = 125)
    private int age;
    @NotNull(message = "position must not be empty")
    @Pattern(regexp = "^(supervisor|coordinator)$",message ="positon should be eathier supervisor or coordinator ")
    private String position;

    private boolean onLeave;

    @NotNull(message ="date cannot be empty")
    @PastOrPresent(message ="Date must be in past or present")
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate hireDate;

    @NotNull(message = "annual leave cannot be null")
    @Positive(message = "annual leave must be a positive number")
    private int annualLeave;

}
