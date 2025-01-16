package com.example.calorimeter5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etName, etAge, etWeight, etHeight;
    private Spinner spExerciseFrequency;
    private Button btnSaveProfile, btnChoosePhoto, btnCalculateCalories;
    private ImageView ivProfilePhoto;
    private TextView tvDailyCalories;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Geri tuşunu etkinleştir
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // SharedPreferences tanımla
        sharedPreferences = getSharedPreferences("ProfilePrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // View bileşenlerini bağlama
        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        spExerciseFrequency = findViewById(R.id.spExerciseFrequency);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnChoosePhoto = findViewById(R.id.btnChoosePhoto);
        btnCalculateCalories = findViewById(R.id.btnCalculateCalories);
        ivProfilePhoto = findViewById(R.id.ivProfilePhoto);
        tvDailyCalories = findViewById(R.id.tvDailyCalories);

        // Eski kaydedilmiş indeks değerini kontrol et ve gerekirse sıfırla
        int savedIndex = sharedPreferences.getInt("exerciseFrequencyIndex", -1);
        if (savedIndex < 0 || savedIndex >= getResources().getStringArray(R.array.exercise_frequencies).length) {
            editor.putInt("exerciseFrequencyIndex", 0).apply();
        }

        // Daha önce kaydedilen bilgileri yükle
        etName.setText(sharedPreferences.getString("name", ""));
        etAge.setText(sharedPreferences.getString("age", ""));
        etWeight.setText(sharedPreferences.getString("weight", ""));
        etHeight.setText(sharedPreferences.getString("height", ""));
        spExerciseFrequency.setSelection(sharedPreferences.getInt("exerciseFrequencyIndex", 0));

        // Profil fotoğrafını yükle
        String profilePhotoUri = sharedPreferences.getString("profilePhoto", "");
        if (!profilePhotoUri.isEmpty()) {
            ivProfilePhoto.setImageURI(Uri.parse(profilePhotoUri));
        }

        // Kaydet Butonu Tıklama Olayı
        btnSaveProfile.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String age = etAge.getText().toString().trim();
            String weight = etWeight.getText().toString().trim();
            String height = etHeight.getText().toString().trim();
            int exerciseFrequencyIndex = spExerciseFrequency.getSelectedItemPosition();

            // Boş alan kontrolü
            if (name.isEmpty() || age.isEmpty() || weight.isEmpty() || height.isEmpty()) {
                Toast.makeText(ProfileActivity.this, "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verileri SharedPreferences ile kaydet
            editor.putString("name", name);
            editor.putString("age", age);
            editor.putString("weight", weight);
            editor.putString("height", height);
            editor.putInt("exerciseFrequencyIndex", exerciseFrequencyIndex);
            editor.apply();

            Toast.makeText(ProfileActivity.this, "Profil bilgileri kaydedildi!", Toast.LENGTH_SHORT).show();
        });

        // Fotoğraf seçme butonu
        btnChoosePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // Günlük kalori hesaplama
        btnCalculateCalories.setOnClickListener(v -> {
            String weight = etWeight.getText().toString().trim();
            String height = etHeight.getText().toString().trim();
            String age = etAge.getText().toString().trim();
            int exerciseFrequencyIndex = spExerciseFrequency.getSelectedItemPosition();

            if (weight.isEmpty() || height.isEmpty() || age.isEmpty()) {
                Toast.makeText(ProfileActivity.this, "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kalori hesaplama
            double bmr;
            double weightKg = Double.parseDouble(weight);
            double heightCm = Double.parseDouble(height);
            int ageYears = Integer.parseInt(age);

            // Erkek için sabit değerlerle örnek hesaplama
            bmr = 88.362 + (13.397 * weightKg) + (4.799 * heightCm) - (5.677 * ageYears);

            // Aktivite çarpanı
            double activityMultiplier = getActivityMultiplier(exerciseFrequencyIndex);
            double dailyCalories = bmr * activityMultiplier;

            tvDailyCalories.setText(String.format("Günlük Kalori İhtiyacı: %.2f kcal", dailyCalories));
        });
    }

    private double getActivityMultiplier(int index) {
        switch (index) {
            case 1: return 1.375; // Hafif aktif
            case 2: return 1.55;  // Orta aktif
            case 3: return 1.725; // Çok aktif
            default: return 1.2;  // Hareketsiz
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            ivProfilePhoto.setImageURI(selectedImageUri);

            // Fotoğraf yolunu SharedPreferences'ta sakla
            editor.putString("profilePhoto", selectedImageUri.toString());
            editor.apply();

            Toast.makeText(this, "Profil fotoğrafı kaydedildi!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
