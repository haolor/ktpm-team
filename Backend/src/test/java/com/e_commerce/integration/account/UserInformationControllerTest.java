package com.e_commerce.integration.account;

import com.e_commerce.configuration.TestSecurityConfig;
import com.e_commerce.controller.account.UserInformationController;
import com.e_commerce.dto.auth.userInfoDTO.UserInfoCreateDTO;
import com.e_commerce.dto.auth.userInfoDTO.UserInfoDTO;
import com.e_commerce.dto.auth.userInfoDTO.UserInfoUpdateDTO;
import com.e_commerce.enums.Gender;
import com.e_commerce.integration.BaseWebMvcTest;
import com.e_commerce.service.account.UserInformationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserInformationController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class UserInformationControllerTest extends BaseWebMvcTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserInformationService userInformationService;

    @Test
    @DisplayName("GET /user-info/all returns list")
    void getAll_returnsList() throws Exception {
        given(userInformationService.getAllUserInfoByAccount()).willReturn(List.of(UserInfoDTO.builder().id(1).build()));

        mockMvc.perform(get("/user-info/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("POST /user-info creates info")
    void createUserInfo_returnsCreated() throws Exception {
        UserInfoDTO dto = UserInfoDTO.builder()
                .id(5)
                .fullName("Full Name")
                .gender(Gender.MALE)
                .build();
        given(userInformationService.createUserInfo(any())).willReturn(dto);

        UserInfoCreateDTO create = UserInfoCreateDTO.builder()
                .fullName("Full Name")
                .gender(Gender.MALE)
                .address("Addr")
                .phoneNumber("0123456789")
                .build();

        mockMvc.perform(post("/user-info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(5));
    }

    @Test
    @DisplayName("PUT /user-info/{id} updates info")
    void updateUserInfo_returnsUpdated() throws Exception {
        UserInfoDTO dto = UserInfoDTO.builder()
                .id(5)
                .fullName("Updated")
                .gender(Gender.FEMALE)
                .build();
        given(userInformationService.updateUserInfo(anyInt(), any())).willReturn(dto);

        UserInfoUpdateDTO update = new UserInfoUpdateDTO();
        update.setFullName("Updated");
        update.setGender(Gender.FEMALE);

        mockMvc.perform(put("/user-info/{id}", 5)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.fullName").value("Updated"));
    }

    @Test
    @DisplayName("GET /user-info/{accountId} returns info list")
    void getByAccount_returnsList() throws Exception {
        given(userInformationService.getUserInfoByAccountId(anyInt())).willReturn(List.of(UserInfoDTO.builder().id(9).build()));

        mockMvc.perform(get("/user-info/{accountId}", 9))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }
}
