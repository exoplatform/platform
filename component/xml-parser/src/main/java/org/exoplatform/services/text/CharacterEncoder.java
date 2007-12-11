/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.services.text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class CharacterEncoder {

  private PrintStream printStream ;

  private char pemArray[] = {
      // 0   1   2   3   4   5   6   7
      'A','B','C','D','E','F','G','H', // 0
      'I','J','K','L','M','N','O','P', // 1
      'Q','R','S','T','U','V','W','X', // 2
      'Y','Z','a','b','c','d','e','f', // 3
      'g','h','i','j','k','l','m','n', // 4
      'o','p','q','r','s','t','u','v', // 5
      'w','x','y','z','0','1','2','3', // 6
      '4','5','6','7','8','9','+','-'  // 7
  };
  //encodes 57 bytes per line
  private int bytesPerLine() {
    return (57);
  }
  //encodes pree bytes per atom
  private int bytesPerAtom() {
    return (3);
  }

  @SuppressWarnings("unused")
  private void encodeLineSuffix(OutputStream aStream) throws Exception {
    printStream.println();
  }
  @SuppressWarnings("unused")
  private void encodeBufferSuffix(OutputStream aStream) throws Exception { }
  @SuppressWarnings("unused")
  private void encodeBufferPrefix(OutputStream aStream) throws Exception {
    this.printStream = new PrintStream(aStream);
  }
  @SuppressWarnings("unused")
  private void encodeLinePrefix(OutputStream aStream, int aLength) throws Exception { }

  private int readFully(InputStream in, byte buffer[])  throws IOException {
    for (int i = 0; i < buffer.length; i++) {
      int q = in.read();
      if (q == -1)
        return i;
      buffer[i] = (byte)q;
    }
    return buffer.length;
  }
  public String encodeBuffer(byte aBuffer[]) {       
    ByteArrayInputStream	in = new ByteArrayInputStream(aBuffer);
    ByteArrayOutputStream	out = new ByteArrayOutputStream();
    try {
      encodeBuffer(in, out);
    } catch (Exception IOException) {            
      throw new Error("ChracterEncoder::encodeBuffer internal error");
    }
    return (out.toString().trim());
  }
  public void encodeBuffer(InputStream in, OutputStream out)  throws Exception {
    int	j;
    int	numBytes;
    byte	tmpbuffer[] = new byte[bytesPerLine()];

    encodeBufferPrefix(out);

    while (true) {
      numBytes = readFully(in, tmpbuffer);
      if (numBytes == -1) {
        break;
      }
      encodeLinePrefix(out, numBytes);
      for (j = 0; j < numBytes; j += bytesPerAtom()) {
        if ((j + bytesPerAtom()) <= numBytes) {
          encodeAtom(out, tmpbuffer, j, bytesPerAtom());
        } else {
          encodeAtom(out, tmpbuffer, j, (numBytes)- j);
        }
      }
      encodeLineSuffix(out);
      if (numBytes < bytesPerLine()) {
        break;
      }
    }
    encodeBufferSuffix(out);
  }
  private void encodeAtom(OutputStream outStream, byte data[], int offset, int len) throws IOException {			
    byte a, b, c;			
    if (len == 1) {
      a = data[offset];
      b = 0;
      c = 0;
      outStream.write(pemArray[(a >>> 2) & 0x3F]);
      outStream.write(pemArray[((a << 4) & 0x30) + ((b >>> 4) & 0xf)]);
      outStream.write('=');
      outStream.write('=');
    } else if (len == 2) {
      a = data[offset];
      b = data[offset+1];
      c = 0;
      outStream.write(pemArray[(a >>> 2) & 0x3F]);
      outStream.write(pemArray[((a << 4) & 0x30) + ((b >>> 4) & 0xf)]);
      outStream.write(pemArray[((b << 2) & 0x3c) + ((c >>> 6) & 0x3)]);
      outStream.write('=');
    } else {
      a = data[offset];
      b = data[offset+1];
      c = data[offset+2];
      outStream.write(pemArray[(a >>> 2) & 0x3F]);
      outStream.write(pemArray[((a << 4) & 0x30) + ((b >>> 4) & 0xf)]);
      outStream.write(pemArray[((b << 2) & 0x3c) + ((c >>> 6) & 0x3)]);
      outStream.write(pemArray[c & 0x3F]);
    }
  }
}

