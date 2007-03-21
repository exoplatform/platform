package org.exoplatform.services.text.unicode;

import java.util.BitSet;

/**
 * Accesses the Normalization Data used for Forms C and D.<br>
 * Copyright (c) 1998-1999 Unicode, Inc. All Rights Reserved.<br>
 * The Unicode Consortium makes no expressed or implied warranty of any
 * kind, and assumes no liability for errors or omissions.
 * No liability is assumed for incidental and consequential damages
 * in connection with or arising out of the use of the information here.
 * @author Mark Davis
 */
public class NormalizerData {

  public static final int NOT_COMPOSITE = '\uFFFF';

  boolean getExcluded (char ch) { return isExcluded.get(ch); }

  String getRawDecompositionMapping (char ch) { return decompose.get(ch); }

  private IntHashtable canonicalClass;

  private IntStringHashtable decompose;

  private IntHashtable compose;

  private BitSet isCompatibility = new BitSet();

  private BitSet isExcluded = new BitSet();

  public int getCanonicalClass(char ch) {
    return canonicalClass.get(ch);
  }

  public char getPairwiseComposition(char first, char second) {
    return (char)compose.get((first << 16) | second);
  }

  public void getRecursiveDecomposition(boolean canonical, char ch, StringBuilder buffer) {
    String decomp = decompose.get(ch); 
    if (decomp == null  || (canonical && isCompatibility.get(ch))) {
      buffer.append(ch);
      return;
    }
    for (int i = 0; i < decomp.length(); ++i) {
      getRecursiveDecomposition(canonical, decomp.charAt(i), buffer);
    }
  }

  NormalizerData(IntHashtable canonicalClass, IntStringHashtable decompose, 
      IntHashtable compose, BitSet isCompatibility, BitSet isExcluded) {
    this.canonicalClass = canonicalClass;
    this.decompose = decompose;
    this.compose = compose;
    this.isCompatibility = isCompatibility;
    this.isExcluded = isExcluded;
  }

}
