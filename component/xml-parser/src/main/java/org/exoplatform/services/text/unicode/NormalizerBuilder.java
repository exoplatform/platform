package org.exoplatform.services.text.unicode;

import java.util.BitSet;

import org.exoplatform.services.common.ThreadSoftRef;

/**
 * Builds the normalization tables. This is a separate class so that it
 * can be unloaded once not needed.<br>
 * Copyright (c) 1998-1999 Unicode, Inc. All Rights Reserved.<br>
 * The Unicode Consortium makes no expressed or implied warranty of any
 * kind, and assumes no liability for errors or omissions.
 * No liability is assumed for incidental and consequential damages
 * in connection with or arising out of the use of the information here.
 * @author Mark Davis
 *
 *  This file was modified for VietPad from the original supplied by Unicode, Inc.
 *  to contain all data to build a Normalizer for the union of Vietnamese and Latin 1.
 *  Unused sections have been removed, and Vietnamese characters above Latin 1 added.
 */
class NormalizerBuilder {
  
  protected static ThreadSoftRef<NormalizerBuilder> INSTANCE = new ThreadSoftRef<NormalizerBuilder>(NormalizerBuilder.class);
  
  static NormalizerBuilder getInstance() { return INSTANCE.getRef(); }
  
  public NormalizerBuilder(){
    
  }

  NormalizerData build() {
    IntHashtable canonicalClass = new IntHashtable(0);
    IntStringHashtable decompose = new IntStringHashtable(null);
    IntHashtable compose = new IntHashtable(NormalizerData.NOT_COMPOSITE);
    BitSet isCompatibility = new BitSet();
    BitSet isExcluded = new BitSet();
    setMinimalDecomp(canonicalClass, decompose, compose, isCompatibility); 
    return new NormalizerData(canonicalClass, decompose, compose, isCompatibility, isExcluded);
  }

  private void setMinimalDecomp(IntHashtable canonicalClass, IntStringHashtable decompose, 
                                       IntHashtable compose, BitSet isCompatibility) {
    String[] decomposeData = {

        // Latin 1, unchanged from original file

        "\u005E", "\u0020\u0302", "K",
        "\u005F", "\u0020\u0332", "K",
        "\u0060", "\u0020\u0300", "K",
        "\u00A0", "\u0020", "K",
        "\u00A8", "\u0020\u0308", "K",
        "\u00AA", "\u0061", "K",
        "\u00AF", "\u0020\u0304", "K",
        "\u00B2", "\u0032", "K",
        "\u00B3", "\u0033", "K",
        "\u00B4", "\u0020\u0301", "K",
        "\u00B5", "\u03BC", "K",
        "\u00B8", "\u0020\u0327", "K",
        "\u00B9", "\u0031", "K",
        "\u00BA", "\u006F", "K",
        "\u00BC", "\u0031\u2044\u0034", "K",
        "\u00BD", "\u0031\u2044\u0032", "K",
        "\u00BE", "\u0033\u2044\u0034", "K",
        "\u00C0", "\u0041\u0300", "",
        "\u00C1", "\u0041\u0301", "",
        "\u00C2", "\u0041\u0302", "",
        "\u00C3", "\u0041\u0303", "",
        "\u00C4", "\u0041\u0308", "",
        "\u00C5", "\u0041\u030A", "",
        "\u00C7", "\u0043\u0327", "",
        "\u00C8", "\u0045\u0300", "",
        "\u00C9", "\u0045\u0301", "",
        "\u00CA", "\u0045\u0302", "",
        "\u00CB", "\u0045\u0308", "",
        "\u00CC", "\u0049\u0300", "",
        "\u00CD", "\u0049\u0301", "",
        "\u00CE", "\u0049\u0302", "",
        "\u00CF", "\u0049\u0308", "",
        "\u00D1", "\u004E\u0303", "",
        "\u00D2", "\u004F\u0300", "",
        "\u00D3", "\u004F\u0301", "",
        "\u00D4", "\u004F\u0302", "",
        "\u00D5", "\u004F\u0303", "",
        "\u00D6", "\u004F\u0308", "",
        "\u00D9", "\u0055\u0300", "",
        "\u00DA", "\u0055\u0301", "",
        "\u00DB", "\u0055\u0302", "",
        "\u00DC", "\u0055\u0308", "",
        "\u00DD", "\u0059\u0301", "",
        "\u00E0", "\u0061\u0300", "",
        "\u00E1", "\u0061\u0301", "",
        "\u00E2", "\u0061\u0302", "",
        "\u00E3", "\u0061\u0303", "",
        "\u00E4", "\u0061\u0308", "",
        "\u00E5", "\u0061\u030A", "",
        "\u00E7", "\u0063\u0327", "",
        "\u00E8", "\u0065\u0300", "",
        "\u00E9", "\u0065\u0301", "",
        "\u00EA", "\u0065\u0302", "",
        "\u00EB", "\u0065\u0308", "",
        "\u00EC", "\u0069\u0300", "",
        "\u00ED", "\u0069\u0301", "",
        "\u00EE", "\u0069\u0302", "",
        "\u00EF", "\u0069\u0308", "",
        "\u00F1", "\u006E\u0303", "",
        "\u00F2", "\u006F\u0300", "",
        "\u00F3", "\u006F\u0301", "",
        "\u00F4", "\u006F\u0302", "",
        "\u00F5", "\u006F\u0303", "",
        "\u00F6", "\u006F\u0308", "",
        "\u00F9", "\u0075\u0300", "",
        "\u00FA", "\u0075\u0301", "",
        "\u00FB", "\u0075\u0302", "",
        "\u00FC", "\u0075\u0308", "",
        "\u00FD", "\u0079\u0301", "",

        // Vietnamese above Latin 1, added to original file from VietPad (http://vietpad.sourceforge.net)

        "\u0102", "\u0041\u0306", "",
        "\u0103", "\u0061\u0306", "",
        "\u0128", "\u0049\u0303", "",
        "\u0129", "\u0069\u0303", "",
        "\u0168", "\u0055\u0303", "",
        "\u0169", "\u0075\u0303", "",
        "\u01A0", "\u004F\u031B", "",
        "\u01A1", "\u006F\u031B", "",
        "\u01AF", "\u0055\u031B", "",
        "\u01B0", "\u0075\u031B", "",
        "\u1EA0", "\u0041\u0323", "",
        "\u1EA1", "\u0061\u0323", "",
        "\u1EA2", "\u0041\u0309", "",
        "\u1EA3", "\u0061\u0309", "",
        "\u1EA4", "\u00C2\u0301", "",
        "\u1EA5", "\u00E2\u0301", "",
        "\u1EA6", "\u00C2\u0300", "",
        "\u1EA7", "\u00E2\u0300", "",
        "\u1EA8", "\u00C2\u0309", "",
        "\u1EA9", "\u00E2\u0309", "",
        "\u1EAA", "\u00C2\u0303", "",
        "\u1EAB", "\u00E2\u0303", "",
        "\u1EAC", "\u1EA0\u0302", "",
        "\u1EAD", "\u1EA1\u0302", "",
        "\u1EAE", "\u0102\u0301", "",
        "\u1EAF", "\u0103\u0301", "",
        "\u1EB0", "\u0102\u0300", "",
        "\u1EB1", "\u0103\u0300", "",
        "\u1EB2", "\u0102\u0309", "",
        "\u1EB3", "\u0103\u0309", "",
        "\u1EB4", "\u0102\u0303", "",
        "\u1EB5", "\u0103\u0303", "",
        "\u1EB6", "\u1EA0\u0306", "",
        "\u1EB7", "\u1EA1\u0306", "",
        "\u1EB8", "\u0045\u0323", "",
        "\u1EB9", "\u0065\u0323", "",
        "\u1EBA", "\u0045\u0309", "",
        "\u1EBB", "\u0065\u0309", "",
        "\u1EBC", "\u0045\u0303", "",
        "\u1EBD", "\u0065\u0303", "",
        "\u1EBE", "\u00CA\u0301", "",
        "\u1EBF", "\u00EA\u0301", "",
        "\u1EC0", "\u00CA\u0300", "",
        "\u1EC1", "\u00EA\u0300", "",
        "\u1EC2", "\u00CA\u0309", "",
        "\u1EC3", "\u00EA\u0309", "",
        "\u1EC4", "\u00CA\u0303", "",
        "\u1EC5", "\u00EA\u0303", "",
        "\u1EC6", "\u1EB8\u0302", "",
        "\u1EC7", "\u1EB9\u0302", "",
        "\u1EC8", "\u0049\u0309", "",
        "\u1EC9", "\u0069\u0309", "",
        "\u1ECA", "\u0049\u0323", "",
        "\u1ECB", "\u0069\u0323", "",
        "\u1ECC", "\u004F\u0323", "",
        "\u1ECD", "\u006F\u0323", "",
        "\u1ECE", "\u004F\u0309", "",
        "\u1ECF", "\u006F\u0309", "",
        "\u1ED0", "\u00D4\u0301", "",
        "\u1ED1", "\u00F4\u0301", "",
        "\u1ED2", "\u00D4\u0300", "",
        "\u1ED3", "\u00F4\u0300", "",
        "\u1ED4", "\u00D4\u0309", "",
        "\u1ED5", "\u00F4\u0309", "",
        "\u1ED6", "\u00D4\u0303", "",
        "\u1ED7", "\u00F4\u0303", "",
        "\u1ED8", "\u1ECC\u0302", "",
        "\u1ED9", "\u1ECD\u0302", "",
        "\u1EDA", "\u01A0\u0301", "",
        "\u1EDB", "\u01A1\u0301", "",
        "\u1EDC", "\u01A0\u0300", "",
        "\u1EDD", "\u01A1\u0300", "",
        "\u1EDE", "\u01A0\u0309", "",
        "\u1EDF", "\u01A1\u0309", "",
        "\u1EE0", "\u01A0\u0303", "",
        "\u1EE1", "\u01A1\u0303", "",
        "\u1EE2", "\u01A0\u0323", "",
        "\u1EE3", "\u01A1\u0323", "",
        "\u1EE4", "\u0055\u0323", "",
        "\u1EE5", "\u0075\u0323", "",
        "\u1EE6", "\u0055\u0309", "",
        "\u1EE7", "\u0075\u0309", "",
        "\u1EE8", "\u01AF\u0301", "",
        "\u1EE9", "\u01B0\u0301", "",
        "\u1EEA", "\u01AF\u0300", "",
        "\u1EEB", "\u01B0\u0300", "",
        "\u1EEC", "\u01AF\u0309", "",
        "\u1EED", "\u01B0\u0309", "",
        "\u1EEE", "\u01AF\u0303", "",
        "\u1EEF", "\u01B0\u0303", "",
        "\u1EF0", "\u01AF\u0323", "",
        "\u1EF1", "\u01B0\u0323", "",
        "\u1EF2", "\u0059\u0300", "",
        "\u1EF3", "\u0079\u0300", "",
        "\u1EF4", "\u0059\u0323", "",
        "\u1EF5", "\u0079\u0323", "",
        "\u1EF6", "\u0059\u0309", "",
        "\u1EF7", "\u0079\u0309", "",
        "\u1EF8", "\u0059\u0303", "",
        "\u1EF9", "\u0079\u0303", "",

    };

    int[] classData = {
        0x0300, 230,
        0x0301, 230,
        0x0302, 230,
        0x0303, 230,
        0x0304, 230,
        0x0305, 230,
        0x0306, 230,
        0x0307, 230,
        0x0308, 230,
        0x0309, 230,
        0x030A, 230,
        0x030B, 230,
        0x030C, 230,
        0x030D, 230,
        0x030E, 230,
        0x030F, 230,
        0x0310, 230,
        0x0311, 230,
        0x0312, 230,
        0x0313, 230,
        0x0314, 230,
        0x0315, 232,
        0x0316, 220,
        0x0317, 220,
        0x0318, 220,
        0x0319, 220,
        0x031A, 232,
        0x031B, 216,
        0x031C, 220,
        0x031D, 220,
        0x031E, 220,
        0x031F, 220,
        0x0320, 220,
        0x0321, 202,
        0x0322, 202,
        0x0323, 220,
        0x0324, 220,
        0x0325, 220,
        0x0326, 220,
        0x0327, 202,
        0x0328, 202,
        0x0329, 220,
        0x032A, 220,
        0x032B, 220,
        0x032C, 220,
        0x032D, 220,
        0x032E, 220,
        0x032F, 220,
        0x0330, 220,
        0x0331, 220,
        0x0332, 220,
        0x0333, 220,
        0x0334, 1,
        0x0335, 1,
        0x0336, 1,
        0x0337, 1,
        0x0338, 1,
        0x0339, 220,
        0x033A, 220,
        0x033B, 220,
        0x033C, 220,
        0x033D, 230,
        0x033E, 230,
        0x033F, 230,
        0x0340, 230,
        0x0341, 230,
        0x0342, 230,
        0x0343, 230,
        0x0344, 230,
        0x0345, 240,
        0x0360, 234,
        0x0361, 234
    };


    for (int i = 0; i < decomposeData.length; i+=3) {
      char value = decomposeData[i].charAt(0);
      String decomp = decomposeData[i+1];
      boolean compat = decomposeData[i+2].equals("K");
      if (compat) isCompatibility.set(value);
      decompose.put(value, decomp);
      if (compat) continue;
      char first = '\u0000';
      char second = decomp.charAt(0);
      if (decomp.length() > 1) {
        first = second;
        second = decomp.charAt(1);
      }
      int pair = (first << 16) | second;
      compose.put(pair, value);
    }

    for (int i = 0; i < classData.length;) {
      canonicalClass.put(classData[i++], classData[i++]);
    }
  }

}
