package org.exoplatform.services.security.jaas.weblogic.authentication;


import javax.management.*;
import weblogic.management.commo.RequiredModelMBeanWrapper;



/**
 * No description provided.
 * @root ExoAuthenticator
 * @customizer org.exoplatform.services.security.jaas.weblogic.authentication.ExoAuthenticatorImpl(new RequiredModelMBeanWrapper(this))
 * @dynamic false

 */
public interface ExoAuthenticatorMBean extends weblogic.management.commo.StandardInterface,weblogic.descriptor.DescriptorBean, weblogic.management.security.authentication.AuthenticatorMBean {
                
        


        /**
         * No description provided.

         * @default "org.exoplatform.services.security.jaas.weblogic.authentication.ExoAuthenticationProviderImpl"
         * @dynamic false
         * @non-configurable
         * @validatePropertyDeclaration false

         * @preserveWhiteSpace
         */
        public String getProviderClassName();


        
        


        /**
         * No description provided.

         * @default "Provider that performs authentication for ExoPlatform"
         * @dynamic false
         * @non-configurable
         * @validatePropertyDeclaration false

         * @preserveWhiteSpace
         */
        public String getDescription();


        
        


        /**
         * No description provided.

         * @default "1.0"
         * @dynamic false
         * @non-configurable
         * @validatePropertyDeclaration false

         * @preserveWhiteSpace
         */
        public String getVersion();


        
        


        /**
         * No description provided.

         * @default ""
         * @dynamic false

         * @preserveWhiteSpace
         */
        public String getLoginModuleClass();


        /**
         * No description provided.

         * @default ""
         * @dynamic false

         * @param newValue - new value for attribute LoginModuleClass
         * @exception javax.management.InvalidAttributeValueException
         * @preserveWhiteSpace
         */
        public void setLoginModuleClass(String newValue)
                throws InvalidAttributeValueException;



        
        /**
         * @default "ExoAuthenticator"
         * @dynamic false
         */
         public String getName();

          

}
