Summary
	* Issue title: Problem with OrganizationIntegrationService with lot of users 
   	* Product Jira Issue: PLF-3081.
    	Complexity: N/A

Proposal
	* Use DataDistributionManager in "OPTIMIZE" mode, as used for users personal JCR folders.
	* Modify the structure of nodes created by OrganizationIntegrationService to reduce the number of read/write operations when synchronizing.

Problem description
	* Using eXo with an LDAP containing 2500 users, this will reduce considerably the performances of Organization operations such as: adding a user to a group.


Fix description

Problem analysis
	* With OrganizationIntegrationService, it is creating a lot of sub-nodes under these sub-folders:
		/OrganizationIntegrationService/memberships/ 
  		/OrganizationIntegrationService/groups/ 
    		/OrganizationIntegrationService/users/ 
    		/OrganizationIntegrationService/profiles/
	* This is a big problem especially with huge number of users. Each synchronisation this folder will contain more and more sub-nodes, especially for membership nodes.

How is the problem fixed?
	* Use DataDistributionManager in "OPTIMIZE" mode, as used for users personal JCR folders.
	* Modify the structure of nodes created by OrganizationIntegrationService to reduce the number of read/write operations when synchronizing.

Tests to perform

Reproduction test
        # Use PLF 3.5.x
        # Integrate with LDAP, containing a lot of user (2500)
	# Synchronize all users (Using rest service for exemple : /portal/rest/management/orgsync/syncAll)
	# Check number of subnode under /OrganizationIntegrationService/memberships/
	# Go in UserManagementPortlet, and try to add a user in web-contributors group. -> this action is too long.

Tests performed at DevLevel
	* JUnit tests: see org.exoplatform.platform.component.organization.test.TestOrganizationIntegration

Tests performed at Support Level
	*

Tests performed at QA
	* Performance tests

Changes in Test Referential

Changes in SNIFF/FUNC/REG tests
	*

Changes in Selenium scripts 
	*

Documentation changes

Documentation (User/Admin/Dev/Ref) changes:


Configuration changes

Configuration changes:
	* NONE

Will previous configuration continue to work?
	* YES

Risks and impacts

Can this bug fix have any side effects on current client projects?
	* This fix requires a Data migration.

Is there a performance risk/cost?
	* NO


Validation (PM/Support/QA)

PM Comment
	* Validated

Support Comment
	*

QA Feedbacks
	*
