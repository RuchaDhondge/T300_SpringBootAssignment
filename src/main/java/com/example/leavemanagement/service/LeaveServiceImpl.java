package com.example.leavemanagement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.leavemanagement.dto.LeaveCreateDTO;
import com.example.leavemanagement.dto.LeaveResponseDTO;
import com.example.leavemanagement.exception.ResourceNotFoundException;
import com.example.leavemanagement.mapper.LeaveMapper;
import com.example.leavemanagement.model.LeaveRequest;
import com.example.leavemanagement.model.LeaveStatus;
import com.example.leavemanagement.repository.LeaveRepository;

import lombok.RequiredArgsConstructor;

/**
 * Default implementation of {@link LeaveService}.
 * Handles business logic for leave requests; entity/DTO conversion is
 * delegated to {@link LeaveMapper}.
 */
@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRepository leaveRepository;
    private final LeaveMapper leaveMapper;

    @Override
    public LeaveResponseDTO createLeave(LeaveCreateDTO createDTO) {
        validateDateRange(createDTO.getStartDate(), createDTO.getEndDate());

        LeaveRequest leaveRequest = leaveMapper.toEntity(createDTO);
        leaveRequest.setStatus(LeaveStatus.PENDING);

        LeaveRequest saved = leaveRepository.save(leaveRequest);
        return leaveMapper.toResponseDTO(saved);
    }

    @Override
    public LeaveResponseDTO getLeaveById(Long id) {
        LeaveRequest leaveRequest = findLeaveOrThrow(id);
        return leaveMapper.toResponseDTO(leaveRequest);
    }

    @Override
    public List<LeaveResponseDTO> getAllLeaves() {
        return leaveRepository.findAll().stream()
                .map(leaveMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LeaveResponseDTO updateLeave(Long id, LeaveCreateDTO updateDTO) {
        LeaveRequest existing = findLeaveOrThrow(id);
        validateDateRange(updateDTO.getStartDate(), updateDTO.getEndDate());

        existing.setEmployeeId(updateDTO.getEmployeeId());
        existing.setLeaveType(updateDTO.getLeaveType());
        existing.setStartDate(updateDTO.getStartDate());
        existing.setEndDate(updateDTO.getEndDate());
        existing.setReason(updateDTO.getReason());
        // id and status are preserved from the existing entity

        LeaveRequest updated = leaveRepository.save(existing);
        return leaveMapper.toResponseDTO(updated);
    }

    @Override
    public void deleteLeave(Long id) {
        if (!leaveRepository.existsById(id)) {
            throw new ResourceNotFoundException("Leave request not found with id: " + id);
        }
        leaveRepository.deleteById(id);
    }

    private LeaveRequest findLeaveOrThrow(Long id) {
        return leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + id));
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
    }
}
