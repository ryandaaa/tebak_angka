package com.example.tebakangka;

import android.content.Context;
import android.media.MediaPlayer;

public class MusicManager {
    private static MusicManager instance;
    private MediaPlayer backgroundMusic;
    private MediaPlayer successSound;
    private MediaPlayer failSound;
    private MediaPlayer buttonSound;
    private Context context;
    private boolean isMusicEnabled = true;

    private MusicManager(Context context) {
        this.context = context;
    }

    public static MusicManager getInstance(Context context) {
        if (instance == null) {
            instance = new MusicManager(context);
        }
        return instance;
    }

    // Background music untuk gameplay
    public void playBackgroundMusic() {
        if (!isMusicEnabled) return;

        try {
            if (backgroundMusic == null) {
                backgroundMusic = MediaPlayer.create(context, R.raw.background_music);
                if (backgroundMusic != null) {
                    backgroundMusic.setLooping(true);
                    backgroundMusic.setVolume(0.3f, 0.3f);
                }
            }
            if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
                backgroundMusic.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pauseBackgroundMusic() {
        try {
            if (backgroundMusic != null && backgroundMusic.isPlaying()) {
                backgroundMusic.pause();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopBackgroundMusic() {
        try {
            if (backgroundMusic != null) {
                if (backgroundMusic.isPlaying()) {
                    backgroundMusic.stop();
                }
                backgroundMusic.release();
                backgroundMusic = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // FIXED: Sound effect untuk sukses - tidak langsung di-release
    public void playSuccessSound() {
        if (!isMusicEnabled) return;

        try {
            // Stop previous success sound if playing
            if (successSound != null) {
                if (successSound.isPlaying()) {
                    successSound.stop();
                }
                successSound.release();
            }

            successSound = MediaPlayer.create(context, R.raw.success_sound);
            if (successSound != null) {
                successSound.setVolume(0.7f, 0.7f);
                successSound.start();

                // Release setelah selesai dimainkan
                successSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        try {
                            mp.release();
                            successSound = null;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // FIXED: Sound effect untuk gagal - tidak langsung di-release
    public void playFailSound() {
        if (!isMusicEnabled) return;

        try {
            // Stop previous fail sound if playing
            if (failSound != null) {
                if (failSound.isPlaying()) {
                    failSound.stop();
                }
                failSound.release();
            }

            failSound = MediaPlayer.create(context, R.raw.fail_sound);
            if (failSound != null) {
                failSound.setVolume(0.7f, 0.7f);
                failSound.start();

                // Release setelah selesai dimainkan
                failSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        try {
                            mp.release();
                            failSound = null;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Sound effect untuk button click
    public void playButtonSound() {
        if (!isMusicEnabled) return;

        try {
            // Create new instance setiap kali untuk button click
            MediaPlayer buttonClick = MediaPlayer.create(context, R.raw.button_click);
            if (buttonClick != null) {
                buttonClick.setVolume(0.5f, 0.5f);
                buttonClick.start();
                buttonClick.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        try {
                            mp.release();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Toggle musik on/off
    public void setMusicEnabled(boolean enabled) {
        this.isMusicEnabled = enabled;
        if (!enabled) {
            pauseBackgroundMusic();
        }
    }

    public boolean isMusicEnabled() {
        return isMusicEnabled;
    }

    // Cleanup ketika aplikasi ditutup
    public void releaseAll() {
        try {
            stopBackgroundMusic();

            if (successSound != null) {
                if (successSound.isPlaying()) {
                    successSound.stop();
                }
                successSound.release();
                successSound = null;
            }

            if (failSound != null) {
                if (failSound.isPlaying()) {
                    failSound.stop();
                }
                failSound.release();
                failSound = null;
            }

            if (buttonSound != null) {
                if (buttonSound.isPlaying()) {
                    buttonSound.stop();
                }
                buttonSound.release();
                buttonSound = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}