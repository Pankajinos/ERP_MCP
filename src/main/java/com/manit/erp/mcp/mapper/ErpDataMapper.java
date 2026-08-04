package com.manit.erp.mcp.mapper;

import com.manit.erp.mcp.dto.erp.FeeErpResponse;
import com.manit.erp.mcp.dto.erp.RegistrationErpResponse;
import com.manit.erp.mcp.dto.erp.ResultErpResponse;
import com.manit.erp.mcp.dto.tool.*;
import com.manit.erp.mcp.util.CalculationUtils;
import com.manit.erp.mcp.util.FeeCategorizer;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Mapper component converting raw, verbose ERP DTOs into minimal, high-value business DTOs.
 */
@Component
public class ErpDataMapper {

    public AcademicSummaryResponse toAcademicSummary(ResultErpResponse erpResponse) {
        if (erpResponse == null || erpResponse.data() == null) {
            return new AcademicSummaryResponse("Unknown Student", "Unknown Program", 0.0, List.of());
        }

        String studentName = "Student";
        String program = "Program";

        if (erpResponse.data().basicDetails() != null && !erpResponse.data().basicDetails().isEmpty()) {
            ResultErpResponse.BasicDetail basic = erpResponse.data().basicDetails().get(0);
            if (basic.fullName() != null && !basic.fullName().isBlank()) studentName = basic.fullName();
            if (basic.programName() != null && !basic.programName().isBlank()) program = basic.programName();
        }

        List<AcademicSummaryResponse.SemesterBreakdown> breakdown = new ArrayList<>();
        double totalWeightedSgpa = 0.0;
        double totalCreditsAllSemesters = 0.0;

        List<ResultErpResponse.SemesterDataItem> semList = erpResponse.data().semesterData();
        if (semList != null) {
            for (int i = 0; i < semList.size(); i++) {
                ResultErpResponse.SemesterDataItem item = semList.get(i);
                if (item.data() == null || item.data().grandTotal() == null) continue;

                ResultErpResponse.GrandTotal gt = item.data().grandTotal();
                int semNum = i + 1;
                double sgpa = CalculationUtils.parseDoubleSafely(gt.sgpa());
                double credits = CalculationUtils.parseDoubleSafely(gt.totalCredits());

                breakdown.add(new AcademicSummaryResponse.SemesterBreakdown(semNum, sgpa, credits));

                if (credits > 0) {
                    totalWeightedSgpa += (sgpa * credits);
                    totalCreditsAllSemesters += credits;
                }
            }
        }

        double cgpa = (totalCreditsAllSemesters > 0)
                ? CalculationUtils.roundToTwoDecimals(totalWeightedSgpa / totalCreditsAllSemesters)
                : 0.0;

        return new AcademicSummaryResponse(studentName, program, cgpa, breakdown);
    }

    public SemesterDetailsResponse toSemesterDetails(
            int semester,
            ResultErpResponse resultErp,
            List<RegistrationErpResponse> regList,
            FeeErpResponse feeErp
    ) {
        double sgpa = 0.0;
        double credits = 0.0;
        List<SemesterDetailsResponse.SubjectDetail> subjects = new ArrayList<>();

        Map<String, String> facultyMap = extractFacultyMap(regList);

        if (resultErp != null && resultErp.data() != null && resultErp.data().semesterData() != null) {
            List<ResultErpResponse.SemesterDataItem> semList = resultErp.data().semesterData();
            if (semester >= 1 && semester <= semList.size()) {
                ResultErpResponse.SemesterDataItem semItem = semList.get(semester - 1);
                if (semItem.data() != null) {
                    if (semItem.data().grandTotal() != null) {
                        sgpa = CalculationUtils.parseDoubleSafely(semItem.data().grandTotal().sgpa());
                        credits = CalculationUtils.parseDoubleSafely(semItem.data().grandTotal().totalCredits());
                    }

                    if (semItem.data().subjects() != null) {
                        for (ResultErpResponse.SubjectItem s : semItem.data().subjects()) {
                            String code = s.subjectCode() != null ? s.subjectCode() : "N/A";
                            String name = s.subname() != null ? s.subname() : "N/A";
                            String faculty = facultyMap.getOrDefault(code, "Faculty Not Assigned");
                            subjects.add(new SemesterDetailsResponse.SubjectDetail(code, name, faculty));
                        }
                    }
                }
            }
        }

        // Aggregate Fee Info for requested semester
        SemesterDetailsResponse.FeeSummary feeSummary = computeFeeSummaryForSemester(semester, feeErp);

        return new SemesterDetailsResponse(semester, sgpa, credits, subjects, feeSummary);
    }

    public SubjectDetailsResponse toSubjectDetails(
            String subjectCode,
            ResultErpResponse resultErp,
            List<RegistrationErpResponse> regList
    ) {
        String codeUpper = subjectCode.trim().toUpperCase(Locale.ROOT);
        Map<String, String> facultyMap = extractFacultyMap(regList);

        if (resultErp != null && resultErp.data() != null && resultErp.data().semesterData() != null) {
            for (ResultErpResponse.SemesterDataItem semItem : resultErp.data().semesterData()) {
                if (semItem.data() != null && semItem.data().subjects() != null) {
                    for (ResultErpResponse.SubjectItem s : semItem.data().subjects()) {
                        if (s.subjectCode() != null && s.subjectCode().trim().equalsIgnoreCase(codeUpper)) {
                            String subName = s.subname() != null ? s.subname() : "N/A";
                            String faculty = facultyMap.getOrDefault(codeUpper, "Faculty Not Assigned");
                            double midterm = CalculationUtils.parseDoubleSafely(s.midTermMarks());
                            double endterm = CalculationUtils.parseDoubleSafely(s.endTermMarks());
                            double total = CalculationUtils.parseDoubleSafely(s.marksObtained());
                            String grade = s.grade() != null ? s.grade() : "N/A";
                            double credits = CalculationUtils.parseDoubleSafely(s.credit());

                            return new SubjectDetailsResponse(
                                    codeUpper,
                                    subName,
                                    faculty,
                                    new SubjectDetailsResponse.MarksBreakdown(midterm, endterm, total),
                                    grade,
                                    credits
                            );
                        }
                    }
                }
            }
        }

        // If not found in result, check registration
        if (regList != null) {
            for (RegistrationErpResponse reg : regList) {
                if (reg.subjects() != null) {
                    for (RegistrationErpResponse.RegisteredSubject rs : reg.subjects()) {
                        if (rs.subjectCode() != null && rs.subjectCode().trim().equalsIgnoreCase(codeUpper)) {
                            return new SubjectDetailsResponse(
                                    codeUpper,
                                    rs.subName() != null ? rs.subName() : "N/A",
                                    rs.empName() != null ? rs.empName() : "Faculty Not Assigned",
                                    new SubjectDetailsResponse.MarksBreakdown(0.0, 0.0, 0.0),
                                    "REGISTERED",
                                    0.0
                            );
                        }
                    }
                }
            }
        }

        return null;
    }

    public RegistrationInfoResponse toRegistrationInfo(Integer semester, List<RegistrationErpResponse> regList) {
        if (regList == null || regList.isEmpty()) {
            return new RegistrationInfoResponse(semester != null ? semester : 1, 0.0, List.of());
        }

        RegistrationErpResponse targetReg = null;
        if (semester != null) {
            for (RegistrationErpResponse r : regList) {
                if (Objects.equals(r.semesterTermNoIdCode(), semester)) {
                    targetReg = r;
                    break;
                }
            }
        }

        if (targetReg == null) {
            // Pick highest/latest semester registration
            targetReg = regList.stream()
                    .max(Comparator.comparingInt(r -> r.semesterTermNoIdCode() != null ? r.semesterTermNoIdCode() : 0))
                    .orElse(regList.get(0));
        }

        int semNumber = targetReg.semesterTermNoIdCode() != null ? targetReg.semesterTermNoIdCode() : (semester != null ? semester : 1);
        double credits = CalculationUtils.parseDoubleSafely(targetReg.credits());

        List<RegistrationInfoResponse.RegisteredSubjectInfo> subjects = new ArrayList<>();
        if (targetReg.subjects() != null) {
            for (RegistrationErpResponse.RegisteredSubject rs : targetReg.subjects()) {
                boolean feedbackPending = !Boolean.TRUE.equals(rs.feedbackIsSubmitted());
                subjects.add(new RegistrationInfoResponse.RegisteredSubjectInfo(
                        rs.subjectCode() != null ? rs.subjectCode() : "N/A",
                        rs.subName() != null ? rs.subName() : "N/A",
                        rs.empName() != null ? rs.empName() : "Not Assigned",
                        feedbackPending
                ));
            }
        }

        return new RegistrationInfoResponse(semNumber, credits, subjects);
    }

    public FeeInfoResponse toFeeInfo(Integer targetSemester, Integer targetYear, FeeErpResponse feeErp) {
        double academic = 0.0;
        double hostel = 0.0;
        double other = 0.0;
        List<FeeInfoResponse.FeeBreakdownItem> breakdown = new ArrayList<>();

        if (feeErp != null && feeErp.feeData() != null) {
            for (FeeErpResponse.FeeItem item : feeErp.feeData()) {
                if (targetSemester != null && item.semesterTypeIdCode() != null && !targetSemester.equals(item.semesterTypeIdCode())) {
                    continue;
                }
                double amt = CalculationUtils.parseDoubleSafely(item.amount() != null ? item.amount() : item.feesPrice());
                String title = item.feesSubHeadTitle() != null ? item.feesSubHeadTitle() : "Miscellaneous Fee";

                FeeCategorizer.FeeCategory category = FeeCategorizer.categorize(title);
                switch (category) {
                    case ACADEMIC -> academic += amt;
                    case HOSTEL -> hostel += amt;
                    case OTHER -> other += amt;
                }
                breakdown.add(new FeeInfoResponse.FeeBreakdownItem(title, amt));
            }
        }

        double total = academic + hostel + other;
        int sem = targetSemester != null ? targetSemester : 1;
        int yr = targetYear != null ? targetYear : 2025;

        return new FeeInfoResponse(sem, yr, academic, hostel, other, total, breakdown);
    }

    public AcademicSearchResultResponse searchAcademicRecords(
            String query,
            ResultErpResponse resultErp,
            List<RegistrationErpResponse> regList
    ) {
        String q = (query != null ? query.trim().toLowerCase(Locale.ROOT) : "");
        List<AcademicSearchResultResponse.SearchMatch> matches = new ArrayList<>();

        if (resultErp != null && resultErp.data() != null && resultErp.data().semesterData() != null) {
            List<ResultErpResponse.SemesterDataItem> semList = resultErp.data().semesterData();
            for (int i = 0; i < semList.size(); i++) {
                int sem = i + 1;
                ResultErpResponse.SemesterDataItem item = semList.get(i);
                if (item.data() != null && item.data().subjects() != null) {
                    for (ResultErpResponse.SubjectItem s : item.data().subjects()) {
                        String name = s.subname() != null ? s.subname() : "";
                        String code = s.subjectCode() != null ? s.subjectCode() : "";
                        String grade = s.grade() != null ? s.grade() : "";
                        double marks = CalculationUtils.parseDoubleSafely(s.marksObtained());
                        double credits = CalculationUtils.parseDoubleSafely(s.credit());

                        boolean match = name.toLowerCase(Locale.ROOT).contains(q)
                                || code.toLowerCase(Locale.ROOT).contains(q)
                                || grade.toLowerCase(Locale.ROOT).equalsIgnoreCase(q)
                                || (q.contains("grade") && q.contains(grade.toLowerCase(Locale.ROOT)))
                                || (q.contains("lab") && name.toLowerCase(Locale.ROOT).contains("lab"))
                                || (q.contains("credit") && q.contains(String.valueOf((int) credits)));

                        if (match || q.isBlank()) {
                            matches.add(new AcademicSearchResultResponse.SearchMatch(
                                    name, code, grade, marks, credits, sem,
                                    String.format("Semester %d | Marks: %.1f | Grade: %s", sem, marks, grade)
                            ));
                        }
                    }
                }
            }
        }

        return new AcademicSearchResultResponse(matches);
    }

    public StudentDashboardResponse toStudentDashboard(
            ResultErpResponse resultErp,
            List<RegistrationErpResponse> regList,
            FeeErpResponse feeErp
    ) {
        AcademicSummaryResponse summary = toAcademicSummary(resultErp);

        int currentSem = 1;
        if (regList != null && !regList.isEmpty()) {
            currentSem = regList.stream()
                    .map(r -> r.semesterTermNoIdCode() != null ? r.semesterTermNoIdCode() : 1)
                    .max(Integer::compareTo)
                    .orElse(1);
        } else if (summary.semesterBreakdown() != null && !summary.semesterBreakdown().isEmpty()) {
            currentSem = summary.semesterBreakdown().size();
        }

        int backlogs = 0;
        if (resultErp != null && resultErp.data() != null && resultErp.data().semesterData() != null) {
            for (ResultErpResponse.SemesterDataItem sem : resultErp.data().semesterData()) {
                if (sem.data() != null && sem.data().subjects() != null) {
                    for (ResultErpResponse.SubjectItem s : sem.data().subjects()) {
                        if ("F".equalsIgnoreCase(s.grade()) || "FAIL".equalsIgnoreCase(s.grade())) {
                            backlogs++;
                        }
                    }
                }
            }
        }

        double totalCredits = summary.semesterBreakdown().stream()
                .mapToDouble(AcademicSummaryResponse.SemesterBreakdown::credits)
                .sum();

        double pendingFees = 0.0;
        if (feeErp != null && feeErp.feeData() != null) {
            pendingFees = feeErp.feeData().stream()
                    .mapToDouble(f -> CalculationUtils.parseDoubleSafely(f.amount()))
                    .sum();
        }

        return new StudentDashboardResponse(
                summary.studentName(),
                currentSem,
                summary.cgpa(),
                totalCredits,
                backlogs,
                pendingFees
        );
    }

    private Map<String, String> extractFacultyMap(List<RegistrationErpResponse> regList) {
        Map<String, String> map = new HashMap<>();
        if (regList != null) {
            for (RegistrationErpResponse reg : regList) {
                if (reg.subjects() != null) {
                    for (RegistrationErpResponse.RegisteredSubject rs : reg.subjects()) {
                        if (rs.subjectCode() != null && rs.empName() != null) {
                            map.put(rs.subjectCode().trim().toUpperCase(Locale.ROOT), rs.empName());
                        }
                    }
                }
            }
        }
        return map;
    }

    private SemesterDetailsResponse.FeeSummary computeFeeSummaryForSemester(int semester, FeeErpResponse feeErp) {
        double academic = 0.0;
        double hostel = 0.0;
        double other = 0.0;

        if (feeErp != null && feeErp.feeData() != null) {
            for (FeeErpResponse.FeeItem item : feeErp.feeData()) {
                if (item.semesterTypeIdCode() != null && item.semesterTypeIdCode() == semester) {
                    double amt = CalculationUtils.parseDoubleSafely(item.amount() != null ? item.amount() : item.feesPrice());
                    FeeCategorizer.FeeCategory category = FeeCategorizer.categorize(item.feesSubHeadTitle());
                    switch (category) {
                        case ACADEMIC -> academic += amt;
                        case HOSTEL -> hostel += amt;
                        case OTHER -> other += amt;
                    }
                }
            }
        }
        return new SemesterDetailsResponse.FeeSummary(academic, hostel, other);
    }
}
