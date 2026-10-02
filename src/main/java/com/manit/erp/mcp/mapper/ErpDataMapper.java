package com.manit.erp.mcp.mapper;

import com.manit.erp.mcp.dto.erp.FeeErpResponse;
import com.manit.erp.mcp.dto.erp.RegistrationErpResponse;
import com.manit.erp.mcp.dto.erp.ResultErpResponse;
import com.manit.erp.mcp.dto.tool.FeeDetailResponse;
import com.manit.erp.mcp.dto.tool.FeePerSemesterResponse;
import com.manit.erp.mcp.dto.tool.SubjectFacultyResponse;
import com.manit.erp.mcp.dto.tool.SubjectMarksResponse;
import com.manit.erp.mcp.exception.ResourceNotFoundException;
import com.manit.erp.mcp.util.CalculationUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Mapper component converting raw ERP responses into granular MCP tool DTOs.
 */
@Component
public class ErpDataMapper {

    private static final Pattern SEM_NUMBER_PATTERN = Pattern.compile("(?:sem(?:ester)?[-_\\s]*|^)(\\d+)(?:st|nd|rd|th|\\b)", Pattern.CASE_INSENSITIVE);

    public FeePerSemesterResponse toFeePerSemester(int semester, FeeErpResponse feeErp) {
        List<FeePerSemesterResponse.FeeItemDetail> items = new ArrayList<>();
        double total = 0.0;

        List<FeeErpResponse.FeeItem> sourceList = (feeErp != null) ? feeErp.allItems() : List.of();
        for (FeeErpResponse.FeeItem item : sourceList) {
            if (matchesSemester(item, semester)) {
                double amt = extractAmount(item);
                String head = extractFeeHead(item);
                String session = extractSession(item);
                String semDesc = extractSemesterDesc(item, semester);

                items.add(new FeePerSemesterResponse.FeeItemDetail(head, amt, session, semDesc));
                total += amt;
            }
        }

        double roundedTotal = CalculationUtils.roundToTwoDecimals(total);
        return new FeePerSemesterResponse(semester, roundedTotal, items.size(), items);
    }

    public FeeDetailResponse toFeeDetailResponse(
            String itemFilter,
            Integer semesterFilter,
            BigDecimal minAmount,
            BigDecimal maxAmount,

            FeeErpResponse feeErp
    ) {
        List<FeeDetailResponse.FeeItemEntry> entries = new ArrayList<>();
        double totalSum = 0.0;

        List<FeeErpResponse.FeeItem> sourceList = (feeErp != null) ? feeErp.allItems() : List.of();
        String filterClean = (itemFilter != null && !itemFilter.isBlank())
                ? itemFilter.trim().toLowerCase(Locale.ROOT)
                : null;

        for (FeeErpResponse.FeeItem feeItem : sourceList) {
            String feeHead = extractFeeHead(feeItem);

            // Filter by item title
            if (filterClean != null) {
                if (!feeHead.toLowerCase(Locale.ROOT).contains(filterClean)) {
                    continue;
                }
            }

            // Filter by semester
            if (semesterFilter != null) {
                if (!matchesSemester(feeItem, semesterFilter)) {
                    continue;
                }
            }

            double amt = extractAmount(feeItem);

            // Filter by minAmount
            if (minAmount != null && amt < minAmount.doubleValue()) {
                continue;
            }

            // Filter by maxAmount
            if (maxAmount != null && amt > maxAmount.doubleValue()) {
                continue;
            }

            Integer sem = resolveItemSemester(feeItem, semesterFilter);
            String semDesc = extractSemesterDesc(feeItem, sem != null ? sem : 0);
            String session = extractSession(feeItem);

            entries.add(new FeeDetailResponse.FeeItemEntry(
                    feeHead,
                    amt,
                    sem,
                    semDesc,
                    session
            ));
            totalSum += amt;
        }

        BigDecimal total = BigDecimal.valueOf(totalSum).setScale(2, RoundingMode.HALF_UP);
        return new FeeDetailResponse(total, entries.size(), entries);
    }

    public SubjectMarksResponse toSubjectMarks(String subject, ResultErpResponse resultErp) {
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Subject code or name must not be empty.");
        }

        String query = subject.trim().toLowerCase(Locale.ROOT);

        if (resultErp != null && resultErp.data() != null && resultErp.data().semesterData() != null) {
            List<ResultErpResponse.SemesterDataItem> semList = resultErp.data().semesterData();


            // Second pass: name contains or code contains
            for (int i = 0; i < semList.size(); i++) {
                ResultErpResponse.SemesterDataItem semItem = semList.get(i);
                if (semItem.data() != null && semItem.data().subjects() != null) {
                    for (ResultErpResponse.SubjectItem s : semItem.data().subjects()) {
                        String name = s.subname() != null ? s.subname().trim().toLowerCase(Locale.ROOT) : "";
                        String code = s.subjectCode() != null ? s.subjectCode().trim().toLowerCase(Locale.ROOT) : "";
                        if (name.contains(query) || code.contains(query)) {
                            int sem = i+1;
                            return buildSubjectMarksResponse(sem, s);
                        }
                    }
                }
            }
        }

        throw new ResourceNotFoundException("Subject marks not found for subject: " + subject);
    }

    public SubjectFacultyResponse toSubjectFaculty(String subject, List<RegistrationErpResponse> regList) {
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Subject code or name must not be empty.");
        }

        String query = subject.trim().toLowerCase(Locale.ROOT);

        if (regList != null) {
            // First pass: exact code match
            for (RegistrationErpResponse reg : regList) {
                if (reg.subjects() != null) {
                    for (RegistrationErpResponse.RegisteredSubject rs : reg.subjects()) {
                        String code = rs.subjectCode() != null ? rs.subjectCode().trim() : "";
                        if (code.equalsIgnoreCase(query)) {
                            return buildSubjectFacultyResponse(reg, rs);
                        }
                    }
                }
            }

            // Second pass: name contains or code contains
            for (RegistrationErpResponse reg : regList) {
                if (reg.subjects() != null) {
                    for (RegistrationErpResponse.RegisteredSubject rs : reg.subjects()) {
                        String name = rs.subName() != null ? rs.subName().trim().toLowerCase(Locale.ROOT) : "";
                        String code = rs.subjectCode() != null ? rs.subjectCode().trim().toLowerCase(Locale.ROOT) : "";
                        if (name.contains(query) || code.contains(query)) {
                            return buildSubjectFacultyResponse(reg, rs);
                        }
                    }
                }
            }
        }

        throw new ResourceNotFoundException("Subject faculty not found for subject: " + subject);
    }

    private String extractFeeHead(FeeErpResponse.FeeItem item) {
        if (item.feesSubHeadTitle() != null && !item.feesSubHeadTitle().isBlank()) {
            return item.feesSubHeadTitle().trim();
        }
        return "Fee Item";
    }

    private double extractAmount(FeeErpResponse.FeeItem item) {
        double amt = CalculationUtils.parseDoubleSafely(item.amount());
        if (amt == 0.0) {
            amt = CalculationUtils.parseDoubleSafely(item.feesPrice());
        }
        return CalculationUtils.roundToTwoDecimals(amt);
    }

    private boolean matchesSemester(FeeErpResponse.FeeItem item, int targetSemester) {
        if (item == null) return false;

        // Check semester_code_desc (e.g., "Sem 5", "Semester 5", "Sem-5", "5th Sem")
        if (item.semesterCodeDesc() != null && !item.semesterCodeDesc().isBlank()) {
            Matcher m = SEM_NUMBER_PATTERN.matcher(item.semesterCodeDesc().trim());
            if (m.find()) {
                int extracted = Integer.parseInt(m.group(1));
                if (extracted == targetSemester) {
                    return true;
                }
            }
        }

        return false;
    }

    private Integer resolveItemSemester(FeeErpResponse.FeeItem item, Integer fallback) {
        if (item.semesterCodeDesc() != null && !item.semesterCodeDesc().isBlank()) {
            Matcher m = SEM_NUMBER_PATTERN.matcher(item.semesterCodeDesc().trim());
            if (m.find()) {
                return Integer.parseInt(m.group(1));
            }
        }

        return fallback;
    }

    private String extractSemesterDesc(FeeErpResponse.FeeItem item, int fallbackSemester) {
        if (item.semesterCodeDesc() != null && !item.semesterCodeDesc().isBlank()) {
            return item.semesterCodeDesc().trim();
        }
        return "Sem " + fallbackSemester;
    }

    private String extractSession(FeeErpResponse.FeeItem item) {
        if (item.feesSession() != null) {
            return item.feesSession().toString().trim();
        }
        return "";
    }

    private SubjectMarksResponse buildSubjectMarksResponse(int semester, ResultErpResponse.SubjectItem s) {
        String code = s.subjectCode() != null ? s.subjectCode() : "N/A";
        String name = s.subname() != null ? s.subname() : "N/A";
        Double midTerm = CalculationUtils.parseDoubleSafely(s.midTermMarks());
        Double endTerm = CalculationUtils.parseDoubleSafely(s.endTermMarks());
        Double obtained = CalculationUtils.parseDoubleSafely(s.marksObtained());
        Double total = CalculationUtils.parseDoubleSafely(s.totalMarks());
        String grade = s.grade() != null ? s.grade() : "N/A";
        String gradePoint = s.gradePoint() != null ? s.gradePoint() : "N/A";
        Double credit = CalculationUtils.parseDoubleSafely(s.credit());

        return new SubjectMarksResponse(
                code,
                name,
                semester,
                midTerm,
                endTerm,
                obtained,
                total,
                grade,
                gradePoint,
                credit
        );
    }

    private SubjectFacultyResponse buildSubjectFacultyResponse(
            RegistrationErpResponse reg,
            RegistrationErpResponse.RegisteredSubject rs
    ) {
        String code = rs.subjectCode() != null ? rs.subjectCode() : "N/A";
        String name = rs.subName() != null ? rs.subName() : "N/A";
        String faculty = rs.empName() != null && !rs.empName().isBlank() ? rs.empName() : "Faculty Not Assigned";
        Integer sem = null;
        if (reg.semesterDetails1() != null && !reg.semesterDetails1().isBlank()) {
            Matcher m = SEM_NUMBER_PATTERN.matcher(reg.semesterDetails1().trim());
            if (m.find()) {
                sem = Integer.parseInt(m.group(1));
            }
        }
        if (sem == null || sem == 0) {
            sem = reg.regSemesterTypeIdCode();
        }
        if (sem == null || sem == 0) {
            sem = reg.semesterTermNoIdCode();
        }
        String dept = reg.depName();

        return new SubjectFacultyResponse(code, name, faculty, sem, dept);
    }
}
