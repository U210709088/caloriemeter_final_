package com.example.calorimeter5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MainMenuActivity extends AppCompatActivity {

    private ProgressBar pbCalorieProgress;
    private TextView tvCalorieProgress, tvMotivation;
    private SharedPreferences caloriePreferences, profilePreferences;

    private static final String CALORIE_PREF_NAME = "CaloriePrefs";
    private static final String PROFILE_PREF_NAME = "ProfilePrefs";
    private static final String KEY_TOTAL_CALORIES = "totalCalories";
    private static final String KEY_LAST_RESET_TIME = "lastResetTime";
    private static final int REQUEST_FOOD_DIARY = 1;
    private static final int REQUEST_EXERCISE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // View bileşenlerini bağlama
        pbCalorieProgress = findViewById(R.id.pbCalorieProgress);
        tvCalorieProgress = findViewById(R.id.tvCalorieProgress);
        tvMotivation = findViewById(R.id.tvMotivation);
        Button btnProfile = findViewById(R.id.btnProfile);
        Button btnFoodDiary = findViewById(R.id.btnFoodDiary);
        Button btnExerciseTracking = findViewById(R.id.btnExerciseTracking);
        Button btnGoal = findViewById(R.id.btnGoal);

        // SharedPreferences tanımlama
        caloriePreferences = getSharedPreferences(CALORIE_PREF_NAME, MODE_PRIVATE);
        profilePreferences = getSharedPreferences(PROFILE_PREF_NAME, MODE_PRIVATE);

        // Günlük kalori hedefini profil bilgilerinden al
        int dailyCalorieGoal = calculateDailyCalorieGoal();

        // Günlük sıfırlama kontrolü
        resetCaloriesIfNeeded();

        // Toplam kaloriyi al ve ilerleme çubuğunu güncelle
        int totalCalories = caloriePreferences.getInt(KEY_TOTAL_CALORIES, 0);
        updateProgressBar(totalCalories, dailyCalorieGoal);

        // Rastgele motivasyon cümlesi seç
        loadRandomMotivation();

        // Profil sayfasına yönlendirme
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // Yemek günlüğüne yönlendirme
        btnFoodDiary.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, FoodDiaryActivity.class);
            startActivityForResult(intent, REQUEST_FOOD_DIARY);
        });

        // Egzersiz takibine yönlendirme
        btnExerciseTracking.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, ExerciseActivity.class);
            startActivityForResult(intent, REQUEST_EXERCISE);
        });

        // Hedef belirleme sayfasına yönlendirme
        btnGoal.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, GoalActivity.class);
            startActivity(intent);
        });
    }

    private int calculateDailyCalorieGoal() {
        try {
            // Profil bilgilerini al
            String weightString = profilePreferences.getString("weight", "70");
            String heightString = profilePreferences.getString("height", "170");
            String ageString = profilePreferences.getString("age", "25");
            String exerciseFrequency = profilePreferences.getString("exerciseFrequency", "hareketsiz");

            // Bilgiler null veya boş ise varsayılan değerlere geç
            double weight = weightString.isEmpty() ? 70 : Double.parseDouble(weightString);
            double height = heightString.isEmpty() ? 170 : Double.parseDouble(heightString);
            int age = ageString.isEmpty() ? 25 : Integer.parseInt(ageString);

            // Temel metabolizma hızını (BMR) hesapla (erkek için)
            double bmr = 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age);

            // Aktivite çarpanını uygula
            double activityMultiplier = getActivityMultiplier(exerciseFrequency);

            return (int) (bmr * activityMultiplier);

        } catch (Exception e) {
            e.printStackTrace();
            // Hata durumunda varsayılan bir değer döndür
            return 2000;
        }
    }

    private double getActivityMultiplier(String exerciseFrequency) {
        if (exerciseFrequency == null) return 1.2; // Varsayılan

        if (exerciseFrequency.toLowerCase().contains("hareketsiz")) return 1.2;
        if (exerciseFrequency.toLowerCase().contains("hafif")) return 1.375;
        if (exerciseFrequency.toLowerCase().contains("orta")) return 1.55;
        if (exerciseFrequency.toLowerCase().contains("çok")) return 1.725;
        return 1.2; // Varsayılan
    }

    private void updateProgressBar(int totalCalories, int calorieGoal) {
        pbCalorieProgress.setMax(calorieGoal);
        pbCalorieProgress.setProgress(totalCalories);

        // Kalori ilerleme metni güncelle
        if (totalCalories >= calorieGoal) {
            tvCalorieProgress.setText("Tebrikler! Günlük hedefinizi tamamladınız!");
        } else {
            tvCalorieProgress.setText("Günlük Kalori Hedefi: " + totalCalories + "/" + calorieGoal + " kcal");
        }
    }

    private void resetCaloriesIfNeeded() {
        long lastResetTime = caloriePreferences.getLong(KEY_LAST_RESET_TIME, 0);
        long currentTime = System.currentTimeMillis();

        // Eğer 24 saat geçtiyse kalori değerlerini sıfırla
        if ((currentTime - lastResetTime) >= 86400000) { // 24 saat
            caloriePreferences.edit()
                    .putInt(KEY_TOTAL_CALORIES, 0)
                    .putLong(KEY_LAST_RESET_TIME, currentTime)
                    .apply();
        }
    }

    private void loadRandomMotivation() {
        SharedPreferences motivationPrefs = getSharedPreferences("MotivationPrefs", MODE_PRIVATE);
        Set<String> savedSet = motivationPrefs.getStringSet("Motivations", new HashSet<>());

        ArrayList<String> motivationList = new ArrayList<>(savedSet);
        if (!motivationList.isEmpty()) {
            Random random = new Random();
            String randomMotivation = motivationList.get(random.nextInt(motivationList.size()));
            tvMotivation.setText(randomMotivation);
        } else {
            tvMotivation.setText("Stay focused! You're making progress towards your goals every day!");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // Yemek veya egzersiz sonrası barı güncelle
            int totalCalories = caloriePreferences.getInt(KEY_TOTAL_CALORIES, 0);
            int dailyCalorieGoal = calculateDailyCalorieGoal();
            updateProgressBar(totalCalories, dailyCalorieGoal);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
