# JapaneseTextClassifier
JapaneseTextClassifierは日本語の文書をシングルラベル分類するライブラリです。

内部では[fastText](https://fasttext.cc/)が使われており、入力データのフォーマットはfastTextに準拠しています。

入力データを自動で形態素解析するため、分かち書きの必要はありません。

## 使い方
```
$ java -jar japanese-text-classifier-0.0.1-all.jar sample train.csv test.csv
```
`model`は出力パス、`train.csv`は学習データ、`test.csv`はテストデータです。

## ライセンス
```
Copyright 2019 Kiyohito Nara

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```