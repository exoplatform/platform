Summary
	* Issue title Performance problem of UIMySpacePlatformToolBarPortlet and UIMyGroupsPlatformToolBarPortlet with big dataset 
	* CCP Issue:  N/A 
    	* Product Jira Issue: PLF-3498.
    	* Complexity: N/A

Proposal
 
Problem description

What is the problem to fix?
	* In Platform v3.5.5-SNAPSHOT - build 20120815 I create dataset 1000 user and 290 space, when I login as root, the time  render the UIMyGroupsPlatformToolBarPortlet and UIMySpacePlatformToolBarPortlet is very long ( more than 20 minutes ) even we already limit the number of space to display in navigation only 10 items.

Fix description

Problem analysis
	* There are some parts of toolbarPortlet not be well coded ( get all UserNode even not sure if will be use or not, load all navigations but displays only 10).

How is the problem fixed?
	* Make code better align.


Tests to perform

Reproduction test
	* Create dataset 1000 user and 290 space
	* Login as root, the time to render the UIMyGroupsPlatformToolBarPortlet and UIMySpacePlatformToolBarPortlet very high (more than 20 minutes ) even we already limit the number of space to display in navigation only 10.

Tests performed at DevLevel
	*

Tests performed at Support Level
	*

Tests performed at QA
	* Performance test

Changes in Test Referential

Changes in SNIFF/FUNC/REG tests
	*

Changes in Selenium scripts 
	*

Documentation changes

Documentation (User/Admin/Dev/Ref) changes:


Configuration changes

Configuration changes:
	* No

Will previous configuration continue to work?
	* Yes

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
	*

QA Feedbacks
	*
