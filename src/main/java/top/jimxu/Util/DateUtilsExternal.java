package top.jimxu.Util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 日期区间工具类（JDK 8+）。
 *
 * 获取闭区间 [startDate, endDate] 内的所有日期，开始日期和结束日期都包含在内。
 */
public class DateUtilsExternal {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private DateUtilsExternal() {
    }

    /**
     * 获取闭区间内的所有日期（包含开始日期和结束日期）。
     *
     * @param startDate 开始日期，格式 yyyy-MM-dd
     * @param endDate   结束日期，格式 yyyy-MM-dd
     * @return 闭区间内所有日期，格式 yyyy-MM-dd 的 List；
     *         入参为空、格式错误，或开始日期晚于结束日期时，返回空列表
     */
    public static List<String> getDatesBetween(String startDate, String endDate) {
        if (startDate == null || endDate == null
                || startDate.trim().isEmpty() || endDate.trim().isEmpty()) {
            return new ArrayList<>();
        }
        LocalDate start;
        LocalDate end;
        try {
            start = LocalDate.parse(startDate.trim(), FORMATTER);
            end = LocalDate.parse(endDate.trim(), FORMATTER);
        } catch (DateTimeParseException e) {
            return new ArrayList<>();
        }
        return getDatesBetween(start, end);
    }

    /**
     * 获取闭区间内的所有日期（包含开始日期和结束日期）。
     *
     * @param startDate 开始日期（包含）
     * @param endDate   结束日期（包含）
     * @return 闭区间内所有日期，格式 yyyy-MM-dd 的 List；
     *         开始日期晚于结束日期时，返回空列表
     */
    public static List<String> getDatesBetween(LocalDate startDate, LocalDate endDate) {
        List<String> dates = new ArrayList<>();
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            return dates;
        }
        // 循环条件用 !isAfter(end)，保证结束日期也被包含，即闭区间
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            dates.add(date.format(FORMATTER));
        }
        return dates;
    }
}
