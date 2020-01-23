package com.example.android.zigbeetestbed;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class DoorlockActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doorlock);
        setButtonListeners();
    }

    private void setButtonListeners(){
        Button digitOne = findViewById(R.id.btOne);
        Button digitTwo = findViewById(R.id.btTwo);
        Button digitThree = findViewById(R.id.btThree);
        Button digitFour = findViewById(R.id.btFour);
        Button digitFive = findViewById(R.id.btFive);
        Button digitSix = findViewById(R.id.btSix);
        Button digitSeven = findViewById(R.id.btSeven);
        Button digitEight = findViewById(R.id.btEight);
        Button digitNine = findViewById(R.id.btNine);
        Button digitZero = findViewById(R.id.btZero);
        Button clearB = findViewById(R.id.btClear);

        final String digitOneText = digitOne.getText().toString();
        final String digitTwoText = digitTwo.getText().toString();
        final String digitThreeText = digitThree.getText().toString();
        final String digitFourText = digitFour.getText().toString();
        final String digitFiveText = digitFive.getText().toString();
        final String digitSixText = digitSix.getText().toString();
        final String digitSevenText = digitSeven.getText().toString();
        final String digitEightText = digitEight.getText().toString();
        final String digitNineText = digitNine.getText().toString();
        final String digitZeroText = digitZero.getText().toString();

        digitOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                digitEnter(digitOneText);
            }
        });
        digitTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                digitEnter(digitTwoText);
            }
        });
        digitThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                digitEnter(digitThreeText);
            }
        });
        digitFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                digitEnter(digitFourText);
            }
        });
        digitFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                digitEnter(digitFiveText);
            }
        });
        digitSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                digitEnter(digitSixText);
            }
        });
        digitSeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                digitEnter(digitSevenText);
            }
        });
        digitEight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                digitEnter(digitEightText);
            }
        });
        digitNine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                digitEnter(digitNineText);
            }
        });
        digitZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                digitEnter(digitZeroText);
            }
        });
        clearB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearText();
            }
        });
    }
    public void digitEnter(String digit){
        EditText pinNumber = findViewById(R.id.lbPin);
        String currentNumber = pinNumber.getText().toString();
        if(currentNumber.length() < 4){
            pinNumber.setText(currentNumber+digit);
        }
    }

    public void clearText(){
        EditText pinNumber = findViewById(R.id.lbPin);
        pinNumber.setText("");
    }
}
