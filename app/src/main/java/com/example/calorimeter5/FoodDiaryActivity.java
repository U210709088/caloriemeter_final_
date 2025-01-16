package com.example.calorimeter5;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class FoodDiaryActivity extends AppCompatActivity {

    private EditText etFoodName, etCalories, etWeight, etPhotoLink;
    private Button btnSaveFood;
    private ListView lvFoodDiary;

    private ArrayList<HashMap<String, String>> foodList;
    private FoodAdapter adapter;
    private SharedPreferences sharedPreferences, caloriePreferences;

    private static final String PREF_NAME = "FoodDiaryPrefs";
    private static final String CALORIE_PREF_NAME = "CaloriePrefs";
    private static final String KEY_TOTAL_CALORIES = "totalCalories";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_diary);

        // Geri tuşunu etkinleştir
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // View bileşenlerini bağlama
        etFoodName = findViewById(R.id.etFoodName);
        etCalories = findViewById(R.id.etCalories);
        etWeight = findViewById(R.id.etWeight);
        etPhotoLink = findViewById(R.id.etPhotoLink);
        btnSaveFood = findViewById(R.id.btnSaveFood);
        lvFoodDiary = findViewById(R.id.lvFoodDiary);

        // Yemek listesini başlatma
        foodList = new ArrayList<>();
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        caloriePreferences = getSharedPreferences(CALORIE_PREF_NAME, MODE_PRIVATE);

        // SharedPreferences'ten verileri yükleme
        loadFoodData();

        // Adapteri başlat ve ListView'e bağla
        adapter = new FoodAdapter(this, foodList, position -> {
            // Yemek listesinden öğeyi sil ve kaloriyi güncelle
            HashMap<String, String> foodEntry = foodList.get(position);
            int calories = Integer.parseInt(foodEntry.get("calories"));

            // Kaloriyi çıkar ve SharedPreferences'ı güncelle
            updateCalories(-calories);

            foodList.remove(position);
            adapter.notifyDataSetChanged();
            saveFoodData(); // SharedPreferences güncelle

            Toast.makeText(FoodDiaryActivity.this, "Yemek silindi!", Toast.LENGTH_SHORT).show();
        });
        lvFoodDiary.setAdapter(adapter);

        // Kaydet butonuna tıklama olayı
        btnSaveFood.setOnClickListener(v -> {
            String foodName = etFoodName.getText().toString().trim();
            String calories = etCalories.getText().toString().trim();
            String weight = etWeight.getText().toString().trim();
            String photoLink = etPhotoLink.getText().toString().trim();

            if (foodName.isEmpty() || calories.isEmpty() || weight.isEmpty() || photoLink.isEmpty()) {
                Toast.makeText(FoodDiaryActivity.this, "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Yemek bilgilerini listeye ekleme
            HashMap<String, String> foodEntry = new HashMap<>();
            foodEntry.put("name", foodName);
            foodEntry.put("calories", calories);
            foodEntry.put("weight", weight);
            foodEntry.put("photo", photoLink);

            // Kaloriyi ekle ve SharedPreferences'ı güncelle
            updateCalories(Integer.parseInt(calories));

            foodList.add(foodEntry);
            adapter.notifyDataSetChanged();
            saveFoodData(); // Verileri SharedPreferences ile kaydet

            // Alanları temizleme
            etFoodName.setText("");
            etCalories.setText("");
            etWeight.setText("");
            etPhotoLink.setText("");

            Toast.makeText(FoodDiaryActivity.this, "Yemek kaydedildi!", Toast.LENGTH_SHORT).show();
        });
    }

    // Geri tuşunu işleme
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

    // SharedPreferences'ten yemek verilerini yükleme
    private void loadFoodData() {
        Set<String> foodSet = sharedPreferences.getStringSet("FoodList", new HashSet<>());
        foodList.clear();
        for (String foodString : foodSet) {
            String[] parts = foodString.split(";");
            if (parts.length == 4) {
                HashMap<String, String> foodEntry = new HashMap<>();
                foodEntry.put("name", parts[0]);
                foodEntry.put("calories", parts[1]);
                foodEntry.put("weight", parts[2]);
                foodEntry.put("photo", parts[3]);
                foodList.add(foodEntry);
            }
        }
    }

    // SharedPreferences ile yemek verilerini kaydetme
    private void saveFoodData() {
        Set<String> foodSet = new HashSet<>();
        for (HashMap<String, String> foodEntry : foodList) {
            String foodString = foodEntry.get("name") + ";" +
                    foodEntry.get("calories") + ";" +
                    foodEntry.get("weight") + ";" +
                    foodEntry.get("photo");
            foodSet.add(foodString);
        }
        sharedPreferences.edit().putStringSet("FoodList", foodSet).apply();
    }
}
