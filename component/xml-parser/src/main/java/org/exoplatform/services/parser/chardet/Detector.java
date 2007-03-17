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


public class Detector extends PSMDetector  implements ICharsetDetector {

  ICharsetDetectionObserver mObserver = null ;

  public Detector() {
    super() ;
  }

  public Detector(int langFlag) {
    super(langFlag) ;
  }

  public void init(ICharsetDetectionObserver aObserver) {
    mObserver = aObserver ;
  }

  public boolean doIt(byte[] aBuf, int aLen, boolean oDontFeedMe) {
    if (aBuf == null || oDontFeedMe ) return false ;

    this.handleData(aBuf, aLen) ;	
    return mDone ;
  }

  public void done() {
    this.dataEnd() ;
  }

  public void report(String charset) {
    if (mObserver != null) mObserver.notify(charset)  ;
  }

  public boolean isAscii(byte[] aBuf, int aLen) {
    for(int i=0; i<aLen; i++) {
      if ((0x0080 & aBuf[i]) != 0) {
        return false ;
      }
    }
    return true ;
  }
}
