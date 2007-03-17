/* -*- Mode: C; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 2 -*-
 *
 * The contents of this file are subject to the Netscape Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/NPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is mozilla.org code.
 *
 * The Initial Developer of the Original Code is Netscape
 * Communications Corporation.  Portions created by Netscape are
 * Copyright (C) 1998 Netscape Communications Corporation. All
 * Rights Reserved.
 *
 * Contributor(s):
 */

package org.exoplatform.services.parser.chardet ;


public abstract class EUCStatistics {

  float[] mFirstByteFreq ;
  float   mFirstByteStdDev;
  float   mFirstByteMean;
  float   mFirstByteWeight;
  float[] mSecondByteFreq;
  float   mSecondByteStdDev;
  float   mSecondByteMean;
  float   mSecondByteWeight;

  public float[] mFirstByteFreq() { return mFirstByteFreq; }  
  public float   mFirstByteStdDev()  { return mFirstByteStdDev; }  
  public float   mFirstByteMean()  { return mFirstByteMean; }  
  public float   mFirstByteWeight()  { return mFirstByteWeight; }  
  public float[] mSecondByteFreq()  { return mSecondByteFreq; }  
  public float   mSecondByteStdDev()  { return mSecondByteStdDev; }  
  public float   mSecondByteMean()  { return mSecondByteMean; }  
  public float   mSecondByteWeight()  { return mSecondByteWeight; }  


//public abstract float[] mFirstByteFreq() ;
//public abstract float   mFirstByteStdDev();
//public abstract float   mFirstByteMean();
//public abstract float   mFirstByteWeight();
//public abstract float[] mSecondByteFreq();
//public abstract float   mSecondByteStdDev();
//public abstract float   mSecondByteMean();
//public abstract float   mSecondByteWeight();

}
