package org.alexander.berg.hungarianlatintranslator;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private final int REQ_CODE = 100;

    TextToSpeech textToSpeechLa;
    EditText editTextHu;
    TextView textViewHu;
    RelativeLayout mainLayoutHu;

    TextToSpeech textToSpeechHu;
    EditText editTextLa;
    TextView textViewLa;
    RelativeLayout mainLayoutLa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((AudioManager)getSystemService(Context.AUDIO_SERVICE)).setParameters("noise_suppression=on");

        editTextHu =findViewById(R.id.editTextHu);
        ImageView speakHu = findViewById(R.id.speakHu);
        textViewHu=findViewById(R.id.textViewHu);
        Button translateButtonHu=findViewById(R.id.translateButtonHu);
        Button changeButtonHu=findViewById(R.id.changeButtonHu);
        mainLayoutHu=findViewById(R.id.mainLayoutHu);
        textToSpeechHu =new TextToSpeech(getApplicationContext(), status -> {
            if(status != TextToSpeech.ERROR) {
                textToSpeechHu.setLanguage(Locale.getDefault());
                textToSpeechHu.speak("", TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        editTextLa =findViewById(R.id.editTextLa);
        ImageView speakLa = findViewById(R.id.speakLa);
        textViewLa=findViewById(R.id.textViewLa);
        Button translateButtonLa=findViewById(R.id.translateButtonLa);
        Button changeButtonLa=findViewById(R.id.changeButtonLa);
        mainLayoutLa=findViewById(R.id.mainLayoutLa);
        textToSpeechLa =new TextToSpeech(getApplicationContext(), status -> {
            if(status != TextToSpeech.ERROR) {
                textToSpeechLa.setLanguage(Locale.ITALY);
                textToSpeechLa.speak("", TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        initComponents(editTextHu, speakHu, textViewHu, translateButtonHu, changeButtonHu, mainLayoutHu, textToSpeechLa, mainLayoutLa, Locale.getDefault(), RetrieveTranslationHuLa.class);
        initComponents(editTextLa, speakLa, textViewLa, translateButtonLa, changeButtonLa, mainLayoutLa, textToSpeechHu, mainLayoutHu, Locale.ITALY, RetrieveTranslationLaHu.class);
    }

    private void initComponents(final EditText editText, ImageView speak, final TextView textView, Button translateButton, Button changeButton, final RelativeLayout mainLayout, final TextToSpeech textToSpeech, final RelativeLayout mainLayoutNew, final Locale locale, final Class clazz) {
        speak.setOnClickListener(v -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, locale.getLanguage());
            intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1000);
            intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1000);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Beszélj :)");
            try {
                startActivityForResult(intent, REQ_CODE);
            } catch (ActivityNotFoundException a) {
                Toast.makeText(getApplicationContext(),
                        "Sorry your device not supported",
                        Toast.LENGTH_SHORT).show();
            }
        });

        translateButton.setOnClickListener(v -> {
            try {
                speakAndWrite(editText, textView, textToSpeech, (AsyncTask<String, Void, List<String>>) clazz.newInstance());
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        });

        changeButton.setOnClickListener(v -> {
            mainLayout.setVisibility(View.GONE);
            mainLayoutNew.setVisibility(View.VISIBLE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE && resultCode == RESULT_OK && null != data) {
            if (mainLayoutHu.getVisibility() == View.VISIBLE) {
                editTextHu.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
                speakAndWrite(editTextHu, textViewHu, textToSpeechLa, new RetrieveTranslationHuLa());
            } else {
                editTextLa.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
                speakAndWrite(editTextLa, textViewLa, textToSpeechHu, new RetrieveTranslationLaHu());
            }
        }
    }

    private void speakAndWrite(EditText editText, TextView textView, final TextToSpeech textToSpeech, AsyncTask<String, Void, List<String>> retrieveTranslation) {
        try {
            List<String> resultList = retrieveTranslation.execute(editText.getText().toString().toLowerCase().trim().replace(" ", "%20")).get();
            if (resultList.size() > 0) {
                final String toSpeak = resultList.get(0);
                textView.setText(resultList.size() == 2 ? resultList.get(0) + ',' + resultList.get(1) : toSpeak);
                Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}