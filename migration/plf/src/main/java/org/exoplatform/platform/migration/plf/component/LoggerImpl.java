/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
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
package org.exoplatform.platform.migration.plf.component;
import org.exoplatform.platform.migration.common.component.Logger;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Dec 23, 2010  
 */
public class LoggerImpl implements Logger{
  
  private Log log = null;

  public void setLogger(Class<?> clazz) {
    this.log = ExoLogger.getLogger(clazz);  
  }

  public void debug(Object message) {
    log.debug(message);
  }

  public void debug(Object message, Throwable t) {
    log.debug(message, t);
    
  }

  public void error(Object message) {
    log.error(message);
    
  }

  public void error(Object message, Throwable t) {
    log.error(message);
    
  }

  public void fatal(Object message) {
    log.fatal(message);
    
  }

  public void fatal(Object message, Throwable t) {
    log.fatal(message, t);
    
  }

  public void info(Object message) {
    log.info(message);
    
  }

  public void info(Object message, Throwable t) {
    log.info(message, t);
    
  }

  public boolean isDebugEnabled() {
    return log.isDebugEnabled();
  }

  public boolean isErrorEnabled() {
    return log.isErrorEnabled();
  }

  public boolean isFatalEnabled() {
    return log.isFatalEnabled();
  }

  public boolean isInfoEnabled() {
    
    return log.isInfoEnabled();
  }

  public boolean isTraceEnabled() {
    return log.isTraceEnabled();
  }

  public boolean isWarnEnabled() {
    return log.isWarnEnabled();
  }

  public void trace(Object message) {
    log.trace(message);
    
  }

  public void trace(Object message, Throwable t) {
    log.trace(message, t);
    
  }

  public void warn(Object message) {
    log.warn(message);
    
  }

  public void warn(Object message, Throwable t) {
    log.warn(message, t);
    
  }

}
