package com.example.leavemanagement.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.leavemanagement.dto.LeaveCreateDTO;
import com.example.leavemanagement.dto.LeaveResponseDTO;
import com.example.leavemanagement.exception.ResourceNotFoundException;
import com.example.leavemanagement.model.LeaveStatus;
import com.example.leavemanagement.model.LeaveType;
import com.example.leavemanagement.service.LeaveService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@WebMvcTest(LeaveController.class)
class LeaveControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LeaveService leaveService;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void createLeave_ValidInput_Returns201() throws Exception {
        LeaveCreateDTO createDTO = LeaveCreateDTO.builder()
                .employeeId(1L)
                .leaveType(LeaveType.CASUAL)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .reason("Personal work")
                .build();

        LeaveResponseDTO responseDTO = LeaveResponseDTO.builder()
                .id(1L)
                .employeeId(1L)
                .leaveType(LeaveType.CASUAL)
                .startDate(createDTO.getStartDate())
                .endDate(createDTO.getEndDate())
                .reason("Personal work")
                .status(LeaveStatus.PENDING)
                .build();

        when(leaveService.createLeave(any(LeaveCreateDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/leaves")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void createLeave_InvalidInput_Returns400() throws Exception {
        LeaveCreateDTO invalidDTO = LeaveCreateDTO.builder()
                .employeeId(null)
                .leaveType(LeaveType.CASUAL)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .reason("")
                .build();

        mockMvc.perform(post("/leaves")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation Failed"))
                .andExpect(jsonPath("$.errors.employeeId").value("Employee ID is required"))
                .andExpect(jsonPath("$.errors.reason").value("Reason cannot be empty"));
    }

    @Test
    void getLeaveById_Success_Returns200() throws Exception {
        LeaveResponseDTO responseDTO = LeaveResponseDTO.builder()
                .id(1L)
                .employeeId(1L)
                .leaveType(LeaveType.ANNUAL)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(3))
                .reason("Vacation")
                .status(LeaveStatus.PENDING)
                .build();

        when(leaveService.getLeaveById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/leaves/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.employeeId").value(1L))
                .andExpect(jsonPath("$.leaveType").value("ANNUAL"));
    }

    @Test
    void getLeaveById_NotFound_Returns404() throws Exception {
        when(leaveService.getLeaveById(anyLong()))
                .thenThrow(new ResourceNotFoundException("Leave request not found with id: 99"));

        mockMvc.perform(get("/leaves/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Leave request not found with id: 99"));
    }

    @Test
    void updateLeave_InvalidInput_Returns400() throws Exception {
        LeaveCreateDTO invalidDTO = LeaveCreateDTO.builder()
                .employeeId(null)
                .leaveType(LeaveType.CASUAL)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .reason("")
                .build();

        mockMvc.perform(put("/leaves/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation Failed"))
                .andExpect(jsonPath("$.errors.employeeId").value("Employee ID is required"))
                .andExpect(jsonPath("$.errors.reason").value("Reason cannot be empty"));
    }
}
