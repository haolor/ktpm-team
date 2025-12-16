package com.e_commerce.integration;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;

/**
 * Base class for WebMvcTest integration tests.
 * Provides common mock beans needed across all controller tests.
 * TestSecurityConfig provides JwtUtil and TokenBlacklistService mocks.
 */
public abstract class BaseWebMvcTest {

    @MockBean
    protected JpaMetamodelMappingContext jpaMetamodelMappingContext;
}
