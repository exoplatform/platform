Summary
	* Issue title: Login history node should be created on start of the container
	* CCP Issue: n/a
	* Product Jira Issue: PLF-3366.
	* Complexity: N/A

Proposal
 
Problem description

What is the problem to fix?
	* Login history node should be created on start of the container

Fix description

Problem analysis
	* Problem happens in Cloud Workspaces for second and following created tenants. For them the login history node doesn't exist. It happens because this node wasn't created on the initial server start, because it will be only created on a first user login.

How is the problem fixed?
	* Login history service made Startable and related REST service depends on it via constructor dependency .

Tests to perform

Reproduction test
	* LoginHistoryServiceImpl component creates exo:LoginHistoryHome node in its construtor where it consumes injected RepositoryService. But RepositoryService itself is Startable component and JCR service will be fully ready only after the container start (call of RepositoryService.start()).
	* History node creation should be moved to the start method, this will create it on start of the container.
	* LoginHistoryRestService consumes LoginHistoryService but doesn't depend on it and uses direct get from the container instead. It should depend, and then the service will be injected by the container, what will also be a guaranty that the Login History service will be properly initialized by the container. The LoginHistoryService instance should be stored in the class variable and used from it in its methods.

Tests performed at DevLevel
	* Tested under Platform 3.5.4 standalone and Cloud Workspaces 1.1, patched jar deployed as part of CW 1.1.0-Beta03.

Tests performed at Support Level
	* Functional test on LoginHistoryService

Tests performed at QA
	*

Changes in Test Referential

Changes in SNIFF/FUNC/REG tests
	* None

Changes in Selenium scripts 
	* None

Documentation changes

Documentation (User/Admin/Dev/Ref) changes:
	* None

Configuration changes

Configuration changes:
	* Nothing

Will previous configuration continue to work?
	* Yes

Risks and impacts

Can this bug fix have any side effects on current client projects?
	* Function or ClassName change: none
	* Data (template, node type) migration/upgrade: none

Is there a performance risk/cost?
	* No

Validation (PM/Support/QA)

PM Comment
	* Validated

Support Comment
	*

QA Feedbacks
	*
