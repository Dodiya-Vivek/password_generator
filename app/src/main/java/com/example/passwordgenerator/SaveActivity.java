package com.example.passwordgenerator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SaveActivity extends AppCompatActivity {

    private PasswordAdapter adapter;
    private List<String> passwords;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_save);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SaveActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        ListView passwordListView = findViewById(R.id.passwordListView);

        passwords = getPasswords();

        adapter = new PasswordAdapter(this, numberPasswords(passwords));
        passwordListView.setAdapter(adapter);

        passwordListView.setOnItemLongClickListener((parent, view, position, id) -> {
            showOptionsDialog(position);
            return true;
        });
    }

    private void showOptionsDialog(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Select Option")
                .setItems(new CharSequence[]{"Edit", "Delete"}, (dialog, which) -> {
                    if (which == 0) {
                        showEditDialog(position);
                    } else if (which == 1) {
                        deletePassword(position);
                    }
                })
                .show();
    }

    private void deletePassword(int position) {
        passwords.remove(position);
        adapter.clear();
        adapter.addAll(numberPasswords(passwords));
        adapter.notifyDataSetChanged();
        savePasswords();

        Toast.makeText(this, "Password deleted", Toast.LENGTH_SHORT).show();
    }

    private void showEditDialog(int position) {
        String oldPassword = passwords.get(position);

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_edit_password, null);
        EditText editText = dialogView.findViewById(R.id.editPassword);
        editText.setText(oldPassword);

        new AlertDialog.Builder(this)
                .setTitle("Edit Password")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newPassword = editText.getText().toString();
                    if (!newPassword.isEmpty()) {
                        passwords.set(position, newPassword);
                        adapter.clear();
                        adapter.addAll(numberPasswords(passwords));
                        adapter.notifyDataSetChanged();
                        savePasswords();
                        Toast.makeText(this, "Password updated", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void savePasswords() {
        SharedPreferences sharedPreferences = getSharedPreferences("passwords", Context.MODE_PRIVATE);
        Set<String> passwordSet = new HashSet<>(passwords);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("passwords", passwordSet);
        editor.apply();
    }

    private List<String> getPasswords() {
        SharedPreferences sharedPreferences = getSharedPreferences("passwords", Context.MODE_PRIVATE);
        Set<String> passwordSet = sharedPreferences.getStringSet("passwords", new HashSet<>());

        return new ArrayList<>(passwordSet);
    }

    private List<String> numberPasswords(List<String> passwords) {
        List<String> numberedPasswords = new ArrayList<>();
        for (int i = 0; i < passwords.size(); i++) {
            numberedPasswords.add((i + 1) + ". " + passwords.get(i));
        }
        return numberedPasswords;
    }
}
