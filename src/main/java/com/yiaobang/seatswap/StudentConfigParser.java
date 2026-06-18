package com.yiaobang.seatswap;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StudentConfigParser {

    private static final Pattern CONFIG_PATTERN = Pattern.compile("^(R|C)(\\d+)\\.(.+)$", Pattern.CASE_INSENSITIVE);

    /**
     * 解析总学生名单文件
     */
    public static List<String> parseTotalFile(File file) throws Exception {
        List<String> students = new ArrayList<>();
        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        for (String line : lines) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                students.add(trimmed);
            }
        }
        return students;
    }

    /**
     * 解析特殊位置规则文件
     */
    public static ParseSpecialResult parseSpecialFile(File file) throws Exception {
        Set<String> specialStudents = new HashSet<>();
        Map<String, Integer> specialRows = new HashMap<>();
        Map<String, Integer> specialCols = new HashMap<>();

        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        for (String line : lines) {
            Matcher matcher = CONFIG_PATTERN.matcher(line.trim());
            if (matcher.matches()) {
                String type = matcher.group(1).toUpperCase();
                int num = Integer.parseInt(matcher.group(2));
                String cleanName = matcher.group(3).trim();

                specialStudents.add(cleanName);
                if ("R".equals(type)) {
                    specialRows.put(cleanName, num - 1); // 转换为0基索引
                } else {
                    specialCols.put(cleanName, num - 1);
                }
            }
        }
        return new ParseSpecialResult(specialStudents, specialRows, specialCols);
    }

    public static class ParseSpecialResult {
        public final Set<String> specialStudents;
        public final Map<String, Integer> specialRows;
        public final Map<String, Integer> specialCols;

        public ParseSpecialResult(Set<String> specialStudents, Map<String, Integer> specialRows, Map<String, Integer> specialCols) {
            this.specialStudents = specialStudents;
            this.specialRows = specialRows;
            this.specialCols = specialCols;
        }
    }
}