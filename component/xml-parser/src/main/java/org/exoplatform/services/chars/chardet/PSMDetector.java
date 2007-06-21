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

public abstract class PSMDetector {

  public static final int ALL                 =  0 ;
  public static final int JAPANESE            =  1 ;
  public static final int CHINESE             =  2 ;
  public static final int SIMPLIFIED_CHINESE  =  3 ;
  public static final int TRADITIONAL_CHINESE =  4 ;
  public static final int KOREAN              =  5 ;

  public static final int NO_OF_LANGUAGES     =  6 ;
  public static final int MAX_VERIFIERS       = 16 ;

  Verifier[] mVerifier ;
  EUCStatistics[] mStatisticsData ;

  EUCSampler	mSampler = new EUCSampler() ;
  byte[]    mState = new byte[MAX_VERIFIERS] ;
  int[]     mItemIdx = new int[MAX_VERIFIERS] ;

  int     mItems ;
  int	   mClassItems ;

  boolean mDone ;
  boolean mRunSampler ;
  boolean mClassRunSampler ;

  public PSMDetector() {
    initVerifiers( PSMDetector.ALL );
    Reset() ;
  }

  public PSMDetector(int langFlag) {
    initVerifiers(langFlag);
    Reset() ;
  }

  public PSMDetector(int aItems, Verifier[] aVerifierSet, 
                     EUCStatistics[] aStatisticsSet)  {
    mClassRunSampler = ( aStatisticsSet != null ) ;
    mStatisticsData = aStatisticsSet ;
    mVerifier = aVerifierSet ;

    mClassItems = aItems ;
    Reset() ;
  }


  public void Reset() {
    mRunSampler = mClassRunSampler ;
    mDone = false ;
    mItems = mClassItems ;

    for(int i=0; i<mItems; i++) {
      mState[i] = 0;
      mItemIdx[i] = i;
    }

    mSampler.reset() ;
  }

  protected void initVerifiers(int currVerSet) {

    int currVerifierSet ;

    if (currVerSet >=0 && currVerSet < NO_OF_LANGUAGES ) {
      currVerifierSet = currVerSet ;
    }
    else {
      currVerifierSet = PSMDetector.ALL ;
    }

    mVerifier = null ;
    mStatisticsData = null ;

    if ( currVerifierSet == PSMDetector.TRADITIONAL_CHINESE ) {

      mVerifier = new Verifier[] {
          new UTF8Verifier(),
          new BIG5Verifier(),
          new ISO2022CNVerifier(),
          new EUCTWVerifier(),
          new CP1252Verifier(),
          new UCS2BEVerifier(),
          new UCS2LEVerifier()
      };

      mStatisticsData = new EUCStatistics[] {
          null,
          new Big5Statistics(),
          null,
          new EUCTWStatistics(),
          null,
          null,
          null
      };
    }

    //==========================================================
    else if ( currVerifierSet == PSMDetector.KOREAN ) {

      mVerifier = new Verifier[] {
          new UTF8Verifier(),
          new EUCKRVerifier(),
          new ISO2022KRVerifier(),
          new CP1252Verifier(),
          new UCS2BEVerifier(),
          new UCS2LEVerifier()
      };
    }

    //==========================================================
    else if ( currVerifierSet == PSMDetector.SIMPLIFIED_CHINESE ) {

      mVerifier = new Verifier[] {
          new UTF8Verifier(),
          new GB2312Verifier(),
          new GB18030Verifier(),
          new ISO2022CNVerifier(),
          new HZVerifier(),
          new CP1252Verifier(),
          new UCS2BEVerifier(),
          new UCS2LEVerifier()
      };
    }

    //==========================================================
    else if ( currVerifierSet == PSMDetector.JAPANESE ) {

      mVerifier = new Verifier[] {
          new UTF8Verifier(),
          new SJISVerifier(),
          new EUCJPVerifier(),
          new ISO2022JPVerifier(),
          new CP1252Verifier(),
          new UCS2BEVerifier(),
          new UCS2LEVerifier()
      };
    }
    //==========================================================
    else if ( currVerifierSet == PSMDetector.CHINESE ) {

      mVerifier = new Verifier[] {
          new UTF8Verifier(),
          new GB2312Verifier(),
          new GB18030Verifier(),
          new BIG5Verifier(),
          new ISO2022CNVerifier(),
          new HZVerifier(),
          new EUCTWVerifier(),
          new CP1252Verifier(),
          new UCS2BEVerifier(),
          new UCS2LEVerifier()
      };

      mStatisticsData = new EUCStatistics[] {
          null,
          new GB2312Statistics(),
          null,
          new Big5Statistics(),
          null,
          null,
          new EUCTWStatistics(),
          null,
          null,
          null
      };
    }

    //==========================================================
    else if ( currVerifierSet == PSMDetector.ALL ) {

      mVerifier = new Verifier[] {
          new UTF8Verifier(),
          new SJISVerifier(),
          new EUCJPVerifier(),
          new ISO2022JPVerifier(),
          new EUCKRVerifier(),
          new ISO2022KRVerifier(),
          new BIG5Verifier(),
          new EUCTWVerifier(),
          new GB2312Verifier(),
          new GB18030Verifier(),
          new ISO2022CNVerifier(),
          new HZVerifier(),
          new CP1252Verifier(),
          new UCS2BEVerifier(),
          new UCS2LEVerifier()
      };

      mStatisticsData = new EUCStatistics[] {
          null,
          null,
          new EUCJPStatistics(),
          null,
          new EUCKRStatistics(),
          null,
          new Big5Statistics(),
          new EUCTWStatistics(),
          new GB2312Statistics(),
          null,
          null,
          null,
          null,
          null,
          null
      };
    }

    mClassRunSampler = ( mStatisticsData != null ) ;
    mClassItems = mVerifier.length ;

  }

  public abstract void report(String charset) ;

  public boolean handleData(byte[] aBuf, int len) {

    int i,j;
    byte b, st;

    for( i=0; i < len; i++) {
      b = aBuf[i] ;

      for (j=0; j < mItems; ) {
        st = Verifier.getNextState( mVerifier[mItemIdx[j]], 
            b, mState[j]) ;
//      if (st != 0)
//      System.out.println( "state(0x" + Integer.toHexString(0xFF&b) +") =>"+ Integer.toHexString(st&0xFF)+ " " + mVerifier[mItemIdx[j]].charset());

        if (st == Verifier.eItsMe) {

//        System.out.println( "eItsMe(0x" + Integer.toHexString(0xFF&b) +") =>"+ mVerifier[mItemIdx[j]].charset());

          report( mVerifier[mItemIdx[j]].charset() );
          mDone = true ;
          return mDone ;

        } else if (st == Verifier.eError ) {

//        System.out.println( "eNotMe(0x" + Integer.toHexString(0xFF&b) +") =>"+ mVerifier[mItemIdx[j]].charset());
          mItems--;
          if (j < mItems ) {
            mItemIdx[j] = mItemIdx[mItems];	
            mState[j]   = mState[mItems];
          }

        } else {

          mState[j++] = st ;

        }
      }

      if ( mItems <= 1 ) {

        if( 1 == mItems) {
          report( mVerifier[mItemIdx[0]].charset() );
        }
        mDone = true ;
        return mDone ;

      } 

      int nonUCS2Num=0;
      int nonUCS2Idx=0;

      for(j=0; j<mItems; j++) {
        if ( (!(mVerifier[mItemIdx[j]].isUCS2())) &&
            (!(mVerifier[mItemIdx[j]].isUCS2())) )  {
          nonUCS2Num++ ;
          nonUCS2Idx = j ;
        }
      }

      if (1 == nonUCS2Num) {
        report( mVerifier[mItemIdx[nonUCS2Idx]].charset() );
        mDone = true ;
        return mDone ;
      }
    }

    if (mRunSampler) Sample(aBuf, len);

    return mDone ;
  }


  public void dataEnd() {

    if (mDone == true)
      return ;

    if (mItems == 2) {
      if ((mVerifier[mItemIdx[0]].charset()).equals("GB18030")) {
        report(mVerifier[mItemIdx[1]].charset()) ;
        mDone = true ;
      } else if ((mVerifier[mItemIdx[1]].charset()).equals("GB18030")) {
        report(mVerifier[mItemIdx[0]].charset()) ;
        mDone = true ;
      }
    }

    if (mRunSampler)
      Sample(null, 0, true);
  }

  public void Sample(byte[] aBuf, int aLen) {
    Sample(aBuf, aLen, false) ;
  }

  public void Sample(byte[] aBuf, int aLen, boolean aLastChance)
  {
    int possibleCandidateNum  = 0;
    int j;
    int eucNum=0 ;

    for (j=0; j< mItems; j++) {
      if (null != mStatisticsData[mItemIdx[j]]) 
        eucNum++ ;
      if ((!mVerifier[mItemIdx[j]].isUCS2()) && 
          (!(mVerifier[mItemIdx[j]].charset()).equals("GB18030")))
        possibleCandidateNum++ ;
    }

    mRunSampler = (eucNum > 1) ;

    if (mRunSampler) {
      mRunSampler = mSampler.sample(aBuf, aLen);
      if(((aLastChance && mSampler.getSomeData()) || 
          mSampler.enoughData())
          && (eucNum == possibleCandidateNum)) {
        mSampler.calFreq();

        int bestIdx = -1;
        int eucCnt=0;
        float bestScore = 0.0f;
        for(j = 0; j < mItems; j++) {
          if((null != mStatisticsData[mItemIdx[j]])  &&
              (!(mVerifier[mItemIdx[j]].charset()).equals("Big5"))) {
            float score = mSampler.getScore(
                mStatisticsData[mItemIdx[j]].mFirstByteFreq(),
                mStatisticsData[mItemIdx[j]].mFirstByteWeight(),
                mStatisticsData[mItemIdx[j]].mSecondByteFreq(),
                mStatisticsData[mItemIdx[j]].mSecondByteWeight() );
//          System.out.println("FequencyScore("+mVerifier[mItemIdx[j]].charset()+")= "+ score);
            if(( 0 == eucCnt++) || (bestScore > score )) {
              bestScore = score;
              bestIdx = j;
            } // if(( 0 == eucCnt++) || (bestScore > score )) 
          } // if(null != ...)
        } // for
        if (bestIdx >= 0)
        {
          report( mVerifier[mItemIdx[bestIdx]].charset());
          mDone = true;
        }
      } // if (eucNum == possibleCandidateNum)
    } // if(mRunSampler)
  }

  public String[] getProbableCharsets() {

    if (mItems <= 0) {
      String[] nomatch = new String[1];
      nomatch[0] = "nomatch" ;
      return nomatch ;
    }

    String ret[] = new String[mItems] ;
    for (int i=0; i<mItems; i++)
      ret[i] = mVerifier[mItemIdx[i]].charset() ;
    return ret ;
  }

}
