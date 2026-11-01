package com.example.taskmanager.dto;

import com.example.taskmanager.entity.Employee;
import com.example.taskmanager.entity.Role;

public record EmployeeResponse(Long id, String name, String email, Role role) {

    public static EmployeeResponse from(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getRole());
    }
}