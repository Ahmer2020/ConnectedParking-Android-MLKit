/*
 * Copyright 2020 Google LLC. All rights reserved.
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

package com.bbits.park.textdetector;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.bbits.park.GlobalContext;
import com.bbits.park.GraphicOverlay;
import com.bbits.park.VisionProcessorBase;
import com.bbits.park.preference.PreferenceUtils;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.Text.Element;
import com.google.mlkit.vision.text.Text.Line;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptionsInterface;

import java.util.List;
import java.util.regex.Pattern;


/** Processor for the text detector demo. */
public class TextRecognitionProcessor extends VisionProcessorBase<Text> {
  GlobalContext data= new GlobalContext();


//  "[A-Z]{3,4}-?\\s?\\d{3,4}|\\d{2}[A-Z]-?\\s?[A-Z]{3,4}"
//  "[A-Z]{3,4}[-|\\s]?\\d{3,4}|\\d{2}[A-Z][-|\\s]?[A-Z]{3,4}"

//  "\\d{2}[A-Z][-|\\s]?"

  private Pattern digitPattern = Pattern.compile("[A-Z]{3,4}[-|\\s]?\\d{3,4}|\\d{2}[A-Z][-|\\s]?[A-Z]{3,4}");


  private static final String TAG = "TextRecProcessor";

  private final TextRecognizer textRecognizer;

  private final EditText editText;
  private final Boolean shouldGroupRecognizedTextInBlocks;
  private final Boolean showLanguageTag;
  private final boolean showConfidence;

//  EditText plateNumEditText = (EditText) findViewById(R.id);
//
//  @Override
//  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//    View view =  inflater.inflate(R.layout.secondefragment, container, false);
//    mWebView = (WebView) view.findViewById(R.id.activity_main_webview);
//    progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
//
//    WebSettings webSettings = mWebView.getSettings();
//    webSettings.setJavaScriptEnabled(true);
//    mWebView.loadUrl("http://www.google.com");
//
//    return view;
//
//
//  }


  public TextRecognitionProcessor(
          Context context, TextRecognizerOptionsInterface textRecognizerOptions, EditText editT) {
    super(context);
    shouldGroupRecognizedTextInBlocks = PreferenceUtils.shouldGroupRecognizedTextInBlocks(context);
    showLanguageTag = PreferenceUtils.showLanguageTag(context);
    showConfidence = PreferenceUtils.shouldShowTextConfidence(context);
    textRecognizer = TextRecognition.getClient(textRecognizerOptions);
//    editText.setText(data.getPlateNum());
    editText = editT;
  }

  @Override
  public void stop() {
    super.stop();
    textRecognizer.close();
  }

  @Override
  protected Task<Text> detectInImage(InputImage image) {

    return textRecognizer.process(image);
//    return textRecognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
//      @Override
//      public void onSuccess(Text text) {
//        data.setPlateNum(text.getText());
//
//      }
//    }).addOnFailureListener(new OnFailureListener() {
//              @Override
//              public void onFailure(@NonNull Exception e) {
//                data.setPlateNum("");
//              }
//            });
  }

  @Override
  protected void onSuccess(@NonNull Text text, @NonNull GraphicOverlay graphicOverlay) {


    String resultText = text.getText();
    String elementText = null;
    for (Text.TextBlock block : text.getTextBlocks()) {
      String blockText = block.getText();
//      Point[] blockCornerPoints = block.getCornerPoints();
//      Rect blockFrame = block.getBoundingBox();
      for (Text.Line line : block.getLines()) {
        String lineText = line.getText();

        Log.e("READ:",lineText);

        if(digitPattern.matcher(lineText).matches()){
//          Log.e("READ:",lineText);
          data.setPlateNum(lineText);
          editText.setText(lineText);

        }
//        Point[] lineCornerPoints = line.getCornerPoints();
//        Rect lineFrame = line.getBoundingBox();

//        for (Text.Element element : line.getElements()) {
//          elementText = element.getText();
//
////          if(){
////            data.setPlateNum(elementText);
////            editText.setText(elementText);
////          }
//
//
////          Log.e("READ:",elementText);
////
////          if(digitPattern.matcher(elementText).matches()){
////            Log.e("READ:",elementText);
////                  data.setPlateNum(elementText);
////                  editText.setText(elementText);
////
//////            graphicOverlay.add(
//////                    new TextGraphic(
//////                            graphicOverlay,
//////                            text,
//////                            shouldGroupRecognizedTextInBlocks,
//////                            showLanguageTag,
//////                            showConfidence));
////
////          }
//
//
//
////          Point[] elementCornerPoints = element.getCornerPoints();
////          Rect elementFrame = element.getBoundingBox();
////          for (Text.Symbol symbol : element.getSymbols()) {
////            String symbolText = symbol.getText();
//////            Point[] symbolCornerPoints = symbol.getCornerPoints();
//////            Rect symbolFrame = symbol.getBoundingBox();
////          }
//
//
//
//
//
//        }
      }
    }


//    if(elementText!=null && elementText.matches("[A-Z]{3,4}\\d{3,4}") ) {
//      data.setPlateNum(elementText);
//      editText.setText(elementText);
//    }
//if(elementText!=null) {
//  data.setPlateNum(elementText);
//  editText.setText(elementText);
//}

    Log.d(TAG, "On-device Text detection successful");
    logExtrasForTesting(text);
    graphicOverlay.add(
            new TextGraphic(
                    graphicOverlay,
                    text,
                    shouldGroupRecognizedTextInBlocks,
                    showLanguageTag,
                    showConfidence));

//    return elementText;
  }

  private static void logExtrasForTesting(Text text) {
    if (text != null) {
      Log.v(MANUAL_TESTING_LOG, "Detected text has : " + text.getTextBlocks().size() + " blocks");
      for (int i = 0; i < text.getTextBlocks().size(); ++i) {
        List<Line> lines = text.getTextBlocks().get(i).getLines();
        Log.v(
            MANUAL_TESTING_LOG,
            String.format("Detected text block %d has %d lines", i, lines.size()));
        for (int j = 0; j < lines.size(); ++j) {
          List<Element> elements = lines.get(j).getElements();
          Log.v(
              MANUAL_TESTING_LOG,
              String.format("Detected text line %d has %d elements", j, elements.size()));
          for (int k = 0; k < elements.size(); ++k) {
            Element element = elements.get(k);
            Log.v(
                MANUAL_TESTING_LOG,
                String.format("Detected text element %d says: %s", k, element.getText()));
            Log.v(
                MANUAL_TESTING_LOG,
                String.format(
                    "Detected text element %d has a bounding box: %s",
                    k, element.getBoundingBox().flattenToString()));
            Log.v(
                MANUAL_TESTING_LOG,
                String.format(
                    "Expected corner point size is 4, get %d", element.getCornerPoints().length));
            for (Point point : element.getCornerPoints()) {
              Log.v(
                  MANUAL_TESTING_LOG,
                  String.format(
                      "Corner point for element %d is located at: x - %d, y = %d",
                      k, point.x, point.y));
            }
          }
        }
      }
    }
  }

  @Override
  protected void onFailure(@NonNull Exception e) {
    Log.w(TAG, "Text detection failed." + e);
  }
}
