/*
 * Copyright (c) 2010-2021 Allette Systems (Australia)
 *    http://www.allette.com.au
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pageseeder.diffx.test;

import org.pageseeder.diffx.DiffXException;
import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.config.DiffXConfig;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.format.SmartXMLDiffOutput;
import org.pageseeder.diffx.load.DOMRecorder;
import org.pageseeder.diffx.load.LineRecorder;
import org.pageseeder.diffx.load.SAXRecorder;
import org.pageseeder.diffx.sequence.Sequence;
import org.pageseeder.diffx.token.TextToken;
import org.pageseeder.diffx.token.Token;
import org.pageseeder.diffx.token.impl.CharToken;
import org.pageseeder.diffx.token.impl.IgnorableSpaceToken;
import org.pageseeder.diffx.token.impl.WordToken;
import org.pageseeder.diffx.xml.PrefixMapping;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for tokens and testing.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class Events {

  /**
   * Prevents creation of instances.
   */
  private Events() {
  }

  public static TextToken toTextToken(String text) {
    if (text.matches("\\s+")) {
      return new IgnorableSpaceToken(text);
    }
    return new WordToken(text);
  }

  public static List<TextToken> toTextTokens(String... words) {
    List<TextToken> tokens = new ArrayList<>();
    for (String word : words) {
      tokens.add(toTextToken(word));
    }
    return tokens;
  }

  public static List<CharToken> toCharTokens(String string) {
    List<CharToken> s = new ArrayList<>();
    for (char c : string.toCharArray()) {
      s.add(new CharToken(c));
    }
    return s;
  }


  public static List<Token> recordXMLEvents(String xml, TextGranularity granularity) throws DiffXException {
    if (xml.isEmpty()) return Collections.emptyList();
    DiffXConfig config = new DiffXConfig();
    config.setGranularity(granularity);
    return recordXMLEvents(xml, config);
  }

  public static List<Token> recordXMLEvents(String xml, DiffXConfig config) throws DiffXException {
    SAXRecorder recorder = new SAXRecorder();
    recorder.setConfig(config);
    return recorder.process(xml).tokens();
  }


  public static Sequence recordXMLSequence(String xml, TextGranularity granularity) throws DiffXException {
    if (xml.isEmpty()) return new Sequence();
    DiffXConfig config = new DiffXConfig();
    config.setGranularity(granularity);
    return recordXMLSequence(xml, config);
  }

  public static Sequence recordXMLSequence(String xml, DiffXConfig config) throws DiffXException {
    SAXRecorder recorder = new SAXRecorder();
    recorder.setConfig(config);
    return recorder.process(xml);
  }


  public static Sequence recordXMLSequence(Document xml, TextGranularity granularity) throws DiffXException {
    DOMRecorder loader = new DOMRecorder();
    DiffXConfig config = new DiffXConfig();
    config.setGranularity(granularity);
    loader.setConfig(config);
    return loader.process(xml);
  }


  public static List<Token> recordLineEvents(String text) {
    if (text.isEmpty()) return Collections.emptyList();
    return new LineRecorder().process(text).tokens();
  }

  public static String toXML(List<? extends Token> tokens) {
    return toXML(tokens, new PrefixMapping());
  }

  public static String toXML(List<? extends Token> tokens, PrefixMapping mapping) {
    try {
      StringWriter xml = new StringWriter();
      SmartXMLDiffOutput f = new SmartXMLDiffOutput(xml);
      f.setWriteXMLDeclaration(false);
      f.declarePrefixMapping(mapping);

      for (Token token : tokens) {
        f.handle(Operator.MATCH, token);
      }
      return xml.toString();
    } catch (IOException ex) {
      // Shouldn't occur as we're writing on a StringWriter
      throw new UncheckedIOException(ex);
    }

  }


}
