package com.example.formapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private EditText inputField;
    private TextView outputField;
    private Button submitButton;
    private Button calculateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputField = findViewById(R.id.input_field);
        outputField = findViewById(R.id.output_field);
        submitButton = findViewById(R.id.submit_button);
        calculateButton = findViewById(R.id.calculate_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDataToServer();
            }
        });

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateCrossSumAndBinary();
            }
        });
    }

    private void sendDataToServer() {
        runOnUiThread(() -> outputField.setText("Loading ... "));

        String inputText = inputField.getText().toString().trim();
        if (!inputText.isEmpty()) {
            // Create a new thread for TCP communication
            new Thread(() -> {
                try {
                    Log.d("MyApp", "Connecting to server...");
                    Socket socket = new Socket("se2-submission.aau.at", 20080);
                    Log.d("MyApp", "Socket connection established.");


                    byte[] studentNumberBytes = inputText.getBytes();
                    String studentNumberString = new String(studentNumberBytes);

                    Log.d("MyApp", "Sending data: " + studentNumberString);
                    DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
                    dataOut.writeBytes(studentNumberString+'\n');
                    dataOut.flush();
                    Log.d("MyApp", "Data sent successfully.");

                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String response = in.readLine();
                    Log.d("MyApp", "Receiving data: " + response);

                    // Update the UI
                    runOnUiThread(() -> outputField.setText("Server Result: " + response));

                    // Close the connection
                    socket.close();
                    Log.d("MyApp", "Connection closed.");

                } catch (IOException e) {
                    Log.e("MyApp", "Error: " + e.getMessage()); // Log error message
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void calculateCrossSumAndBinary() {
        String inputText = inputField.getText().toString().trim();
        if (!inputText.isEmpty()) {
            int inputNumber = Integer.parseInt(inputText);
            int crossSum = calculateCrossSum(inputNumber);
            String binaryRepresentation = Integer.toBinaryString(crossSum);

            runOnUiThread(() -> outputField.setText("Binary Cross Sum: " + binaryRepresentation));
        }
    }

    private int calculateCrossSum(int number) {
        int sum = 0;
        while (number != 0) {
            sum += number % 10;
            number /= 10;
        }
        return sum;
    }
}
