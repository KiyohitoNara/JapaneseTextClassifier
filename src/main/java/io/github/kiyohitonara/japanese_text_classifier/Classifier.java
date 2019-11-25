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

package io.github.kiyohitonara.japanese_text_classifier;

import com.github.jfasttext.JFastText;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Files;
import io.github.kiyohitonara.japanese_text_classifier.tokenizers.BaseTokenizer;
import io.github.kiyohitonara.japanese_text_classifier.tokenizers.KuromojiTokenizer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

/**
 * 日本語の文書をシングルラベル分類するクラス
 *
 * @author Kiyohito Nara
 */
public class Classifier {
    private String path;

    private BaseTokenizer tokenizer;

    /**
     * @param path 出力パス
     */
    public Classifier(String path) {
        this.path = path;

        this.tokenizer = KuromojiTokenizer.getInstance();
    }

    /**
     * 学習を行います。
     *
     * @param path 学習データのパス
     */
    public void fit(String path) {
        Multimap<String, String> texts = loadFile(path);
        Multimap<String, String> tokenizedTexts = tokenizeTexts(texts);

        writeTexts(this.path + ".csv", tokenizedTexts);

        String[] args = {"supervised", "-input", this.path + ".csv", "-output", this.path};
        JFastText jFastText = new JFastText();
        jFastText.runCmd(args);
    }

    /**
     * 評価を行います。
     *
     * @param path  テストデータのパス
     * @param label 対象ラベル（全ラベルを対象にするときはnull）
     */
    public void evaluate(String path, String label) {
        Multimap<String, String> texts = loadFile(path);
        if (label != null && texts.containsKey(label)) {
            Multimap<String, String> tmpTexts = ArrayListMultimap.create();
            tmpTexts.putAll(label, texts.get(label));

            texts.clear();
            texts.putAll(tmpTexts);
        }

        Multimap<String, String> tokenizedTexts = tokenizeTexts(texts);

        writeTexts(this.path + ".csv", tokenizedTexts);

        String[] args = {"test", this.path + ".bin", this.path + ".csv"};
        JFastText jFastText = new JFastText();
        jFastText.runCmd(args);
    }

    /**
     * データを読み込みます。
     *
     * @param path データのパス
     * @return ラベルをkey、文書をvalueとしたMultimap
     */
    private Multimap<String, String> loadFile(String path) {
        System.out.println("Loading.");

        Multimap<String, String> texts = ArrayListMultimap.create();

        try {
            File file = new File(path);
            for (String line : Files.readLines(file, Charsets.UTF_8)) {
                String[] column = line.split(",", -1);
                texts.put(column[0], column[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return texts;
    }

    /**
     * 形態素解析を行います。
     *
     * @param texts 形態素解析する文書群
     * @return ラベルをkey、形態素解析された文書をvalueとしたMultimap
     */
    private Multimap<String, String> tokenizeTexts(Multimap<String, String> texts) {
        System.out.println("Tokenizing.");

        Multimap<String, String> tokenizedTexts = ArrayListMultimap.create();

        for (Entry<String, String> text : texts.entries()) {
            List<String> words = this.tokenizer.tokenize(text.getValue());
            tokenizedTexts.put(text.getKey(), Joiner.on(" ").join(words));
        }

        return tokenizedTexts;
    }

    /**
     * 文書群をファイルに書き込みます。
     *
     * @param path  ファイルのパス
     * @param texts 書き込む文書群
     */
    private void writeTexts(String path, Multimap<String, String> texts) {
        System.out.println("Writing.");

        try {
            File file = new File(path);
            BufferedWriter bufferedWriter = Files.newWriter(file, Charsets.UTF_8);
            for (Entry<String, String> tokenizedText : texts.entries()) {
                bufferedWriter.write(tokenizedText.getKey());
                bufferedWriter.write(",");
                bufferedWriter.write(tokenizedText.getValue());
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 推定を行います。
     *
     * @param text 推定する文書
     * @return 推定ラベル
     */
    public String predict(String text) {
        List<String> words = this.tokenizer.tokenize(text);

        JFastText jFastText = new JFastText();
        jFastText.loadModel(this.path + ".bin");

        return jFastText.predict(Joiner.on(" ").join(words));
    }

    public static void main(String[] args) {
        Classifier classifier = new Classifier(args[0]);
        classifier.fit(args[1]);
        classifier.evaluate(args[2], null);
    }
}
