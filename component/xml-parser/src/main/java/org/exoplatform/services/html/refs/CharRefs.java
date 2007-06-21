//HTMLParser Library $Name: v1_5 $ - A java-based parser for HTML
//http://sourceforge.org/projects/htmlparser
//Copyright (C) 2004 Derrick Oswald 
//Copyright (C) 2006  eXo Platform SARL

//Revision Control Information

//$Source: /cvsroot/htmlparser/htmlparser/src/org/htmlparser/util/Translate.java,v $
//$Author: derrickoswald $ nhudinhthuan@yahoo.com
//$Date: 2004/07/31 16:42:33 $
//$Revision: 1.46 $

//This library is free software; you can redistribute it and/or
//modify it under the terms of the GNU Lesser General Public
//License as published by the Free Software Foundation; either
//version 2.1 of the License, or (at your option) any later version.

//This library is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
//Lesser General Public License for more details.

//You should have received a copy of the GNU Lesser General Public
//License along with this library; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

package org.exoplatform.services.html.refs;

import java.util.Arrays;
import java.util.Comparator;

final class CharRefs {
  
  boolean sorted = false;

  CharRef[] charRefs = {

      new CharRef ("nbsp",     '\u00a0'), // no-break space = non-breaking space, U+00A0 ISOnum
      new CharRef ("iexcl",    '\u00a1'), // inverted exclamation mark, U+00A1 ISOnum
      new CharRef ("cent",     '\u00a2'), // cent sign, U+00A2 ISOnum
      new CharRef ("pound",    '\u00a3'), // pound sign, U+00A3 ISOnum
      new CharRef ("curren",   '\u00a4'), // currency sign, U+00A4 ISOnum
      new CharRef ("yen",      '\u00a5'), // yen sign = yuan sign, U+00A5 ISOnum
      new CharRef ("brvbar",   '\u00a6'), // broken bar = broken vertical bar, U+00A6 ISOnum
      new CharRef ("sect",     '\u00a7'), // section sign, U+00A7 ISOnum
      new CharRef ("uml",      '\u00a8'), // diaeresis = spacing diaeresis, U+00A8 ISOdia
      new CharRef ("copy",     '\u00a9'), // copyright sign, U+00A9 ISOnum
      new CharRef ("ordf",     '\u00aa'), // feminine ordinal indicator, U+00AA ISOnum
      new CharRef ("laquo",    '\u00ab'), // left-pointing double angle quotation mark = left pointing guillemet, U+00AB ISOnum
      new CharRef ("not",      '\u00ac'), // not sign, U+00AC ISOnum
      new CharRef ("shy",      '\u00ad'), // soft hyphen = discretionary hyphen, U+00AD ISOnum
      new CharRef ("reg",      '\u00ae'), // registered sign = registered trade mark sign, U+00AE ISOnum
      new CharRef ("macr",     '\u00af'), // macron = spacing macron = overline = APL overbar, U+00AF ISOdia
      new CharRef ("deg",      '\u00b0'), // degree sign, U+00B0 ISOnum
      new CharRef ("plusmn",   '\u00b1'), // plus-minus sign = plus-or-minus sign, U+00B1 ISOnum
      new CharRef ("sup2",     '\u00b2'), // superscript two = superscript digit two = squared, U+00B2 ISOnum
      new CharRef ("sup3",     '\u00b3'), // superscript three = superscript digit three = cubed, U+00B3 ISOnum
      new CharRef ("acute",    '\u00b4'), // acute accent = spacing acute, U+00B4 ISOdia
      new CharRef ("micro",    '\u00b5'), // micro sign, U+00B5 ISOnum
      new CharRef ("para",     '\u00b6'), // pilcrow sign = paragraph sign, U+00B6 ISOnum
      new CharRef ("middot",   '\u00b7'), // middle dot = Georgian comma = Greek middle dot, U+00B7 ISOnum
      new CharRef ("cedil",    '\u00b8'), // cedilla = spacing cedilla, U+00B8 ISOdia
      new CharRef ("sup1",     '\u00b9'), // superscript one = superscript digit one, U+00B9 ISOnum
      new CharRef ("ordm",     '\u00ba'), // masculine ordinal indicator, U+00BA ISOnum
      new CharRef ("raquo",    '\u00bb'), // right-pointing double angle quotation mark = right pointing guillemet, U+00BB ISOnum
      new CharRef ("frac14",   '\u00bc'), // vulgar fraction one quarter = fraction one quarter, U+00BC ISOnum
      new CharRef ("frac12",   '\u00bd'), // vulgar fraction one half = fraction one half, U+00BD ISOnum
      new CharRef ("frac34",   '\u00be'), // vulgar fraction three quarters = fraction three quarters, U+00BE ISOnum
      new CharRef ("iquest",   '\u00bf'), // inverted question mark = turned question mark, U+00BF ISOnum
      new CharRef ("Agrave",   '\u00c0'), // latin capital letter A with grave = latin capital letter A grave, U+00C0 ISOlat1
      new CharRef ("Aacute",   '\u00c1'), // latin capital letter A with acute, U+00C1 ISOlat1
      new CharRef ("Acirc",    '\u00c2'), // latin capital letter A with circumflex, U+00C2 ISOlat1
      new CharRef ("Atilde",   '\u00c3'), // latin capital letter A with tilde, U+00C3 ISOlat1
      new CharRef ("Auml",     '\u00c4'), // latin capital letter A with diaeresis, U+00C4 ISOlat1
      new CharRef ("Aring",    '\u00c5'), // latin capital letter A with ring above = latin capital letter A ring, U+00C5 ISOlat1
      new CharRef ("AElig",    '\u00c6'), // latin capital letter AE = latin capital ligature AE, U+00C6 ISOlat1
      new CharRef ("Ccedil",   '\u00c7'), // latin capital letter C with cedilla, U+00C7 ISOlat1
      new CharRef ("Egrave",   '\u00c8'), // latin capital letter E with grave, U+00C8 ISOlat1
      new CharRef ("Eacute",   '\u00c9'), // latin capital letter E with acute, U+00C9 ISOlat1
      new CharRef ("Ecirc",    '\u00ca'), // latin capital letter E with circumflex, U+00CA ISOlat1
      new CharRef ("Euml",     '\u00cb'), // latin capital letter E with diaeresis, U+00CB ISOlat1
      new CharRef ("Igrave",   '\u00cc'), // latin capital letter I with grave, U+00CC ISOlat1
      new CharRef ("Iacute",   '\u00cd'), // latin capital letter I with acute, U+00CD ISOlat1
      new CharRef ("Icirc",    '\u00ce'), // latin capital letter I with circumflex, U+00CE ISOlat1
      new CharRef ("Iuml",     '\u00cf'), // latin capital letter I with diaeresis, U+00CF ISOlat1
      new CharRef ("ETH",      '\u00d0'), // latin capital letter ETH, U+00D0 ISOlat1
      new CharRef ("Ntilde",   '\u00d1'), // latin capital letter N with tilde, U+00D1 ISOlat1
      new CharRef ("Ograve",   '\u00d2'), // latin capital letter O with grave, U+00D2 ISOlat1
      new CharRef ("Oacute",   '\u00d3'), // latin capital letter O with acute, U+00D3 ISOlat1
      new CharRef ("Ocirc",    '\u00d4'), // latin capital letter O with circumflex, U+00D4 ISOlat1
      new CharRef ("Otilde",   '\u00d5'), // latin capital letter O with tilde, U+00D5 ISOlat1
      new CharRef ("Ouml",     '\u00d6'), // latin capital letter O with diaeresis, U+00D6 ISOlat1
      new CharRef ("times",    '\u00d7'), // multiplication sign, U+00D7 ISOnum
      new CharRef ("Oslash",   '\u00d8'), // latin capital letter O with stroke = latin capital letter O slash, U+00D8 ISOlat1
      new CharRef ("Ugrave",   '\u00d9'), // latin capital letter U with grave, U+00D9 ISOlat1
      new CharRef ("Uacute",   '\u00da'), // latin capital letter U with acute, U+00DA ISOlat1
      new CharRef ("Ucirc",    '\u00db'), // latin capital letter U with circumflex, U+00DB ISOlat1
      new CharRef ("Uuml",     '\u00dc'), // latin capital letter U with diaeresis, U+00DC ISOlat1
      new CharRef ("Yacute",   '\u00dd'), // latin capital letter Y with acute, U+00DD ISOlat1
      new CharRef ("THORN",    '\u00de'), // latin capital letter THORN, U+00DE ISOlat1
      new CharRef ("szlig",    '\u00df'), // latin small letter sharp s = ess-zed, U+00DF ISOlat1
      new CharRef ("agrave",   '\u00e0'), // latin small letter a with grave = latin small letter a grave, U+00E0 ISOlat1
      new CharRef ("aacute",   '\u00e1'), // latin small letter a with acute, U+00E1 ISOlat1
      new CharRef ("acirc",    '\u00e2'), // latin small letter a with circumflex, U+00E2 ISOlat1
      new CharRef ("atilde",   '\u00e3'), // latin small letter a with tilde, U+00E3 ISOlat1
      new CharRef ("auml",     '\u00e4'), // latin small letter a with diaeresis, U+00E4 ISOlat1
      new CharRef ("aring",    '\u00e5'), // latin small letter a with ring above = latin small letter a ring, U+00E5 ISOlat1
      new CharRef ("aelig",    '\u00e6'), // latin small letter ae = latin small ligature ae, U+00E6 ISOlat1
      new CharRef ("ccedil",   '\u00e7'), // latin small letter c with cedilla, U+00E7 ISOlat1
      new CharRef ("egrave",   '\u00e8'), // latin small letter e with grave, U+00E8 ISOlat1
      new CharRef ("eacute",   '\u00e9'), // latin small letter e with acute, U+00E9 ISOlat1
      new CharRef ("ecirc",    '\u00ea'), // latin small letter e with circumflex, U+00EA ISOlat1
      new CharRef ("euml",     '\u00eb'), // latin small letter e with diaeresis, U+00EB ISOlat1
      new CharRef ("igrave",   '\u00ec'), // latin small letter i with grave, U+00EC ISOlat1
      new CharRef ("iacute",   '\u00ed'), // latin small letter i with acute, U+00ED ISOlat1
      new CharRef ("icirc",    '\u00ee'), // latin small letter i with circumflex, U+00EE ISOlat1
      new CharRef ("iuml",     '\u00ef'), // latin small letter i with diaeresis, U+00EF ISOlat1
      new CharRef ("eth",      '\u00f0'), // latin small letter eth, U+00F0 ISOlat1
      new CharRef ("ntilde",   '\u00f1'), // latin small letter n with tilde, U+00F1 ISOlat1
      new CharRef ("ograve",   '\u00f2'), // latin small letter o with grave, U+00F2 ISOlat1
      new CharRef ("oacute",   '\u00f3'), // latin small letter o with acute, U+00F3 ISOlat1
      new CharRef ("ocirc",    '\u00f4'), // latin small letter o with circumflex, U+00F4 ISOlat1
      new CharRef ("otilde",   '\u00f5'), // latin small letter o with tilde, U+00F5 ISOlat1
      new CharRef ("ouml",     '\u00f6'), // latin small letter o with diaeresis, U+00F6 ISOlat1
      new CharRef ("divide",   '\u00f7'), // division sign, U+00F7 ISOnum
      new CharRef ("oslash",   '\u00f8'), // latin small letter o with stroke, = latin small letter o slash, U+00F8 ISOlat1
      new CharRef ("ugrave",   '\u00f9'), // latin small letter u with grave, U+00F9 ISOlat1
      new CharRef ("uacute",   '\u00fa'), // latin small letter u with acute, U+00FA ISOlat1
      new CharRef ("ucirc",    '\u00fb'), // latin small letter u with circumflex, U+00FB ISOlat1
      new CharRef ("uuml",     '\u00fc'), // latin small letter u with diaeresis, U+00FC ISOlat1
      new CharRef ("yacute",   '\u00fd'), // latin small letter y with acute, U+00FD ISOlat1
      new CharRef ("thorn",    '\u00fe'), // latin small letter thorn, U+00FE ISOlat1
      new CharRef ("yuml",     '\u00ff'), // latin small letter y with diaeresis, U+00FF ISOlat1

      new CharRef ("fnof",     '\u0192'), // latin small f with hook = function = florin, U+0192 ISOtech

      new CharRef ("Alpha",    '\u0391'), // greek capital letter alpha, U+0391
      new CharRef ("Beta",     '\u0392'), // greek capital letter beta, U+0392
      new CharRef ("Gamma",    '\u0393'), // greek capital letter gamma, U+0393 ISOgrk3
      new CharRef ("Delta",    '\u0394'), // greek capital letter delta, U+0394 ISOgrk3
      new CharRef ("Epsilon",  '\u0395'), // greek capital letter epsilon, U+0395
      new CharRef ("Zeta",     '\u0396'), // greek capital letter zeta, U+0396
      new CharRef ("Eta",      '\u0397'), // greek capital letter eta, U+0397
      new CharRef ("Theta",    '\u0398'), // greek capital letter theta, U+0398 ISOgrk3
      new CharRef ("Iota",     '\u0399'), // greek capital letter iota, U+0399
      new CharRef ("Kappa",    '\u039a'), // greek capital letter kappa, U+039A
      new CharRef ("Lambda",   '\u039b'), // greek capital letter lambda, U+039B ISOgrk3
      new CharRef ("Mu",       '\u039c'), // greek capital letter mu, U+039C
      new CharRef ("Nu",       '\u039d'), // greek capital letter nu, U+039D
      new CharRef ("Xi",       '\u039e'), // greek capital letter xi, U+039E ISOgrk3
      new CharRef ("Omicron",  '\u039f'), // greek capital letter omicron, U+039F
      new CharRef ("Pi",       '\u03a0'), // greek capital letter pi, U+03A0 ISOgrk3
      new CharRef ("Rho",      '\u03a1'), // greek capital letter rho, U+03A1

      new CharRef ("Sigma",    '\u03a3'), // greek capital letter sigma, U+03A3 ISOgrk3
      new CharRef ("Tau",      '\u03a4'), // greek capital letter tau, U+03A4
      new CharRef ("Upsilon",  '\u03a5'), // greek capital letter upsilon, U+03A5 ISOgrk3
      new CharRef ("Phi",      '\u03a6'), // greek capital letter phi, U+03A6 ISOgrk3
      new CharRef ("Chi",      '\u03a7'), // greek capital letter chi, U+03A7
      new CharRef ("Psi",      '\u03a8'), // greek capital letter psi, U+03A8 ISOgrk3
      new CharRef ("Omega",    '\u03a9'), // greek capital letter omega, U+03A9 ISOgrk3
      new CharRef ("alpha",    '\u03b1'), // greek small letter alpha, U+03B1 ISOgrk3
      new CharRef ("beta",     '\u03b2'), // greek small letter beta, U+03B2 ISOgrk3
      new CharRef ("gamma",    '\u03b3'), // greek small letter gamma, U+03B3 ISOgrk3
      new CharRef ("delta",    '\u03b4'), // greek small letter delta, U+03B4 ISOgrk3
      new CharRef ("epsilon",  '\u03b5'), // greek small letter epsilon, U+03B5 ISOgrk3
      new CharRef ("zeta",     '\u03b6'), // greek small letter zeta, U+03B6 ISOgrk3
      new CharRef ("eta",      '\u03b7'), // greek small letter eta, U+03B7 ISOgrk3
      new CharRef ("theta",    '\u03b8'), // greek small letter theta, U+03B8 ISOgrk3
      new CharRef ("iota",     '\u03b9'), // greek small letter iota, U+03B9 ISOgrk3
      new CharRef ("kappa",    '\u03ba'), // greek small letter kappa, U+03BA ISOgrk3
      new CharRef ("lambda",   '\u03bb'), // greek small letter lambda, U+03BB ISOgrk3
      new CharRef ("mu",       '\u03bc'), // greek small letter mu, U+03BC ISOgrk3
      new CharRef ("nu",       '\u03bd'), // greek small letter nu, U+03BD ISOgrk3
      new CharRef ("xi",       '\u03be'), // greek small letter xi, U+03BE ISOgrk3
      new CharRef ("omicron",  '\u03bf'), // greek small letter omicron, U+03BF NEW
      new CharRef ("pi",       '\u03c0'), // greek small letter pi, U+03C0 ISOgrk3
      new CharRef ("rho",      '\u03c1'), // greek small letter rho, U+03C1 ISOgrk3
      new CharRef ("sigmaf",   '\u03c2'), // greek small letter final sigma, U+03C2 ISOgrk3
      new CharRef ("sigma",    '\u03c3'), // greek small letter sigma, U+03C3 ISOgrk3
      new CharRef ("tau",      '\u03c4'), // greek small letter tau, U+03C4 ISOgrk3
      new CharRef ("upsilon",  '\u03c5'), // greek small letter upsilon, U+03C5 ISOgrk3
      new CharRef ("phi",      '\u03c6'), // greek small letter phi, U+03C6 ISOgrk3
      new CharRef ("chi",      '\u03c7'), // greek small letter chi, U+03C7 ISOgrk3
      new CharRef ("psi",      '\u03c8'), // greek small letter psi, U+03C8 ISOgrk3
      new CharRef ("omega",    '\u03c9'), // greek small letter omega, U+03C9 ISOgrk3
      new CharRef ("thetasym", '\u03d1'), // greek small letter theta symbol, U+03D1 NEW
      new CharRef ("upsih",    '\u03d2'), // greek upsilon with hook symbol, U+03D2 NEW
      new CharRef ("piv",      '\u03d6'), // greek pi symbol, U+03D6 ISOgrk3
      // General Punctuation
      new CharRef ("bull",     '\u2022'), // bullet = black small circle, U+2022 ISOpub
      // bullet is NOT the same as bullet operator, U+2219
      new CharRef ("hellip",   '\u2026'), // horizontal ellipsis = three dot leader, U+2026 ISOpub
      new CharRef ("prime",    '\u2032'), // prime = minutes = feet, U+2032 ISOtech
      new CharRef ("Prime",    '\u2033'), // double prime = seconds = inches, U+2033 ISOtech
      new CharRef ("oline",    '\u203e'), // overline = spacing overscore, U+203E NEW
      new CharRef ("frasl",    '\u2044'), // fraction slash, U+2044 NEW
      // Letterlike Symbols
      new CharRef ("weierp",   '\u2118'), // script capital P = power set = Weierstrass p, U+2118 ISOamso
      new CharRef ("image",    '\u2111'), // blackletter capital I = imaginary part, U+2111 ISOamso
      new CharRef ("real",     '\u211c'), // blackletter capital R = real part symbol, U+211C ISOamso
      new CharRef ("trade",    '\u2122'), // trade mark sign, U+2122 ISOnum
      new CharRef ("alefsym",  '\u2135'), // alef symbol = first transfinite cardinal, U+2135 NEW

      new CharRef ("larr",     '\u2190'), // leftwards arrow, U+2190 ISOnum
      new CharRef ("uarr",     '\u2191'), // upwards arrow, U+2191 ISOnum
      new CharRef ("rarr",     '\u2192'), // rightwards arrow, U+2192 ISOnum
      new CharRef ("darr",     '\u2193'), // downwards arrow, U+2193 ISOnum
      new CharRef ("harr",     '\u2194'), // left right arrow, U+2194 ISOamsa
      new CharRef ("crarr",    '\u21b5'), // downwards arrow with corner leftwards = carriage return, U+21B5 NEW
      new CharRef ("lArr",     '\u21d0'), // leftwards double arrow, U+21D0 ISOtech

      new CharRef ("uArr",     '\u21d1'), // upwards double arrow, U+21D1 ISOamsa
      new CharRef ("rArr",     '\u21d2'), // rightwards double arrow, U+21D2 ISOtech

      new CharRef ("dArr",     '\u21d3'), // downwards double arrow, U+21D3 ISOamsa
      new CharRef ("hArr",     '\u21d4'), // left right double arrow, U+21D4 ISOamsa

      new CharRef ("forall",   '\u2200'), // for all, U+2200 ISOtech
      new CharRef ("part",     '\u2202'), // partial differential, U+2202 ISOtech
      new CharRef ("exist",    '\u2203'), // there exists, U+2203 ISOtech
      new CharRef ("empty",    '\u2205'), // empty set = null set = diameter, U+2205 ISOamso
      new CharRef ("nabla",    '\u2207'), // nabla = backward difference, U+2207 ISOtech
      new CharRef ("isin",     '\u2208'), // element of, U+2208 ISOtech
      new CharRef ("notin",    '\u2209'), // not an element of, U+2209 ISOtech
      new CharRef ("ni",       '\u220b'), // contains as member, U+220B ISOtech

      new CharRef ("prod",     '\u220f'), // n-ary product = product sign, U+220F ISOamsb

      new CharRef ("sum",      '\u2211'), // n-ary sumation, U+2211 ISOamsb

      new CharRef ("minus",    '\u2212'), // minus sign, U+2212 ISOtech
      new CharRef ("lowast",   '\u2217'), // asterisk operator, U+2217 ISOtech
      new CharRef ("radic",    '\u221a'), // square root = radical sign, U+221A ISOtech
      new CharRef ("prop",     '\u221d'), // proportional to, U+221D ISOtech
      new CharRef ("infin",    '\u221e'), // infinity, U+221E ISOtech
      new CharRef ("ang",      '\u2220'), // angle, U+2220 ISOamso
      new CharRef ("and",      '\u2227'), // logical and = wedge, U+2227 ISOtech
      new CharRef ("or",       '\u2228'), // logical or = vee, U+2228 ISOtech
      new CharRef ("cap",      '\u2229'), // intersection = cap, U+2229 ISOtech
      new CharRef ("cup",      '\u222a'), // union = cup, U+222A ISOtech
      new CharRef ("int",      '\u222b'), // integral, U+222B ISOtech
      new CharRef ("there4",   '\u2234'), // therefore, U+2234 ISOtech
      new CharRef ("sim",      '\u223c'), // tilde operator = varies with = similar to, U+223C ISOtech

      new CharRef ("cong",     '\u2245'), // approximately equal to, U+2245 ISOtech
      new CharRef ("asymp",    '\u2248'), // almost equal to = asymptotic to, U+2248 ISOamsr
      new CharRef ("ne",       '\u2260'), // not equal to, U+2260 ISOtech
      new CharRef ("equiv",    '\u2261'), // identical to, U+2261 ISOtech
      new CharRef ("le",       '\u2264'), // less-than or equal to, U+2264 ISOtech
      new CharRef ("ge",       '\u2265'), // greater-than or equal to, U+2265 ISOtech
      new CharRef ("sub",      '\u2282'), // subset of, U+2282 ISOtech
      new CharRef ("sup",      '\u2283'), // superset of, U+2283 ISOtech

      new CharRef ("nsub",     '\u2284'), // not a subset of, U+2284 ISOamsn
      new CharRef ("sube",     '\u2286'), // subset of or equal to, U+2286 ISOtech
      new CharRef ("supe",     '\u2287'), // superset of or equal to, U+2287 ISOtech
      new CharRef ("oplus",    '\u2295'), // circled plus = direct sum, U+2295 ISOamsb
      new CharRef ("otimes",   '\u2297'), // circled times = vector product, U+2297 ISOamsb
      new CharRef ("perp",     '\u22a5'), // up tack = orthogonal to = perpendicular, U+22A5 ISOtech
      new CharRef ("sdot",     '\u22c5'), // dot operator, U+22C5 ISOamsb

      new CharRef ("lceil",    '\u2308'), // left ceiling = apl upstile, U+2308 ISOamsc
      new CharRef ("rceil",    '\u2309'), // right ceiling, U+2309 ISOamsc
      new CharRef ("lfloor",   '\u230a'), // left floor = apl downstile, U+230A ISOamsc
      new CharRef ("rfloor",   '\u230b'), // right floor, U+230B ISOamsc
      new CharRef ("lang",     '\u2329'), // left-pointing angle bracket = bra, U+2329 ISOtech

      new CharRef ("rang",     '\u232a'), // right-pointing angle bracket = ket, U+232A ISOtech

      new CharRef ("loz",      '\u25ca'), // lozenge, U+25CA ISOpub

      new CharRef ("spades",   '\u2660'), // black spade suit, U+2660 ISOpub

      new CharRef ("clubs",    '\u2663'), // black club suit = shamrock, U+2663 ISOpub
      new CharRef ("hearts",   '\u2665'), // black heart suit = valentine, U+2665 ISOpub
      new CharRef ("diams",    '\u2666'), // black diamond suit, U+2666 ISOpub

      new CharRef ("quot",     '\u0022'), // quotation mark = APL quote, U+0022 ISOnum
      new CharRef ("amp",      '\u0026'), // ampersand, U+0026 ISOnum
      new CharRef ("lt",       '\u003c'), // less-than sign, U+003C ISOnum
      new CharRef ("gt",       '\u003e'), // greater-than sign, U+003E ISOnum

      new CharRef ("OElig",    '\u0152'), // latin capital ligature OE, U+0152 ISOlat2
      new CharRef ("oelig",    '\u0153'), // latin small ligature oe, U+0153 ISOlat2

      new CharRef ("Scaron",   '\u0160'), // latin capital letter S with caron, U+0160 ISOlat2
      new CharRef ("scaron",   '\u0161'), // latin small letter s with caron, U+0161 ISOlat2
      new CharRef ("Yuml",     '\u0178'), // latin capital letter Y with diaeresis, U+0178 ISOlat2

      new CharRef ("circ",     '\u02c6'), // modifier letter circumflex accent, U+02C6 ISOpub
      new CharRef ("tilde",    '\u02dc'), // small tilde, U+02DC ISOdia

      new CharRef ("ensp",     '\u2002'), // en space, U+2002 ISOpub
      new CharRef ("emsp",     '\u2003'), // em space, U+2003 ISOpub
      new CharRef ("thinsp",   '\u2009'), // thin space, U+2009 ISOpub
      new CharRef ("zwnj",     '\u200c'), // zero width non-joiner, U+200C NEW RFC 2070
      new CharRef ("zwj",      '\u200d'), // zero width joiner, U+200D NEW RFC 2070
      new CharRef ("lrm",      '\u200e'), // left-to-right mark, U+200E NEW RFC 2070
      new CharRef ("rlm",      '\u200f'), // right-to-left mark, U+200F NEW RFC 2070
      new CharRef ("ndash",    '\u2013'), // en dash, U+2013 ISOpub
      new CharRef ("mdash",    '\u2014'), // em dash, U+2014 ISOpub
      new CharRef ("lsquo",    '\u2018'), // left single quotation mark, U+2018 ISOnum
      new CharRef ("rsquo",    '\u2019'), // right single quotation mark, U+2019 ISOnum
      new CharRef ("sbquo",    '\u201a'), // single low-9 quotation mark, U+201A NEW
      new CharRef ("ldquo",    '\u201c'), // left double quotation mark, U+201C ISOnum
      new CharRef ("rdquo",    '\u201d'), // right double quotation mark, U+201D ISOnum
      new CharRef ("bdquo",    '\u201e'), // double low-9 quotation mark, U+201E NEW
      new CharRef ("dagger",   '\u2020'), // dagger, U+2020 ISOpub
      new CharRef ("Dagger",   '\u2021'), // double dagger, U+2021 ISOpub
      new CharRef ("permil",   '\u2030'), // per mille sign, U+2030 ISOtech
      new CharRef ("lsaquo",   '\u2039'), // single left-pointing angle quotation mark, U+2039 ISO proposed

      new CharRef ("rsaquo",   '\u203a'), // single right-pointing angle quotation mark, U+203A ISO proposed

      new CharRef ("euro",     '\u20ac'), // euro sign, U+20AC NEW
  };

  void sort(Comparator<CharRef> comparator) {
    Arrays.sort(charRefs, comparator);    
    sorted = true;
  }

  CharRef searchByName(String name, Comparator<CharRef> comparator){    
    try{
      CharRef ref = new CharRef(name, -1);
      int idx = Arrays.binarySearch(charRefs, ref, comparator);
      if(idx < 0) return null;
      return charRefs[idx];
    }catch(Exception exp){
      exp.printStackTrace();   
      return null;
    }
  }

  CharRef searchByValue(char c, Comparator<CharRef> comparator){
    CharRef ref = new CharRef("", c);
    int idx = Arrays.binarySearch(charRefs, ref, comparator);
    if(idx < 0) return null;
    return charRefs[idx];
  }
  
  boolean isSorted(){ return sorted; }

}
