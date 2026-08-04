package com.manit.erp.mcp.util;

import java.util.Locale;

/**
 * Utility to group granular ERP fee head titles into standard Academic, Hostel, and Other categories.
 */
public class FeeCategorizer {

    public enum FeeCategory {
        ACADEMIC,
        HOSTEL,
        OTHER
    }

    public static FeeCategory categorize(String feeHeadTitle) {
        if (feeHeadTitle == null) {
            return FeeCategory.OTHER;
        }

        String lower = feeHeadTitle.toLowerCase(Locale.ROOT);

        if (lower.contains("tuition") || lower.contains("academic") || lower.contains("exam") ||
            lower.contains("registration") || lower.contains("library") || lower.contains("lab") ||
            lower.contains("course") || lower.contains("admission")) {
            return FeeCategory.ACADEMIC;
        }

        if (lower.contains("hostel") || lower.contains("mess") || lower.contains("room") ||
            lower.contains("electricity") || lower.contains("water") || lower.contains("maintenance")) {
            return FeeCategory.HOSTEL;
        }

        return FeeCategory.OTHER;
    }
}
