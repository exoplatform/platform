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

import java.util.Date;

/**
 * see
 * http://code.google.com/apis/opensocial/docs/0.7/reference/opensocial.Organization.Field.html
 *
 */
public class Organization extends AbstractGadgetData {

  public static enum Field {
    ADDRESS("address"),
    DESCRIPTION("description"),
    END_DATE("endDate"),
    FIELD("field"),
    NAME("name"),
    SALARY("salary"),
    START_DATE("startDate"),
    SUB_FIELD("subField"),
    TITLE("title"),
    WEBPAGE("webpage");

    private final String jsonString;

    private Field(String jsonString) {
      this.jsonString = jsonString;
    }

    @Override
    public String toString() {
      return this.jsonString;
    }
  }

  private Address address;
  private String description;
  private Date endDate;
  private String field;
  private String name;
  private String salary;
  private Date startDate;
  private String subField;
  private String title;
  private String webpage;

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSalary() {
    return salary;
  }

  public void setSalary(String salary) {
    this.salary = salary;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public String getSubField() {
    return subField;
  }

  public void setSubField(String subField) {
    this.subField = subField;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getWebpage() {
    return webpage;
  }

  public void setWebpage(String webpage) {
    this.webpage = webpage;
  }

}
