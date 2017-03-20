/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.omegat.core.data;

import org.omegat.tokenizer.ITokenizer;

/**
 *
 * @author tomi
 */
public interface IProject {
    ProjectProperties getProjectProperties();
    ITokenizer getSourceTokenizer();
    ITokenizer getTargetTokenizer();
}
