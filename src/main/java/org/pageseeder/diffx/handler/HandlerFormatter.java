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
import org.pageseeder.diffx.config.DiffXConfig;
import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.format.DiffXFormatter;

public class HandlerFormatter implements DiffXFormatter {

  private final DiffHandler handler;

  public HandlerFormatter(DiffHandler handler) {
    this.handler = handler;
  }

  @Override
  public void format(DiffXEvent event) throws IllegalStateException {
    this.handler.handle(Operator.MATCH, event);
  }

  @Override
  public void insert(DiffXEvent event) throws IllegalStateException {
    this.handler.handle(Operator.INS, event);
  }

  @Override
  public void delete(DiffXEvent event) throws IllegalStateException {
    this.handler.handle(Operator.DEL, event);
  }

  @Override
  public void setConfig(DiffXConfig config) {

  }
}
