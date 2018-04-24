/**
 * pdfmonkパッケージ
 * <p>
 * 基本的には、itextをラップしたライブラリに過ぎないが、しかしラッパライブラリを提供する理由は以下である。
 * </p>
 * <ul>
 * <li>PDFの座標系は、ページ左下を原点とし、右上方向に数字が大きくなる座標系になっている。これは非常に扱いにくい。
 * <li>加えて、PDFの座標系がポイント単位になっていること。これも非常に扱いにくい。
 * <li>そもそものPDFの仕様、つまり４層のレイヤーに個別に描画ができ、それを重ね合わせた結果が最終結果となるという方式がわかりにくい
 * （そして、この４層レイヤーは等しく同じ機能を持っているわけではない）。
 * </ul>
 * <p>
 * これを解消するために、以下の機能を提供する。
 * </p>
 * <ul>
 * <li>ミリメートルでパージサイズを定義し、上下左右のマージンを指定できるようにする。
 * <li>座標系としては、ページの印刷領域の左上を原点とし、右下に伸びるミリメートルの座標系とする。
 * <li>できる限り、４層のレイヤーの扱いが明確になるようにする。ただし、本来的な制限から、単純な扱いにはならないのだが。
 * </ul>
 * <p>
 * このライブラリで基本となるのはitextのドキュメントをラップした{@link MkDocument}である。{@link MkDocument}を参照のこと。
 * </p>
 */
package com.cm55.pdfmonk;
