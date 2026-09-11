package com.example.leavemanagement.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.leavemanagement.dto.LeaveCreateDTO;
import com.example.leavemanagement.dto.LeaveResponseDTO;
import com.example.leavemanagement.exception.ResourceNotFoundException;
import com.example.leavemanagement.mapper.LeaveMapper;
import com.example.leavemanagement.model.LeaveRequest;
import com.example.leavemanagement.model.LeaveStatus;
import com.example.leavemanagement.model.LeaveType;
import com.example.leavemanagement.repository.LeaveRepository;

@ExtendWith(MockitoExtension.class)
class LeaveServiceTest {

    @Mock
    private LeaveRepository leaveRepository;

    private final LeaveMapper leaveMapper = new LeaveMapper();

    private LeaveServiceImpl leaveService;

    private LeaveCreateDTO createDTO;
    private LeaveRequest leaveRequest;

    @BeforeEach
    void setUp() {
        leaveService = new LeaveServiceImpl(leaveRepository, leaveMapper);

        createDTO = LeaveCreateDTO.builder()
                .employeeId(1L)
                .leaveType(LeaveType.SICK)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(2))
                .reason("Not feeling well")
                .build();

        leaveRequest = LeaveRequest.builder()
                .id(1L)
                .employeeId(1L)
                .leaveType(LeaveType.SICK)
                .startDate(createDTO.getStartDate())
                .endDate(createDTO.getEndDate())
                .reason("Not feeling well")
                .status(LeaveStatus.PENDING)
                .build();
    }

    @Test
    void createLeave_Success() {
        when(leaveRepository.save(any(LeaveRequest.class))).thenReturn(leaveRequest);

        LeaveResponseDTO response = leaveService.createLeave(createDTO);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(LeaveStatus.PENDING);
        assertThat(response.getEmployeeId()).isEqualTo(1L);
        assertThat(response.getLeaveType()).isEqualTo(LeaveType.SICK);
        verify(leaveRepository, times(1)).save(any(LeaveRequest.class));
    }

    @Test
    void createLeave_InvalidDates_ThrowsException() {
        createDTO.setStartDate(LocalDate.now().plusDays(5));
        createDTO.setEndDate(LocalDate.now().plusDays(1));

        assertThatThrownBy(() -> leaveService.createLeave(createDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("End date cannot be before start date");

        verify(leaveRepository, never()).save(any(LeaveRequest.class));
    }

    @Test
    void getLeaveById_Success() {
        when(leaveRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));

        LeaveResponseDTO response = leaveService.getLeaveById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmployeeId()).isEqualTo(1L);
        assertThat(response.getStatus()).isEqualTo(LeaveStatus.PENDING);
    }

    @Test
    void getLeaveById_NotFound_ThrowsException() {
        when(leaveRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> leaveService.getLeaveById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Leave request not found with id: 99");
    }

    @Test
    void updateLeave_NotFound_ThrowsException() {
        when(leaveRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> leaveService.updateLeave(99L, createDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Leave request not found with id: 99");

        verify(leaveRepository, never()).save(any(LeaveRequest.class));
    }

    @Test
    void deleteLeave_NotFound_ThrowsException() {
        when(leaveRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> leaveService.deleteLeave(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Leave request not found with id: 99");

        verify(leaveRepository, never()).deleteById(any());
    }
}
