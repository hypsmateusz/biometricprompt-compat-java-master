package com.natigbabayev.biometricprompt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashSet;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class NoteEditorActivity extends AppCompatActivity {
    String seckey;
    String salt;
    String iv;
    int noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        SharedPreferences prefs = getSharedPreferences("com.example.myapplication", Context.MODE_PRIVATE);
        if (prefs.getString("SECRET_KEY","") == "") {
            SecretKey secretKey = null;
            try {
                secretKey = KeyGenerator.getInstance("AES").generateKey();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            String stringSecretKey = Base64.encodeToString(
                    secretKey.getEncoded(), Base64.DEFAULT);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("SECRET_KEY", stringSecretKey);
            editor.commit();

        }
        SharedPreferences prefse = getSharedPreferences("com.example.myapplication", Context.MODE_PRIVATE);
        if (prefse.getString("IV","") == "") {
            SecretKey secretKey = null;
            SecureRandom ivRandom = new SecureRandom();
            byte[] iv = new byte[16];
            ivRandom.nextBytes(iv);
            String stringSecretKey = Base64.encodeToString(
                    iv, Base64.DEFAULT);

            SharedPreferences.Editor editor = prefse.edit();
            editor.putString("IV", stringSecretKey);
            editor.commit();
        }
        SharedPreferences prefses = getSharedPreferences("com.example.myapplication", Context.MODE_PRIVATE);
        if (prefses.getString("SALT","") == "") {
            SecretKey secretKey = null;
            SecureRandom ivRandom = new SecureRandom();
            byte[] iv = new byte[16];
            ivRandom.nextBytes(iv);
            String stringSecretKey = Base64.encodeToString(
                    iv, Base64.DEFAULT);

            SharedPreferences.Editor editor = prefses.edit();
            editor.putString("SALT", stringSecretKey);
            editor.commit();
        }
        SharedPreferences pref= getSharedPreferences("com.example.myapplication", 0);
        if (prefs.getString("SECRET_KEY","") != ""){
            seckey = prefs.getString("SECRET_KEY","");
        }
        if (prefs.getString("IV","") != ""){
            iv = prefs.getString("IV","");
        }
        if (prefs.getString("SALT","") != ""){
            salt = prefs.getString("SALT","");
        }
        final Encryption encryption = Encryption.getDefault(seckey,salt,iv);

        EditText editText = (EditText) findViewById(R.id.editText);


        Intent intent = getIntent();
        noteId = intent.getIntExtra("noteId", -1);

        if (noteId != -1) {
            editText.setText(encryption.decryptOrNull(MainActivity.notes.get(noteId))
            );
        } else {
            MainActivity.notes.add("");
            noteId = MainActivity.notes.size() - 1;
            MainActivity.arrayAdapter.notifyDataSetChanged();

        }

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                MainActivity.notes.set(noteId, encryption.encryptOrNull(String.valueOf(s)));
                MainActivity.arrayAdapter.notifyDataSetChanged();
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.myapplication", Context.MODE_PRIVATE);

                HashSet<String> set = new HashSet<>(MainActivity.notes);

                sharedPreferences.edit().putStringSet("notes", set).apply();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


}
