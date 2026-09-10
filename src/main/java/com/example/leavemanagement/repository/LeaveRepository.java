package com.example.leavemanagement.repository;

import java.util.List;
import java.util.Optional;

import com.example.leavemanagement.model.LeaveRequest;

/**
 * Repository abstraction for storing and retrieving leave requests.
 */
public interface LeaveRepository {

    LeaveRequest save(LeaveRequest leaveRequest);

    Optional<LeaveRequest> findById(Long id);

    List<LeaveRequest> findAll();

    boolean deleteById(Long id);

    boolean existsById(Long id);
}
