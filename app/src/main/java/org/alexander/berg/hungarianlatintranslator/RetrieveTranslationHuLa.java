package org.alexander.berg.hungarianlatintranslator;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

public class RetrieveTranslationHuLa extends AsyncTask<String, Void, List<String>> {

    enum Dictionary {
        SAJAT1("http://latinhungarian.000webhostapp.com/hu_la.php?hu=", "", ""),
        SAJAT2("http://translator.mywebcommunity.org/translation.php?hu=", "", ""),
        DICTZONE("https://dictzone.com/magyar-orvosi-szotar/", "", "#r > tbody > tr:nth-child(2) > td:nth-child(2) > p:nth-child(1) > a"),
        GOOGLE("https://translate.google.com/#view=home&op=translate&sl=hu&tl=la&text=", "", "body > div.frame > div.page.tlid-homepage.homepage.translate-text > div.homepage-content-wrap > div.tlid-source-target.main-header > div.source-target-row > div.tlid-results-container.results-container > div.tlid-result.result-dict-wrapper > div.result.tlid-copy-target > div.text-wrap.tlid-copy-target > div > span.tlid-translation.translation > span");

        private String prefix;
        private String suffix;
        private String cssSelector;

        Dictionary (String prefix, String suffix, String cssSelector) {
            this.prefix = prefix;
            this.suffix = suffix;
            this.cssSelector = cssSelector;
        }
    }

    private static final String DICTIONARY_FORMAT = "https://dictzone.com/latin-magyar-szotar/";

    @Override
    protected List<String> doInBackground(String... strings) {
        List<String> result = new ArrayList<>();
        try {
            Element element = null;
            for (Dictionary dictionary : Dictionary.values()) {
                Document doc = Jsoup.connect(dictionary.prefix+strings[0]+dictionary.suffix).get();
                element = dictionary.cssSelector.isEmpty() ? doc : doc.selectFirst(dictionary.cssSelector);
                if (element != null && !element.text().isEmpty()) {
                    break;
                }
            }

            if (element == null) {
                result.add("");
            } else {
                result.add(element.text());
                Document doc2 = Jsoup.connect(DICTIONARY_FORMAT+element.text().trim()).get();
                Element element2 = doc2.selectFirst("#r > tbody > tr:nth-child(2) > td:nth-child(1) > i");
                if (element2 != null && doc2.selectFirst("#r > tbody > tr:nth-child(2) > td:nth-child(1) > b:nth-child(2)").text().toLowerCase().equals(element.text().toLowerCase())) {
                    result.add(element2.text());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
