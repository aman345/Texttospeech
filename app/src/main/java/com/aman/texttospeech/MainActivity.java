package com.aman.texttospeech;

import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.util.Function;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  implements RecognitionListener {
    private TextView returnedtext;
    ImageButton recButton;
    private ProgressBar progressBar;
    private SpeechRecognizer speechRecognizer = null;
    private Intent recognizerIntent;
    static final int REQUEST_PERMISSION_KEY=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        returnedtext = findViewById(R.id.textview);
        progressBar = findViewById(R.id.progressbar);
        recButton = findViewById(R.id.mainButton);

        String[] PERMISSIONS ={Manifest.permission.RECORD_AUDIO};
        if(!PermissionFunction.hasPermissions(this,PERMISSIONS)){
            ActivityCompat.requestPermissions(this,PERMISSIONS,REQUEST_PERMISSION_KEY);
        }
        progressBar.setVisibility(View.INVISIBLE);
        speechRecognizer =SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(this);

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,"en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);


        recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 5000);
        recognizerIntent.putExtra("android.speech.extra.DICTATION_MODE", true);

        recButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                speechRecognizer.startListening(recognizerIntent);
                recButton.setEnabled(false);
            }
        });
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Log.d("Log", "onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d("Log","OnBeginningOfSpeech");
        progressBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void onRmsChanged(float v) {
        Log.d("Log", "onRmsChanged: " + v);
        progressBar.setProgress((int) v);

    }

    @Override
    public void onBufferReceived(byte[] bytes) {
        Log.d("Log","onBufferReceived"+bytes);
    }

    @Override
    public void onEndOfSpeech() {
        Log.d("Log", "onEndOfSpeech");
        progressBar.setVisibility(View.INVISIBLE);
        recButton.setEnabled(true);

    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d("Log", "FAILED " + errorMessage);
        progressBar.setVisibility(View.INVISIBLE);
        returnedtext.setText(errorMessage);
        recButton.setEnabled(true);

    }

    @Override
    public void onResults(Bundle bundle) {
        Log.d("Log", "onResults");
    }

    @Override
    public void onPartialResults(Bundle bundle) {
        Log.d("Log", "onPartialResults");

        ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        text = matches.get(0);
        returnedtext.setText(text);
    }

    @Override
    public void onEvent(int i, Bundle bundle) {
        Log.d("Log", "onEvent");

    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

}
