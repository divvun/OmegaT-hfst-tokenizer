/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apache.lucene.analysis.tokenattributes;

/**
 *
 * @author tomi
 */
public interface CharTermAttribute {
    public void copyBuffer(char[] buffer, int offset, int length);
    public CharTermAttribute setLength(int length);
}
