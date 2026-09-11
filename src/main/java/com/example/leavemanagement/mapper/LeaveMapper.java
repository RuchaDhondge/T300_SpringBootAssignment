package com.example.leavemanagement.mapper;

import org.springframework.stereotype.Component;

import com.example.leavemanagement.dto.LeaveCreateDTO;
import com.example.leavemanagement.dto.LeaveResponseDTO;
import com.example.leavemanagement.model.LeaveRequest;

/**
 * Dedicated mapper responsible for converting between {@link LeaveRequest}
 * entities and their DTO representations. Keeping this logic out of the
 * controller and service layers enforces a clean separation of concerns.
 */
@Component
public class LeaveMapper {

    public LeaveRequest toEntity(LeaveCreateDTO dto) {
        return LeaveRequest.builder()
                .employeeId(dto.getEmployeeId())
                .leaveType(dto.getLeaveType())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .reason(dto.getReason())
                .build();
    }

    public LeaveResponseDTO toResponseDTO(LeaveRequest entity) {
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
