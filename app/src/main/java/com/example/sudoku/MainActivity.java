package com.example.sudoku;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private SudokuBoard gameBoard;
    private Solver gameBoardSolver;

    private ImageView heart1, heart2, heart3; // Сердечки
    private FrameLayout winFrame; // FrameLayout для победы
    private FrameLayout loseFrame; // FrameLayout для проигрыша

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация доски и решателя
        gameBoard = findViewById(R.id.SudokuBoard);
        gameBoardSolver = gameBoard.getSolver();

        // Инициализация сердечек
        heart1 = findViewById(R.id.heart1);
        heart2 = findViewById(R.id.heart2);
        heart3 = findViewById(R.id.heart3);

        // Инициализация FrameLayout для диалоговых окон
        winFrame = findViewById(R.id.winFrame);
        loseFrame = findViewById(R.id.loseFrame);

        // Получаем уровень сложности из Intent
        String difficulty = getIntent().getStringExtra("difficulty");

        // Генерация случайной доски с учетом уровня сложности
        generateNewBoard(difficulty); // Метод для генерации новой доски

        // Обработчик кнопки "Вернуться в главное меню"
        findViewById(R.id.backToMenuButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(intent);
                finish(); // Закрываем текущую активность
            }
        });

        // Обработчик кнопки "Заново" в фрейме проигрыша
        findViewById(R.id.replayButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartGame(); // Перезапускаем игру
            }
        });
    }

    /**
     * Универсальный метод для обработки нажатий на кнопки с числами
     */
    public void onNumberButtonClick(View view) {
        // Получаем текст с кнопки (число)
        String buttonText = ((Button) view).getText().toString();
        int number = Integer.parseInt(buttonText);

        // Проверяем, выбрана ли ячейка
        if (gameBoardSolver.getSelectedRow() != -1 && gameBoardSolver.getSelectedColumn() != -1) {
            // Устанавливаем число в выбранную ячейку
            boolean isCorrect = gameBoardSolver.setNumberPos(number);
            gameBoard.invalidate(); // Перерисовываем доску

            // Обновляем сердечки
            updateHearts();

            // Проверяем количество ошибок
            if (!isCorrect && gameBoardSolver.getErrorCount() >= 3) {
                showLoseFrame(); // Показываем диалоговое окно проигрыша
                gameBoardSolver.resetBoard(); // Сбрасываем доску
                resetHearts();
            } else if (!isCorrect) {
                //   Toast.makeText(this, "Неверное число! Ошибок: " + gameBoardSolver.getErrorCount(), Toast.LENGTH_SHORT).show();
            }

            // Проверяем, все ли вхождения цифры правильные
            if (gameBoardSolver.isNumberFullyCorrect(number)) {
                hideNumberButton(number); // Скрываем кнопку с этой цифрой
            }
        } else {
            Toast.makeText(this, "Пожалуйста, выберите ячейку", Toast.LENGTH_SHORT).show();
        }

        // Проверяем, решена ли судоку
        if (gameBoardSolver.isSolved()) {
            showWinFrame(); // Показываем диалоговое окно победы
        }
    }

    /**
     * Скрывает кнопку с заданной цифрой.
     */
    private void hideNumberButton(int number) {
        GridLayout gridLayout = findViewById(R.id.numberButtonsLayout); // GridLayout с кнопками

        if (gridLayout == null) {
            System.out.println("GridLayout не найден!"); // Отладочное сообщение
            return;
        }

        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View child = gridLayout.getChildAt(i);
            if (child instanceof Button) {
                Button button = (Button) child;
                if (button.getText().toString().equals(String.valueOf(number))) {
                    button.setVisibility(View.INVISIBLE); // Скрываем кнопку
                }
            }
        }
    }

    /**
     * Показать FrameLayout с сообщением о победе
     */
    private void showWinFrame() {
        winFrame.setVisibility(View.VISIBLE); // Делаем видимым FrameLayout
    }

    /**
     * Скрыть FrameLayout победы
     */
    private void hideWinFrame() {
        winFrame.setVisibility(View.GONE); // Скрываем FrameLayout
    }

    /**
     * Показать FrameLayout с сообщением о проигрыше
     */
    private void showLoseFrame() {
        loseFrame.setVisibility(View.VISIBLE); // Делаем видимым FrameLayout
    }

    /**
     * Скрыть FrameLayout проигрыша
     */
    private void hideLoseFrame() {
        loseFrame.setVisibility(View.GONE); // Скрываем FrameLayout
    }

    /**
     * Обновление состояния сердечек
     */
    private void updateHearts() {
        int livesRemaining = 3 - gameBoardSolver.getErrorCount();

        heart1.setVisibility(livesRemaining > 0 ? View.VISIBLE : View.INVISIBLE);
        heart2.setVisibility(livesRemaining > 1 ? View.VISIBLE : View.INVISIBLE);
        heart3.setVisibility(livesRemaining > 2 ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * Сброс сердечек
     */
    private void resetHearts() {
        heart1.setVisibility(View.VISIBLE);
        heart2.setVisibility(View.VISIBLE);
        heart3.setVisibility(View.VISIBLE);
    }

    /**
     * Генерация новой доски
     */
    private void generateNewBoard(String difficulty) {
        gameBoardSolver.generateBoard(difficulty); // Генерируем новую доску
        gameBoard.invalidate(); // Перерисовываем доску
        resetHearts(); // Сбрасываем сердечки
        gameBoardSolver.resetErrorCount(); // Сбрасываем счетчик ошибок
        unhideAllNumberButtons(); // Показываем все кнопки с числами
    }

    /**
     * Показать все кнопки с числами
     */
    private void unhideAllNumberButtons() {
        GridLayout gridLayout = findViewById(R.id.numberButtonsLayout); // GridLayout с кнопками

        if (gridLayout == null) {
            System.out.println("GridLayout не найден!"); // Отладочное сообщение
            return;
        }

        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View child = gridLayout.getChildAt(i);
            if (child instanceof Button) {
                Button button = (Button) child;
                button.setVisibility(View.VISIBLE); // Показываем кнопку
            }
        }
    }

    /**
     * Перезапуск игры
     */
    private void restartGame() {
        hideLoseFrame(); // Скрываем фрейм проигрыша
        gameBoardSolver.resetBoard(); // Очищаем редактируемые ячейки
        resetHearts(); // Сбрасываем сердечки
        gameBoardSolver.resetErrorCount(); // Сбрасываем счетчик ошибок
        unhideAllNumberButtons(); // Показываем все кнопки с числами
        gameBoard.invalidate(); // Перерисовываем доску
    }
}