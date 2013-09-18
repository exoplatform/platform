Summary
	- Issue title UIGroupsNavigationPortlet: Slowness in readNavigationsAndCache() method
	- Product Jira Issue: PLF-5372.
	- Complexity: N/A

Proposal

Problem description
What is the problem to fix?
	* UIGroupsNavigationPortlet: Slowness in readNavigationsAndCache() method

Fix description
Problem analysis
* We load the UserNode for each type of navigation (SITE, GROUP and USER) which is not necessary (only group nav are needed for this portlet)

How is the problem fixed?
	* In this portlet we need to display only the group navigation so we should load the UserNode only a group navigation (the current nav) and not all navigation type (SITE and USER)

Tests to perform
Reproduction test
	- Profiling session UIGroupsNavigationPortlet.png showed that the invocation of UIGroupsNavigationPortlet.readNavigationsAndCache() method in the context of Intranet Home page is causing some slowness 

Tests performed at DevLevel
	* N/A

Tests performed at Support Level
	* N/A

Tests performed at QA
	* N/A

Changes in Test Referential
Changes in SNIFF/FUNC/REG tests
	*

Changes in Selenium scripts 
	* 

Documentation changes
Documentation (User/Admin/Dev/Ref) changes:
	* No

Configuration changes
Configuration changes:
	* No

Will previous configuration continue to work?
	* Yes

Risks and impacts
Can this bug fix have any side effects on current client projects?

Any change in API (name, signature, annotation of a class/method)? No 
Data (template, node type) upgrade:  No
Is there a performance risk/cost? No
