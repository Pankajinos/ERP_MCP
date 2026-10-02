package com.manit.erp.mcp.services;

import com.manit.erp.mcp.clients.FeeApiClient;
import com.manit.erp.mcp.dto.erp.FeeErpResponse;
import com.manit.erp.mcp.dto.tool.FeeDetailResponse;
import com.manit.erp.mcp.dto.tool.FeePerSemesterResponse;
import com.manit.erp.mcp.mapper.ErpDataMapper;
import com.manit.erp.mcp.services.impl.FeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
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

    private FeeErpResponse createSampleFeeResponse() {
        FeeErpResponse.FeeItem tuitionSem5 = new FeeErpResponse.FeeItem(
                1, 1, 4705, 1, "2024-2025", 5, 42000.0, "Tuition Fee", 1, "Sem 5", 42000.0, "2025-01-01"
        );
        FeeErpResponse.FeeItem hostelSem5 = new FeeErpResponse.FeeItem(
                1, 1, 4705, 1, "2024-2025", 5, 7500.0, "Hostel Rent", 1, "Sem 5", 7500.0, "2025-01-01"
        );
        FeeErpResponse.FeeItem librarySem5 = new FeeErpResponse.FeeItem(
                1, 1, 4705, 1, "2024-2025", 5, 500.0, "SF-Library Fee", 1, "Sem 5", 500.0, "2025-01-01"
        );
        FeeErpResponse.FeeItem tuitionSem4 = new FeeErpResponse.FeeItem(
                1, 1, 4705, 1, "2023-2024", 4, 40000.0, "Tuition Fee", 1, "Sem 4", 40000.0, "2024-01-01"
        );
        return new FeeErpResponse(List.of(tuitionSem5, hostelSem5, librarySem5, tuitionSem4));
    }

    @Test
    void testGetFeeDetailsPerSemester_Success() {
        when(feeApiClient.fetchStudentFees(any(), any())).thenReturn(Mono.just(createSampleFeeResponse()));

        Mono<FeePerSemesterResponse> mono = feeService.getFeeDetailsPerSemester(5);

        StepVerifier.create(mono)
                .assertNext(res -> {
                    assertEquals(5, res.semester());
                    assertEquals(50000.0, res.totalAmount());
                    assertEquals(3, res.itemCount());
                    assertEquals(3, res.items().size());
                })
                .verifyComplete();
    }

    @Test
    void testGetFeeDetailsPerSemester_InvalidSemester() {
        Mono<FeePerSemesterResponse> mono = feeService.getFeeDetailsPerSemester(15);

        StepVerifier.create(mono)
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void testGetFeeDetailPerItem_FilterByItem() {
        when(feeApiClient.fetchStudentFees(any(), any())).thenReturn(Mono.just(createSampleFeeResponse()));

        Mono<FeeDetailResponse> mono = feeService.getFeeDetailPerItem("Hostel", null, null, null);

        StepVerifier.create(mono)
                .assertNext(res -> {
                    assertEquals(1, res.matchCount());
                    assertEquals("Hostel Rent", res.feeItems().get(0).feeHead());
                    assertEquals(7500.0, res.feeItems().get(0).amount());
                    assertEquals(new BigDecimal("7500.00"), res.totalAmount());
                })
                .verifyComplete();
    }

    @Test
    void testGetFeeDetailPerItem_FilterByItemAndSemester() {
        when(feeApiClient.fetchStudentFees(any(), any())).thenReturn(Mono.just(createSampleFeeResponse()));

        Mono<FeeDetailResponse> mono = feeService.getFeeDetailPerItem("Tuition", 5, null, null);

        StepVerifier.create(mono)
                .assertNext(res -> {
                    assertEquals(1, res.matchCount());
                    assertEquals(5, res.feeItems().get(0).semester());
                    assertEquals(42000.0, res.feeItems().get(0).amount());
                })
                .verifyComplete();
    }

    @Test
    void testGetFeeDetailPerItem_FilterByAmountRange() {
        when(feeApiClient.fetchStudentFees(any(), any())).thenReturn(Mono.just(createSampleFeeResponse()));

        Mono<FeeDetailResponse> mono = feeService.getFeeDetailPerItem(
                null, null, BigDecimal.valueOf(1000), BigDecimal.valueOf(10000)
        );

        StepVerifier.create(mono)
                .assertNext(res -> {
                    assertEquals(1, res.matchCount());
                    assertEquals("Hostel Rent", res.feeItems().get(0).feeHead());
                })
                .verifyComplete();
    }

    @Test
    void testGetFeeDetailPerItem_NoFiltersReturnsAll() {
        when(feeApiClient.fetchStudentFees(any(), any())).thenReturn(Mono.just(createSampleFeeResponse()));

        Mono<FeeDetailResponse> mono = feeService.getFeeDetailPerItem(null, null, null, null);

        StepVerifier.create(mono)
                .assertNext(res -> {
                    assertEquals(4, res.matchCount());
                    assertEquals(new BigDecimal("90000.00"), res.totalAmount());
                })
                .verifyComplete();
    }

    @Test
    void testGetFeeDetailsPerSemester_SemesterMatchedFromDescAndAmountFromFeesPrice() {
        // semester_type_id_code is null/different, but semester_code_desc is "Sem 5"
        // amount is 0.0, but fees_price is 45000.0
        FeeErpResponse.FeeItem item = new FeeErpResponse.FeeItem(
                1, 1, 4705, 1, "2024-2025", 1, null, null, null,
                45000.0, "College Tuition", null, null, null,
                1, "Sem 5", 0.0, "2025-01-01"
        );
        FeeErpResponse resp = new FeeErpResponse(List.of(item));
        when(feeApiClient.fetchStudentFees(any(), any())).thenReturn(Mono.just(resp));

        Mono<FeePerSemesterResponse> mono = feeService.getFeeDetailsPerSemester(5);

        StepVerifier.create(mono)
                .assertNext(res -> {
                    assertEquals(5, res.semester());
                    assertEquals(45000.0, res.totalAmount());
                    assertEquals(1, res.itemCount());
                    assertEquals("College Tuition", res.items().get(0).feeHead());
                })
                .verifyComplete();
    }
}
