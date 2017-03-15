/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apache.lucene.analysis.standard;

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.Version;

/**
 *
 * @author tomi
 */
public final class StandardTokenizer extends TokenStream {
    public StandardTokenizer(Version version, final Reader reader) {}
    public final void setReader(final Reader reader) throws IOException {}
    @Override
    public final boolean incrementToken() throws IOException { return false; }
}
