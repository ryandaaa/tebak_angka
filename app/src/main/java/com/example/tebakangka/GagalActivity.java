package com.example.tebakangka;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class GagalActivity extends AppCompatActivity {

    private static final String TAG = "GagalActivity";
    private MusicManager musicManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gagal);

        Log.d(TAG, "GagalActivity onCreate");

        // Initialize music manager
        musicManager = MusicManager.getInstance(this);

        // Get correct answer from intent
        int correctAnswer = getIntent().getIntExtra("correctAnswer", 0);

        TextView correctAnswerText = findViewById(R.id.correctAnswerText);
        correctAnswerText.setText("Angka yang benar adalah: " + correctAnswer);

        Button tryAgainButton = findViewById(R.id.tryAgainButton);
        Button backButton = findViewById(R.id.backButton);
        TextView sadIcon = findViewById(R.id.sadIcon);

        // Play fail sound dengan fallback
        playFailSoundWithFallback();

        // Sad icon animation
        sadIcon.setAlpha(0f);
        sadIcon.setScaleX(0.5f);
        sadIcon.setScaleY(0.5f);
        sadIcon.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(800)
                .start();

        // Shake animation for sad icon
        ObjectAnimator shakeAnimator = ObjectAnimator.ofFloat(sadIcon, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f);
        shakeAnimator.setDuration(1000);
        shakeAnimator.setStartDelay(1000);
        shakeAnimator.start();

        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicManager.playButtonSound();

                Intent intent = new Intent(GagalActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicManager.playButtonSound();

                Intent intent = new Intent(GagalActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void playFailSoundWithFallback() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Attempting to play fail sound...");

                // Coba play fail sound custom
                if (tryPlayCustomFailSound()) {
                    Log.d(TAG, "Custom fail sound played");
                    return;
                }

                // Fallback: Play error sound sistem
                if (tryPlayErrorSound()) {
                    Log.d(TAG, "System error sound played as fallback");
                    return;
                }

                // Final fallback: Show toast
                Toast.makeText(GagalActivity.this, "😞 Game Over!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Toast shown as final fallback");
            }
        }, 300);
    }

    private boolean tryPlayCustomFailSound() {
        try {
            MediaPlayer failSound = MediaPlayer.create(this, R.raw.fail_sound);
            if (failSound != null) {
                failSound.setVolume(0.8f, 0.8f);
                failSound.start();
                failSound.setOnCompletionListener(MediaPlayer::release);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to play custom fail sound: " + e.getMessage());
        }
        return false;
    }

    private boolean tryPlayErrorSound() {
        try {
            // Generate error tone
            android.media.ToneGenerator toneGen = new android.media.ToneGenerator(
                    android.media.AudioManager.STREAM_MUSIC, 80);
            toneGen.startTone(android.media.ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 500);

            new Handler().postDelayed(toneGen::release, 700);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Failed to play error sound: " + e.getMessage());
        }
        return false;
    }
}