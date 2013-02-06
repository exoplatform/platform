/*
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

@Application
@Portlet
@Assets(
    scripts = {
        @Script(id = "jquery", src = "js/common/jquery-1.8.3.js", location = juzu.asset.AssetLocation.SERVER),
        @Script(src = "gettingStarted.js", location = AssetLocation.CLASSPATH)

    },
    stylesheets = {
        @Stylesheet(src = "style/gettingstarted.css", location = AssetLocation.CLASSPATH)
    }
)
@Bindings(@Binding(NodeHierarchyCreator.class))
package org.exoplatform.platform.portlet.juzu.gettingstarted;

import juzu.Application;
import juzu.asset.AssetLocation;
import juzu.plugin.asset.Assets;
import juzu.plugin.asset.Script;
import juzu.plugin.asset.Stylesheet;
import juzu.plugin.binding.Binding;
import juzu.plugin.binding.Bindings;
import juzu.plugin.portlet.Portlet;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;

