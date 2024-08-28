package com.example.passwordgenerator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private TextView passwordTextView;
    private CheckBox charactersCheckBox;
    private CheckBox specialCharactersCheckBox;
    private Button generateButton;
    private Button saveButton;
    private ImageButton aboutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        passwordTextView = findViewById(R.id.passwordTextView);
        charactersCheckBox = findViewById(R.id.charactersCheckBox);
        specialCharactersCheckBox = findViewById(R.id.specialCharactersCheckBox);
        generateButton = findViewById(R.id.generateButton);
        saveButton = findViewById(R.id.savebtn);
        aboutButton = findViewById(R.id.imageButton);
        aboutButton.setOnClickListener(View -> {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        });

        generateButton.setOnClickListener(v -> generatePassword());

        saveButton.setOnClickListener(v -> showSaveDialog());

        // Generate and show default password
        generatePassword();
    }

    private void generatePassword() {
        boolean includeCharacters = charactersCheckBox.isChecked();
        boolean includeSpecialCharacters = specialCharactersCheckBox.isChecked();

        String password = generateRandomPassword(includeCharacters, includeSpecialCharacters);
        passwordTextView.setText(password);
    }

    private String generateRandomPassword(boolean includeCharacters, boolean includeSpecialCharacters) {
        String numbers = "0123456789";
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String specialCharacters = "!@#$%^&*()-_=+[]{}|;:'\",.<>?/";

        StringBuilder allowedChars = new StringBuilder(numbers);
        if (includeCharacters) {
            allowedChars.append(characters);
        }
        if (includeSpecialCharacters) {
            allowedChars.append(specialCharacters);
        }

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < 6; i++) { // Default password length set to 12
            int index = random.nextInt(allowedChars.length());
            password.append(allowedChars.charAt(index));
        }

        return password.toString();
    }

    private void showSaveDialog() {
        String password = passwordTextView.getText().toString();
        if (password.isEmpty()) {
            Toast.makeText(this, "No password to save", Toast.LENGTH_SHORT).show();
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_save_password, null);
        EditText editText = dialogView.findViewById(R.id.editPasswordName);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Save Password")
                .setView(dialogView)
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", (d, which) -> d.dismiss())
                .create();

        dialog.setOnShowListener(d -> {
            Button saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            saveButton.setOnClickListener(v -> {
                String passwordName = editText.getText().toString();
                if (!passwordName.isEmpty()) {
                    savePassword(passwordName, password);
                    dialog.dismiss();
                    Intent intent = new Intent(MainActivity.this, SaveActivity.class);
                    startActivity(intent);
                } else {
                    editText.setError("Name cannot be empty");
                }
            });
        });

        dialog.show();
    }

    private void savePassword(String name, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("passwords", Context.MODE_PRIVATE);
        Set<String> passwords = sharedPreferences.getStringSet("passwords", new HashSet<>());
        passwords.add(name + ": " + password);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("passwords", passwords);
        editor.apply();
    }
}
