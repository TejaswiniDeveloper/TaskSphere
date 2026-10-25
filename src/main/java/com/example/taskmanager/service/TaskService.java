package com.example.taskmanager.service;

import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.entity.Employee;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.TaskStatus;
import com.example.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EmployeeService employeeService;

    public Task createTask(Task task) {
        if (task.getAssignedTo() != null && task.getAssignedTo().getId() != null) {
            Employee employee = employeeService.getEmployeeById(task.getAssignedTo().getId());
            task.setAssignedTo(employee);
        }
        return taskRepository.save(task);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    }

    public List<Task> getTasksByEmployeeId(Long employeeId) {
        return taskRepository.findByAssignedTo_Id(employeeId);
    }

    public List<Task> getMyTasks(String email) {
        return taskRepository.findByAssignedTo_Email(email);
    }

    public Task updateMyTaskStatus(Long id, TaskStatus status, String email) {
        Task task = getTaskById(id);
        if (task.getAssignedTo() == null || !email.equals(task.getAssignedTo().getEmail())) {
            throw new AccessDeniedException("You can only update your own tasks");
        }
        task.setStatus(status);
        return taskRepository.save(task);
    }

    public Task updateTask(Long id, Task updatedTask) {
        Task existing = getTaskById(id);
        existing.setTitle(updatedTask.getTitle());
        existing.setDescription(updatedTask.getDescription());
        existing.setStatus(updatedTask.getStatus());
        existing.setDueDate(updatedTask.getDueDate());
        return taskRepository.save(existing);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}