=========================
    eXo Platform JMX Client
     25 August 2011
=========================

--------------
* Introduction
--------------

command line based JMX client. It's designed to allow user to access JMX from command line without graphical environment. 


--------------
* Usage
--------------

java -jar exo-cmd-jmxclient-${version}.jar <HOST_NAME> <HOST_PORT> [-u<USER>(Optionnal)] [-p<PASSWORD>(Optionnal)]  <BEAN_NAME> <OPERATION_NAME> <OPERATION_ARG_1> <OPERATION_ARG_2>...

<HOST_NAME>
		MBean Server host name
<HOST_PORT>
		MBean Server port name
<USER>		(Optionnal)
		user name
<PASSWORD>	(Optionnal)
		password
<BEAN_NAME>
		Bean name
<OPERATION_NAME>
		operation to invoke in the selected MBean
<OPERATION_ARG_1> <OPERATION_ARG_2>...
		List of operation arguments

Note: The list of arguments are sorted and couldn't be permuted.