eXo Groovy Console gadget

=============================================================================================================

Team members:

* Do Thanh Tung
  - Team : PLF/INT
  - Email: tungdt@exoplatform.com
* Vu Minh Tung
  - Team : PLF/INT
  - Email: tungvm@exoplatform.com
* Nguyen Thanh Trung
  - Team : UI
  - Email: trungnt@exoplatform.com
=============================================================================================================

Descriptions:

  eXo Groovy Console allows you to run Groovy code interactively. It accepts eXo API and 
can access eXo components deployed in portal container so this can be used as a tool for 
drafting code, tesing or exploring eXo API/components/data interactively on a live system.

  In a session, variables and import statements are saved automatically as session context 
for user to reference later if needed.

=============================================================================================================

How to install

  This gadget consists of two parts:
  - Service: get Groovy expressions/script submitted from client, executes and returns result. Use intranet IDE to deploy.
  - Gadget: contains gadget XML file, javascript and stylesheet. Use intranet IDE to deploy.

=============================================================================================================

TODO:

1. Parse class and function definitions from input script, or implement an alternative way to save them to session's context. Currently class and function definitions are not saved due to the difficulty of parsing them using regular expression, however we can send them along with other statements to the console once at a time (as a single command in the console via copy/paste, or as a script)

2. Implement multi-lines edit mode to support editting multi-lines script right in the console. Currently to run a multi-lines script, user has to use extended input text area, or copy the script to clipboard then paste to the console.

3. Replace the extended input text area with a full-featured code editor that supports syntax highlight, code suggestion,...

4. Add Jython support to allow users to write script in Jython.


