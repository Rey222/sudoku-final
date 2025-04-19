package com.example.sudoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Solver {
    private int[][] board;
    private final int[][] initialBoard;
    private boolean[][] isGenerated; // Массив для отметки сгенерированных цифр
    private boolean[][] isLocked; // Новый массив для заблокированных ячеек
    private int selectedRow = -1;
    private int selectedColumn = -1;
    private int errorCount = 0; // Счетчик ошибок
    private final int maxLives = 3; // Максимальное количество жизней

    public Solver() {
        board = new int[9][9];
        initialBoard = new int[9][9];
        isGenerated = new boolean[9][9]; // Инициализация массива
        isLocked = new boolean[9][9]; // Инициализация массива для заблокированных ячеек
    }

    /**
     * Генерация случайной доски судоку
     */
    public void generateBoard(String difficulty) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                board[i][j] = 0;
                isGenerated[i][j] = false;
            }
        }

        if (!solveHelper(0, 0)) {
            throw new RuntimeException("Не удалось сгенерировать доску.");
        }

        for (int i = 0; i < 9; i++) {
            System.arraycopy(board[i], 0, initialBoard[i], 0, 9);
            for (int j = 0; j < 9; j++) {
                if (board[i][j] != 0) {
                    isGenerated[i][j] = true;
                }
            }
        }

        removeNumbers(difficulty);
    }

    private void removeNumbers(String difficulty) {
        int cellsToRemove = 0;

        switch (difficulty) {
            case "Легкий":
                cellsToRemove = 20;
                break;
            case "Средний":
                cellsToRemove = 35;
                break;
            case "Сложный":
                cellsToRemove = 50;
                break;
            default:
                cellsToRemove = 40;
        }

        Random random = new Random();
        while (cellsToRemove > 0) {
            int row = random.nextInt(9);
            int col = random.nextInt(9);

            if (!isGenerated[row][col]) {
                continue;
            }

            if (board[row][col] != 0) {
                board[row][col] = 0;
                isGenerated[row][col] = false;
                cellsToRemove--;
            }
        }
    }

    private boolean solveHelper(int row, int col) {
        if (row == 9) {
            return true;
        }

        if (col == 9) {
            return solveHelper(row + 1, 0);
        }

        if (board[row][col] != 0) {
            return solveHelper(row, col + 1);
        }

        ArrayList<Integer> numbers = new ArrayList<>();
        for (int num = 1; num <= 9; num++) {
            numbers.add(num);
        }
        Collections.shuffle(numbers);

        for (int num : numbers) {
            if (isValid(row, col, num)) {
                board[row][col] = num;

                if (solveHelper(row, col + 1)) {
                    return true;
                }

                board[row][col] = 0;
            }
        }

        return false;
    }

    private boolean isValid(int row, int col, int num) {
        for (int d = 0; d < 9; d++) {
            if (board[row][d] == num) return false;
        }

        for (int r = 0; r < 9; r++) {
            if (board[r][col] == num) return false;
        }

        int boxRowStart = row - row % 3;
        int boxColStart = col - col % 3;
        for (int r = boxRowStart; r < boxRowStart + 3; r++) {
            for (int d = boxColStart; d < boxColStart + 3; d++) {
                if (board[r][d] == num) return false;
            }
        }

        return true;
    }

    public boolean isCorrect(int row, int col) {
        return board[row][col] == initialBoard[row][col];
    }

    public int[][] getBoard() {
        return board;
    }

    public int[][] getSolution() {
        return initialBoard;
    }

    public void setInitialBoard(int[][] initialBoard) {
        for (int i = 0; i < 9; i++) {
            System.arraycopy(initialBoard[i], 0, this.initialBoard[i], 0, 9);
            System.arraycopy(initialBoard[i], 0, board[i], 0, 9);
        }
    }

    public boolean isSolved() {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (board[r][c] == 0 || !isCorrect(r, c)) {
                    System.out.println("Ошибка в ячейке: (" + r + ", " + c + ")");
                    return false;
                }
            }
        }

        System.out.println("Судоку решили");
        return true;
    }

    public boolean isNumberFullyCorrect(int number) {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (initialBoard[r][c] == number && (board[r][c] != number || !isCorrect(r, c))) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean setNumberPos(int number) {
        if (selectedRow != -1 && selectedColumn != -1 && isEditable(selectedRow, selectedColumn)) {
            if (board[selectedRow][selectedColumn] == number) {
                board[selectedRow][selectedColumn] = 0;
            } else {
                board[selectedRow][selectedColumn] = number;

                if (isCorrect(selectedRow, selectedColumn)) {
                    lockCell(selectedRow, selectedColumn);
                } else {
                    incrementErrorCount();
                    return false;
                }
            }
        }
        return true;
    }

    private void incrementErrorCount() {
        errorCount++;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public int getRemainingLives() {
        return maxLives - errorCount;
    }

    public void resetErrorCount() {
        errorCount = 0; // Сброс счетчика ошибок
    }


    public void lockCell(int row, int col) {
        isLocked[row][col] = true;
    }

    public boolean isEditable(int row, int column) {
        return !isGenerated[row][column] && !isLocked[row][column];
    }

    public boolean isLocked(int row, int col) {
        return isLocked[row][col];
    }

    public boolean solve() {
        return solveHelper(0, 0);
    }

    public void resetBoard() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!isGenerated[i][j]) { // Очищаем только редактируемые ячейки
                    board[i][j] = 0;
                    isLocked[i][j] = false; // Снимаем блокировку
                }
            }
        }
        resetErrorCount(); // Сбрасываем счетчик ошибок
    }

    public void setSelectedRow(int row) {
        this.selectedRow = row;
    }

    public void setSelectedColumn(int column) {
        this.selectedColumn = column;
    }

    public int getSelectedRow() {
        return selectedRow;
    }

    public int getSelectedColumn() {
        return selectedColumn;
    }

    public boolean isGenerated(int row, int col) {
        return isGenerated[row][col];
    }
}