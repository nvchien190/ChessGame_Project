package vn.edu.fpt.chessgame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import vn.edu.fpt.chessgame.model.ChessPiece;
import vn.edu.fpt.chessgame.model.setupBoard;

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