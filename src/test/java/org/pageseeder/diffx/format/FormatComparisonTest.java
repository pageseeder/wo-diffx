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
package org.pageseeder.diffx.format;

import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.DiffXException;
import org.pageseeder.diffx.action.Operation;
import org.pageseeder.diffx.action.Operations;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.core.DefaultXMLProcessor;
import org.pageseeder.diffx.handler.OperationHandler;
import org.pageseeder.diffx.test.Events;
import org.pageseeder.diffx.token.Token;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

public class FormatComparisonTest {

  @Test
  public void compareFormatsExample1() throws IOException, DiffXException {
    String xml1 = "<body><p class='test'>Hello</p><ul><li>Monday evening</li><li>Tuesday night</li></ul></body>";
    String xml2 = "<body><p id='a'>Hello</p><ol><li>Monday</li><li>Thursday night</li></ol></body>";
    List<Operation> operations = toOperations(xml1, xml2);
    printAllFormats(operations);
  }

  @Test
  public void compareFormatsExample2() throws IOException, DiffXException {
    String xml1 = "<body><p id='1'>Other representations might be used by specialist equipment</p></body>";
    String xml2 = "<body><p id='2'>Another representation may be used by specialist equipment.</p></body>";
    List<Operation> operations = toOperations(xml1, xml2);
    printAllFormats(operations);
  }

  @Test
  public void compareOutputExample1() throws IOException, DiffXException {
    String xml1 = "<body><p class='test'>Hello</p><ul><li>Monday evening</li><li>Tuesday night</li></ul></body>";
    String xml2 = "<body><p id='a'>Hello</p><ol><li>Monday</li><li>Thursday night</li></ol></body>";
    List<Operation> operations = toOperations(xml1, xml2);
    printAllOutputs(operations);
  }

  @Test
  public void compareOutputExample2() throws IOException, DiffXException {
    String xml1 = "<body><p id='1'>Other representations might be used by specialist equipment</p></body>";
    String xml2 = "<body><p id='2'>Another representation may be used by specialist equipment.</p></body>";
    List<Operation> operations = toOperations(xml1, xml2);
    printAllOutputs(operations);
  }

  private static List<Operation> toOperations(String xml1, String xml2) throws DiffXException {
    List<? extends Token> from = Events.recordXMLEvents(xml1, TextGranularity.SPACE_WORD);
    List<? extends Token> to = Events.recordXMLEvents(xml2, TextGranularity.SPACE_WORD);
    OperationHandler handler = new OperationHandler();
    DefaultXMLProcessor processor = new DefaultXMLProcessor();
    processor.setCoalesce(true);
    processor.diff(to, from, handler);
    return handler.getOperations();
  }

  private static void printAllFormats(List<Operation> operations) throws IOException {
    printSafeXMLFormatter(operations);
  }

  private static void printAllOutputs(List<Operation> operations) throws IOException {
    printBasicXMLOutput(operations);
    printConvenientXMLDiffOutput(operations);
    printDefaultXMLOutput(operations);
    printSmartXMLDiffOutput(operations);
    printStrictXMLOutput(operations);
  }

  private static void printSafeXMLFormatter(List<Operation> operations) throws IOException {
    StringWriter xml = new StringWriter();
    XMLDiffXFormatter formatter = new SafeXMLFormatter(xml);
    formatter.setWriteXMLDeclaration(false);
    Operations.format(operations, formatter);
    xml.flush();
    System.out.println(formatter.getClass().getSimpleName());
    System.out.println(xml);
  }

  private static void printStrictXMLOutput(List<Operation> operations){
    StringWriter xml = new StringWriter();
    XMLDiffOutput output = new StrictXMLDiffOutput(xml);
    printXMLDiffOutput(operations, output);
    System.out.println(output.getClass().getSimpleName());
    System.out.println(xml);
  }

  private static void printConvenientXMLDiffOutput(List<Operation> operations) {
    StringWriter xml = new StringWriter();
    XMLDiffOutput output = new ConvenientXMLDiffOutput(xml);
    printXMLDiffOutput(operations, output);
    System.out.println(output.getClass().getSimpleName());
    System.out.println(xml);
  }

  private static void printBasicXMLOutput(List<Operation> operations) {
    StringWriter xml = new StringWriter();
    XMLDiffOutput output = new BasicXMLDiffOutput(xml);
    printXMLDiffOutput(operations, output);
    System.out.println(output.getClass().getSimpleName());
    System.out.println(xml);
  }

  private static void printDefaultXMLOutput(List<Operation> operations) {
    StringWriter xml = new StringWriter();
    DefaultXMDiffOutput output = new DefaultXMDiffOutput(xml);
    printXMLDiffOutput(operations, output);
    System.out.println(output.getClass().getSimpleName());
    System.out.println(xml);
  }

  private static void printSmartXMLDiffOutput(List<Operation> operations) throws IOException {
    StringWriter xml = new StringWriter();
    XMLDiffOutput output = new SmartXMLDiffOutput(xml);
    printXMLDiffOutput(operations, output);
    System.out.println(output.getClass().getSimpleName());
    System.out.println(xml);
  }

  private static void printXMLDiffOutput(List<Operation> operations, XMLDiffOutput output) {
    output.setWriteXMLDeclaration(false);
    output.start();
    Operations.handle(operations, output);
    output.end();
  }

}
