/*
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.platform.migration.bos.exporter;

import javax.jcr.NamespaceException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.RepositoryException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.exoplatform.services.jcr.dataflow.ItemDataConsumer;
import org.exoplatform.services.jcr.impl.Constants;
import org.exoplatform.services.jcr.impl.core.value.ValueFactoryImpl;
import org.exoplatform.services.jcr.impl.xml.exporting.WorkspaceSystemViewStreamExporter;

/**
 * Created by The eXo Platform MEA Author : Anouar Chattouna anouar.chattouna@exoplatform.com June 08, 2011
 */

public class WorkspaceStreamExporter extends WorkspaceSystemViewStreamExporter {

  private static final String JOS_PREFIX = "jos";
  private static final String JOS_URI = "http://nl.ijs.si/jos/";

  /**
   * Ensures a workspace backup: See {@link WorkspaceSystemViewStreamExporter}.
   */
  public WorkspaceStreamExporter(XMLStreamWriter writer, ItemDataConsumer dataManager, NamespaceRegistry namespaceRegistry,
      ValueFactoryImpl systemValueFactory, boolean skipBinary, boolean noRecurse) throws NamespaceException, RepositoryException {
    super(writer, dataManager, namespaceRegistry, systemValueFactory, skipBinary, noRecurse);
  }

  /**
   * Redefines the URI of the "jos" prefix. See {@link org.exoplatform.services.jcr.impl.xml.exporting.StreamExporter#startPrefixMapping()}.
   */
  protected void startPrefixMapping() throws RepositoryException, XMLStreamException {
    String[] prefixes = getNamespaceRegistry().getPrefixes();
    for (String prefix : prefixes) {
      // skeep xml prefix
      if ((prefix == null) || (prefix.length() < 1) || prefix.equals(Constants.NS_XML_PREFIX)) {
        continue;
      }
      // edit jos prefix
      if (prefix.equals(JOS_PREFIX)) {
        writer.writeNamespace(prefix, JOS_URI);
        continue;
      }
      writer.writeNamespace(prefix, getNamespaceRegistry().getURI(prefix));

    }
  }

}
