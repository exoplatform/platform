/**
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.platform.navigation.component.help;

/**
 * @author <a href="kmenzli@exoplatform.com">Kmenzli</a>
 */
public interface HelpService {

    public static final String DEFAULT_HELP_PAGE= " http://docs.exoplatform.com/PLF35/index.jsp?topic=%2Forg.exoplatform.doc.35%2FUserGuide.html";
    String fetchHelpPage(String currentNavigation);
}
