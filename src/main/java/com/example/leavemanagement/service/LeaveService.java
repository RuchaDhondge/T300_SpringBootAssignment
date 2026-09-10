package com.example.leavemanagement.service;

import java.util.List;

import com.example.leavemanagement.dto.LeaveCreateDTO;
import com.example.leavemanagement.dto.LeaveResponseDTO;

/**
 * Service contract for managing leave requests.
 */
public interface LeaveService {

    LeaveResponseDTO createLeave(LeaveCreateDTO createDTO);

    LeaveResponseDTO getLeaveById(Long id);

    List<LeaveResponseDTO> getAllLeaves();

    LeaveResponseDTO updateLeave(Long id, LeaveCreateDTO updateDTO);

    void deleteLeave(Long id);
}
