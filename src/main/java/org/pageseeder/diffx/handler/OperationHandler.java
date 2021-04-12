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

import org.pageseeder.diffx.action.Operation;
import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.event.DiffXEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates a list of operations from the output of the algorithms.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public class OperationHandler implements DiffHandler {

  /**
   * The list of operations produced by this formatter.
   */
  private final List<Operation> operations = new ArrayList<>();

  /**
   * Adds the event to an operation
   */
  @Override
  public void handle(Operator operator, DiffXEvent event) {
    this.operations.add(new Operation(operator, event));
  }

  /**
   * @return the list of actions generated by this formatter.
   */
  public List<Operation> getOperations() {
    return this.operations;
  }

}
