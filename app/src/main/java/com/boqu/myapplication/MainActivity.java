package com.boqu.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    // Declare the views at the class level
    private EditText textInputEditText;
    private Button submitButton;
    private TextView helloTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the views
        textInputEditText = findViewById(R.id.textInputEditText);
        submitButton = findViewById(R.id.submitButton);
        helloTextView = findViewById(R.id.helloTextView);

        // Set OnClickListener for the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredName = textInputEditText.getText().toString();

                if (!enteredName.isEmpty()) {
                    helloTextView.setText("Hello, " + enteredName + "!");
                    helloTextView.setVisibility(View.VISIBLE);
                } else {
                    helloTextView.setText("Hello, World!");
                    helloTextView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
