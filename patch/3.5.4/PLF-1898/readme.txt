Summary
	* Issue title: Multi-Schema support in Single database 
    	* CCP Issue:  N/A
    	* Product Jira Issue: PLF-1898.
    	* Complexity: N/A

Problem description

What is the problem to fix?
	* Multi-Schema support in Single database 

Fix description

Problem analysis
	* Need configuration changes of JCR lock manager and Hibernate service

How is the problem fixed?
	* Changed configuration of JCR lock manager's JDBC loader: from JBossCache native implementation to JCR's implementation org.exoplatform.services.jcr.impl.core.lock.jbosscache.JDBCCacheLoader
	* Added (commented) optional configiration parameter for database schema of Hibernate service in configuration.properties: gatein.idm.datasource.schema

Tests to perform

Reproduction test
	* n/a

Tests performed at DevLevel
	* Database schema for Hibernate service tested under HSQL, by default it fails but this tells that configuration parameter is in use, so configuration works properly. Platform works as expected with the configuration changes

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
	* Changed Platform Configuration documentation in section about IDM datasources configuration

Configuration changes

Configuration changes:
	* No

Will previous configuration continue to work?
	*

Risks and impacts

Can this bug fix have any side effects on current client projects?

Is there a performance risk/cost?
	* maybe with -Xmx1024m JVM heap, need use bigger value

Validation (PM/Support/QA)

PM Comment
	* Validated

Support Comment
	* Validated

QA Feedbacks
	*
