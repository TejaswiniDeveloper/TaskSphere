package com.example.taskmanager.service;

import com.example.taskmanager.entity.Employee;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.TaskStatus;
import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private TaskService taskService;

    private Task taskOwnedBy(String email) {
        Employee owner = new Employee();
        owner.setId(1L);
        owner.setEmail(email);

        Task task = new Task();
        task.setId(10L);
        task.setTitle("Prepare report");
        task.setStatus(TaskStatus.PENDING);
        task.setAssignedTo(owner);
        return task;
    }

    @Test
    void updateMyTaskStatus_assigneeCanChangeStatus() {
        Task task = taskOwnedBy("owner@example.com");
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task updated = taskService.updateMyTaskStatus(10L, TaskStatus.COMPLETED, "owner@example.com");

        assertEquals(TaskStatus.COMPLETED, updated.getStatus());
    }

    @Test
    void updateMyTaskStatus_otherUserIsDenied() {
        Task task = taskOwnedBy("owner@example.com");
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        assertThrows(AccessDeniedException.class,
                () -> taskService.updateMyTaskStatus(10L, TaskStatus.COMPLETED, "someone.else@example.com"));

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void createTask_unknownEmployeeThrowsNotFound() {
        Employee stub = new Employee();
        stub.setId(999L);
        Task task = new Task();
        task.setTitle("Orphan task");
        task.setAssignedTo(stub);

        when(employeeService.getEmployeeById(999L))
                .thenThrow(new ResourceNotFoundException("Employee not found with id: 999"));

        assertThrows(ResourceNotFoundException.class, () -> taskService.createTask(task));

        verify(taskRepository, never()).save(any(Task.class));
    }
}