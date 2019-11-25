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

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Kuromojiで形態素解析するクラス
 *
 * @author Kiyohito Nara
 */
public class KuromojiTokenizer extends BaseTokenizer {
    private static final KuromojiTokenizer INSTANCE = new KuromojiTokenizer();

    private Tokenizer tokenizer;

    private KuromojiTokenizer() {
        super();

        this.tokenizer = new Tokenizer();
    }

    public static KuromojiTokenizer getInstance() {
        return INSTANCE;
    }

    /**
     * 形態素解析を行います。
     *
     * @param text 形態素解析する文書
     * @return 入力文書の形態素群
     */
    public List<String> tokenize(String text) {
        String normalizedSentence = super.normalize(text);

        List<String> words = Lists.newArrayList();
        for (Token token : this.tokenizer.tokenize(normalizedSentence)) {
            words.add(token.getSurface());
        }

        return words;
    }
}
