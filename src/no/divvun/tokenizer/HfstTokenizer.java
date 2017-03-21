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
import org.apache.lucene.util.Version;
import org.omegat.core.Core;
import org.omegat.core.data.IProject;
import org.omegat.tokenizer.BaseTokenizer;
import org.omegat.tokenizer.ITokenizer.StemmingMode;
import org.omegat.tokenizer.Tokenizer;
import org.omegat.util.Language;
import org.omegat.util.Log;
import org.omegat.util.Preferences;
import fi.seco.hfst.Transducer;
import fi.seco.hfst.TransducerHeader;
import fi.seco.hfst.TransducerAlphabet;
import fi.seco.hfst.UnweightedTransducer;
import fi.seco.hfst.WeightedTransducer;
// import fi.seco.hfst.NoTokenizationException;


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

    Language language = getProjectLanguage();
    File transducerFile = analysers.get(language);

    try {
      transducer = loadTransducer(new FileInputStream(transducerFile));
      return transducer;
    }
    catch (Exception fe) {

    }

    return null;
  }

  private Language getProjectLanguage() {
    IProject proj = Core.getProject();
    Language lang = new Language(Tokenizer.DISCOVER_AT_RUNTIME);
    if (proj.getSourceTokenizer() == this) {
        lang = proj.getProjectProperties().getSourceLanguage();
    } else if (proj.getTargetTokenizer() == this) {
        lang = proj.getProjectProperties().getTargetLanguage();
    }
    return lang;
  }

  @Override
  protected TokenStream getTokenStream(final String strOrig, final boolean stemsAllowed,
          final boolean stopWordsAllowed) throws IOException {
    
    StandardTokenizer tokenizer = loadTokenizer(strOrig);
    
    // Lucene 4.x
    //StandardTokenizer tokenizer = new StandardTokenizer(getBehavior(), new StringReader(strOrig));
    
    // Lucene 5.x
    //StandardTokenizer tokenizer = new StandardTokenizer();
    //tokenizer.setReader(new StringReader(strOrig));

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
  
  private StandardTokenizer loadTokenizer(String strOrig) {
    java.lang.reflect.Method m = null;
    StringReader sr = new StringReader(strOrig);
    try {
      m = BaseTokenizer.class.getMethod("getBehavior", (Class<?>[]) null);
    }
    catch (NoSuchMethodException nsme) {
      try {
        java.lang.reflect.Constructor ctor = StandardTokenizer.class.getConstructor();
        StandardTokenizer t = (StandardTokenizer) ctor.newInstance();
        
        for (java.lang.reflect.Method method : t.getClass().getMethods()) {
          if ("setReader".equals(method.getName())) {
            method.invoke(t, sr);
          }
        }
        
        return t;
      }
      catch (Exception ex) {
        Log.log(ex);
      }
    }

    catch (SecurityException e) {
      return null;
    }

    try {
      java.lang.reflect.Constructor ctor = StandardTokenizer.class.getConstructor(Version.class, StringReader.class);
      return (StandardTokenizer) ctor.newInstance(m.invoke(this, (Object[]) null), sr);
    }
    catch (Exception e) {
      System.out.println("Ex: " + e);
    }

    return null;
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

    DataInputStream charstream = new DataInputStream(transducerfile);
    TransducerHeader h = new TransducerHeader(charstream);
    TransducerAlphabet a = new TransducerAlphabet(charstream, h.getSymbolCount());

    if (h.isWeighted()) {
      return new WeightedTransducer(charstream, h, a);
    }
    else {
      return new UnweightedTransducer(charstream, h, a);
    }
  }
}
