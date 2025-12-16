package com.e_commerce.integration.otp;

import com.e_commerce.configuration.TestSecurityConfig;
import com.e_commerce.controller.otp.OTPController;
import com.e_commerce.integration.BaseWebMvcTest;
import com.e_commerce.util.OtpUtil;
import com.e_commerce.util.RateLimitService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OTPController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class OTPControllerTest extends BaseWebMvcTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    OtpUtil otpUtil;

    @MockBean
    RateLimitService rateLimitService;

    @MockBean
    ApplicationEventPublisher applicationEventPublisher;


    @Test
    @DisplayName("POST /otp/resend returns otp")
    void resendOtp_returnsCode() throws Exception {
        doNothing().when(rateLimitService).checkOtpLimit("mail@test.com");
        given(otpUtil.generateOtp("mail@test.com")).willReturn("654321");
        doNothing().when(applicationEventPublisher).publishEvent(ArgumentMatchers.any());

        mockMvc.perform(post("/otp/resend").param("email", "mail@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("654321"));
    }
}
