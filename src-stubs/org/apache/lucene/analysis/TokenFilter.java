/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apache.lucene.analysis;

import java.io.IOException;
import org.apache.lucene.util.AttributeSource;

/**
 *
 * @author tomi
 */
public abstract class TokenFilter extends TokenStream {
  protected final TokenStream input;
  protected TokenFilter(TokenStream input) { this.input = input; }
  public final <T> T addAttribute(Class<T> attClass) { return null; }
  public final AttributeSource.State captureState() { return null; }
  public final void restoreState(AttributeSource.State state) { }
  public boolean incrementToken() throws IOException {return false;}

  public void reset() throws IOException {
    input.reset();
  }
}
