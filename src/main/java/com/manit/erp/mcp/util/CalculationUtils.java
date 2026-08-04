package com.manit.erp.mcp.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Calculations helper for SGPA/CGPA and numerical parsing.
 */
public class CalculationUtils {

    public static Double parseDoubleSafely(Object val) {
        if (val == null) return 0.0;
        try {
            if (val instanceof Number n) {
                return n.doubleValue();
            }
            String str = val.toString().trim();
            if (str.isEmpty() || str.equalsIgnoreCase("null") || str.equalsIgnoreCase("N/A")) {
                return 0.0;
            }
            return Double.parseDouble(str);
        } catch (Exception e) {
            return 0.0;
        }
    }

    public static Integer parseIntegerSafely(Object val) {
        if (val == null) return 0;
        try {
            if (val instanceof Number n) {
                return n.intValue();
            }
            String str = val.toString().trim();
            if (str.isEmpty() || str.equalsIgnoreCase("null")) {
                return 0;
            }
            return Integer.parseInt(str);
        } catch (Exception e) {
            return 0;
        }
    }

    public static double roundToTwoDecimals(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
