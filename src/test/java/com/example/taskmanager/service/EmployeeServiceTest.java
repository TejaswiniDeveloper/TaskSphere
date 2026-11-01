package com.example.taskmanager.service;

import com.example.taskmanager.entity.Employee;
import com.example.taskmanager.entity.Role;
import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void createEmployee_hashesPasswordBeforeSaving() {
        Employee employee = new Employee();
        employee.setName("Asha");
        employee.setEmail("asha@example.com");
        employee.setPassword("plain123");
        employee.setRole(Role.EMPLOYEE);

        when(passwordEncoder.encode("plain123")).thenReturn("HASHED");
        when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));

        Employee saved = employeeService.createEmployee(employee);

        assertEquals("HASHED", saved.getPassword());
        verify(employeeRepository).save(employee);
    }

    @Test
    void getEmployeeById_throwsWhenMissing() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.getEmployeeById(99L));
    }
}