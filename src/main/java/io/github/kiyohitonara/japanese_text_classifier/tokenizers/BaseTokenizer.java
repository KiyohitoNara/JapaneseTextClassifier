/*
 * Copyright 2019 Kiyohito Nara
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.kiyohitonara.japanese_text_classifier.tokenizers;

import com.ibm.icu.text.Transliterator;

import java.util.List;

abstract public class BaseTokenizer {
    private Transliterator transliterator;

    protected BaseTokenizer() {
        this.transliterator = Transliterator.getInstance("Halfwidth-Fullwidth");
    }

    abstract public List<String> tokenize(String sentence);

    protected String normalize(String sentence) {
        return this.transliterator.transliterate(sentence);
    }
}
