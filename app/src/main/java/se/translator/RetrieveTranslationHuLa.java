package se.translator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;

public class RetrieveTranslationHuLa implements RetrieveTranslation {

    @AllArgsConstructor
    enum Dictionary {
        DICTZONE("https://dictzone.com/magyar-orvosi-szotar/","#r > tbody > tr:nth-child(2) > td:nth-child(2) > p:nth-child(1) > a"),
        GOOGLE("https://translate.google.com/#view=home&op=translate&sl=hu&tl=la&text=", "body > div.frame > div.page.tlid-homepage.homepage.translate-text > div.homepage-content-wrap > div.tlid-source-target.main-header > div.source-target-row > div.tlid-results-container.results-container > div.tlid-result.result-dict-wrapper > div.result.tlid-copy-target > div.text-wrap.tlid-copy-target > div > span.tlid-translation.translation > span");

        private String prefix;
        private String cssSelector;
    }

    private static final String DICTIONARY_SUFFIX = "https://dictzone.com/latin-magyar-szotar/";

    @Override
    public List<String> geTranslatedText(String text) {
        List<String> result = new ArrayList<>();
        try {
            Element element = null;
            for (Dictionary dictionary : Dictionary.values()) {
                Document doc = Jsoup.connect(dictionary.prefix+text).get();
                element = dictionary.cssSelector.isEmpty() ? doc : doc.selectFirst(dictionary.cssSelector);
                if (element != null && !element.text().isEmpty()) {
                    break;
                }
            }

            if (element == null) {
                result.add("");
            } else {
                String translatedText = element.text().replaceAll("\\|","").toLowerCase().trim();
                result.add(translatedText);
                Document doc2 = Jsoup.connect(DICTIONARY_SUFFIX+translatedText).get();
                Element element2 = doc2.selectFirst("#r > tbody > tr:nth-child(2) > td:nth-child(1) > i");
                if (element2 != null && doc2.selectFirst("#r > tbody > tr:nth-child(2) > td:nth-child(1) > b:nth-child(2)").text().toLowerCase().trim().equals(translatedText)) {
                    result.add(element2.text());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
