Summary

    * Status: UI bug when too many spaces
    * CCP Issue: CCP-922, Product Jira Issue: PLF-1521.
    * Complexity: N/A

The Proposal
Problem description

What is the problem to fix?
1)login to acme intranet site
2)create as much spaces as spaces list's height in spaces navigation exceeds the page height as in this screenshot==>KO
(PS:may be you need to refresh the page after creating all spaces to see all of them in spaces list)
The Portal/Gatein Menu does not work properly when this list is too big:
One proposal for a solution:

we show only few of them (5/7...) kind of do not take too much space in the menu
a new entry menu "All...:" or More ... that goes to http://ServerIP:PortNumber/portal/public/intranet/spaces directly
Another proposal :
it would be great if the 5 spaces shown are the spaces the user uses the most
Fix description

abstract : separating My Spaces and the other group pages in two menus. My Spaces would be displayed only on intranet
Hence, the toolbar menus would be :
acme : My Sites, My Groups, Dashboard, Site Editor
intranet : My Sites, My Spaces, My Groups, Dashboard, Site Editor

My Spaces menu :

=========
My Spaces
=========
Space 1
Space 2
Space 3
Space 4
Space 5
Space 6
Space 7
Space 8
Space 9
Space 10
---------
More..
Find Spaces

    * limit the number of spaces displayed to 10
    * add a "More..." (EN) / "Plus..." menu that will link to "/intranet/spaces"

My Groups menu available everywhere

==========
My Groups
==========
Content Manager
===============
Sites Explorer
Newsletter Manager
Form Generator
Sites Administration
=============
Administrator
=============
Portal Administration
==========
Developer
==========
IDE
=============
Manage Groups
The following *functional change*, would allow to satisfy everyone :


In addition, this could be backed by a portlet preference to under which portals we display the My Spaces menu

Patch files:PLF-1521.patch

Tests to perform

Reproduction test
* login to acme intranet site
* create as much spaces as spaces list's height in spaces navigation exceeds the page height

Tests performed at DevLevel
*

Tests performed at QA/Support Level
*
Documentation changes

Documentation changes:
* None
Configuration changes

Configuration changes:
* None

Will previous configuration continue to work?
* yes
Risks and impacts

Can this bug fix have any side effects on current client projects?

    * Function or ClassName change

Is there a performance risk/cost?
* None
Validation (PM/Support/QA)

PM Comment
* PL review: patch validated

Support Comment
* Support review: patch validated

QA Feedbacks
*

