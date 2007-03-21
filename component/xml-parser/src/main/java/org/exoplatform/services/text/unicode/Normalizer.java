package org.exoplatform.services.text.unicode;

/**
 * Implements Unicode Normalization Forms C, D, KC, KD.<br>
 * Copyright (c) 1991-2005 Unicode, Inc.
 * For terms of use, see http://www.unicode.org/terms_of_use.html
 * For documentation, see UAX#15.<br>
 * The Unicode Consortium makes no expressed or implied warranty of any
 * kind, and assumes no liability for errors or omissions.
 * No liability is assumed for incidental and consequential damages
 * in connection with or arising out of the use of the information here.
 * @author Mark Davis
 * Updates for supplementary code points:
 * Vladimir Weinstein & Markus Scherer
 */

public class Normalizer {

  final static byte COMPATIBILITY_MASK = 1, COMPOSITION_MASK = 2;

  public final static byte D = 0;
  
  public final static byte C = COMPOSITION_MASK;
  
  public final static byte KD = COMPATIBILITY_MASK;
  
  public final static byte KC = (byte) (COMPATIBILITY_MASK + COMPOSITION_MASK);

  private static NormalizerData data = null;

  private byte form;

  public Normalizer(){
    this(Normalizer.C);
  }

  public Normalizer(byte form) {
    this.form = form;
    if (data == null) data = NormalizerBuilder.getInstance().build();
  }

  public StringBuilder normalize(String source, StringBuilder target) {
    if (source.length() == 0) return target;
    internalDecompose(source, target);
    if ((form & COMPOSITION_MASK) == 0)  return target;
    internalCompose(target);
    return target;
  }

  public String normalize(String source) {
    return normalize(source, new StringBuilder()).toString();
  }
  
  public char[] normalize(char [] chars) {
    StringBuilder builder  = new StringBuilder();
    normalize(new String(chars), builder);
    return builder.toString().toCharArray();
  }

  private void internalDecompose(String source, StringBuilder target) {
    StringBuilder buffer = new StringBuilder();
    boolean canonical = (form & COMPATIBILITY_MASK) == 0;
    int ch32;
    for (int i = 0; i < source.length(); i += UTF16.getInstance().getCharCount(ch32)) {
      buffer.setLength(0);
      ch32 = UTF16.getInstance().charAt(source, i);
      data.getRecursiveDecomposition(canonical, (char)ch32, buffer);
      int ch;
      for (int j = 0; j < buffer.length(); j += UTF16.getInstance().getCharCount(ch)) {
        ch = UTF16.getInstance().charAt(buffer, j);
        int chClass = data.getCanonicalClass((char)ch);
        int k = target.length(); // insertion point
        if (chClass != 0) {
          int ch2;
          for (; k > 0; k -= UTF16.getInstance().getCharCount(ch2)) {
            ch2 = UTF16.getInstance().charAt(target, k-1);
            if (data.getCanonicalClass((char)ch2) <= chClass) break;
          }
        }
        target.insert(k, UTF16.getInstance().valueOf(ch));
      }
    }
  }
  
  private void internalCompose(StringBuilder target) {
    int starterPos = 0;
    int starterCh = UTF16.getInstance().charAt(target,0);
    int compPos = UTF16.getInstance().getCharCount(starterCh);
    int lastClass = data.getCanonicalClass((char)starterCh);
    if (lastClass != 0) lastClass = 256; 
    int oldLen = target.length();

    int ch;
    for (int decompPos = compPos; decompPos < target.length(); decompPos += UTF16.getInstance().getCharCount(ch)) {
      ch = UTF16.getInstance().charAt(target, decompPos);
      int chClass = data.getCanonicalClass((char)ch);
      int composite = data.getPairwiseComposition((char)starterCh, (char)ch);
      if (composite != NormalizerData.NOT_COMPOSITE && (lastClass < chClass || lastClass == 0)) {
        UTF16.getInstance().setCharAt(target, starterPos, composite);
        starterCh = composite;
        continue;
      } 
      if (chClass == 0) {
        starterPos = compPos;
        starterCh  = ch;
      }
      lastClass = chClass;
      UTF16.getInstance().setCharAt(target, compPos, ch);
      if (target.length() != oldLen) { 
        decompPos += target.length() - oldLen;
        oldLen = target.length();
      }
      compPos += UTF16.getInstance().getCharCount(ch);
    }
    target.setLength(compPos);
  }

}
