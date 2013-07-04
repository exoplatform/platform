Summary
	* Issue title Exception: "Error while handling request" and "Can not get path of Logo" while running test with scenario LOGIN_SERVICE	
	* Product Jira Issue: PLF-5109.
	* Complexity: N/A

Proposal
Problem description
What is the problem to fix?
	* Exceptions are thrown in that are caused by using session which is alreadly closed.

Fix description
Problem analysis
	* The problem is in "getHomePageLogoURI()" of "NavigationServiceHandler" class. In this method, global session which is shared between many threads is implemented. When a thread closes global session and another thread try to use this session, exception noticing session.logout() occurs 

How is the problem fixed?
	* This issue is fixed by using local session once getting home page  logo URI in this method, so that each thread will open its own session when it gets logo URI.

Tests to perform
Reproduction test
	* While doing performance test with PLF4.0.x, 2 exception is raised
		* Error while handling request due to *Session provider already closed*
		* Cannot get path of Logo following with *This kind of operation is forbidden after a session.logout()*

Tests performed at Support Level
	* N/A

Tests performed at QA
	* Performance test
	
Changes in Test Referential
Changes in SNIFF/FUNC/REG tests
	*No
Changes in Selenium scripts 
	* No

Documentation changes
Documentation (User/Admin/Dev/Ref) changes:
	* No

Configuration changes
Configuration changes:
	* No

Will previous configuration continue to work?
	* N/A

Risks and impacts
Can this bug fix have any side effects on current client projects?
	* Any change in API (name, signature, annotation of a class/method)? : No
	* Data (template, node type) upgrade:  No

Is there a performance risk/cost?
	* No