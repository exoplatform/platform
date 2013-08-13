Summary
	Issue title: [Migration] gadget upgrade plugin does not work properly
	Product Jira Issue: PLF-5343.
	Complexity: N/A

Proposal
 

Problem description
What is the problem to fix?
	- [Migration] gadget upgrade plugin does not work properly

Fix description
Problem analysis
	- Gadget upgrade plugin remove the gadget and try force import gadget again. But it always can not import gadget by itself. So this plugin rely in GadgetDeplyer to import removed gadgets

How is the problem fixed?
	- Use ServletLocalImporter to import gadgets.
Tests to perform
Reproduction test
	- Activate Upgrade-Gadgets plugin
	- Start PLF server with data from older version
	- The gadget cannot be replaced directly
2013-08-06 14:18:15,688 | INFO | Replacing gadget FeaturedPoll with new content ... [o.e.p.u.p.UpgradeLocalGadgetsPlugin<Catalina-startStop-1>] 2013-08-06 14:18:18,489 | INFO | Gadget FeaturedPoll wasn't imported. It will be imported automatically with GadgetDeployer Service. [o.e.p.u.p.UpgradeLocalGadgetsPlugin<Catalina-startStop-1>] 2013-08-06 14:18:18,495 | INFO | Replacing gadget CacheLevels with new content ... [o.e.p.u.p.UpgradeLocalGadgetsPlugin<Catalina-startStop-1>]
Tests performed at DevLevel
	- 

Tests performed at Support Level
	- Upgrade test

Tests performed at QA
	- 

Changes in Test Referential
Changes in SNIFF/FUNC/REG tests
	- 

Changes in Selenium scripts 
	- 

Documentation changes
Documentation (User/Admin/Dev/Ref) changes:
	- No

Configuration changes
Configuration changes:
	- No

Will previous configuration continue to work?
	- Yes

Risks and impacts
Can this bug fix have any side effects on current client projects?
	- N/A

Any change in API (name, signature, annotation of a class/method)?: No 
Data (template, node type) upgrade: No
Is there a performance risk/cost? No
