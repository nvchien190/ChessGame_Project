package vn.edu.fpt.chessgame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

import vn.edu.fpt.chessgame.logic.LanguageHelper;
import vn.edu.fpt.chessgame.logic.LanguageSelector;
import vn.edu.fpt.chessgame.logic.OnlineChessManager;
import vn.edu.fpt.chessgame.logic.OnlineOptionActivity;
import vn.edu.fpt.chessgame.logic.StartGameActivity;
import vn.edu.fpt.chessgame.model.Match;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // layout chính của menu

        Button button2Player = findViewById(R.id.buttonStart);
        Button buttonStartBot = findViewById(R.id.buttonStartBot);

        // 2 người
        button2Player.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StartGameActivity.class);
            intent.putExtra("isOnline", false);         // 👈 chơi offline
            intent.putExtra("playWithBot", false);      // 👈 không bot
            startActivity(intent);
        });

// Chơi với Bot
        buttonStartBot.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StartGameActivity.class);
            intent.putExtra("isOnline", false);         // 👈 chơi offline
            intent.putExtra("playWithBot", true);       // 👈 có bot
            startActivity(intent);
        });


        Button buttonOnline = findViewById(R.id.buttonStartOnline);
        buttonOnline.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OnlineOptionActivity.class);
            startActivity(intent);
        });
        Spinner spinner = findViewById(R.id.spinnerLanguage);
        LanguageSelector.attachToSpinner(this, spinner);

    }








}