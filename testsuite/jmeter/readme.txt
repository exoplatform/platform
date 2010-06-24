How to try:

1/ Deploy some artifacts in your local repo
* http://code.google.com/p/jmeter-maven-plugin/wiki/HOWTOUsePlugin
* or wait for http://jira.exoplatform.org/browse/SWF-355 to be resolved

2/ Configure
* in /src/test/jmeter/ you can add/modify some *.jmx files
** all of them will be executed
* in the same folder, you can change some default properties for jmeter
* in the pom.xml file, you can add/change some properties related to your jmx files
** such as the host on which the server runs, the number of threads, etc
** you can choose between 2 xsl stylesheets to generate the report, one is detailed, one is not
* in /src/test_resources you can modify the xsl stylesheets to change the way the results are displayed

3/ Run
* simply run `mvn verify`
* results are in
** /target/jmeter-reports/ : the jmeter report files (jtl format)
** /target/site/jmeter-reports/ : the html report
