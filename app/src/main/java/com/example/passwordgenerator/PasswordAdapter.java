package com.example.passwordgenerator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class PasswordAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> passwords;

    public PasswordAdapter(@NonNull Context context, @NonNull List<String> objects) {
        super(context, R.layout.list_item_custom, objects);
        this.context = context;
        this.passwords = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_custom, parent, false);
        }

        String passwordEntry = passwords.get(position);
        String[] parts = passwordEntry.split(": ");

        TextView itemPasswordName = convertView.findViewById(R.id.itemPasswordName);
        TextView itemPasswordValue = convertView.findViewById(R.id.itemPasswordValue);

        if (parts.length == 2) {
            itemPasswordName.setText(parts[0]);
            itemPasswordValue.setText(parts[1]);
        } else {
            itemPasswordName.setText("Unknown");
            itemPasswordValue.setText(passwordEntry);
        }

        return convertView;
    }
}
