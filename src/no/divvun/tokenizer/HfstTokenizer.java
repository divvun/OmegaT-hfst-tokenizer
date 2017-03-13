package no.divvun.tokenizer;

import java.io.File;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.omegat.tokenizer.ITokenizer.StemmingMode;
import org.omegat.util.Language;
import org.omegat.util.Preferences;
import net.sf.hfst.Transducer;
import net.sf.hfst.TransducerHeader;
import net.sf.hfst.TransducerAlphabet;
import net.sf.hfst.UnweightedTransducer;
import net.sf.hfst.WeightedTransducer;
import net.sf.hfst.NoTokenizationException;


@Tokenizer(languages = { Tokenizer.DISCOVER_AT_RUNTIME })
public class HfstTokenizer extends BaseTokenizer {

  private static Map<Language, File> analysers;

  private Transducer transducer;

  private Transducer getTransducer() {
    if (transducer != null) {
      return transducer;
    }

    if (analysers == null) {
      populateInstalledTransducers();
    }

    Language language = getLanguage();
    File transducerFile = analysers.get(language);

    try {
      transducer = loadTransducer(new FileInputStream(transducerFile));
      return transducer;
    }
    catch (Exception fe) {
      // nothing
    }

    return null;
  }

  @Override
  protected TokenStream getTokenStream(final String strOrig, final boolean stemsAllowed,
          final boolean stopWordsAllowed) throws IOException {
    StandardTokenizer tokenizer = new StandardTokenizer();
    tokenizer.setReader(new StringReader(strOrig));

    if (stemsAllowed) {
      Transducer transducer = getTransducer();
      if (transducer == null) {
        return tokenizer;
      }

      return new HfstStemFilter(tokenizer, transducer);
    } else {
      return tokenizer;
    }
  }

  @Override
  public String[] getSupportedLanguages() {

      populateInstalledTransducers();

      Set<Language> commonLangs = analysers.keySet();

      return langsToStrings(commonLangs);
  }

  private static void populateInstalledTransducers() {
    analysers = new HashMap<Language, File>();

    String dictionaryDirPath = Preferences.getPreference(Preferences.SPELLCHECKER_DICTIONARY_DIRECTORY);
    if (dictionaryDirPath.isEmpty()) {
        return;
    }

    File dictionaryDir = new File(dictionaryDirPath);
    if (!dictionaryDir.isDirectory()) {
        return;
    }

    for (File file : dictionaryDir.listFiles()) {
      String name = file.getName();

      if (name.endsWith(".hfstol")) {
        Language lang = new Language(name.substring(name.lastIndexOf("-") +1, name.lastIndexOf(".")));
        analysers.put(lang, file);
      }
    }
  }

  private Transducer loadTransducer(FileInputStream transducerfile)
        throws java.io.FileNotFoundException, java.io.IOException {

    TransducerHeader h = new TransducerHeader(transducerfile);
    DataInputStream charstream = new DataInputStream(transducerfile);
    TransducerAlphabet a = new TransducerAlphabet(charstream, h.getSymbolCount());

    if (h.isWeighted()) {
      return new WeightedTransducer(transducerfile, h, a);
    }
    else {
      return new UnweightedTransducer(transducerfile, h, a);
    }
  }

  private static String[] langsToStrings(Set<Language> langs) {
    List<String> result = new ArrayList<String>();
    for (Language lang : langs) {
      result.add(lang.getLanguage().toLowerCase());
      result.add(lang.getLanguageCode().toLowerCase());
    }
    return result.toArray(new String[result.size()]);
  }
}
