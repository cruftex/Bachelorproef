package org.cache2k.benchmark.traces;

/*
 * #%L
 * traces
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

import org.cache2k.benchmark.util.AccessTrace;

import java.io.IOException;

/**
 * UMass WebSearch3 trace. Truncated to one million requests using only
 * read access and only the first LBA of a sequence.
 *
 * <p>fileName=WebSearch3.spc.bz2, sha1sum=b01df4d3a1d3379d8a33dfb35958e8749e6c4e02
 *
 * @author Jens Wilke
 */
public class CacheAccessTraceUmassWebSearch3 {

  static final TraceCache.Provider PROVIDER = new TraceCache.Provider() {
    @Override
    public AccessTrace provide() throws IOException {
      return CacheAccessTraceUmassFinancial1.provideUmassTrace("WebSearch3.spc.bz2");
    }
  };

  public static AccessTrace getInstance() {
    return TraceCache.getTraceLazy(CacheAccessTraceUmassWebSearch3.class.getName(), PROVIDER);
  }

}
