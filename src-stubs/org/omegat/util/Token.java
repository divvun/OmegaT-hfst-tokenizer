/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool 
          with fuzzy matching, translation memory, keyword search, 
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2000-2006 Keith Godfrey and Maxym Mykhalchuk
               2006 Henry Pijffers (henry.pijffers@saxnot.com)
               2007 Zoltan Bartko (bartkozoltan@bartkozoltan.com)
               2015 Aaron Madlon-Kay
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

package org.omegat.util;

import java.util.regex.Pattern;

/**
 * Offset marks the display offset of character - this might be different than
 * the characters position in the char array due existence of multi-char
 * characters.
 * <p>
 * Since 1.6 strips '&' in given token text.
 * 
 * @author Keith Godfrey
 * @author Maxym Mykhalchuk
 * @author Henry Pijffers (henry.pijffers@saxnot.com)
 * @author Zoltan Bartko
 * @author Aaron Madlon-Kay
 */
public class Token {
    /**
     * Two tokens are thought equal if their hash code is equal.
     */
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof Token) {
            return hash == ((Token) other).hash;
        }
        return false;
    }

    /**
     * -1 if text is null, text's hashcode otherwise.
     */
    private int hash;

    public int hashCode() {
        return hash;
    }

    private static Pattern AMP = Pattern.compile("\\&");

    private final String stripAmpersand(String s) {
        return AMP.matcher(s).replaceAll("");
    }

    /**
     * Creates a new token.
     * 
     * @param _text
     *            the text of the token
     * @param _offset
     *            the starting position of this token in parent string
     */
    public Token(String _text, int _offset) {
        this(_text, _offset, _text.length());
    }

    /**
     * Creates a new token.
     * 
     * @param _text
     *            the text of the token
     * @param _offset
     *            the starting position of this token in parent string
     * @param _length
     *            length of token
     */
    public Token(String _text, int _offset, int _length) {
        length = _length;
        hash = (_text == null) ? -1 : stripAmpersand(_text).hashCode();
        offset = _offset;
    }

    private int length;
    private int offset;

    /** Returns the length of a token. */
    public final int getLength() {
        return length;
    }

    /** Returns token's offset in a source string. */
    public final int getOffset() {
        return offset;
    }

    public final String toString() {
        return hash + "@" + offset;
    }

    /**
     * Return the section of the string denoted by the token
     */
    public String getTextFromString(String input) {
        return input.substring(offset, length + offset);
    }
    
    /**
     * Get the strings represented by the provided tokens, from the original string
     * they were produced from. For debugging purposes.
     */
    public static String[] getTextsFromString(Token[] tokens, String string) {
        String[] result = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            result[i] = tokens[i].getTextFromString(string);
        }
        return result;
    }
}
