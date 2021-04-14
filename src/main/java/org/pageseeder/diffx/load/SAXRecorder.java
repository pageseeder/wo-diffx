/*
 * Copyright 2010-2015 Allette Systems (Australia)
 * http://www.allette.com.au
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pageseeder.diffx.load;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.pageseeder.diffx.config.DiffXConfig;
import org.pageseeder.diffx.event.AttributeEvent;
import org.pageseeder.diffx.event.CloseElementEvent;
import org.pageseeder.diffx.event.OpenElementEvent;
import org.pageseeder.diffx.event.TextEvent;
import org.pageseeder.diffx.event.impl.EventFactory;
import org.pageseeder.diffx.event.impl.ProcessingInstructionEvent;
import org.pageseeder.diffx.load.text.TextTokenizer;
import org.pageseeder.diffx.load.text.TokenizerFactory;
import org.pageseeder.diffx.sequence.EventSequence;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.XMLConstants;

/**
 * Records the SAX events in an {@link org.pageseeder.diffx.sequence.EventSequence}.
 *
 * <p>It is possible to specify the name of the XML reader implementation class.
 * By default this class will try to use the Crimson parser
 * <code>org.apache.crimson.parser.XMLReaderImpl</code>.
 *
 * <p>The XML reader implementation must support the following features settings
 * <pre>
 *   http://xml.org/sax/features/validation         => false
 *   http://xml.org/sax/features/namespaces         => true | false
 *   http://xml.org/sax/features/namespace-prefixes => true | false
 * </pre>
 *
 * @author Christophe Lauret
 * @author Jean-Baptiste Reure
 *
 * @version 0.9.0
 * @since 0.6.0
 */
public final class SAXRecorder implements XMLRecorder {

  /**
   * The XML reader.
   */
  private static XMLReader reader;

  /**
   * The default XML reader in use.
   */
  private static final String DEFAULT_XML_READER;
  static {
    String className;
    try {
      className = XMLReaderFactory.createXMLReader().getClass().getName();
    } catch (SAXException ex) {
      // FIXME: Exception handling!!!
      //      className = XMLReaderImpl.class.getName();
      className = "";
    }
    DEFAULT_XML_READER = className;
  }

  /**
   * The XML reader class in use (set to the default XML reader).
   */
  private static String readerClassName = DEFAULT_XML_READER;

  /**
   * Indicates whether a new reader instance should be created because the specified class name
   * has changed.
   */
  private static boolean newReader = true;

  /**
   * The DiffX configuration to use
   */
  private DiffXConfig config = new DiffXConfig();

  /**
   * The sequence of event for this recorder.
   */
  protected EventSequence sequence;

  /**
   * Runs the recorder on the specified file.
   *
   * <p>This method will count on the {@link InputSource} to guess the correct encoding.
   *
   * @param file The file to process.
   *
   * @return The recorded sequence of events.
   *
   * @throws LoadingException If thrown while parsing.
   * @throws IOException      Should I/O error occur.
   */
  @Override
  public EventSequence process(File file) throws LoadingException, IOException {
    try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
      return process(new InputSource(in));
    }
  }

  /**
   * Runs the recorder on the specified string.
   *
   * <p>This method is provided for convenience. It is best to only use this method for
   * short strings.
   *
   * @param xml The XML string to process.
   *
   * @return The recorded sequence of events.
   *
   * @throws LoadingException If thrown while parsing.
   * @throws IOException      Should I/O error occur.
   */
  @Override
  public EventSequence process(String xml) throws LoadingException, IOException {
    return this.process(new InputSource(new StringReader(xml)));
  }

  /**
   * Runs the recorder on the specified input source.
   *
   * @param is The input source.
   *
   * @return The recorded sequence of events.
   *
   * @throws LoadingException If thrown whilst parsing.
   * @throws IOException      Should I/O error occur.
   */
  @Override
  public EventSequence process(InputSource is) throws LoadingException, IOException {
    if (reader == null || newReader) {
      init();
    }
    reader.setContentHandler(new RecorderHandler());
    reader.setErrorHandler(new RecorderErrorHandler());
    try {
      reader.setFeature("http://xml.org/sax/features/namespaces", this.config.isNamespaceAware());
      reader.setFeature("http://xml.org/sax/features/namespace-prefixes", this.config.isReportPrefixDifferences());
      reader.parse(is);
    } catch (SAXException ex) {
      throw new LoadingException(ex);
    }
    return this.sequence;
  }

  /**
   * Returns the configuration used by this recorder.
   *
   * @return the configuration used by this recorder.
   */
  public DiffXConfig getConfig() {
    return this.config;
  }

  /**
   * Sets the configuration used by this recorder.
   *
   * @param config The configuration used by this recorder.
   */
  public void setConfig(DiffXConfig config) {
    this.config = config;
  }

  /**
   * Returns the name XMLReader class used by the SAXRecorders.
   *
   * @return the name XMLReader class used by the SAXRecorders.
   */
  public static String getXMLReaderClass() {
    return readerClassName;
  }

  /**
   * Sets the name of the XML reader class to use.
   *
   * <p>Use <code>null</code> to reset the XML reader class and use the default XML reader.
   *
   * <p>A new reader will be created only if the specified class is different from the current one.
   *
   * @param className The name of the XML reader class to use;
   *                  or <code>null</code> to reset the XML reader.
   */
  public static void setXMLReaderClass(String className) {
    // if the className is null reset to default
    if (className == null) {
      className = DEFAULT_XML_READER;
    }
    // reload only if different from the current one.
    newReader = !className.equals(readerClassName);
    readerClassName = className;
  }

  /**
   * Initialises the XML reader using the defined class name.
   *
   * @throws LoadingException If one of the features could not be set.
   */
  private static void init() throws LoadingException {
    try {
      reader = XMLReaderFactory.createXMLReader(readerClassName);
      reader.setFeature("http://xml.org/sax/features/validation", false);
    } catch (SAXException ex) {
      throw new LoadingException(ex);
    }
  }

  // static inner class for processing the XML files --------------------------------------------

  /**
   * A SAX2 handler that records XML events.
   *
   * <p>This class is an inner class as there is no reason to expose its method to the
   * public API.
   *
   * @author Christophe Lauret, Jean-Baptiste Reure
   * @version 0.6.0
   */
  private final class RecorderHandler extends DefaultHandler {

    /**
     * A buffer for character data.
     */
    private final StringBuilder ch = new StringBuilder();

    /**
     * The comparator in order to sort attribute correctly.
     */
    private final AttributeComparator comparator = new AttributeComparator();

    /**
     * The weight of the current element.
     */
    private int currentWeight = -1;

    /**
     * The last open element event, should only contain <code>OpenElementEvent</code>s.
     */
    private final List<OpenElementEvent> openElements = new ArrayList<>();

    /**
     * The stack of weight, should only contain <code>Integer</code>.
     */
    private final List<Integer> weights = new ArrayList<>();

    /**
     * The factory that will produce events according to the configuration.
     */
    private EventFactory efactory;

    /**
     * The text tokenizer according to the configuration.
     */
    private TextTokenizer tokenizer;

    @Override
    public void startDocument() {
      SAXRecorder.this.sequence = new EventSequence();
      this.efactory = new EventFactory(SAXRecorder.this.config.isNamespaceAware());
      this.tokenizer = TokenizerFactory.get(SAXRecorder.this.config);
      SAXRecorder.this.sequence.addNamespace(XMLConstants.XML_NS_URI, XMLConstants.XML_NS_PREFIX);
      SAXRecorder.this.sequence.addNamespace(XMLConstants.NULL_NS_URI, XMLConstants.DEFAULT_NS_PREFIX);
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) {
      // For the root element only, we may replace the mapping to the default prefix
      // (this method is called BEFORE the start element)
      SAXRecorder.this.sequence.addNamespace(uri, prefix, this.openElements.isEmpty());
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
      recordCharacters();
      if (this.currentWeight > 0) {
        this.weights.add(this.currentWeight);
      }
      this.currentWeight = 1;
      OpenElementEvent open = this.efactory.makeOpenElement(uri, localName, qName);
      this.openElements.add(open);
      SAXRecorder.this.sequence.addEvent(open);
      handleAttributes(attributes);
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
      recordCharacters();
      OpenElementEvent open = popLastOpenElement();
      open.setWeight(this.currentWeight);
      CloseElementEvent close = this.efactory.makeCloseElement(open);
      close.setWeight(this.currentWeight);
      SAXRecorder.this.sequence.addEvent(close);
      // calculate weights
      this.currentWeight += popWeight();
    }

    @Override
    public void characters(char[] buf, int pos, int len) {
      this.ch.append(buf, pos, len);
    }

    @Override
    public void ignorableWhitespace(char[] buf1, int pos, int len) {
      // this method is only useful if the XML provides a Schema or DTD
      // to define in which cases whitespaces can be considered ignorable.
      // By default, all white spaces are significant and therefore reported
      // by the characters method.
    }

    @Override
    public void processingInstruction(String target, String data) {
      SAXRecorder.this.sequence.addEvent(new ProcessingInstructionEvent(target, data));
      this.currentWeight++;
    }

    @Override
    public void endDocument() {
    }

    /**
     * Records the characters which are in the buffer.
     */
    private void recordCharacters() {
      if (this.ch.length() > 0) {
        List<TextEvent> events = this.tokenizer.tokenize(this.ch);
        for (TextEvent e : events) {
          SAXRecorder.this.sequence.addEvent(e);
        }
        this.currentWeight += events.size();
        this.ch.setLength(0);
      }
    }

    /**
     * Returns the last open element and remove it from the stack.
     *
     * @return The last open element.
     */
    private OpenElementEvent popLastOpenElement() {
      return this.openElements.remove(this.openElements.size() - 1);
    }

    /**
     * Returns the last weight and remove it from the stack.
     *
     * @return The weight on top of the stack.
     */
    private int popWeight() {
      if (this.weights.size() > 0)
        return this.weights.remove(this.weights.size() - 1);
      else
        return 0;
    }

    /**
     * Handles the attributes, will add them to the sequence in order if any.
     *
     * @param attributes The attributes to handle.
     */
    private void handleAttributes(Attributes attributes) {
      // only one attribute
      if (attributes.getLength() == 1) {
        SAXRecorder.this.sequence.addEvent(this.efactory.makeAttribute(attributes.getURI(0),
            attributes.getLocalName(0),
            attributes.getQName(0),
            attributes.getValue(0)));
        // several attributes
      } else if (attributes.getLength() > 1) {
        // store all the attributes
        AttributeEvent[] attEvents = new AttributeEvent[attributes.getLength()];
        for (int i = 0; i < attributes.getLength(); i++) {
          attEvents[i] = this.efactory.makeAttribute(attributes.getURI(i),
              attributes.getLocalName(i),
              attributes.getQName(i),
              attributes.getValue(i));
          attEvents[i].setWeight(2);
          this.currentWeight += 2;
        }
        // sort them
        Arrays.sort(attEvents, this.comparator);
        // add them to the sequence
        for (AttributeEvent attEvent : attEvents) {
          SAXRecorder.this.sequence.addEvent(attEvent);
        }
      }
    }

  }

  /**
   * A tight error handler that will throw an exception for any error type.
   *
   * ErrorHandler used only so that namespace related errors are reported ???
   * (they are error type and not fatal error).
   *
   * @author Jean-baptiste Reure
   * @version 0.6.0
   */
  private static final class RecorderErrorHandler implements ErrorHandler {

    @Override
    public void error(SAXParseException ex) throws SAXException {
      throw ex;
    }

    @Override
    public void fatalError(SAXParseException ex) throws SAXException {
      throw ex;
    }

    @Override
    public void warning(SAXParseException ex) throws SAXException {
      throw ex;
    }
  }

}
