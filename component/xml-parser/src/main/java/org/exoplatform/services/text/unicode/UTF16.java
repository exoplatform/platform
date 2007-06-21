/**
 *******************************************************************************
 * Copyright (C) 1996-2006, International Business Machines Corporation and    *
 * others. All Rights Reserved.                                                *
 *******************************************************************************
 */
package org.exoplatform.services.text.unicode;

import org.exoplatform.services.common.ThreadSoftRef;

public class UTF16 {
  
  private static final int LEAD_SURROGATE_SHIFT_ = 10;
  
  public static final int SUPPLEMENTARY_MIN_VALUE  = 0x10000;
  
  public static final int LEAD_SURROGATE_MIN_VALUE = 0xD800;
  
  public static final int TRAIL_SURROGATE_MAX_VALUE = 0xDFFF;
  
  private static final int TRAIL_SURROGATE_MASK_     = 0x3FF;
  
  public static final int TRAIL_SURROGATE_MIN_VALUE = 0xDC00;
  
  public static final int CODEPOINT_MIN_VALUE = 0;
  
  public static final int CODEPOINT_MAX_VALUE = 0x10ffff;
  
  public static final int SURROGATE_MIN_VALUE = LEAD_SURROGATE_MIN_VALUE;
  
  private static final int SURROGATE_OFFSET_ =
    SUPPLEMENTARY_MIN_VALUE - (SURROGATE_MIN_VALUE << LEAD_SURROGATE_SHIFT_) - TRAIL_SURROGATE_MIN_VALUE;

  private static final int LEAD_SURROGATE_OFFSET_ = 
    LEAD_SURROGATE_MIN_VALUE - (SUPPLEMENTARY_MIN_VALUE >> LEAD_SURROGATE_SHIFT_);
  
  static ThreadSoftRef<UTF16> INSTANCE = new ThreadSoftRef<UTF16>(UTF16.class);
  
  static UTF16 getInstance() { return INSTANCE.getRef(); }
  
  public int getRawSupplementary(char lead, char trail) {
    return (lead << LEAD_SURROGATE_SHIFT_) + trail + SURROGATE_OFFSET_;
  }
  
  public final int LEAD_SURROGATE_MAX_VALUE = 0xDBFF;
  
  public int charAt(String source, int offset16) {
    char single = source.charAt(offset16);
    if (single < LEAD_SURROGATE_MIN_VALUE) return single;
    return _charAt(source, offset16, single);
  }
  
  private int _charAt(String source, int offset16, char single) {
    if (single > TRAIL_SURROGATE_MAX_VALUE) return single;

    if (single <= LEAD_SURROGATE_MAX_VALUE) {
      ++ offset16;
      if (source.length() != offset16) {
        char trail = source.charAt(offset16);
        if (trail >= TRAIL_SURROGATE_MIN_VALUE &&
            trail <= TRAIL_SURROGATE_MAX_VALUE) return getRawSupplementary(single, trail);
      }
    } else {
      -- offset16;
      if (offset16 >= 0) {
        char lead = source.charAt(offset16);
        if (lead >= LEAD_SURROGATE_MIN_VALUE && lead <= LEAD_SURROGATE_MAX_VALUE) {
          return getRawSupplementary(lead, single);
        }
      }
    }
    return single; 
  }
  
  public int charAt(StringBuilder source, int offset16) {
    if (offset16 < 0 || offset16 >= source.length()) {
      throw new StringIndexOutOfBoundsException(offset16);
    }

    char single = source.charAt(offset16);
    if (!isSurrogate(single)) return single;

    if (single <= LEAD_SURROGATE_MAX_VALUE)  {
      ++ offset16;
      if (source.length() != offset16) {
        char trail = source.charAt(offset16);
        if (isTrailSurrogate(trail)) return getRawSupplementary(single, trail);
      }
    } else {
      -- offset16;
      if (offset16 >= 0) {
        char lead = source.charAt(offset16);
        if (isLeadSurrogate(lead)) return getRawSupplementary(lead, single);
      }
    }
    return single; 
  }
  
  public void setCharAt(StringBuilder target, int offset16, int char32) {
    int count = 1;
    char single = target.charAt(offset16);

    if (isSurrogate(single)) {
      if (isLeadSurrogate(single) && (target.length() > offset16 + 1)
          && isTrailSurrogate(target.charAt(offset16 + 1))) {
        count ++;
      } else {
        if (isTrailSurrogate(single) && (offset16 > 0) && 
             isLeadSurrogate(target.charAt(offset16 -1))) {
          offset16 --;
          count ++;
        }
      }
    }
    target.replace(offset16, offset16 + count, valueOf(char32));
  }
  
  public boolean isSurrogate(char char16){
    return LEAD_SURROGATE_MIN_VALUE <= char16 && char16 <= TRAIL_SURROGATE_MAX_VALUE;
  }
  
  public boolean isTrailSurrogate(char char16) {
    return (TRAIL_SURROGATE_MIN_VALUE <= char16 && char16 <= TRAIL_SURROGATE_MAX_VALUE);
  }
  
  public boolean isLeadSurrogate(char char16) {
    return LEAD_SURROGATE_MIN_VALUE <= char16 && char16 <= LEAD_SURROGATE_MAX_VALUE;
  }
  
  public int getCharCount(int char32) {
    return char32 < SUPPLEMENTARY_MIN_VALUE ? 1 : 2;
  }

  public String valueOf(int char32) {
    if (char32 < CODEPOINT_MIN_VALUE || char32 > CODEPOINT_MAX_VALUE) {
      throw new IllegalArgumentException("Illegal codepoint");
    }
    return toString(char32);
  }
  
  
  public char getLeadSurrogate(int char32) {
    return (char32 >= SUPPLEMENTARY_MIN_VALUE) ?
              (char)(LEAD_SURROGATE_OFFSET_ + (char32 >> LEAD_SURROGATE_SHIFT_)) : 0;
  }
  
  public char getTrailSurrogate(int char32) {
    return char32 >= SUPPLEMENTARY_MIN_VALUE ?
            (char)(TRAIL_SURROGATE_MIN_VALUE + (char32 & TRAIL_SURROGATE_MASK_)) : (char)char32 ;
  }
  
  private String toString(int ch) {
    if (ch < SUPPLEMENTARY_MIN_VALUE) return String.valueOf((char)ch);
    StringBuilder result = new StringBuilder(getLeadSurrogate(ch)).append(getTrailSurrogate(ch));
    return result.toString();
  }

}
