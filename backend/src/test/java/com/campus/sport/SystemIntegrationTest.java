package com.campus.sport;

import com.campus.sport.entity.User;
import com.campus.sport.entity.Venue;
import com.campus.sport.entity.VenueSchedule;
import com.campus.sport.entity.SportRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SystemIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String userToken;
    private static String adminToken;
    private static Long venueId;
    private static Long scheduleId;
    private static Long reservationId;
    private static Long reservationId2; // For reject test

    // --- 1. User Registration Tests ---

    @Test
    @Order(1)
    public void test01_UserRegistration_Success() throws Exception {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("123456");
        user.setName("Test Student");
        user.setPhone("13800138000");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @Order(2)
    public void test01_UserRegistration_Fail_Duplicate() throws Exception {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("123456");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(3)
    public void test01_UserRegistration_Fail_EmptyFields() throws Exception {
        User user = new User();
        user.setUsername(""); 
        user.setPassword("123456");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(jsonPath("$.code").value(500)); // Service check
    }

    // --- 2. Login Tests ---

    @Test
    @Order(4)
    public void test02_UserLogin_Success() throws Exception {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("123456");

        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Map map = objectMapper.readValue(response, Map.class);
        Map data = (Map) map.get("data");
        userToken = (String) data.get("token");
    }

    @Test
    @Order(5)
    public void test02_UserLogin_Fail_WrongPassword() throws Exception {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("wrongpass");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(6)
    public void test03_AdminLogin_Success() throws Exception {
        User user = new User();
        user.setUsername("admin");
        user.setPassword("123456");

        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Map map = objectMapper.readValue(response, Map.class);
        Map data = (Map) map.get("data");
        adminToken = (String) data.get("token");
    }

    // --- 3. Venue Management Tests ---

    @Test
    @Order(7)
    public void test04_AdminVenueSettings_Success() throws Exception {
        Venue venue = new Venue();
        venue.setName("Test Gym");
        venue.setType("INDOOR");
        venue.setDefaultMaxPeople(50);
        venue.setOpenDays("1,2,3,4,5,6,7");
        venue.setOpenStartTime(8);
        venue.setOpenEndTime(22);
        venue.setStatus(1);
        venue.setLocation("Building A");
        
        mockMvc.perform(post("/venue/add")
                .header("satoken", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(venue)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        MvcResult result = mockMvc.perform(get("/venue/list")
                .header("satoken", adminToken)
                .param("name", "Test Gym"))
                .andExpect(status().isOk())
                .andReturn();
        
        String response = result.getResponse().getContentAsString();
        Map map = objectMapper.readValue(response, Map.class);
        Map data = (Map) map.get("data");
        List records = (List) data.get("records");
        Map firstVenue = (Map) records.get(0);
        venueId = ((Number) firstVenue.get("id")).longValue();
        
        // Add Schedule
        List<VenueSchedule> schedules = new ArrayList<>();
        VenueSchedule schedule = new VenueSchedule();
        schedule.setVenueId(venueId);
        schedule.setDate(LocalDate.now().plusDays(1));
        schedule.setTimeSlot(10);
        schedule.setMaxPeople(10);
        schedule.setCurrentPeople(0);
        schedule.setStatus(1);
        schedules.add(schedule);

        mockMvc.perform(post("/schedule/batchSave")
                .header("satoken", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(schedules)))
                .andExpect(status().isOk());
                
        MvcResult schResult = mockMvc.perform(get("/schedule/list")
                .header("satoken", adminToken)
                .param("venueId", venueId.toString())
                .param("date", LocalDate.now().plusDays(1).toString()))
                .andExpect(status().isOk())
                .andReturn();
                
        String schResponse = schResult.getResponse().getContentAsString();
        Map schMap = objectMapper.readValue(schResponse, Map.class);
        List schData = (List) schMap.get("data");
        Map firstSch = (Map) schData.get(0);
        scheduleId = ((Number) firstSch.get("id")).longValue();
    }

    @Test
    @Order(8)
    public void test04_AdminVenueUpdate() throws Exception {
        Venue venue = new Venue();
        venue.setId(venueId);
        venue.setName("Updated Gym");
        venue.setType("INDOOR");
        venue.setDefaultMaxPeople(60); // Changed
        
        mockMvc.perform(post("/venue/update")
                .header("satoken", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(venue)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
                
        // Verify
        mockMvc.perform(get("/venue/" + venueId)
                .header("satoken", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Updated Gym"));
    }

    @Test
    @Order(9)
    public void test04_AdminVenueSettings_Fail_NoAuth() throws Exception {
        Venue venue = new Venue();
        venue.setName("Unauthorized Gym");
        
        mockMvc.perform(post("/venue/add")
                .header("satoken", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(venue)))
                .andExpect(jsonPath("$.code").value(403));
    }

    // --- 4. Reservation Tests ---

    @Test
    @Order(10)
    public void test05_UserReservation_Success() throws Exception {
        mockMvc.perform(post("/reservation/add")
                .header("satoken", userToken)
                .param("scheduleId", scheduleId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
                
        MvcResult result = mockMvc.perform(get("/reservation/my")
                .header("satoken", userToken))
                .andExpect(status().isOk())
                .andReturn();
                
        String response = result.getResponse().getContentAsString();
        Map map = objectMapper.readValue(response, Map.class);
        Map data = (Map) map.get("data");
        List records = (List) data.get("records");
        Map firstRes = (Map) records.get(0);
        reservationId = ((Number) firstRes.get("id")).longValue();
    }

    @Test
    @Order(11)
    public void test05_UserCancelReservation() throws Exception {
        mockMvc.perform(post("/reservation/cancel/" + reservationId)
                .header("satoken", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
                
        // Create another one for Audit tests
        mockMvc.perform(post("/reservation/add")
                .header("satoken", userToken)
                .param("scheduleId", scheduleId.toString()))
                .andExpect(status().isOk());
                
        // Get new ID
        MvcResult result = mockMvc.perform(get("/reservation/my")
                .header("satoken", userToken))
                .andReturn();
        String response = result.getResponse().getContentAsString();
        Map map = objectMapper.readValue(response, Map.class);
        Map data = (Map) map.get("data");
        List records = (List) data.get("records");
        Map firstRes = (Map) records.get(0); // Should be the new one (sorted desc usually)
        reservationId = ((Number) firstRes.get("id")).longValue();
    }

    @Test
    @Order(12)
    public void test05_UserReservation_Fail_InvalidID() throws Exception {
        mockMvc.perform(post("/reservation/add")
                .header("satoken", userToken)
                .param("scheduleId", "-1"))
                .andExpect(jsonPath("$.code").value(500));
    }

    // --- 5. Sport Record Tests ---

    @Test
    @Order(13)
    public void test06_PersonalRecords_Success() throws Exception {
        SportRecord record = new SportRecord();
        record.setDuration(60);
        record.setCalories(500.0);
        record.setSportType("Running");
        record.setRecordDate(LocalDate.now());

        mockMvc.perform(post("/sport/add")
                .header("satoken", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(record)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/sport/my")
                .header("satoken", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.records").isArray());
    }

    // --- 6. Admin Audit Tests ---

    @Test
    @Order(14)
    public void test07_AdminAudit_Success() throws Exception {
        // Approve reservationId (created in test05_UserCancelReservation)
        mockMvc.perform(post("/reservation/audit/" + reservationId)
                .header("satoken", adminToken)
                .param("status", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @Order(15)
    public void test07_AdminAudit_Reject() throws Exception {
        // User creates yet another reservation
        mockMvc.perform(post("/reservation/add")
                .header("satoken", userToken)
                .param("scheduleId", scheduleId.toString()))
                .andExpect(status().isOk());
                
        // Get ID
        MvcResult result = mockMvc.perform(get("/reservation/my")
                .header("satoken", userToken))
                .andReturn();
        String response = result.getResponse().getContentAsString();
        Map map = objectMapper.readValue(response, Map.class);
        Map data = (Map) map.get("data");
        List records = (List) data.get("records");
        Map firstRes = (Map) records.get(0);
        reservationId2 = ((Number) firstRes.get("id")).longValue();

        // Admin rejects
        mockMvc.perform(post("/reservation/audit/" + reservationId2)
                .header("satoken", adminToken)
                .param("status", "2")) // 2 = Reject
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @Order(16)
    public void test07_AdminAudit_Fail_NoAuth() throws Exception {
        mockMvc.perform(post("/reservation/audit/" + reservationId)
                .header("satoken", userToken)
                .param("status", "1"))
                .andExpect(jsonPath("$.code").value(403));
    }

    // --- 7. User Management Tests ---

    @Test
    @Order(17)
    public void test08_AdminUserManagement_Success() throws Exception {
        MvcResult result = mockMvc.perform(get("/user/list")
                .header("satoken", adminToken)
                .param("username", "testUser"))
                .andExpect(status().isOk())
                .andReturn();
                
        String response = result.getResponse().getContentAsString();
        Map map = objectMapper.readValue(response, Map.class);
        Map data = (Map) map.get("data");
        List records = (List) data.get("records");
        Map firstUser = (Map) records.get(0);
        Long testUserId = ((Number) firstUser.get("id")).longValue();
        
        mockMvc.perform(post("/user/delete/" + testUserId)
                .header("satoken", adminToken))
                .andExpect(status().isOk());
    }
}
