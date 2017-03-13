/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apache.lucene.analysis;

import java.io.IOException;
/**
 *
 * @author tomi
 */
public abstract class TokenStream {
    public abstract boolean incrementToken() throws IOException;
    public void reset() throws IOException {}
}
