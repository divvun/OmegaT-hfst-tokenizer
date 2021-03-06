package no.divvun.tokenizer;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.AttributeSource.State;

import fi.seco.hfst.Transducer;
import fi.seco.hfst.Transducer.Result;
// import fi.seco.hfst.NoTokenizationException;


public final class HfstStemFilter extends TokenFilter {
  private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
  private final PositionIncrementAttribute posIncAtt = addAttribute(PositionIncrementAttribute.class);
  private final Transducer stemmer;
  private List<String> buffer;
  private State savedState;

  public HfstStemFilter(TokenStream input, Transducer transducer) {
    super(input);
    stemmer = transducer;
  }

  @Override
  public boolean incrementToken() throws IOException {
    if (buffer != null && !buffer.isEmpty()) {
      final char nextStem[] = buffer.remove(0).toCharArray();
      restoreState(savedState);
      posIncAtt.setPositionIncrement(0);
      termAtt.copyBuffer(nextStem, 0, nextStem.length);
      termAtt.setLength(nextStem.length);
      return true;
    }

    if (!input.incrementToken()) {
      return false;
    }

    try {
      Collection<Result> res = stemmer.analyze(termAtt.toString());
      List<String> stems = new ArrayList<String>();
      // res.forEach(s -> {
      for (Result s : res) {
        if (s.toString().contains("+Cmp")) continue;
        String stem = s.toString().replaceAll("[,#\\[\\] ]", "").replaceAll("\\+[^+]+","");
        // String stem = analysis.substring(0, analysis.indexOf("+"));
        if (!stems.contains(stem)) {
          // System.out.println("Hfst: Stem: " + stem);
          stems.add(stem);
        }
      }
      buffer = stems;
    } catch (Exception ex) {
      System.out.println("NoTokenizationException");
    }

    if (buffer.isEmpty()) { // we do not know this word, return it unchanged
      return true;
    }

    char stem[] = buffer.remove(0).toCharArray();

    // System.out.println(stem);

    termAtt.copyBuffer(stem, 0, stem.length);
    termAtt.setLength(stem.length);

    if (!buffer.isEmpty()) {
      savedState = captureState();
    }

    return true;
  }

  @Override
  public void reset() throws IOException {
    super.reset();
    buffer = null;
  }
}
