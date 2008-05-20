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

/**
 * see
 * http://code.google.com/apis/opensocial/docs/0.7/reference/opensocial.Address.Field.html
 *
 */
public class Address extends AbstractGadgetData {

  public static enum Field {
    COUNTRY("country"),
    EXTENDED_ADDRESS("extendedAddress"),
    LATITUDE("latitude"),
    LOCALITY("locality"),
    LONGITUDE("longitude"),
    PO_BOX("poBox"),
    POSTAL_CODE("postalCode"),
    REGION("region"),
    STREET_ADDRESS("streetAddress"),
    TYPE("type"),
    UNSTRUCTURED_ADDRESS("unstructuredAddress");

    private final String jsonString;

    private Field(String jsonString) {
      this.jsonString = jsonString;
    }

    @Override
    public String toString() {
      return this.jsonString;
    }
  }

  private String country;
  private String extendedAddress;
  private Float latitude;
  private Float longitude;
  private String locality;
  private String poBox;
  private String postalCode;
  private String region;
  private String streetAddress;
  private String type;
  private String unstructuredAddress;

  public Address(String unstructuredAddress) {
    this.unstructuredAddress = unstructuredAddress;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getExtendedAddress() {
    return extendedAddress;
  }

  public void setExtendedAddress(String extendedAddress) {
    this.extendedAddress = extendedAddress;
  }

  public Float getLatitude() {
    return latitude;
  }

  public void setLatitude(Float latitude) {
    this.latitude = latitude;
  }

  public String getLocality() {
    return locality;
  }

  public void setLocality(String locality) {
    this.locality = locality;
  }

  public Float getLongitude() {
    return longitude;
  }

  public void setLongitude(Float longitude) {
    this.longitude = longitude;
  }

  public String getPoBox() {
    return poBox;
  }

  public void setPoBox(String poBox) {
    this.poBox = poBox;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public String getStreetAddress() {
    return streetAddress;
  }

  public void setStreetAddress(String streetAddress) {
    this.streetAddress = streetAddress;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getUnstructuredAddress() {
    return unstructuredAddress;
  }

  public void setUnstructuredAddress(String unstructuredAddress) {
    this.unstructuredAddress = unstructuredAddress;
  }

}
