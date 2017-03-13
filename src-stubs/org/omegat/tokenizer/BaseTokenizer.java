/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool 
          with fuzzy matching, translation memory, keyword search, 
          glossaries, and translation leveraging into updated projects.
 
 Copyright (C) 2008 Alex Buloichik (alex73mail@gmail.com)
               2013, 2015 Aaron Madlon-Kay
               Home page: http://www.omegat.org/
               Support center: http://groups.yahoo.com/group/OmegaT/

 This file is part of OmegaT.

 OmegaT is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 OmegaT is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **************************************************************************/
package org.omegat.tokenizer;

import java.io.IOException;

import org.omegat.util.Language;
import org.omegat.util.Token;
import org.apache.lucene.analysis.TokenStream;
/**
 * Base class for Lucene-based tokenizers.
 * 
 * @author Alex Buloichik (alex73mail@gmail.com)
 * @author Aaron Madlon-Kay
 */
public abstract class BaseTokenizer implements ITokenizer {

    public Token[] tokenizeWords(final String strOrig, final StemmingMode stemmingMode) {return null;}
    
    public String[] tokenizeWordsToStrings(String str, StemmingMode stemmingMode) {return null;}

    public Token[] tokenizeVerbatim(final String strOrig) {return null;}
    
    public String[] tokenizeVerbatimToStrings(String str) {return null;}
    
    protected abstract TokenStream getTokenStream(String strOrig, boolean stemsAllowed, boolean stopWordsAllowed)
            throws IOException;
   
    public String[] getSupportedLanguages() { return null; }
    
    protected Language getLanguage() { return null; }

}
