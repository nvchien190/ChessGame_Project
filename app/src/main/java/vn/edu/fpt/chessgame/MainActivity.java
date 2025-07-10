package vn.edu.fpt.chessgame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import vn.edu.fpt.chessgame.logic.StartGameActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // layout chính của menu

        Button button2Player = findViewById(R.id.buttonStart);


        button2Player.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StartGameActivity.class);
            intent.putExtra("playWithBot", false); // ✅ truyền false để chơi 2 người
            startActivity(intent);   });


        Button buttonStartBot = findViewById(R.id.buttonStartBot);

        buttonStartBot.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StartGameActivity.class);
            intent.putExtra("playWithBot", true); // truyền cờ hiệu
            startActivity(intent);
        });



    }


}