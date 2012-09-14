Summary
	* Issue title: DashboardInformationRESTService should not depend on Default Portal 
    	* CCP Issue:  CCP-1498 
    	* Product Jira Issue: PLF-3464.
   	* Complexity: N/A

Proposal

Problem description

What is the problem to fix?
	* DashboardInformationRESTService should not depend on Default Portal 

Fix description

Problem analysis
	* The DashboardInformationRESTService  depends on default Portal to retrieve the userPortalConfig.this dependence forces:
    		- Always have a portal defined as "default"
    		- Give access permission to all users to that portal, so that the default portal is visible to everybody. 

How is the problem fixed?
	* The information about the dashboard should be retrievable without having to pass by a any portal: Don't use the userPortalConfig, Loading directly the UserNavigation.

Tests to perform

Reproduction test
	* Modify the access and edit permissions of the default portal, so that mary has no permissions anymore and the portal becomes invisible. For example give permission to "executive-board" or "developer".
	* Log in as "mary" who has no permission to see the default portal.
	* Go to mary's dashboard and create an entry (optional step)
	* Call the DashboardInformationRESTService:
    		http://localhost:8080/rest/private/dashboards
    		-> NOK: Error 500. No entry returned. Instead there is a NPE in the logs.

Tests performed at DevLevel
	* c/f above

Tests performed at Support Level
	* c/f above

Tests performed at QA
	*

Changes in Test Referential

Changes in SNIFF/FUNC/REG tests
	*

Changes in Selenium scripts 
	*

Documentation changes

Documentation (User/Admin/Dev/Ref) changes:


Configuration changes

Configuration changes:
	*

Will previous configuration continue to work?
	*

Risks and impacts

Can this bug fix have any side effects on current client projects?
	* Function or ClassName change: 
	* Data (template, node type) migration/upgrade: 

Is there a performance risk/cost?
	*


Validation (PM/Support/QA)

PM Comment
	* Validated

Support Comment
	* Validated

QA Feedbacks
	*
