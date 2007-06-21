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

package org.exoplatform.services.chars.chardet ;

public abstract class Verifier {

  static final byte eStart = (byte)0;
  static final byte eError = (byte)1;
  static final byte eItsMe = (byte)2;
  static final int eidxSft4bits = 3;
  static final int eSftMsk4bits = 7;
  static final int eBitSft4bits = 2;
  static final int eUnitMsk4bits = 0x0000000F;
  
  protected int[] cclass   ; 
  protected int[] states   ; 
  protected int  stFactor ; 
  protected String charset  ; 

  Verifier() {
  }
  
  public int[]  cclass()   { return cclass ;   }
  public int[]  states()   { return states ;   }
  public int    stFactor() { return stFactor ; }
  public String charset()  { return charset ;  }

  public boolean isUCS2() { return  false; } ;
  
  public static byte getNextState(Verifier v, byte b, byte s) {

    return (byte) ( 0xFF & 
        (((v.states()[((
            (s*v.stFactor()+(((v.cclass()[((b&0xFF)>>Verifier.eidxSft4bits)]) 
                >> ((b & Verifier.eSftMsk4bits) << Verifier.eBitSft4bits)) 
                & Verifier.eUnitMsk4bits ))&0xFF)
                >> Verifier.eidxSft4bits) ]) >> (((
                    (s*v.stFactor()+(((v.cclass()[((b&0xFF)>>Verifier.eidxSft4bits)]) 
                        >> ((b & Verifier.eSftMsk4bits) << Verifier.eBitSft4bits)) 
                        & Verifier.eUnitMsk4bits ))&0xFF) 
                        & Verifier.eSftMsk4bits) << Verifier.eBitSft4bits)) & Verifier.eUnitMsk4bits )
    ) ;

  }


}
