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

public class BerhasilActivity extends AppCompatActivity {

    private static final String TAG = "BerhasilActivity";
    private MusicManager musicManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_berhasil);

        Log.d(TAG, "BerhasilActivity onCreate");

        // Initialize music manager
        musicManager = MusicManager.getInstance(this);

        // Get attempts from intent
        int attempts = getIntent().getIntExtra("attempts", 0);

        TextView attemptsInfo = findViewById(R.id.attemptsInfo);
        attemptsInfo.setText("Dengan " + attempts + " percobaan");

        Button playAgainButton = findViewById(R.id.button2);
        Button shareButton = findViewById(R.id.shareButton);
        TextView trophyIcon = findViewById(R.id.trophyIcon);

        // Play success sound dengan multiple fallback options
        playSuccessSoundWithFallback();

        // Trophy animation
        trophyIcon.setScaleX(0f);
        trophyIcon.setScaleY(0f);
        trophyIcon.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(600)
                .setStartDelay(200)
                .start();

        // Rotation animation for trophy
        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(trophyIcon, "rotation", 0f, 360f);
        rotateAnimator.setDuration(1000);
        rotateAnimator.setStartDelay(800);
        rotateAnimator.start();

        playAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicManager.playButtonSound();

                Intent intent = new Intent(BerhasilActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicManager.playButtonSound();

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                        "Aku berhasil menebak angka dalam " + attempts + " percobaan di game Tebak Angka! 🎯");
                startActivity(Intent.createChooser(shareIntent, "Bagikan hasil"));
            }
        });
    }

    private void playSuccessSoundWithFallback() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Attempting to play success sound...");

                // Coba play success sound custom
                if (tryPlayCustomSuccessSound()) {
                    Log.d(TAG, "Custom success sound played");
                    return;
                }

                // Fallback 1: Coba sistem notification sound
                if (tryPlayNotificationSound()) {
                    Log.d(TAG, "Notification sound played as fallback");
                    return;
                }

                // Fallback 2: Play beep sederhana
                playSimpleBeep();
                Log.d(TAG, "Simple beep played as final fallback");
            }
        }, 300);
    }

    private boolean tryPlayCustomSuccessSound() {
        try {
            MediaPlayer successSound = MediaPlayer.create(this, R.raw.success_sound);
            if (successSound != null) {
                successSound.setVolume(0.8f, 0.8f);
                successSound.start();
                successSound.setOnCompletionListener(MediaPlayer::release);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to play custom success sound: " + e.getMessage());
        }
        return false;
    }

    private boolean tryPlayNotificationSound() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (notification != null) {
                MediaPlayer mp = MediaPlayer.create(this, notification);
                if (mp != null) {
                    mp.setVolume(0.6f, 0.6f);
                    mp.start();
                    mp.setOnCompletionListener(MediaPlayer::release);
                    return true;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to play notification sound: " + e.getMessage());
        }
        return false;
    }

    private void playSimpleBeep() {
        try {
            // Generate simple beep using ToneGenerator if available
            android.media.ToneGenerator toneGen = new android.media.ToneGenerator(
                    android.media.AudioManager.STREAM_MUSIC, 80);
            toneGen.startTone(android.media.ToneGenerator.TONE_PROP_BEEP2, 300);

            new Handler().postDelayed(toneGen::release, 500);
        } catch (Exception e) {
            Log.e(TAG, "Failed to play simple beep: " + e.getMessage());
            // Final fallback: just show a toast
            Toast.makeText(this, "🎉 Selamat! Anda Menang!", Toast.LENGTH_SHORT).show();
        }
    }
}