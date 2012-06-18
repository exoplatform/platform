Summary
	* Issue title: Cannot create portal container exception in Jboss with java ibm
	* CCP Issue:  n.a
	* Product Jira Issue: PLF-2592.
	* Complexity: N/A

Proposal
 
Problem description

What is the problem to fix?
	* Cannot create portal container exception in Jboss with java ibm

Fix description

Problem analysis
	* JDK IBM has in its own lib xerces. This could cause many problems. 
	* The default IBM XMLOutputFactory (com.ctc.wstx.stax.WstxOutputFactory) write empty XML elements without enclosure element, means: <script ... />. But the ZephyrWriterFactory write it in the right way: <script ...></script>. (HTML standard, see this page: http://www.whatwg.org/specs/web-apps/current-work/multipage/scripting-1.html). The JDK sun XMLOutputFactory is com.sun.xml.internal.stream.XMLOutputFactoryImpl

How is the problem fixed?
	* Add sjsxp-1.0.1.jar into both PLF-tomcat and PLF-Jboss bundle
	* Use com.sun.xml.stream.ZephyrWriterFactory as default by add configuration in $PLF-tomcat/bin/setenv.sh and PLF-Jboss/bin/run.conf

EXO_XML="-Djavax.xml.stream.XMLOutputFactory=com.sun.xml.stream.ZephyrWriterFactory -Djavax.xml.stream.XMLInputFactory=com.sun.xml.stream.ZephyrParserFactory -Djavax.xml.stream.XMLEventFactory=com.sun.xml.stream.events.ZephyrEventFactory"

	* Patch file: git pull request :https://github.com/exoplatform/platform/pull/54

Tests to perform

Reproduction test
	* Cannot create portal container' and many other exceptions at start up

Tests performed at DevLevel
	* Functional test

Tests performed at Support Level
	*  Functional test

Tests performed at QA


Changes in Test Referential

Changes in SNIFF/FUNC/REG tests
	*N/A

Changes in Selenium scripts 
	* N/A

Documentation changes

Documentation (User/Admin/Dev/Ref) changes:
	* N/a

Configuration changes

Configuration changes:
	* Add configuration in $PLF-tomcat/bin/setenv.sh and PLF-Jboss/bin/run.conf
	* Will previous configuration continue to work?
		Yes: if run PLF with Sun JDK
		No: if run PLF with IBM JDK

Risks and impacts

Can this bug fix have any side effects on current client projects?
	* Function or ClassName change: No 
	* Data (template, node type) migration/upgrade: n/a 

Is there a performance risk/cost?
	* n/a

Validation (PM/Support/QA)

PM Comment
	* Validated

Support Comment
	* Validated

QA Feedbacks
	* 

