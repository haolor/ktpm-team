package com.e_commerce.integration.account;

import com.e_commerce.configuration.TestSecurityConfig;
import com.e_commerce.controller.account.AccountController;
import com.e_commerce.dto.auth.accountDTO.*;
import com.e_commerce.enums.AccountRole;
import com.e_commerce.integration.BaseWebMvcTest;
import com.e_commerce.service.account.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AccountController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AccountControllerTest extends BaseWebMvcTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    AccountService accountService;

    @Test
    @DisplayName("POST /auth/login returns tokens")
    void login_returnsTokens() throws Exception {
        AuthenticationDTO auth = AuthenticationDTO.builder()
                .accountName("user")
                .accessToken("access")
                .refreshToken("refresh")
                .role("USER")
                .build();
        given(accountService.signIn(any())).willReturn(auth);

        LoginForm form = new LoginForm();
        form.setEmail("user@test.com");
        form.setPassword("Password1!");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("access"));
    }

    @Test
    @DisplayName("POST /auth/register creates account")
    void register_returnsAccount() throws Exception {
        AccountDTO dto = AccountDTO.builder()
                .id(1)
                .email("new@test.com")
                .accountName("New User")
                .createAt(LocalDateTime.now())
                .status(true)
                .role("USER")
                .active("ACTIVE")
                .build();
        given(accountService.createAccount(any())).willReturn(dto);

        RegistrationForm form = RegistrationForm.builder()
                .email("new@test.com")
                .accountName("New User")
                .password("Password1!")
                .role(AccountRole.USER)
                .build();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("new@test.com"));
    }

    @Test
    @DisplayName("POST /auth/logout revokes token")
    void logout_returnsOk() throws Exception {
        doNothing().when(accountService).logout("token");

        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /auth/all-user returns list")
    void getAllUser_returnsList() throws Exception {
        given(accountService.getAccountAllByRoleUser()).willReturn(List.of(AccountDTO.builder().id(1).build()));

        mockMvc.perform(get("/auth/all-user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("GET /auth returns customers")
    void getCustomers_returnsList() throws Exception {
        given(accountService.getCustomerInfoList()).willReturn(List.of(AccountDTO.builder().id(2).build()));

        mockMvc.perform(get("/auth"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("POST /auth/refresh-token returns new tokens")
    void refreshToken_returnsAuth() throws Exception {
        AuthenticationDTO auth = AuthenticationDTO.builder()
                .accessToken("newAccess")
                .refreshToken("newRefresh")
                .role("USER")
                .build();
        given(accountService.refreshToken(any())).willReturn(auth);

        RefreshTokenDTO form = new RefreshTokenDTO("refresh");

        mockMvc.perform(post("/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("newAccess"));
    }

    @Test
    @DisplayName("GET /auth/activate activates account")
    void activate_returnsOk() throws Exception {
        doNothing().when(accountService).activeAccount("token");

        mockMvc.perform(get("/auth/activate").param("token", "token"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /auth/forgot-password returns message")
    void forgotPassword_returnsMessage() throws Exception {
        ForgotPasswordResponseDTO response = ForgotPasswordResponseDTO.builder()
                .message("sent")
                .expiresIn(300)
                .build();
        given(accountService.forgotPasswordRequest(any())).willReturn(response);

        ForgotPasswordRequestDTO form = ForgotPasswordRequestDTO.builder()
                .email("forgot@test.com")
                .build();

        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value("sent"));
    }

    @Test
    @DisplayName("POST /auth/reset-password resets password")
    void resetPassword_returnsOk() throws Exception {
        doNothing().when(accountService).resetPassword(any());

        ResetPasswordDTO form = ResetPasswordDTO.builder()
                .email("reset@test.com")
                .newPassword("Password1!")
                .build();

        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /auth/verify-otp verifies code")
    void verifyOtp_returnsResult() throws Exception {
        OtpVerificationResponseDTO response = OtpVerificationResponseDTO.builder()
                .message("ok")
                .email("otp@test.com")
                .build();
        given(accountService.verifyOtp(any())).willReturn(response);

        OtpVerificationRequestDTO form = OtpVerificationRequestDTO.builder()
                .email("otp@test.com")
                .otp("123456")
                .build();

        mockMvc.perform(post("/auth/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value("ok"))
                .andExpect(jsonPath("$.data.email").value("otp@test.com"));
    }

    @Test
    @DisplayName("POST /auth/resend-verification sends email")
    void resendVerification_returnsOk() throws Exception {
        doNothing().when(accountService).resendVerificationEmail("email@test.com");

        mockMvc.perform(post("/auth/resend-verification")
                        .param("email", "email@test.com"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /auth/{id}/lock locks account")
    void lockAccount_returnsOk() throws Exception {
        doNothing().when(accountService).lockAccount(anyInt());

        mockMvc.perform(put("/auth/{id}/lock", 5))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /auth/{id}/unlock unlocks account")
    void unlockAccount_returnsOk() throws Exception {
        doNothing().when(accountService).unlockAccount(anyInt());

        mockMvc.perform(put("/auth/{id}/unlock", 6))
                .andExpect(status().isOk());
    }
}
