/*
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
package org.exoplatform.platform.component.jmxclient;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;


import java.util.logging.Level;
import  java.util.logging.Logger;

public class CommandHandler {

  private static final Logger LOGGER = Logger.getAnonymousLogger();

  public static void main(String[] args) {
    try {
      if ((args.length < 4)) {
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("<HOST_NAME> <HOST_PORT> -u<USER>(Optionnal) -p<PASSWORD>(Optionnal) <BEAN_NAME> <OPERATION_NAME> <OPERATION_ARG_1> <OPERATION_ARG_2>...");
        }
        return;
      }

      CommandArguments commandArguments = extractArguments(args);
      JMXConnector jmxConnector = connectToJMXServer(commandArguments);
      MBeanServerConnection serverConnection = jmxConnector.getMBeanServerConnection();
      String beanName = commandArguments.getBeanName();
      ObjectName objectName = (beanName != null) && (beanName.length() > 0) ? new ObjectName(beanName) : null;
      Set<?> beans = serverConnection.queryMBeans(objectName, null);
      if (beans.size() == 1) {
        ObjectInstance instance = (ObjectInstance) beans.iterator().next();
        invokeOperation(serverConnection, instance, commandArguments.getCommand(), commandArguments.getmBeanArguments());
      } else {

        if (LOGGER.isLoggable(Level.INFO)) {

            LOGGER.info("Cannot find bean: " + objectName.getCanonicalName());
        }
      }
      jmxConnector.close();
    } catch (Exception exception) {

        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info(exception.getMessage());
        }
    }
  }

  public static CommandArguments extractArguments(String[] args) {
    CommandArguments commandArguments = new CommandArguments();
    commandArguments.setHostname(args[0]);
    commandArguments.setHostport(args[1]);
    int argsIndex = 4;
    if (args[2].contains("-u")) {
      Map environmentMap = new HashMap<String, Object>(1);
      environmentMap.put("jmx.remote.credentials", new String[] { args[2].substring(2), args[3].substring(2) });
      commandArguments.setEnvironmentMap(environmentMap);
      argsIndex = 6;
    }
    commandArguments.setBeanName(args[argsIndex - 2]);
    commandArguments.setCommand(args[argsIndex - 1]);
    if (args.length > argsIndex) {
      commandArguments.setmBeanArguments(Arrays.copyOfRange(args, argsIndex, args.length));
    }

    return commandArguments;
  }

  private static JMXConnector connectToJMXServer(CommandArguments commandArguments) throws MalformedURLException, IOException {
    JMXConnector jmxConnector;
    JMXServiceURL jmxServiceURL = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + commandArguments.getHostname() +
            ":" + commandArguments.getHostport() + "/jmxrmi");
    if (commandArguments.getEnvironmentMap() == null) {
      jmxConnector = JMXConnectorFactory.connect(jmxServiceURL);
    } else {
      jmxConnector = JMXConnectorFactory.connect(jmxServiceURL, commandArguments.getEnvironmentMap());
    }
    return jmxConnector;
  }

  public static void invokeOperation(MBeanServerConnection mbsc, ObjectInstance instance, String command, String[] mBeanArguments)
      throws Exception {
    MBeanOperationInfo[] beanOperationInfos = mbsc.getMBeanInfo(instance.getObjectName()).getOperations();
    MBeanOperationInfo beanOperationInfo = null;
    for (MBeanOperationInfo beanOperationInfoTmp : beanOperationInfos) {
      if (beanOperationInfoTmp.getName().equals(command)) {
        beanOperationInfo = beanOperationInfoTmp;
      }
    }
    if (beanOperationInfo == null) {

        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("Operation (" + command + ") not found in bean(" + instance.getObjectName()
                    + ")\n list of available operations: ");
        }

      for (MBeanOperationInfo beanOperationInfoTmp : beanOperationInfos) {
          if (LOGGER.isLoggable(Level.INFO)) {
              LOGGER.info(beanOperationInfoTmp.getName());
          }
      }
    } else {
      MBeanParameterInfo paraminfos[] = beanOperationInfo.getSignature();
      int paramsLength = paraminfos != null ? paraminfos.length : 0;
      int argumentsLength = mBeanArguments != null ? mBeanArguments.length : 0;
      if (paramsLength == argumentsLength) {
        String signature[] = new String[paramsLength];
        Object params[] = paramsLength != 0 ? new Object[paramsLength] : null;
        for (int i = 0; i < paraminfos.length; i++) {
          MBeanParameterInfo paraminfo = paraminfos[i];
          Constructor<?> c = Class.forName(paraminfo.getType()).getConstructor(new Class[] { String.class });
          params[i] = c.newInstance(new Object[] { mBeanArguments[i] });
          signature[i] = paraminfo.getType();
        }

        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("Invocation response: " + mbsc.invoke(instance.getObjectName(), command, params, signature));
        }

      } else {
          if (LOGGER.isLoggable(Level.INFO)) {
              LOGGER.info("Parameters does not match operation signature");
          }
      }
    }
  }
}
