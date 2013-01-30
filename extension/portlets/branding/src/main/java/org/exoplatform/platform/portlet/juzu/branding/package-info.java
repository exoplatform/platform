/**
 * Copyright ( C ) 2012 eXo Platform SAS.
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
                @Script(id = "jquery",src ="js/common/jquery-1.8.3.js", location = juzu.asset.AssetLocation.SERVER),
                @Script(id="form",src = "js/common/jquery.form.js", depends="jquery", location = juzu.asset.AssetLocation.SERVER),
                @Script(id="firedrop",src="js/common/jquery.filedrop.js",depends="jquery",location = juzu.asset.AssetLocation.SERVER),
                @Script(src = "branding.js", depends={"form","firedrop"}, location = AssetLocation.CLASSPATH)
        }  
)
package org.exoplatform.platform.portlet.juzu.branding;
import juzu.Application;
import juzu.asset.AssetLocation;
import juzu.plugin.asset.Assets;
import juzu.plugin.asset.Script;
import juzu.plugin.portlet.Portlet;
