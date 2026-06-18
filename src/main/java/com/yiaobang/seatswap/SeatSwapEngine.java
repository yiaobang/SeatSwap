package com.yiaobang.seatswap;

import java.security.SecureRandom;
import java.util.*;

public class SeatSwapEngine {

    private final SecureRandom random = new SecureRandom();

    /**
     * 核心排座算法（强规则驱动 + 加密级洗牌）
     * @return EngineResult 包含排座矩阵和失败的特殊学生列表
     */
    public EngineResult arrangeSeats(int rows, int cols, List<String> allStudents, 
                                     Set<String> specialStudents, 
                                     Map<String, Integer> specialRows, 
                                     Map<String, Integer> specialCols) {
        
        String[][] layout = new String[rows][cols];
        List<String> remainingStudents = new ArrayList<>(allStudents);
        List<String> failedSpecials = new ArrayList<>();

        // 1. 将特殊请求列表进行安全洗牌，确保冲突时的第一层概率公平性
        List<String> shuffledSpecials = new ArrayList<>(specialStudents);
        Collections.shuffle(shuffledSpecials, random);

        for (String student : shuffledSpecials) {
            if (!allStudents.contains(student)) continue;
            
            Integer reqRow = specialRows.get(student);
            Integer reqCol = specialCols.get(student);
            boolean placed = false;

            // 处理行锁定 (R)
            if (reqRow != null && reqRow >= 0 && reqRow < rows) {
                List<Integer> availableCols = getEmptyCols(layout, cols, reqRow);
                if (!availableCols.isEmpty()) {
                    layout[reqRow][availableCols.get(random.nextInt(availableCols.size()))] = student;
                    placed = true;
                }
            }
            // 处理列锁定 (C)
            else if (reqCol != null && reqCol >= 0 && reqCol < cols) {
                List<Integer> availableRows = getEmptyRows(layout, rows, reqCol);
                if (!availableRows.isEmpty()) {
                    layout[availableRows.get(random.nextInt(availableRows.size()))][reqCol] = student;
                    placed = true;
                }
            }

            if (placed) {
                remainingStudents.remove(student);
            } else {
                failedSpecials.add(student);
            }
        }

        // 2. 剩余学生随机洗牌并填补空位
        Collections.shuffle(remainingStudents, random);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (layout[r][c] == null && !remainingStudents.isEmpty()) {
                    layout[r][c] = remainingStudents.remove(0);
                }
            }
        }

        return new EngineResult(layout, failedSpecials);
    }

    private List<Integer> getEmptyCols(String[][] layout, int maxCols, int r) {
        List<Integer> list = new ArrayList<>();
        for (int c = 0; c < maxCols; c++) if (layout[r][c] == null) list.add(c);
        return list;
    }

    private List<Integer> getEmptyRows(String[][] layout, int maxRows, int c) {
        List<Integer> list = new ArrayList<>();
        for (int r = 0; r < maxRows; r++) if (layout[r][c] == null) list.add(r);
        return list;
    }

    // 用于封装返回结果的静态内部类
    public static class EngineResult {
        public final String[][] layout;
        public final List<String> failedSpecials;

        public EngineResult(String[][] layout, List<String> failedSpecials) {
            this.layout = layout;
            this.failedSpecials = failedSpecials;
        }
    }
}