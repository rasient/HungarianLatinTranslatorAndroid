package org.alexander.berg.hungarianlatintranslator;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.alexander.berg.hungarianlatintranslator.roomdb.Translation;
import org.alexander.berg.hungarianlatintranslator.roomdb.TranslationDatabase;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TranslationDatabase db;
    private final int REQ_CODE = 100;

    TextToSpeech textToSpeechLaHu;
    AutoCompleteTextView editTextHuLa;
    TextView textViewHuLa;
    RelativeLayout mainLayoutHu;

    TextToSpeech textToSpeechHuLa;
    AutoCompleteTextView editTextLaHu;
    TextView textViewLaHu;
    RelativeLayout mainLayoutLa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((AudioManager)getSystemService(Context.AUDIO_SERVICE)).setParameters("noise_suppression=on");
        db = TranslationDatabase.getInstance(getApplicationContext());

        editTextHuLa =findViewById(R.id.editTextHu);
        AsyncTask.execute(() -> {
            String [] words = db.translationDao().getAllWordHu();
            runOnUiThread(() -> editTextHuLa.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, words)));
        });
        ImageView speakHu = findViewById(R.id.speakHu);
        textViewHuLa =findViewById(R.id.textViewHu);
        Button translateButtonHu=findViewById(R.id.translateButtonHu);
        Button changeButtonHu=findViewById(R.id.changeButtonHu);
        mainLayoutHu=findViewById(R.id.mainLayoutHu);
        textToSpeechHuLa =new TextToSpeech(getApplicationContext(), status -> {
            if(status != TextToSpeech.ERROR) {
                textToSpeechHuLa.setLanguage(Locale.getDefault());
                textToSpeechHuLa.speak("", TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        editTextLaHu =findViewById(R.id.editTextLa);
        AsyncTask.execute(() -> {
            String [] words = db.translationDao().getAllWordLa();
            runOnUiThread(() -> editTextLaHu.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, words)));
        });
        ImageView speakLa = findViewById(R.id.speakLa);
        textViewLaHu =findViewById(R.id.textViewLa);
        Button translateButtonLa=findViewById(R.id.translateButtonLa);
        Button changeButtonLa=findViewById(R.id.changeButtonLa);
        mainLayoutLa=findViewById(R.id.mainLayoutLa);
        textToSpeechLaHu =new TextToSpeech(getApplicationContext(), status -> {
            if(status != TextToSpeech.ERROR) {
                textToSpeechLaHu.setLanguage(Locale.ITALY);
                textToSpeechLaHu.speak("", TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        initComponents(editTextHuLa, speakHu, textViewHuLa, translateButtonHu, changeButtonHu, mainLayoutHu, textToSpeechLaHu, mainLayoutLa, Locale.getDefault(), Locale.ITALY, RetrieveTranslationHuLa.class);
        initComponents(editTextLaHu, speakLa, textViewLaHu, translateButtonLa, changeButtonLa, mainLayoutLa, textToSpeechHuLa, mainLayoutHu, Locale.ITALY, Locale.getDefault(), RetrieveTranslationLaHu.class);
    }

    private void initComponents(final EditText editText, ImageView speak, final TextView textView, Button translateButton, Button changeButton, final RelativeLayout mainLayout, final TextToSpeech textToSpeech, final RelativeLayout mainLayoutNew, final Locale localeFrom, final Locale localeTo, final Class clazz) {
        speak.setOnClickListener(v -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, localeFrom.getLanguage());
            intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1000);
            intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1000);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, localeFrom);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "org.alexander.berg");
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
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                //Find the currently focused view, so we can grab the correct window token from it.
                View view = this.getCurrentFocus();
                //If no view currently has focus, create a new one, just so we can grab a window token from it
                if (view == null) {
                    view = new View(this);
                }
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                speakAndWrite(localeFrom, localeTo, editText, textView, textToSpeech, (RetrieveTranslation) clazz.newInstance());
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
                editTextHuLa.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
                editTextHuLa.dismissDropDown();
                speakAndWrite(Locale.getDefault(), Locale.ITALY,editTextHuLa, textViewHuLa, textToSpeechLaHu, new RetrieveTranslationHuLa());
            } else {
                editTextLaHu.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
                editTextLaHu.dismissDropDown();
                speakAndWrite(Locale.ITALY, Locale.getDefault(), editTextLaHu, textViewLaHu, textToSpeechHuLa, new RetrieveTranslationLaHu());
            }
        }
    }

    private void speakAndWrite(final Locale from, final Locale to, EditText editText, TextView textView, final TextToSpeech textToSpeech, RetrieveTranslation retrieveTranslation) {
        String text = editText.getText().toString().toLowerCase().trim();
        AsyncTask.execute(() -> {
            String toSpeak = null;
            String toShow = "";
            if (from.equals(Locale.getDefault()) && to.equals(Locale.ITALY)) {
                List<Translation> results = db.translationDao().findByWordHuLa(text);
                for (Translation result : results) {
                    if (toSpeak == null) {
                        toSpeak = result.getWordLa();
                    }
                    if (!toShow.isEmpty()) {
                        toShow = toShow + "\n";
                    }
                    toShow = toShow + result.getWordLa() + (!result.getSuffixLa().isEmpty() ? ", " : "") + result.getSuffixLa();
                }
            } else if (from.equals(Locale.ITALY) && to.equals(Locale.getDefault())) {
                List<Translation> results = db.translationDao().findByWordLaHu(text);
                for (Translation result : results) {
                    if (toSpeak == null) {
                        toSpeak = result.getWordHu();
                    }
                    if (!toShow.isEmpty()) {
                        toShow = toShow + "\n";
                    }
                    toShow = toShow + result.getWordHu();
                }
            }
            if (toSpeak == null || toSpeak.equals("")) {
                List<String> resultList = null;
                resultList = retrieveTranslation.geTranslatedText(text.replace(" ", "%20"));
                if (resultList.size() > 0) {
                    toSpeak = resultList.get(0);
                    toShow = resultList.size() == 2 ? resultList.get(0) + ',' + resultList.get(1) : toSpeak;
                }
            }
            final String toShowString = toShow;
            final String toSpeakString = toSpeak;

            runOnUiThread(() -> {
                textView.setText(toShowString);
                Toast.makeText(getApplicationContext(), toSpeakString,Toast.LENGTH_SHORT).show();
                textToSpeech.speak(toSpeakString, TextToSpeech.QUEUE_FLUSH, null, null);
            });

        });
    }
}