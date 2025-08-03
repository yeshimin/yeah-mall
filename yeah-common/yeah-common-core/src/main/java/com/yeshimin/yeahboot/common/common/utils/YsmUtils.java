package com.yeshimin.yeahboot.common.common.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class YsmUtils {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    // yyyy-MM-dd
    private static final Pattern DATE_PATTERN =
            Pattern.compile("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$");
    // yyyy-MM-dd HH:mm:ss
    private static final Pattern DATE_TIME_PATTERN =
            Pattern.compile("^\\d{4}-(0[1-9]|1[0-2])-" +    // 年-月
                    "(0[1-9]|[12]\\d|3[01]) " +                   // 日
                    "([01]\\d|2[0-3]):" +                         // 时（00~23）
                    "[0-5]\\d:" +                                 // 分（00~59）
                    "[0-5]\\d$");                                 // 秒（00~59）

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_FORMATTER_COMPACT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ================================================================================
    // 时间相关

    /**
     * LocalDateTime转Date
     */
    public static Date date(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            throw new IllegalArgumentException("localDateTime cannot be null");
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 按日期范围生成每一个日期
     */
    public static List<LocalDate> dates(LocalDate beginDate, LocalDate endDate) {
        if (beginDate == null || endDate == null) {
            throw new IllegalArgumentException("beginDate and endDate cannot be null");
        }
        return Stream.iterate(beginDate, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(beginDate, endDate.plusDays(1)))
                .collect(Collectors.toList());
    }

    /**
     * 根据字符串类型的日期生成日期对象
     * 日期格式：yyyy-MM-dd | yyyy-MM | yyyy
     * 其他格式不支持
     */
    public static LocalDate date(String date) {
        if (date == null || date.isEmpty()) {
            throw new IllegalArgumentException("date cannot be null or empty");
        }

        if (date.length() == 10) {
            return LocalDate.parse(date, DATE_FORMATTER);
        } else if (date.length() == 7) {
            return LocalDate.parse(date + "-01", DATE_FORMATTER);
        } else if (date.length() == 4) {
            return LocalDate.parse(date + "-01-01", DATE_FORMATTER);
        } else {
            throw new IllegalArgumentException("unsupported date format");
        }
    }

    /**
     * 获取指定日期的月初时间
     * 例如：2025-03-14 -> 2025-03-01
     */
    public static LocalDate monthBegin(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("date cannot be null");
        }
        return date.withDayOfMonth(1);
    }

    /**
     * 获取指定日期的月末时间
     * 例如：2025-03-14 -> 2025-03-31
     */
    public static LocalDate monthEnd(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("date cannot be null");
        }
        return date.withDayOfMonth(date.lengthOfMonth());
    }

    /**
     * 获取指定日期的最小时间
     * 例如：2025-03-14 12:34:56 -> 2025-03-14 00:00:00
     */
    public static LocalDateTime minTime(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("date cannot be null");
        }
        return date.atStartOfDay();
    }

    /**
     * 获取指定日期的最大时间
     * 例如：2025-03-14 12:34:56 -> 2025-03-14 23:59:59
     */
    public static LocalDateTime maxTime(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("date cannot be null");
        }
        return date.atTime(23, 59, 59);
    }

    /**
     * 判断是否日期格式
     */
    public static boolean isDate(String date) {
        if (date == null || date.isEmpty()) {
            return false;
        }
        return DATE_PATTERN.matcher(date).matches();
    }

    /**
     * 判断是否日期时间格式
     */
    public static boolean isDateTime(String dateTime) {
        if (dateTime == null || dateTime.isEmpty()) {
            return false;
        }
        return DATE_TIME_PATTERN.matcher(dateTime).matches();
    }

    /**
     * 生成日期字符串
     */
    public static String dateStr() {
        LocalDate date = LocalDate.now();
        return dateStr(date, DATE_FORMATTER);
    }

    /**
     * 生成日期字符串
     */
    public static String dateStr(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("date cannot be null");
        }
        return dateStr(date, DATE_FORMATTER);
    }

    /**
     * 生成日期字符串
     */
    public static String dateStr(String format) {
        if (format == null || format.isEmpty()) {
            throw new IllegalArgumentException("format cannot be null or empty");
        }
        return dateStr(LocalDate.now(), format);
    }

    /**
     * 生成日期字符串
     */
    public static String dateStr(DateTimeFormatter formatter) {
        if (formatter == null) {
            throw new IllegalArgumentException("formatter cannot be null");
        }
        return dateStr(LocalDate.now(), formatter);
    }

    /**
     * 生成日期字符串
     *
     * @param compact 是否紧凑格式
     */
    public static String dateStr(boolean compact) {
        return dateStr(compact ? DATE_FORMATTER_COMPACT : DATE_FORMATTER);
    }

    /**
     * 生成日期字符串
     */
    public static String dateStr(LocalDate date, String format) {
        if (date == null) {
            throw new IllegalArgumentException("date cannot be null");
        }
        if (format == null || format.isEmpty()) {
            throw new IllegalArgumentException("format cannot be null or empty");
        }
        return dateStr(date, DateTimeFormatter.ofPattern(format));
    }

    /**
     * 生成日期字符串
     */
    public static String dateStr(LocalDate date, DateTimeFormatter formatter) {
        if (date == null) {
            throw new IllegalArgumentException("date cannot be null");
        }
        if (formatter == null) {
            throw new IllegalArgumentException("formatter cannot be null");
        }
        return date.format(formatter);
    }

    /**
     * 获取第几周
     */
    public static int weekOfYear(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("date cannot be null");
        }
        WeekFields weekFields = WeekFields.of(Locale.CHINA);
        return date.get(weekFields.weekOfYear());
    }

    // 时间相关
    // ================================================================================

    /**
     * 对半拆分map，奇数的话，前面的map中多一个
     * 比如：{a=1, b=2, c=3, d=4, e=5}，拆分为：{a=1, b=2, c=3}，{d=4, e=5}
     */
    public static <K, V> List<Map<K, V>> splitMap(Map<K, V> map) {
        if (map == null || map.isEmpty()) {
            return Arrays.asList(new LinkedHashMap<>(), new LinkedHashMap<>());
        }

        Map<K, V> map1 = new LinkedHashMap<>();
        Map<K, V> map2 = new LinkedHashMap<>();

        int mid = (map.size() + 1) / 2;
        int count = 0;

        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (count < mid) {
                map1.put(entry.getKey(), entry.getValue());
            } else {
                map2.put(entry.getKey(), entry.getValue());
            }
            count++;
        }

        return Arrays.asList(map1, map2);
    }

    /**
     * 对半拆分集合，奇数的话，前面的集合中多一个
     * 比如：[1, 2, 3, 4, 5]，拆分为：[1, 2, 3]，[4, 5]
     */
    public static <T> List<Collection<T>> splitCollection(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return Arrays.asList(new ArrayList<>(), new ArrayList<>());
        }

        List<T> list1 = new ArrayList<>();
        List<T> list2 = new ArrayList<>();

        int mid = (collection.size() + 1) / 2;
        int count = 0;

        for (T item : collection) {
            if (count < mid) {
                list1.add(item);
            } else {
                list2.add(item);
            }
            count++;
        }

        return Arrays.asList(list1, list2);
    }

    /**
     * 路径拼接，首部不需要/
     */
    public static String path(String... paths) {
        if (paths == null || paths.length == 0) {
            return "";
        }
        return Stream.of(paths)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(path -> !path.isEmpty())
                .collect(Collectors.joining("/"));
    }
}