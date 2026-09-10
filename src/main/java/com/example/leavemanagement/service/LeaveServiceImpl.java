package com.example.leavemanagement.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.leavemanagement.dto.LeaveCreateDTO;
import com.example.leavemanagement.dto.LeaveResponseDTO;
import com.example.leavemanagement.exception.ResourceNotFoundException;
import com.example.leavemanagement.model.LeaveRequest;
import com.example.leavemanagement.model.LeaveStatus;
import com.example.leavemanagement.repository.LeaveRepository;

import lombok.RequiredArgsConstructor;

/**
 * Default implementation of {@link LeaveService}.
 * Handles business logic and DTO-entity mapping for leave requests.
 */
@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRepository leaveRepository;

    @Override
    public LeaveResponseDTO createLeave(LeaveCreateDTO createDTO) {
        if (createDTO.getEndDate().isBefore(createDTO.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        LeaveRequest leaveRequest = mapToEntity(createDTO);
        leaveRequest.setStatus(LeaveStatus.PENDING);

        LeaveRequest saved = leaveRepository.save(leaveRequest);
        return mapToResponseDTO(saved);
    }

    @Override
    public LeaveResponseDTO getLeaveById(Long id) {
        LeaveRequest leaveRequest = leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + id));
        return mapToResponseDTO(leaveRequest);
    }

    @Override
    public List<LeaveResponseDTO> getAllLeaves() {
        return leaveRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LeaveResponseDTO updateLeave(Long id, LeaveCreateDTO updateDTO) {
        LeaveRequest existing = leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + id));

        if (updateDTO.getEndDate().isBefore(updateDTO.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        existing.setEmployeeId(updateDTO.getEmployeeId());
        existing.setLeaveType(updateDTO.getLeaveType());
        existing.setStartDate(updateDTO.getStartDate());
        existing.setEndDate(updateDTO.getEndDate());
        existing.setReason(updateDTO.getReason());
        // id and status are preserved from the existing entity

        LeaveRequest updated = leaveRepository.save(existing);
        return mapToResponseDTO(updated);
    }

    @Override
    public void deleteLeave(Long id) {
        if (!leaveRepository.existsById(id)) {
            throw new ResourceNotFoundException("Leave request not found with id: " + id);
        }
        leaveRepository.deleteById(id);
    }

    private LeaveRequest mapToEntity(LeaveCreateDTO dto) {
        return LeaveRequest.builder()
                .employeeId(dto.getEmployeeId())
                .leaveType(dto.getLeaveType())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .reason(dto.getReason())
                .build();
    }

    private LeaveResponseDTO mapToResponseDTO(LeaveRequest entity) {
        return LeaveResponseDTO.builder()
                .id(entity.getId())
                .employeeId(entity.getEmployeeId())
                .leaveType(entity.getLeaveType())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .reason(entity.getReason())
                .status(entity.getStatus())
                .build();
    }
}
