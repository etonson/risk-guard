package util.lang;

/*
    @Author : Eton.lin
    @Description : 數字相關驗證
    @Date : 2025/4/25 下午 07:48
*/
public class NumberUtils {

    /*
      判斷是否為 int 範圍內的整數
    */
    public static boolean isInteger(String str) {
        if (str == null || str.trim().isEmpty()) return false;
        try {
            Integer.parseInt(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /*
     判斷是否為 long 範圍內的整數
     */
    public static boolean isLong(String str) {
        if (str == null || str.trim().isEmpty()) return false;
        try {
            Long.parseLong(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /*
      判斷是否為 double（浮點數，可接受科學記號）
     */
    public static boolean isDouble(String str) {
        if (str == null || str.trim().isEmpty()) return false;
        try {
            Double.parseDouble(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /*
      判斷是否為正整數（不含負號與零）
     */
    public static boolean isPositiveInteger(String str) {
        return str != null && str.matches("^[1-9]\\d*$");
    }

    /*
      判斷是否為十六進位數（例如 0x1A3F 或 1A3F）
     */
    public static boolean isHex(String str) {
        return str != null && str.matches("^(0x)?[0-9a-fA-F]+$");
    }

    /*
      判斷是否為 BigInteger（不考慮範圍限制）
     */
    public static boolean isBigInteger(String str) {
        if (str == null || str.trim().isEmpty()) return false;
        try {
            new java.math.BigInteger(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
