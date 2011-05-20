Summary

    * Status: Remove XSD declaration from exo.platform.migration.common
    * CCP Issue: CCP-802, Product Jira Issue: PLF-1270.
    * Complexity: N/A

The Proposal
Problem description

What is the problem to fix?

    * When we migrate from AIO to PLF 3.0.3 a problem occurred and we get an exception after startup the server:
      ?
      server6/lib/exo.platform.migration.common-3.0.3.jar!/conf/portal/configuration.xml
      org.jibx.runtime.JiBXException: No unmarshaller for element "{http://www.exoplaform.org/xml/ns/kernel_1_1.xsd}configuration" (line 2, col 243)
              at org.jibx.runtime.impl.UnmarshallingContext.unmarshalElement(UnmarshallingContext.java:2770)
              at org.jibx.runtime.impl.UnmarshallingContext.unmarshalDocument(UnmarshallingContext.java:2916)
              at org.exoplatform.container.configuration.ConfigurationManagerImpl.addConfiguration(ConfigurationManagerImpl.java:119)
              at org.exoplatform.container.configuration.ConfigurationManagerImpl.addConfiguration(ConfigurationManagerImpl.java:95)
              at org.exoplatform.container.RootContainer.createPortalContainer(RootContainer.java:111)
              at org.exoplatform.portal.application.PortalController.init(PortalController.java:65)
              at org.apache.catalina.core.StandardWrapper.loadServlet(StandardWrapper.java:1161)
              at org.apache.catalina.core.StandardWrapper.load(StandardWrapper.java:981)
              at org.apache.catalina.core.StandardContext.loadOnStartup(StandardContext.java:4058)
              at org.apache.catalina.core.StandardContext.start(StandardContext.java:4364)
      ...

Fix description

How is the problem fixed?

    * Remove the configuration from common artifact, and duplicate it in aio and plf part, in order to define for each part its own XSD.

Patch files:PLF-1270.patch

Tests to perform

Reproduction test
* Apply AIO migration to PLF.

Tests performed at DevLevel
* None

Tests performed at QA/Support Level
*
Documentation changes

Documentation changes:
* No
Configuration changes

Configuration changes:
* No

Will previous configuration continue to work?
* Yes
Risks and impacts

Can this bug fix have any side effects on current client projects?

    * Function or ClassName change: None

Is there a performance risk/cost?
* No
Validation (PM/Support/QA)

PM Comment
* PL review: Patch validated

Support Comment
* Support review: Patch validated

QA Feedbacks
*

