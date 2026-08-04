package com.manit.erp.mcp.services;

import com.manit.erp.mcp.clients.FeeApiClient;
import com.manit.erp.mcp.dto.erp.FeeErpResponse;
import com.manit.erp.mcp.dto.tool.FeeInfoResponse;
import com.manit.erp.mcp.mapper.ErpDataMapper;
import com.manit.erp.mcp.services.impl.FeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeeServiceTest {

    @Mock
    private FeeApiClient feeApiClient;

    private FeeService feeService;

    @BeforeEach
    void setUp() {
        ErpDataMapper mapper = new ErpDataMapper();
        feeService = new FeeServiceImpl(feeApiClient, mapper);
    }

    @Test
    void testGetFeeInfo_Categorization() {
        FeeErpResponse.FeeItem tuition = new FeeErpResponse.FeeItem(1, 1, 4705, 1, 2025, 5, 42000.0, "Tuition Fee", 1, "Sem 5", 42000.0, "2025-01-01");
        FeeErpResponse.FeeItem hostel = new FeeErpResponse.FeeItem(1, 1, 4705, 1, 2025, 5, 7500.0, "Hostel Rent", 1, "Sem 5", 7500.0, "2025-01-01");
        FeeErpResponse.FeeItem library = new FeeErpResponse.FeeItem(1, 1, 4705, 1, 2025, 5, 500.0, "Library Fee", 1, "Sem 5", 500.0, "2025-01-01");

        FeeErpResponse mockResponse = new FeeErpResponse(List.of(tuition, hostel, library));
        when(feeApiClient.fetchStudentFees(any(), any())).thenReturn(Mono.just(mockResponse));

        Mono<FeeInfoResponse> resultMono = feeService.getFeeInfo(5, 2025);

        StepVerifier.create(resultMono)
                .assertNext(feeInfo -> {
                    assertEquals(5, feeInfo.semester());
                    assertEquals(2025, feeInfo.year());
                    assertEquals(42500.0, feeInfo.academicFee()); // Tuition + Library
                    assertEquals(7500.0, feeInfo.hostelFee());
                    assertEquals(50000.0, feeInfo.total());
                    assertEquals(3, feeInfo.breakdown().size());
                })
                .verifyComplete();
    }
}
