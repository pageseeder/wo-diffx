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
package org.pageseeder.diffx.event.impl;

import java.io.IOException;

import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.event.TextEvent;
import org.pageseeder.xmlwriter.XMLWriter;

/**
 * Event corresponding to a single character.
 *
 * @author Christophe Lauret
 *
 * @version 0.9.0
 * @since 0.7.0
 */
public final class CharEvent extends DiffXEventBase implements TextEvent {

  /**
   * The character associated with this event.
   */
  public final char c;

  /**
   * Creates a new character event.
   *
   * @param c The character to wrap.
   */
  public CharEvent(char c) {
    this.c = c;
  }

  public char getChar() {
    return this.c;
  }

  @Override
  public int hashCode() {
    return this.c;
  }

  @Override
  public boolean equals(DiffXEvent e) {
    if (e.getClass() != this.getClass()) return false;
    return this.c == ((CharEvent)e).c;
  }

  @Override
  public String toString() {
    return Character.toString(this.c);
  }

  @Override
  public void toXML(XMLWriter xml) throws IOException {
    xml.writeText(this.c);
  }

  @Override
  public StringBuffer toXML(StringBuffer xml) throws NullPointerException {
    // The code below assumes, this event is only used for text nodes (not within an attribute)
    switch (this.c) {
      case '<': return xml.append("&lt;");
      case '&': return xml.append("&amp;");
      case '>': return xml.append("&gt;");
      default: return xml.append(this.c);
    }
  }

  @Override
  public String getCharacters() {
    return Character.toString(this.c);
  }
}
