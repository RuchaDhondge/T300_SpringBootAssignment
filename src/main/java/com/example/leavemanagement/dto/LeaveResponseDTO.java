package com.example.leavemanagement.dto;

import java.time.LocalDate;

import com.example.leavemanagement.model.LeaveStatus;
import com.example.leavemanagement.model.LeaveType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response payload representing a leave request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveResponseDTO {

    private Long id;
    private Long employeeId;
    private LeaveType leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private LeaveStatus status;
}
