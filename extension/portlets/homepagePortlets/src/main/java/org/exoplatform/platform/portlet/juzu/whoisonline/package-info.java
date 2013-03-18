@Portlet
@Application(name = "WhoIsOnline")
@Bindings(@Binding(value = WhoIsOnline.class, implementation = WhoIsOnlineImpl.class))
package org.exoplatform.platform.portlet.juzu.whoisonline;

import juzu.Application;
import juzu.plugin.binding.Binding;
import juzu.plugin.binding.Bindings;
import juzu.plugin.portlet.Portlet;

