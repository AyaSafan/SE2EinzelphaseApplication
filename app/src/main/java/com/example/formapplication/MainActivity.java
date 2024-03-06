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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText inputField = findViewById(R.id.input_field);
        TextView outputField = findViewById(R.id.output_field);
        Button submitButton = findViewById(R.id.submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = inputField.getText().toString().trim();
                if (!inputText.isEmpty()) {
                    // Create a new thread to handle the TCP communication
                    new Thread(() -> {
                        try {
                            Log.d("MyApp", "Connecting to server..."); // Log connection attempt

                            Socket socket = new Socket("se2-submission.aau.at", 20080);

                            // Send the student number as a raw byte stream
                            byte[] studentNumberBytes = inputText.getBytes();
                            String studentNumberString = new String(studentNumberBytes);
                            String base64String = Base64.encodeToString(studentNumberBytes, Base64.DEFAULT);



                            // Use BufferedWriter for simplicity, but write directly to the OutputStream
                            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                            Log.d("MyApp", "Sending data: " + base64String);
                            out.write(base64String);  // Write raw bytes as string
                            out.flush(); // Flush the writer to send the data immediately
                            Log.d("MyApp", "Data sent successfully.");

                            /*OutputStream out = socket.getOutputStream();
                            Log.d("MyApp", "Sending data: " + Arrays.toString(studentNumberBytes));
                            out.write(studentNumberBytes);
                            out.flush();
                            Log.d("MyApp", "Data sent successfully.");*/

                            // Get a response from the server (optional)
                            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            String response = in.readLine();
                            Log.d("MyApp", "Receiving data: " + response);

                            // Update the UI
                            runOnUiThread(() -> outputField.setText(response));

                            // Close the connection
                            socket.close();
                            Log.d("MyApp", "Connection closed."); // Log connection closure


                        } catch (IOException e) {
                            Log.e("MyApp", "Error: " + e.getMessage()); // Log error message
                            e.printStackTrace();
                            // Handle connection errors appropriately (e.g., inform the user)
                        }
                    }).start();
                }
            }
        });

    }
}