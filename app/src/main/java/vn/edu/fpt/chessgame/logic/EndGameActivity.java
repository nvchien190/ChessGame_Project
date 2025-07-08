package vn.edu.fpt.chessgame.logic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import vn.edu.fpt.chessgame.MainActivity;
import vn.edu.fpt.chessgame.R;

public class EndGameActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.endgame_activity);

        TextView winnerText = findViewById(R.id.winnerText);
        Button playAgainButton = findViewById(R.id.playAgainButton);
        Button exitButton = findViewById(R.id.exitButton);

        // Nhận dữ liệu từ Intent
        String winner = getIntent().getStringExtra("winner");
        winnerText.setText("♛ " + winner + " thắng ván cờ!");

        playAgainButton.setOnClickListener(v -> {
            Intent intent = new Intent(EndGameActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        exitButton.setOnClickListener(v -> finishAffinity());
    }
}
