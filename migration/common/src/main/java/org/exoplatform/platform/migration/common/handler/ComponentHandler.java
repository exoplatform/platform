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
package org.exoplatform.platform.migration.common.handler;

import java.io.ByteArrayOutputStream;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.container.xml.Component;
import org.exoplatform.platform.migration.common.constants.Constants;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;

public abstract class ComponentHandler extends BaseComponentPlugin {

  private String targetComponentName;

  public String getTargetComponentName() {
    return targetComponentName;
  }

  public void setTargetComponentName(String targetComponentName) {
    this.targetComponentName = targetComponentName;
  }

  @Override
  public boolean equals(Object obj) {
    if ((obj != null) && (obj instanceof ComponentHandler)) {
      return getName().equals(((ComponentHandler) obj).getName());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return getName().hashCode();
  }

  public abstract Entry invoke(Component component, ExoContainer container) throws Exception;

  protected byte[] toXML(Object obj) throws Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      IBindingFactory bfact = BindingDirectory.getFactory(obj.getClass());
      IMarshallingContext mctx = bfact.createMarshallingContext();
      mctx.setIndent(2);
      mctx.marshalDocument(obj, "UTF-8", null, out);
      String outConf = new String(out.toByteArray());
      outConf = outConf.replace("<configuration>", Constants.KERNEL_CONFIGURATION_1_1_URI).replaceAll(Constants.EMPTY_FIELD_REGULAR_EXPRESSION, "");
      return outConf.getBytes();
    } catch (Exception ie) {
      throw ie;
    }
  }

  public static class Entry {

    public Entry(String componentName) {
      this.componentName = componentName;
    }

    private String componentName;
    private EntryType type;
    private byte[] content;

    public EntryType getType() {
      return type;
    }

    public String getComponentName() {
      return componentName;
    }

    public void setComponentName(String componentName) {
      this.componentName = componentName;
    }

    public void setType(EntryType type) {
      this.type = type;
    }

    public byte[] getContent() {
      return content;
    }

    public void setContent(byte[] content) {
      this.content = content;
    }

    @Override
    public boolean equals(Object obj) {
      if ((obj != null) && (obj instanceof Entry)) {
        return getComponentName().equals(((Entry) obj).getComponentName());
      }
      return false;
    }

    @Override
    public int hashCode() {
      return getComponentName().hashCode();
    }
  }

  public enum EntryType {
    XML(".xml", "text/xml"), ZIP(".zip", "application/zip");
    private String extension;
    private String mediaType;

    private EntryType(String extension, String mediaType) {
      this.extension = extension;
      this.mediaType = mediaType;
    }

    public String getExtension() {
      return extension;
    }

    public String getMediaType() {
      return mediaType;
    }

    @Override
    public String toString() {
      return getExtension();
    }
  }

}