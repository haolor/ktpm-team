package com.e_commerce.integration;

import com.e_commerce.config.TestConfig;
import com.e_commerce.entity.account.Account;
import com.e_commerce.enums.AccountRole;
import com.e_commerce.repository.account.AccountRepository;
import com.e_commerce.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestConfig.class)
public abstract class IntegrationTestBase {

    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected AccountRepository accountRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected JwtUtil jwtUtil;

    private static final String DEFAULT_EMAIL = "testuser@example.com";

    @BeforeEach
    void resetPort() {
        restTemplate.getRestTemplate().setUriTemplateHandler(
                restTemplate.getRestTemplate().getUriTemplateHandler());
    }

    protected String ensureToken() {
        Account account = accountRepository.findByEmail(DEFAULT_EMAIL)
                .orElseGet(() -> {
                    Account acc = new Account();
                    acc.setId(1);
                    acc.setEmail(DEFAULT_EMAIL);
                    acc.setAccountName("test-user");
                    acc.setPassword(passwordEncoder.encode("Password123!"));
                    acc.setRole(AccountRole.USER);
                    acc.setStatus(true);
                    acc.setActive(true);
                    return accountRepository.save(acc);
                });
        return jwtUtil.generateToken(account);
    }

    protected HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(ensureToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
