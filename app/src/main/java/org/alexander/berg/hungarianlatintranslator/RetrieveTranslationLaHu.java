package org.alexander.berg.hungarianlatintranslator;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RetrieveTranslationLaHu extends AsyncTask<String, Void, List<String>> {

    enum Dictionary {
        SAJAT1("http://latinhungarian.000webhostapp.com/la_hu.php?la=", "", ""),
        SAJAT2("http://translator.mywebcommunity.org/translation.php?la=", "", ""),
        DICTZONE("https://dictzone.com/orvosi-magyar-szotar/", "", "#r > tbody > tr:nth-child(2) > td:nth-child(2) > p:nth-child(1) > a"),
        GOOGLE("https://translate.google.com/#view=home&op=translate&sl=la&tl=hu&text=", "", "body > div.frame > div.page.tlid-homepage.homepage.translate-text > div.homepage-content-wrap > div.tlid-source-target.main-header > div.source-target-row > div.tlid-results-container.results-container > div.tlid-result.result-dict-wrapper > div.result.tlid-copy-target > div.text-wrap.tlid-copy-target > div > span.tlid-translation.translation > span");

        private String prefix;
        private String suffix;
        private String cssSelector;

        Dictionary (String prefix, String suffix, String cssSelector) {
            this.prefix = prefix;
            this.suffix = suffix;
            this.cssSelector = cssSelector;
        }
    }

    @Override
    protected List<String> doInBackground(String... strings) {
        List<String> result = new ArrayList<>();
        try {
            Element element = null;
            for (Dictionary dictionary : Dictionary.values()) {
                Document doc = Jsoup.connect(dictionary.prefix+strings[0].trim()+dictionary.suffix).get();
                element = doc.selectFirst(dictionary.cssSelector);
                if (element != null) {
                    break;
                }
            }

            if (element == null) {
                result.add("");
            } else {
                result.add(element.text());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
