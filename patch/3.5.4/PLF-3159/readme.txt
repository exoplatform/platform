Summary
	* Issue title: Spaces are not sorted by access frequency in special case
	* CCP Issue: N/A
	* Product Jira Issue: PLF-3159 PLF-2884.
	* Complexity:  Low

Proposal

 
Problem description

What is the problem to fix?
	* Spaces are not sorted by access frequency in special case

Fix description

Problem analysis
	* SpaceAccessService uses a thread local variable to keep information about sessions. However it is execute in another thread than main thread so it caused the bug

How is the problem fixed?
	* Remove the usage of thread local variable

Tests to perform

Reproduction test

Case 1:
	- Create "Platform Team" space
	- Access Platform Team Home at: http://localhost:8080/portal/g/:spaces:platform_team/platform_team
	- Bookmark this link and sign out
	- Click on bookmarked link, and sign in.
    -> Spaces are not sorted by access frequency and there are many exceptions that are flooding Intranet log.

Case 2:
	- Create 2 new users: user1 and user2
	- Login as user1
	- Create new space i.e. space1
	- Login as user2
	- Go to My Space page -> Select All spaces tab and click on "Request to Join" link to send request to space1
	- Login as user1
	- Do nothing and logout user1
	- Login user1 again
		-> Result: Throw out NPE in terminal  

Case 3:
	- Login as John
	- Create new space: test
	- Go to Group and Role -> Group Management
	- Select Group: Space/test and add Mary as manager
	- Logout John
	- Login Mary -> Throw out NPE in terminal

Tests performed at DevLevel
	* n/a

Tests performed at Support Level
	* n/a

Tests performed at QA
	* n/a

Changes in Test Referential

Changes in SNIFF/FUNC/REG tests
	* No

Changes in Selenium scripts 
	* No

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

    Function or ClassName change: No
    Data (template, node type) migration/upgrade: No

Is there a performance risk/cost?
	* No

Validation (PM/Support/QA)

PM Comment
	* Validated on behalf of PM

Support Comment
	* Validated

QA Feedbacks
	* 
