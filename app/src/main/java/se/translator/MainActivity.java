package se.translator;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ortiz.touchview.TouchImageView;

import org.alexander.berg.hungarianlatintranslator.R;

import se.translator.roomdb.Translation;
import se.translator.roomdb.TranslationDatabase;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    TranslationDatabase db;
    private final int REQ_CODE = 100;

    TextToSpeech textToSpeechLaHu;
    AutoCompleteTextView editTextHuLa;
    TextView textViewHuLa;
    RelativeLayout mainLayoutHu;
    Locale localeHu;

    TextToSpeech textToSpeechHuLa;
    AutoCompleteTextView editTextLaHu;
    TextView textViewLaHu;
    RelativeLayout mainLayoutLa;
    Locale localeLa;
    TextView suffixLa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((AudioManager) Objects.requireNonNull(getSystemService(Context.AUDIO_SERVICE))).setParameters("noise_suppression=on");
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
        localeHu=new Locale("hu");
        textToSpeechHuLa =new TextToSpeech(getApplicationContext(), status -> {
            if(status != TextToSpeech.ERROR) {
                textToSpeechHuLa.setLanguage(localeHu);
                textToSpeechHuLa.speak("", TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        editTextLaHu =findViewById(R.id.editTextLa);
        AsyncTask.execute(() -> {
            String [] words = db.translationDao().getAllWordLa();
            runOnUiThread(() -> editTextLaHu.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, words)));
        });
        editTextLaHu.setOnClickListener(v -> suffixLa.setText(""));
        editTextLaHu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                AsyncTask.execute(() -> {
                    String suffix = db.translationDao().findSuffixByLa(s.toString());
                    runOnUiThread(() -> suffixLa.setText(suffix));
                });
            }
        });
        ImageView speakLa = findViewById(R.id.speakLa);
        textViewLaHu =findViewById(R.id.textViewLa);
        Button translateButtonLa=findViewById(R.id.translateButtonLa);
        Button changeButtonLa=findViewById(R.id.changeButtonLa);
        mainLayoutLa=findViewById(R.id.mainLayoutLa);
        localeLa=Locale.ITALIAN;
        textToSpeechLaHu =new TextToSpeech(getApplicationContext(), status -> {
            if(status != TextToSpeech.ERROR) {
                textToSpeechLaHu.setLanguage(localeLa);
                textToSpeechLaHu.speak("", TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
        suffixLa =findViewById(R.id.suffixLa);

        initComponents(editTextHuLa, speakHu, textViewHuLa, translateButtonHu, changeButtonHu, mainLayoutHu, textToSpeechLaHu, mainLayoutLa, localeHu, localeLa, RetrieveTranslationHuLa.class);
        initComponents(editTextLaHu, speakLa, textViewLaHu, translateButtonLa, changeButtonLa, mainLayoutLa, textToSpeechHuLa, mainLayoutHu, localeLa, localeHu, RetrieveTranslationLaHu.class);
        Spinner declinatioSpinner = findViewById(R.id.declinatioSpinner);
        final TouchImageView declinatioImageView = findViewById(R.id.declinatioImageView);
        declinatioSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                declinatioImageView.setImageResource( id == 0 ? R.drawable.declinatio0 : (id == 1 ? R.drawable.declinatio1 : (id == 2 ? R.drawable.declinatio2 : (id == 3 ? R.drawable.praepositio : R.drawable.prefix))));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void initComponents(final EditText editText, ImageView speak, final TextView textView, Button translateButton, Button changeButton, final RelativeLayout mainLayout, final TextToSpeech textToSpeech, final RelativeLayout mainLayoutNew, final Locale localeFrom, final Locale localeTo, final Class clazz) {
        speak.setOnClickListener(v -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, localeFrom.getLanguage());
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, localeFrom);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "se.translator");
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
                Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getWindowToken(), 0);
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
                editTextHuLa.setText(Objects.requireNonNull(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)).get(0));
                editTextHuLa.dismissDropDown();
                speakAndWrite(localeHu, localeLa,editTextHuLa, textViewHuLa, textToSpeechLaHu, new RetrieveTranslationHuLa());
            } else {
                editTextLaHu.setText(Objects.requireNonNull(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)).get(0));
                editTextLaHu.dismissDropDown();
                speakAndWrite(localeLa, localeHu, editTextLaHu, textViewLaHu, textToSpeechHuLa, new RetrieveTranslationLaHu());
            }
        }
    }

    private void speakAndWrite(final Locale from, final Locale to, EditText editText, TextView textView, final TextToSpeech textToSpeech, RetrieveTranslation retrieveTranslation) {
        String text = editText.getText().toString().toLowerCase().trim();
        AsyncTask.execute(() -> {
            String toSpeak = null;
            StringBuilder toShow = new StringBuilder();
            if (from.equals(localeHu) && to.equals(localeLa)) {
                List<Translation> results = db.translationDao().findByWordHuLa(text);
                for (Translation result : results) {
                    if (toSpeak == null) {
                        toSpeak = result.getWordLa();
                    }
                    if (toShow.length() > 0) {
                        toShow.append("\n");
                    }
                    toShow.append(result.getWordLa()).append(!result.getSuffixLa().isEmpty() ? ", " : "").append(result.getSuffixLa());
                }
            } else if (from.equals(localeLa) && to.equals(localeHu)) {
                List<Translation> results = db.translationDao().findByWordLaHu(text);
                for (Translation result : results) {
                    if (toSpeak == null) {
                        toSpeak = result.getWordHu();
                    }
                    if (toShow.length() > 0) {
                        toShow.append("\n");
                    }
                    toShow.append(result.getWordHu());
                }
            }
            if (toSpeak == null || toSpeak.equals("")) {
                List<String> resultList = retrieveTranslation.geTranslatedText(text.replace(" ", "%20"));
                if (resultList.size() > 0) {
                    toSpeak = resultList.get(0);
                    toShow = new StringBuilder(resultList.size() == 2 ? resultList.get(0) + ',' + resultList.get(1) : toSpeak);
                }
            }
            final String toShowString = toShow.toString();
            final String toSpeakString = toSpeak;

            runOnUiThread(() -> {
                textView.setText(toShowString);
                Toast.makeText(getApplicationContext(), toSpeakString,Toast.LENGTH_SHORT).show();
                textToSpeech.speak(toSpeakString, TextToSpeech.QUEUE_FLUSH, null, null);
            });

        });
    }
}