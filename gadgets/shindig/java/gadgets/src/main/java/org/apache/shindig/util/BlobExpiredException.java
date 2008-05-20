/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shindig.util;

import java.util.Date;

/**
 * Thrown when a blob has expired.
 */
public class BlobExpiredException extends BlobCrypterException {

  public final Date minDate;
  public final Date used;
  public final Date maxDate;

  public BlobExpiredException(long minTime, long now, long maxTime) {
    this(new Date(minTime*1000), new Date(now*1000), new Date(maxTime*1000));
  }

  public BlobExpiredException(Date minTime, Date now, Date maxTime) {
    super("Blob expired, was valid from " + minTime + " to " + maxTime
        + ", attempted use at " + now);
    this.minDate = minTime;
    this.used = now;
    this.maxDate = maxTime;
  }

}
