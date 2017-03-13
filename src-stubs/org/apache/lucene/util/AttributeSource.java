/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apache.lucene.util;

/**
 *
 * @author tomi
 */
public class AttributeSource {
  public static final class State {
    public final State captureState() { return null; }
    public final void restoreState(State state) {}
  }
}
