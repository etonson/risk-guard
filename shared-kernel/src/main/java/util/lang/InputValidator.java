package util.lang;


import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.Optional;
import java.util.regex.Pattern;

/*
    @Author : Eton.lin
    @Description : 輸入欄位檢查工具
    @Date : 2025/4/21 下午 05:06
*/
public class InputValidator {
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private static final Pattern MOBILE_PATTERN =
        Pattern.compile("^09\\d{8}$");

    private static final Pattern ALPHA_NUMERIC_PATTERN =
        Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]+$");

    private static final int[] ID_WEIGHTS = {1, 9, 8, 7, 6, 5, 4, 3, 2, 1};

    /*
     非空白字串
     */
    public static boolean isNotEmpty(String str) {
        return StringUtils.isNotBlank(str);
    }

    /*
     空白字串
     */
    public static boolean isEmpty(String str) {
        str = Optional.ofNullable(str).orElse("");
        return StringUtils.isEmpty(str.trim());
    }

    /*
     純數字
     */
    public static boolean isNumeric(String str) {
        return StringUtils.isNumeric(str);
    }


    /*
    純數字並大於0
    */
    public static boolean isNumericGTZero(String str) {
        if (isNumeric(str)) {
            return Integer.parseInt(str) > 0;
        } else {
            return false;
        }
    }

    public static boolean isValidNumeric(String input) {
        return input != null && input.matches("\\d+");
    }

    /*
     民國年月日格式 (1140419)
     */
    public static boolean isValidRocDate(String rocDateStr) {
        if (!isNumeric(rocDateStr) || rocDateStr.length() != 7) {
            return false;
        }
        try {
            int rocYear = Integer.parseInt(rocDateStr.substring(0, 3)) + 1911;
            int month = Integer.parseInt(rocDateStr.substring(3, 5));
            int day = Integer.parseInt(rocDateStr.substring(5, 7));
            return LocalDate.of(rocYear, month, day) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /*
     民國年月日格式 (1140419)
     */
    public static boolean isValidNotRocDate(String rocDateStr) {
        return !isValidRocDate(rocDateStr);
    }

    /*
      字串長度限制驗證
     */
    public static boolean isValidText(String str, int maxLength) {
        return isNotEmpty(str) && str.length() <= maxLength;
    }

    /*
     Email 格式驗證
     */
    public static boolean isValidEmail(String str) {
        return isNotEmpty(str) && EMAIL_PATTERN.matcher(str).matches();
    }

    /*
      手機號碼驗證（台灣格式 09xxxxxxxx）
     */
    public static boolean isValidMobile(String str) {
        return isNotEmpty(str) && MOBILE_PATTERN.matcher(str).matches();
    }

    /*
      英數字混合驗證（至少一個英文 + 一個數字）
     */
    public static boolean isAlphaNumeric(String str) {
        return isNotEmpty(str) && ALPHA_NUMERIC_PATTERN.matcher(str).matches();
    }

}
