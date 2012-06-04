Summary
	* Issue title: Bad UI (Tool bar, Navigation bar) of default portal templates
	* CCP Issue:  n/a
	* Product Jira Issue: PLF-2757.
	* Complexity: N/A

Proposal
 
Problem description

What is the problem to fix?
	* Bad UI (Tool bar, Navigation bar) of default portal templates

Fix description

Problem analysis
	* Create a new portal with any default portal template:
		- The Tool bar is pushed to the left, the right-most part (User tool bar, Setting, Pin icon) is therefore invisible in the real size, see IntranetTemplate-Toolbar-NOK. They only appear after zooming in several times.
		- With portal of Empty or ACME template, add a new navigation node (and set Visible).
		- Community bundle: this node doesn't appear in the navigation bar, see NoNewNavigationNode-NavigationBar.
		- Other bundles: when the navigation list is too long, there isn't the scroll bar but PLF-3.5.1-tomcat-emptytemplate-BadUI-LongNavigationBar or PLF-3.5.1-tomcat-ACMEtemplate-LongNavigationBar-BadUI

How is the problem fixed?
	* Move site templates in their corresponding extension:
		- Acme-site template should be transferred to acme-website extension
		- Intranet -site templates should be transferred to acme-intranet extension
		- Empty-site template should stay on platform-extension

Patch file: git pull request :https://github.com/exoplatform/platform/pull/29

Tests to perform

Reproduction test
	* Create new portal with avaible templates -> UI error

Tests performed at DevLevel
	* n/a

Tests performed at Support Level
	* n/a

Tests performed at QA
	* n/a

Changes in Test Referential

Changes in SNIFF/FUNC/REG tests
	* n/a

Changes in Selenium scripts 
	* n/a

Documentation changes

Documentation (User/Admin/Dev/Ref) changes:


Configuration changes

Configuration changes:
	*

Will previous configuration continue to work?
	*
	
Risks and impacts

Can this bug fix have any side effects on current client projects?

    Function or ClassName change: 
    Data (template, node type) migration/upgrade: 

Is there a performance risk/cost?
	* No

Validation (PM/Support/QA)

PM Comment
	* Validated.

Support Comment
	* Validated

QA Feedbacks
	* n/a
