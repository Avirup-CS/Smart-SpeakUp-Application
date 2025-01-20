package com.example.registration_login_module;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.registration_login_module.AudioRecorder;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class SelectRole extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private TextView botResponseView;
    private EditText userInputField;
    private Button sendButton;
    private Button recordButton;
    private Button stopRecordingButton;
    private GenerativeModelFutures model;
    private StringBuilder chatHistory;

    private TextToSpeech textToSpeech;
    private String role;
    private AudioRecorder audioRecorder;
    private boolean audioPermissionGranted = false;
    private boolean isExiting = false; // Flag to track if the user wants to exit
    private ListenableFuture<GenerateContentResponse> currentGenerationRequest = null; // Track the ongoing model request

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_role);

        // Initialize UI components
        botResponseView = findViewById(R.id.bot_response);
//        userInputField = findViewById(R.id.user_input);
        sendButton = findViewById(R.id.exit_button);
        recordButton = findViewById(R.id.record_button);
        stopRecordingButton = findViewById(R.id.stop_recording_button);

        // Initialize conversation history
        chatHistory = new StringBuilder();

        // Check and request audio recording permissions
        checkAudioPermission();

        // Receive the role from the intent
        role = getIntent().getStringExtra("ROLE");
        if (role == null || role.isEmpty()) {
            role = "assistant"; // Default role if none is provided
        }

        // Initialize AudioRecorder
        audioRecorder = new AudioRecorder(getString(R.string.assembly_ai_api_key));

        // Set up model configuration
        GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
        configBuilder.temperature = 0.15f;
        configBuilder.topK = 32;
        configBuilder.topP = 1f;
        configBuilder.maxOutputTokens = 4096;

        // Initialize the Generative Model
        GenerativeModel gm = new GenerativeModel(
                "gemini-1.5-flash",
                getString(R.string.google_ai_api_key),
                configBuilder.build()
        );
        model = GenerativeModelFutures.from(gm);

        // Initialize Text-to-Speech engine
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US);
                String initialMessage = "Hello! I am your " + role + " today. Please tell me about yourself.";
                chatHistory.append(initialMessage).append("\n");
                botResponseView.setText(initialMessage);
                textToSpeech.speak(initialMessage, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        // Set up record button
        recordButton.setOnClickListener(v -> {
            if (isExiting) {
                Toast.makeText(this, "You have exited the conversation.", Toast.LENGTH_SHORT).show();
                return; // Prevent recording after exit
            }

            if (!audioPermissionGranted) {
                Toast.makeText(this, "Audio permission not granted", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                audioRecorder.startRecording(this);
                botResponseView.setText("Recording... Speak now.");
            } catch (IOException e) {
                botResponseView.setText("Recording failed: " + e.getMessage());
            }
        });

        // Set up stop recording button
        stopRecordingButton.setOnClickListener(v -> {
            if (isExiting) {
                Toast.makeText(this, "You have exited the conversation.", Toast.LENGTH_SHORT).show();
                return; // Prevent stop recording after exit
            }

            if (!audioPermissionGranted) {
                Toast.makeText(this, "Audio permission not granted", Toast.LENGTH_SHORT).show();
                return;
            }

            audioRecorder.stopRecording();
            botResponseView.setText("Processing your speech...");

            new Thread(() -> {
                try {
                    File pcmFile = new File(getExternalCacheDir(), "recorded_audio.pcm");
                    File wavFile = audioRecorder.convertPcmToWav(pcmFile);
                    String transcription = audioRecorder.transcribeAudio(wavFile);
                    runOnUiThread(() -> handleUserInput(transcription));
                } catch (Exception e) {
                    runOnUiThread(() -> botResponseView.setText("Error: " + e.getMessage()));
                }
            }).start();
        });

        // Set up send button for manual input
        sendButton.setOnClickListener(v -> {
            String exitMessage = "Thank you for your time. See you later!";
            chatHistory.append(exitMessage).append("\n");
            botResponseView.setText(exitMessage); // Only display the exit message
            sendButton.setEnabled(false); // Disable the send button
            recordButton.setEnabled(false); // Disable the record button
            isExiting = true;
            if (currentGenerationRequest != null) {
                currentGenerationRequest.cancel(true); // Cancel any ongoing generation request
            }
            textToSpeech.speak(exitMessage, TextToSpeech.QUEUE_FLUSH, null, null); // Speak exit message

            // Add a delay to allow the exit message to be spoken before finishing the activity
            new android.os.Handler().postDelayed(() -> {
                finish(); // Close the current activity and return to the previous one
            }, 2000); // Wait 2 seconds (adjust the delay if needed)

        });
    }

    private void checkAudioPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_RECORD_AUDIO_PERMISSION);
            } else {
                audioPermissionGranted = true;
            }
        } else {
            audioPermissionGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            audioPermissionGranted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (!audioPermissionGranted) {
                Toast.makeText(this, "Permission for audio recording is required to use this feature.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleUserInput(String userInput) {
        // If the user wants to exit
        if (userInput.equalsIgnoreCase("exit") || userInput.equalsIgnoreCase("thank you that's all") || userInput.equalsIgnoreCase("that's all")) {
            String exitMessage = "Thank you for your time. See you later!";
            chatHistory.append(exitMessage).append("\n");
            botResponseView.setText(exitMessage); // Only display the exit message
            sendButton.setEnabled(false); // Disable the send button
            recordButton.setEnabled(false); // Disable the record button
            isExiting = true; // Set the flag to indicate that the user wants to exit
            if (currentGenerationRequest != null) {
                currentGenerationRequest.cancel(true); // Cancel any ongoing generation request
            }
            textToSpeech.speak(exitMessage, TextToSpeech.QUEUE_FLUSH, null, null); // Speak exit message
            return;
        }

        // Append user input to chat history
        chatHistory.append("You: ").append(userInput).append("\n");

        // Append system instruction with the role
        String systemInstruction = "You are a " + role +
                ". Your task is to engage in conversations and check for grammatical errors but ignore the punctuations as the person is speaking in front of you. " +
                "Ask a question according to your role at the end to better understand the user and make them understand. If you give suggestions in point format, use numbers.";

        Content content = new Content.Builder()
                .addText(systemInstruction)
                .addText(chatHistory.toString())
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        currentGenerationRequest = response; // Keep track of the current request

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    if (isExiting) return; // If exiting, don't update the UI
                    String botResponse = result.getText();
                    runOnUiThread(() -> {
                        chatHistory.append(botResponse).append("\n");
                        botResponseView.setText(botResponse);
                        textToSpeech.speak(botResponse, TextToSpeech.QUEUE_FLUSH, null, null);
                    });
                }

                @Override
                public void onFailure(Throwable t) {
                    runOnUiThread(() -> botResponseView.setText("Error: " + t.getMessage()));
                }
            }, this.getMainExecutor());
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
