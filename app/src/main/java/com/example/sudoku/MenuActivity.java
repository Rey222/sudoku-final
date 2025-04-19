package com.example.sudoku;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    private Spinner difficultySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Инициализация элементов интерфейса
        Button playButton = findViewById(R.id.playButton);
        difficultySpinner = findViewById(R.id.difficultySpinner);
        Button rulesButton = findViewById(R.id.rulesButton);

        // Обработка нажатия на кнопку "Играть"
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Получаем выбранный уровень сложности
                String difficulty = difficultySpinner.getSelectedItem().toString();

                // Переход к игре
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                intent.putExtra("difficulty", difficulty); // Передаем уровень сложности
                startActivity(intent);
            }
        });

        // Обработка нажатия на кнопку "Правила"
        rulesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRulesDialog(); // Показываем диалог с правилами
            }
        });
    }

    /**
     * Показывает диалоговое окно с правилами судоку
     */
    private void showRulesDialog() {
        // Создаем AlertDialog
        new AlertDialog.Builder(this)
                .setTitle("Правила игры в Судоку") // Заголовок окна
                .setMessage("Судоку — это головоломка с числами.\n\n" +
                        "Цель игры: заполнить пустые ячейки цифрами от 1 до 9 так, чтобы:\n" +
                        "1. В каждой строке были все цифры от 1 до 9 без повторений.\n" +
                        "2. В каждом столбце были все цифры от 1 до 9 без повторений.\n" +
                        "3. В каждом маленьком квадрате 3x3 были все цифры от 1 до 9 без повторений.\n\n" +
                        "Удачи!")
                .setPositiveButton("Закрыть", null) // Кнопка для закрытия окна
                .show(); // Показываем диалог
    }
}