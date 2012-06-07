Summary
	* Issue title: Migrate JOS JCR namespace from PLF-3.0.x old data to run on PLF-3.5.x
	* CCP Issue:  n/a
	* Product Jira Issue: PLF-2424.
	* Complexity: N/A

Proposal
 
Problem description

What is the problem to fix?
	* Migrate JOS JCR namespace from PLF-3.0.x old data to run on PLF-3.5

Fix description

Problem analysis
	* PLF-3.0.x defines  JOS namespace as "http://nl.ijs.si/jos/".
	* But now PLF-3.5 changes JOS namespace  by new value "http://www.exoplatform.com/jcr-services/organization-service/1.0/"
	* Expected: Migrate namespace JOS from PLf-3.0.x old data to run on PLF-3.5

How is the problem fixed?
	* Delete nodes using the old namespace
	* Unregister the node-types using the old namespace
	* Update the namespace URI

	* Patch file: git pull request: https://github.com/exoplatform/platform/pull/32

Tests to perform

Reproduction test
	* PLF-3.0.x defines JOS namespace as "http://nl.ijs.si/jos/".
	* But now PLF-3.5 changes JOS namespace by new value "http://www.exoplatform.com/jcr-services/organization-service/1.0/"

Tests performed at DevLevel
	* Migration test

Tests performed at Support Level
	* Migration test
	
Tests performed at QA
	* Migration test

Changes in Test Referential

Changes in SNIFF/FUNC/REG tests
	* n/a

Changes in Selenium scripts 
	* n/a

Documentation changes

Documentation (User/Admin/Dev/Ref) changes:
	* Need to update Admin guide: https://jira.exoplatform.org/browse/DOC-1562

Configuration changes

Configuration changes:
	* Active this plugin by adding the following instruction in configuration.properties 
		commons.upgrade.UpgradeNamespaceJosPlugin.enable=true
	* Deactive this plugin by adding the following instruction in configuration.properties 
		commons.upgrade.UpgradeNamespaceJosPlugin.enable=false
	* Update the ordering of plugins

Will previous configuration continue to work?
	* Yes: if we do not need to migrate from PLF 3.0.x to PLF 3.5.x
	* No: if we need to migrate from PLF 3.0.x to PLF 3.5.x

Risks and impacts

Can this bug fix have any side effects on current client projects?
	* Function or ClassName change: N/a 
	* Data (template, node type) migration/upgrade: N/a 

Is there a performance risk/cost?
	* n/a

Validation (PM/Support/QA)

PM Comment
	* Validated

Support Comment
	* Validated

QA Feedbacks
	* 
