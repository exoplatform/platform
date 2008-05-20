/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.shindig.social.opensocial.model;

import org.apache.shindig.social.AbstractGadgetData;

import java.util.List;

/**
 * see
 * http://code.google.com/apis/opensocial/docs/0.7/reference/opensocial.Collection.html
 */
public class ApiCollection<T> extends AbstractGadgetData {
  private List<T> items;
  private int offset;
  private int totalSize;

  public ApiCollection(List<T> items) {
    this(items, 0, items.size());
  }

  public ApiCollection(List<T> items, int offset, int totalSize) {
    this.items = items;
    this.offset = offset;
    this.totalSize = totalSize;
  }

  public List<T> getItems() {
    return items;
  }

  public void setItems(List<T> items) {
    this.items = items;
  }

  public int getOffset() {
    return offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }

  public int getTotalSize() {
    return totalSize;
  }

  public void setTotalSize(int totalSize) {
    this.totalSize = totalSize;
  }
}
