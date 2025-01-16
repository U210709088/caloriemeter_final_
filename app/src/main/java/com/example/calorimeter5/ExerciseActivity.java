package com.example.calorimeter5;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ExerciseActivity extends AppCompatActivity {

    private EditText etExerciseName, etDuration, etCaloriesBurned;
    private RecyclerView rvExerciseList;

    private ArrayList<HashMap<String, String>> exerciseList;
    private ExerciseAdapter adapter;
    private SharedPreferences sharedPreferences, caloriePreferences;

    private static final String PREF_NAME = "ExercisePrefs";
    private static final String CALORIE_PREF_NAME = "CaloriePrefs";
    private static final String KEY_TOTAL_CALORIES = "totalCalories";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        // Geri tuşunu etkinleştir
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // View bileşenlerini bağlama
        etExerciseName = findViewById(R.id.etExerciseName);
        etDuration = findViewById(R.id.etDuration);
        etCaloriesBurned = findViewById(R.id.etCaloriesBurned);
        rvExerciseList = findViewById(R.id.rvExerciseList);

        // Egzersiz listesini başlatma
        exerciseList = new ArrayList<>();
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        caloriePreferences = getSharedPreferences(CALORIE_PREF_NAME, MODE_PRIVATE);

        // Verileri SharedPreferences'ten yükleme
        loadExerciseData();

        // Adapteri başlat ve RecyclerView'e bağla
        adapter = new ExerciseAdapter(this, exerciseList, position -> {
            // Egzersiz listesinden öğeyi sil ve kaloriyi güncelle
            HashMap<String, String> exercise = exerciseList.get(position);
            int caloriesBurned = Integer.parseInt(exercise.get("calories"));

            // Kaloriyi çıkar ve SharedPreferences'ı güncelle
            updateCalories(caloriesBurned);

            exerciseList.remove(position);
            adapter.notifyDataSetChanged();
            saveExerciseData(); // SharedPreferences güncelle

            Toast.makeText(ExerciseActivity.this, "Egzersiz silindi!", Toast.LENGTH_SHORT).show();
        });

        rvExerciseList.setLayoutManager(new LinearLayoutManager(this));
        rvExerciseList.setAdapter(adapter);

        findViewById(R.id.btnSaveExercise).setOnClickListener(v -> {
            String exerciseName = etExerciseName.getText().toString().trim();
            String duration = etDuration.getText().toString().trim();
            String caloriesBurned = etCaloriesBurned.getText().toString().trim();

            if (exerciseName.isEmpty() || duration.isEmpty() || caloriesBurned.isEmpty()) {
                Toast.makeText(ExerciseActivity.this, "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show();
                return;
            }

            HashMap<String, String> exercise = new HashMap<>();
            exercise.put("name", exerciseName);
            exercise.put("duration", duration);
            exercise.put("calories", caloriesBurned);

            // Kaloriyi çıkar ve SharedPreferences'ı güncelle
            updateCalories(-Integer.parseInt(caloriesBurned));

            exerciseList.add(exercise);
            adapter.notifyDataSetChanged();
            saveExerciseData(); // Verileri SharedPreferences ile kaydet

            etExerciseName.setText("");
            etDuration.setText("");
            etCaloriesBurned.setText("");
            Toast.makeText(ExerciseActivity.this, "Egzersiz kaydedildi!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // Kalori hesaplama ve güncelleme
    private void updateCalories(int calories) {
        int totalCalories = caloriePreferences.getInt(KEY_TOTAL_CALORIES, 0);
        int newTotalCalories = totalCalories + calories;

        caloriePreferences.edit()
                .putInt(KEY_TOTAL_CALORIES, Math.max(newTotalCalories, 0)) // Negatif değeri önlemek için
                .apply();

        // Ana menüye güncelleme bilgisi gönder
        setResult(RESULT_OK);

        Toast.makeText(this, "Güncel Kalori: " + newTotalCalories, Toast.LENGTH_SHORT).show();
    }


    private void loadExerciseData() {
        Set<String> exerciseSet = sharedPreferences.getStringSet("ExerciseList", new HashSet<>());
        exerciseList.clear();
        for (String exerciseString : exerciseSet) {
            String[] parts = exerciseString.split(";");
            if (parts.length == 3) {
                HashMap<String, String> exercise = new HashMap<>();
                exercise.put("name", parts[0]);
                exercise.put("duration", parts[1]);
                exercise.put("calories", parts[2]);
                exerciseList.add(exercise);
            }
        }
    }

    private void saveExerciseData() {
        Set<String> exerciseSet = new HashSet<>();
        for (HashMap<String, String> exercise : exerciseList) {
            String exerciseString = exercise.get("name") + ";" +
                    exercise.get("duration") + ";" +
                    exercise.get("calories");
            exerciseSet.add(exerciseString);
        }
        sharedPreferences.edit().putStringSet("ExerciseList", exerciseSet).apply();
    }
}
