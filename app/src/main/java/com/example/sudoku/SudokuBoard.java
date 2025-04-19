package com.example.sudoku;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class SudokuBoard extends View {

    private final int boardColor;
    private final int cellHighlightColor;
    private final int rowColumnHighlightColor;
    private final Paint boardColorPaint = new Paint();
    private final Paint cellHighlightColorPaint = new Paint();
    private final Paint rowColumnHighlightColorPaint = new Paint();
    private final Paint letterPaint = new Paint();
    private final Rect letterPaintBounds = new Rect();

    private int cellSize;
    private final Solver solver;

    private boolean isGameCompleted = false;

    public SudokuBoard(Context context, AttributeSet attrs) {
        super(context, attrs);

        solver = new Solver();

        boardColor = getResources().getColor(android.R.color.black);
        cellHighlightColor = getResources().getColor(android.R.color.holo_blue_light);
        rowColumnHighlightColor = 0x4087CEFA;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int sizePixels = Math.min(getMeasuredWidth(), getMeasuredHeight());
        cellSize = sizePixels / 9;

        setMeasuredDimension(sizePixels, sizePixels);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        boardColorPaint.setStyle(Paint.Style.STROKE);
        boardColorPaint.setStrokeWidth(4);
        boardColorPaint.setColor(boardColor);

        cellHighlightColorPaint.setStyle(Paint.Style.FILL);
        cellHighlightColorPaint.setColor(cellHighlightColor);

        rowColumnHighlightColorPaint.setStyle(Paint.Style.FILL);
        rowColumnHighlightColorPaint.setColor(rowColumnHighlightColor);

        letterPaint.setStyle(Paint.Style.FILL);
        letterPaint.setTextSize(cellSize);

        drawCells(canvas);
        drawLines(canvas);
        drawNumbers(canvas);
    }

    private void drawCells(Canvas canvas) {
        int selectedRow = solver.getSelectedRow();
        int selectedColumn = solver.getSelectedColumn();

        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (r == selectedRow && c == selectedColumn) {
                    canvas.drawRect(c * cellSize, r * cellSize, (c + 1) * cellSize, (r + 1) * cellSize, cellHighlightColorPaint);
                } else if (r == selectedRow || c == selectedColumn) {
                    canvas.drawRect(c * cellSize, r * cellSize, (c + 1) * cellSize, (r + 1) * cellSize, rowColumnHighlightColorPaint);
                }
            }
        }
    }

    private void drawLines(Canvas canvas) {
        for (int i = 0; i < 10; i++) {
            if (i % 3 == 0) {
                boardColorPaint.setStrokeWidth(8);
            } else {
                boardColorPaint.setStrokeWidth(4);
            }
            canvas.drawLine(0, i * cellSize, getWidth(), i * cellSize, boardColorPaint);
            canvas.drawLine(i * cellSize, 0, i * cellSize, getHeight(), boardColorPaint);
        }
    }

    private void drawNumbers(Canvas canvas) {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (solver.getBoard()[r][c] != 0) {
                    String text = Integer.toString(solver.getBoard()[r][c]);

                    letterPaint.getTextBounds(text, 0, text.length(), letterPaintBounds);
                    float textWidth = letterPaint.measureText(text);
                    float textHeight = letterPaintBounds.height();

                    float x = c * cellSize + (cellSize - textWidth) / 2;
                    float y = r * cellSize + cellSize / 2 + textHeight / 2;

                    if (solver.isGenerated(r, c)) {
                        letterPaint.setColor(getResources().getColor(android.R.color.black));
                    } else if (!solver.isCorrect(r, c)) {
                        letterPaint.setColor(getResources().getColor(android.R.color.holo_red_light));
                    } else {
                        letterPaint.setColor(getResources().getColor(R.color.gray));
                    }

                    canvas.drawText(text, x, y, letterPaint);
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isGameCompleted) {
            return false; // Игра завершена, больше не обрабатываем нажатия
        }

        float x = event.getX();
        float y = event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int selectedRow = (int) (y / cellSize);
            int selectedColumn = (int) (x / cellSize);

            if (selectedRow >= 0 && selectedRow < 9 && selectedColumn >= 0 && selectedColumn < 9) {
                if (!solver.isLocked(selectedRow, selectedColumn)) {
                    solver.setSelectedRow(selectedRow);
                    solver.setSelectedColumn(selectedColumn);
                    invalidate(); // Перерисовываем доску

                    // Проверяем, решена ли судоку
                    checkForWin();
                } else {
                    Toast.makeText(getContext(), "Эта ячейка уже заблокирована!", Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        }
        return false;
    }

    public void checkForWin() {
        if (solver.isSolved()) { // Вызываем метод isSolved
            isGameCompleted = true;

            Toast.makeText(getContext(), "Поздравляем! Вы решили судоку!", Toast.LENGTH_LONG).show();
            highlightAllCells();
            lockBoard();
        }
    }

    private void highlightAllCells() {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                solver.lockCell(r, c); // Блокируем все ячейки
            }
        }
        invalidate(); // Перерисовываем доску
    }

    private void lockBoard() {
        setOnTouchListener(null); // Отключаем обработку касаний
    }

    public Solver getSolver() {
        return solver;
    }
}