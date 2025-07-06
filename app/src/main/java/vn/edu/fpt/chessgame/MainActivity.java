package vn.edu.fpt.chessgame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button btnNewGame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // layout chính của menu

        btnNewGame = findViewById(R.id.buttonStart);
        btnNewGame.setOnClickListener(v -> {
            // Chuyển sang màn hình chơi mới
            Intent intent = new Intent(MainActivity.this, NewGameActivity.class);
            startActivity(intent);
        });
    }

}