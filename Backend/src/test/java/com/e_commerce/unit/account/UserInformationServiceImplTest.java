package com.e_commerce.unit.account;

import com.e_commerce.dto.auth.accountDTO.AccountDTO;
import com.e_commerce.dto.auth.accountDTO.LoginForm;
import com.e_commerce.dto.auth.accountDTO.RegistrationForm;
import com.e_commerce.dto.auth.userInfoDTO.UserInfoCreateDTO;
import com.e_commerce.dto.auth.userInfoDTO.UserInfoDTO;
import com.e_commerce.dto.auth.userInfoDTO.UserInfoUpdateDTO;
import com.e_commerce.entity.account.Account;
import com.e_commerce.entity.account.UserInformation;
import com.e_commerce.enums.AccountRole;
import com.e_commerce.enums.Gender;
import com.e_commerce.exceptions.CustomException;
import com.e_commerce.exceptions.ErrorResponse;
import com.e_commerce.mapper.account.AccountMapper;
import com.e_commerce.mapper.account.UserInformationMapper;
import com.e_commerce.repository.account.AccountRepository;
import com.e_commerce.repository.account.UserInformationRepository;
import com.e_commerce.service.account.AccountService;
import com.e_commerce.service.account.TokenService;
import com.e_commerce.service.account.impl.AccountServiceImpl;
import com.e_commerce.service.account.impl.UserInformationServiceImpl;
import com.e_commerce.service.account.token.TokenBlacklistService;
import com.e_commerce.util.JwtUtil;
import com.e_commerce.util.LoginAttemptService;
import com.e_commerce.util.OtpUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserInformationServiceImplTest {

    @Mock
    private UserInformationMapper userInformationMapper;

    @Mock
    private UserInformationRepository userInformationRepository;

    @Mock
    private AccountService accountService;

    // Mocks cho AccountServiceImpl (login/register)
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private TokenService tokenService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private OtpUtil otpUtil;

    @Mock
    private LoginAttemptService loginAttemptService;

    @InjectMocks
    private UserInformationServiceImpl userInformationService;

    private AccountServiceImpl accountServiceImpl;

    private Account testAccount;
    private UserInformation testUserInfo;
    private UserInfoDTO testUserInfoDTO;
    private LoginForm defaultLoginForm;
    private Account defaultAccount;
    private RegistrationForm registrationForm;
    private AccountDTO accountDTO;

    @BeforeEach
    void setUp() {
        testAccount = new Account();
        testAccount.setId(1);
        testAccount.setEmail("test@example.com");

        testUserInfo = new UserInformation();
        testUserInfo.setId(1);
        testUserInfo.setFullName("Test User");
        testUserInfo.setPhoneNumber("0123456789");
        testUserInfo.setAddress("Test Address");
        testUserInfo.setGender(Gender.MALE);
        testUserInfo.setAccount(testAccount);
        testUserInfo.setIsDefault(true);

        testUserInfoDTO = new UserInfoDTO();
        testUserInfoDTO.setId(1);
        testUserInfoDTO.setFullName("Test User");
        testUserInfoDTO.setPhoneNumber("0123456789");
        testUserInfoDTO.setAddress("Test Address");
        testUserInfoDTO.setGender(Gender.MALE);

        defaultLoginForm = new LoginForm();
        defaultLoginForm.setEmail("user@example.com");
        defaultLoginForm.setPassword("rawpass");

        defaultAccount = new Account();
        defaultAccount.setId(100);
        defaultAccount.setEmail("user@example.com");
        defaultAccount.setPassword("encodedPass");
        defaultAccount.setActive(true);
        defaultAccount.setStatus(true);
        defaultAccount.setRole(AccountRole.USER);

        registrationForm = new RegistrationForm();
        registrationForm.setEmail("new@example.com");
        registrationForm.setPassword("Password1!");
        registrationForm.setAccountName("New User");
        registrationForm.setRole(AccountRole.USER);

        accountDTO = new AccountDTO();

        accountServiceImpl = new AccountServiceImpl(passwordEncoder, jwtUtil, accountMapper, accountRepository,
                tokenBlacklistService, tokenService, eventPublisher, otpUtil, loginAttemptService);
    }

    // ---------- Tests cho AccountService (login/register) ----------

    @Test
    void signIn_ShouldThrowException_WhenAccountNotFound() {
        when(accountRepository.findByEmail(defaultLoginForm.getEmail())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class,
                () -> accountServiceImpl.signIn(defaultLoginForm));

        assertTrue(exception.getErrors().contains(ErrorResponse.ACCOUNT_NOT_FOUND));
        verify(accountRepository).findByEmail(defaultLoginForm.getEmail());
    }

    @Test
    void signIn_ShouldThrowException_WhenAccountDisabled() {
        defaultAccount.setStatus(false);
        when(accountRepository.findByEmail(defaultLoginForm.getEmail())).thenReturn(Optional.of(defaultAccount));

        CustomException exception = assertThrows(CustomException.class,
                () -> accountServiceImpl.signIn(defaultLoginForm));

        assertTrue(exception.getErrors().contains(ErrorResponse.ACCOUNT_DISABLED));
        verify(accountRepository).findByEmail(defaultLoginForm.getEmail());
    }

    @Test
    void signIn_ShouldThrowException_WhenAccountLocked() {
        defaultAccount.setActive(false);
        when(accountRepository.findByEmail(defaultLoginForm.getEmail())).thenReturn(Optional.of(defaultAccount));

        CustomException exception = assertThrows(CustomException.class,
                () -> accountServiceImpl.signIn(defaultLoginForm));

        assertTrue(exception.getErrors().contains(ErrorResponse.ACCOUNT_LOCKED));
        verify(accountRepository).findByEmail(defaultLoginForm.getEmail());
    }

    @Test
    void signIn_Success() {
        when(accountRepository.findByEmail(defaultLoginForm.getEmail())).thenReturn(Optional.of(defaultAccount));
        when(passwordEncoder.matches(defaultLoginForm.getPassword(), defaultAccount.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(defaultAccount)).thenReturn("jwt");
        when(jwtUtil.generateRefreshToken(defaultAccount)).thenReturn("refresh");

        var authDto = accountServiceImpl.signIn(defaultLoginForm);

        assertNotNull(authDto);
        assertEquals("jwt", authDto.getAccessToken());
        assertEquals("refresh", authDto.getRefreshToken());
        verify(tokenService).generateRefreshToken(defaultAccount, "refresh");
    }

    @Test
    void createAccount_ShouldThrowException_WhenEmailExists() {
        when(accountRepository.existsByEmail(registrationForm.getEmail())).thenReturn(true);

        CustomException exception = assertThrows(CustomException.class,
                () -> accountServiceImpl.createAccount(registrationForm));

        assertTrue(exception.getErrors().contains(ErrorResponse.ACCOUNT_ALREADY_EXISTS));
        verify(accountRepository).existsByEmail(registrationForm.getEmail());
    }

    @Test
    void createAccount_Success() {
        when(accountRepository.existsByEmail(registrationForm.getEmail())).thenReturn(false);
        when(accountMapper.convertCreateDTOToEntity(registrationForm)).thenReturn(defaultAccount);
        when(passwordEncoder.encode(registrationForm.getPassword())).thenReturn("encodedPass");
        when(accountRepository.save(defaultAccount)).thenReturn(defaultAccount);
        when(accountMapper.convertEntityToDTO(defaultAccount)).thenReturn(accountDTO);

        AccountDTO result = accountServiceImpl.createAccount(registrationForm);

        assertNotNull(result);
        verify(accountRepository).save(defaultAccount);
        verify(tokenService).generateToken(defaultAccount);
        verify(eventPublisher).publishEvent(any());
    }

    @Test
    void createUserInfo_WithAccountAndFullName_Success() {
        // Arrange
        when(userInformationRepository.save(any(UserInformation.class))).thenReturn(testUserInfo);
        when(userInformationMapper.convertEntityToDTO(any(UserInformation.class))).thenReturn(testUserInfoDTO);

        // Act
        UserInfoDTO result = userInformationService.createUserInfo(testAccount, "Test User");

        // Assert
        assertNotNull(result);
        assertEquals("Test User", result.getFullName());
        verify(userInformationRepository).save(any(UserInformation.class));
        verify(userInformationMapper).convertEntityToDTO(any(UserInformation.class));
    }

    @Test
    void createUserInfo_WithCreateDTO_Success() {
        // Arrange
        UserInfoCreateDTO createDTO = new UserInfoCreateDTO();
        createDTO.setFullName("New User");
        createDTO.setPhoneNumber("0987654321");
        createDTO.setAddress("New Address");
        createDTO.setGender(Gender.FEMALE);

        when(accountService.getAccountAuth()).thenReturn(testAccount);
        when(userInformationMapper.convertCreateDTOToEntity(any(UserInfoCreateDTO.class))).thenReturn(testUserInfo);
        when(userInformationRepository.validateForCheckout(anyInt())).thenReturn(false);
        when(userInformationRepository.save(any(UserInformation.class))).thenReturn(testUserInfo);
        when(userInformationMapper.convertEntityToDTO(any(UserInformation.class))).thenReturn(testUserInfoDTO);

        // Act
        UserInfoDTO result = userInformationService.createUserInfo(createDTO);

        // Assert
        assertNotNull(result);
        verify(accountService).getAccountAuth();
        verify(userInformationRepository).save(any(UserInformation.class));
    }

    @Test
    void getUserInfoByAccountId_Success() {
        // Arrange
        List<UserInformation> userInfoList = Arrays.asList(testUserInfo);
        List<UserInfoDTO> userInfoDTOList = Arrays.asList(testUserInfoDTO);

        when(accountService.getAccountEntityById(anyInt())).thenReturn(testAccount);
        when(userInformationRepository.findByAccount_Id(anyInt())).thenReturn(userInfoList);
        when(userInformationMapper.convertEntityListToDTOList(anyList())).thenReturn(userInfoDTOList);

        // Act
        List<UserInfoDTO> result = userInformationService.getUserInfoByAccountId(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userInformationRepository).findByAccount_Id(anyInt());
    }

    @Test
    void updateUserInfo_Success() {
        // Arrange
        UserInfoUpdateDTO updateDTO = new UserInfoUpdateDTO();
        updateDTO.setFullName("Updated Name");
        updateDTO.setPhoneNumber("0999999999");
        updateDTO.setAddress("Updated Address");
        updateDTO.setGender(Gender.FEMALE);

        when(userInformationRepository.findById(anyInt())).thenReturn(Optional.of(testUserInfo));
        when(userInformationRepository.save(any(UserInformation.class))).thenReturn(testUserInfo);
        when(userInformationMapper.convertEntityToDTO(any(UserInformation.class))).thenReturn(testUserInfoDTO);

        // Act
        UserInfoDTO result = userInformationService.updateUserInfo(1, updateDTO);

        // Assert
        assertNotNull(result);
        verify(userInformationRepository).findById(1);
        verify(userInformationRepository).save(testUserInfo);
    }

    @Test
    void getUserInformationEntityById_NotFound_ThrowsException() {
        // Arrange
        when(userInformationRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomException.class, () -> userInformationService.getUserInformationEntityById(999));
    }

    @Test
    void getAllUserInfoByAccount_Success() {
        // Arrange
        List<UserInformation> userInfoList = Arrays.asList(testUserInfo);
        List<UserInfoDTO> userInfoDTOList = Arrays.asList(testUserInfoDTO);

        when(accountService.getAccountAuth()).thenReturn(testAccount);
        when(userInformationRepository.findByAccount_IdOrderByIsDefaultDesc(anyInt())).thenReturn(userInfoList);
        when(userInformationMapper.convertEntityListToDTOList(anyList())).thenReturn(userInfoDTOList);

        // Act
        List<UserInfoDTO> result = userInformationService.getAllUserInfoByAccount();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(accountService).getAccountAuth();
    }
}
