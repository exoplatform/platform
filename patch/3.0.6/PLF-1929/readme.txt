Summary

    * Status: CLONE - Exception when select Content Explorer
    * CCP Issue: CCP-1032, Product Jira Issue: PLF-1929.
    * Complexity: N/A

The Proposal
Problem description

What is the problem to fix?
After committing ECMS-2493, Content Explorer page is now inaccessible in PLF 3.0.6-snapshot (access from webdav is still ok).

    * Go to Content Explorer page --> exception
      
      Aug 22, 2011 10:32:23 AM org.exoplatform.ecm.webui.component.explorer.UIJcrExplorerContainer initExplorer
      SEVERE: Unexpected error
      java.lang.NullPointerException
          at org.exoplatform.services.cms.drives.impl.ManageDriveServiceImpl.getGroupDrives(ManageDriveServiceImpl.java:471)
          at org.exoplatform.services.cms.drives.impl.ManageDriveServiceImpl.getDriveByUserRoles(ManageDriveServiceImpl.java:442)
          at org.exoplatform.ecm.webui.component.explorer.UIJCRExplorerPortlet.canUseConfigDrive(UIJCRExplorerPortlet.java:275)
          at org.exoplatform.ecm.webui.component.explorer.UIJcrExplorerContainer.initExplorer(UIJcrExplorerContainer.java:86)
          at org.exoplatform.ecm.webui.component.explorer.UIJCRExplorerPortlet.<init>(UIJCRExplorerPortlet.java:123)
          at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
          at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:39)
          at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:27)
          at java.lang.reflect.Constructor.newInstance(Constructor.java:513)
          at org.exoplatform.webui.Util.createObject(Util.java:63)
      ...

Fix description

How is the problem fixed?
Update the configuration concerning group drives:

    * Remove NewGroupListener in organization-component-plugins-configuration.xml
    * Add new group drive template (Groups) in dms-drives-configuration.xml

Patch file: PLF-1929.patch

Tests to perform

Reproduction test
*

Tests performed at DevLevel
*

Tests performed at QA/Support Level
*
Documentation changes

Documentation changes:
*

Configuration changes

Configuration changes:

    * See above.

Will previous configuration continue to work?

    * No.

Risks and impacts

Can this bug fix have any side effects on current client projects?

    * Function or ClassName change: no

Is there a performance risk/cost?

    * No.

Validation (PM/Support/QA)

PM Comment
*

Support Comment
*

QA Feedbacks
*
