package com.example.calorimeter5;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnRegister, btnLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase Authentication instance
        mAuth = FirebaseAuth.getInstance();

        // EditText ve Button bileşenlerini bağlama
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnRegister = findViewById(R.id.btn_register);
        btnLogin = findViewById(R.id.btn_login);

        // Kayıt Ol Butonu
        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // E-posta ve şifre boş mu kontrol et
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(MainActivity.this, "Geçerli bir e-posta adresi girin.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(MainActivity.this, "Şifre en az 6 karakter olmalıdır.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Firebase ile kullanıcı oluşturma
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Kayıt başarılı!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Kayıt başarısız: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Giriş Yap Butonu
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // E-posta ve şifre boş mu kontrol et
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(MainActivity.this, "Geçerli bir e-posta adresi girin.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Firebase ile kullanıcı giriş yapma
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Giriş başarılı!", Toast.LENGTH_SHORT).show();

                            // MainMenuActivity'ye yönlendirme
                            Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
                            startActivity(intent);
                            finish(); // MainActivity'yi kapat
                        } else {
                            Toast.makeText(MainActivity.this, "Giriş başarısız: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
