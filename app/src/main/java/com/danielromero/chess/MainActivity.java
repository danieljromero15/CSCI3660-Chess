package com.danielromero.chess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.GridView;

public class MainActivity extends AppCompatActivity {

    View[][] chessGrid = new View[8][8];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                chessGrid[i][j] = findViewById(getResources().getIdentifier(getIDfromNums(i, j), "id", getPackageName()));
                chessGrid[i][j].setOnClickListener(this::printDebug);
            }
        }



    }

    private void printDebug(View view) {
        Log.d("view", String.valueOf(view));
    }

    private String getIDfromNums(int x, int y){
        return String.valueOf((char)(x + 97)) + String.valueOf(y + 1);
    }
}