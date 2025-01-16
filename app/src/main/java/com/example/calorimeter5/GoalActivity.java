package com.example.calorimeter5;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GoalActivity extends AppCompatActivity {

    private ListView lvMotivationList;
    private EditText etNewMotivation;
    private Button btnAddMotivation;

    private ArrayList<String> motivationList;
    private ArrayAdapter<String> adapter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);

        // Geri tuşunu etkinleştir
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // View bileşenlerini bağla
        lvMotivationList = findViewById(R.id.lvMotivationList);
        etNewMotivation = findViewById(R.id.etNewMotivation);
        btnAddMotivation = findViewById(R.id.btnAddMotivation);

        // SharedPreferences ile verileri saklama
        sharedPreferences = getSharedPreferences("MotivationPrefs", MODE_PRIVATE);
        Set<String> savedSet = sharedPreferences.getStringSet("Motivations", null);

        if (savedSet == null) {
            // Varsayılan motivasyon cümleleri
            motivationList = new ArrayList<>(Arrays.asList(
                    "Stay focused! You're making progress every day!",
                    "Consistency is the key to success!",
                    "Never give up, your goals are within reach!",
                    "You are stronger than you think!",
                    "Small steps lead to big changes!"
            ));
        } else {
            motivationList = new ArrayList<>(savedSet);
        }

        // Liste adaptörünü bağla
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, motivationList);
        lvMotivationList.setAdapter(adapter);

        // Yeni motivasyon cümlesi ekleme
        btnAddMotivation.setOnClickListener(v -> {
            String newMotivation = etNewMotivation.getText().toString().trim();
            if (newMotivation.isEmpty()) {
                Toast.makeText(GoalActivity.this, "Please enter a motivational sentence!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Listeye ekleme ve kaydetme
            motivationList.add(newMotivation);
            adapter.notifyDataSetChanged();
            etNewMotivation.setText("");

            // SharedPreferences ile kaydet
            Set<String> updatedSet = new HashSet<>(motivationList);
            sharedPreferences.edit().putStringSet("Motivations", updatedSet).apply();

            Toast.makeText(GoalActivity.this, "Motivational sentence added!", Toast.LENGTH_SHORT).show();
        });
    }

    // Geri tuşu desteği
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
