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
package org.pageseeder.diffx.handler;

import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.format.DiffXFormatter;

import java.io.IOException;
import java.io.UncheckedIOException;

public class FormattingAdapter implements DiffHandler {

  protected final DiffXFormatter formatter;

  public FormattingAdapter(DiffXFormatter formatter) {
    this.formatter = formatter;
  }

  @Override
  public void handle(Operator operator, DiffXEvent event) throws UncheckedIOException, IllegalStateException {
    try {
      switch (operator) {
        case MATCH:
          this.formatter.format(event);
          break;
        case INS:
          this.formatter.insert(event);
          break;
        case DEL:
          this.formatter.delete(event);
          break;
        default:
          // Ignore
      }
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  @Override
  public String toString() {
    return "FormattingAdapter("+this.formatter+")";
  }

}
