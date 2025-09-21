package com.example.tebakangka;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    int angkaTebakan, attemptTebakan = 1, sisaTebakan = 10;
    private MusicManager musicManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize music manager
        musicManager = MusicManager.getInstance(this);

        angkaTebakan = (int) (Math.random() * 100) + 1;

        Button tombolSubmit = findViewById(R.id.button);
        EditText inputAngka = findViewById(R.id.inputAngka);
        TextView textSisaTebakan = findViewById(R.id.textView4);
        View cardContainer = findViewById(R.id.cardContainer);

        // Animasi masuk untuk card
        cardContainer.setAlpha(0f);
        cardContainer.setTranslationY(100f);
        cardContainer.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(800)
                .start();

        tombolSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Play button sound
                musicManager.playButtonSound();

                String angka = inputAngka.getText().toString();

                // Animasi button press
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(tombolSubmit, "scaleX", 1f, 0.9f, 1f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(tombolSubmit, "scaleY", 1f, 0.9f, 1f);
                scaleX.setDuration(150);
                scaleY.setDuration(150);
                scaleX.start();
                scaleY.start();

                if (angka.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Masukkan angka terlebih dahulu!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int angkaInput = Integer.parseInt(angka);
                attemptTebakan += 1;
                sisaTebakan -= 1;

                if (attemptTebakan <= 10) {
                    if (angkaInput > angkaTebakan) {
                        Toast.makeText(MainActivity.this, "Terlalu Besar! 📈", Toast.LENGTH_SHORT).show();
                    } else if (angkaInput < angkaTebakan) {
                        Toast.makeText(MainActivity.this, "Terlalu Kecil! 📉", Toast.LENGTH_SHORT).show();
                    } else if (angkaInput == angkaTebakan) {
                        // STOP background music sebelum pindah activity
                        musicManager.pauseBackgroundMusic();

                        // Pindah ke BerhasilActivity
                        Intent intent = new Intent(MainActivity.this, BerhasilActivity.class);
                        intent.putExtra("attempts", attemptTebakan - 1);
                        startActivity(intent);
                        finish();
                        return;
                    }
                    inputAngka.setText("");
                    textSisaTebakan.setText("Sisa Tebakan: " + String.valueOf(sisaTebakan));

                    // Animasi untuk sisa tebakan
                    textSisaTebakan.animate()
                            .scaleX(1.2f)
                            .scaleY(1.2f)
                            .setDuration(100)
                            .withEndAction(() -> {
                                textSisaTebakan.animate()
                                        .scaleX(1f)
                                        .scaleY(1f)
                                        .setDuration(100)
                                        .start();
                            })
                            .start();
                } else {
                    // STOP background music sebelum pindah activity
                    musicManager.pauseBackgroundMusic();

                    Intent intent = new Intent(MainActivity.this, GagalActivity.class);
                    intent.putExtra("correctAnswer", angkaTebakan);
                    startActivity(intent);
                    finish();
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Mulai background music ketika activity resume
        musicManager.playBackgroundMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause music ketika activity tidak terlihat
        musicManager.pauseBackgroundMusic();
    }
}