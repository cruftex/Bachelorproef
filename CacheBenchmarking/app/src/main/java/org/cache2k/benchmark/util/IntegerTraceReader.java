package org.cache2k.benchmark.util;

/*
 * #%L
 * util
 * %%
 * Copyright (C) 2013 - 2016 headissue GmbH, Munich
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.Charset;

/**
 * Read in an access log trace from integer values.
 *
 * @author Jens Wilke; created: 2013-11-19
 */
public class IntegerTraceReader extends AccessPattern {

  LineNumberReader reader;
  int value;

  /**
   * Read in, default charset.
   */
  public IntegerTraceReader(InputStream in, Charset cs) {
    reader = new LineNumberReader(new InputStreamReader(in, cs));
  }

  @Override
  public boolean isEternal() {
    return false;
  }

  @Override
  public boolean hasNext() throws Exception {
    String s;
    do {
      s = reader.readLine();
      if (s == null) {
        return false;
      }
    } while (s.startsWith("#") || s.trim().length() == 0);
    try {
      value = Integer.parseInt(s);
    } catch(NumberFormatException e) {
      System.err.println("parse error line " + reader.getLineNumber() + ": " + s);
      return hasNext();
    }
    return true;
  }

  @Override
  public int next() throws IOException {
    return value;
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }

}
