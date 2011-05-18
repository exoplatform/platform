Summary

    * Status: Space Settings option can be seen by non-moderator user
    * CCP Issue: CPP-831, Product Jira Issue: PLF-1303.
    * Complexity: N/A

The Proposal
Problem description

What is the problem to fix?
We expect that only moderator user can see this option in toolbar.
At the moment, if we are not moderator and click on space setting into toolbar, a blank page will be displayed.
Expected: if you're not manager of a space, its space settings page node must not be displayed.
Fix description

How is the problem fixed?
* See if the current user have edit permissions on the space. If not, we will not render the SpaceSettings link in the Space menu.

Patch files: PLF-1303.patch

Tests to perform

Reproduction test
* Steps to reproduce:
1. Login as root
2. Create a new space (eXo Support)
3. Add some user to the newly created space (eg: marry, john)
4. Logout and Login as john
5. Go to Space/Invitation
6. Accept invitation of root
7. Go to My Space/eXo Support
--> View Setting Space

Expected: Only moderator user can see this option in tool-bar

Tests performed by Developer
*

Tests performed at QA/Support Level
*
Documentation changes

Documentation changes:
* No
Configuration changes

Configuration changes:
* No

Will previous configuration continue to work?
* Yes
Risks and impacts

Can this bug fix have any side effects on current client projects?

    * Function or ClassName change: No

Is there a performance risk/cost?
* No
Validation (PM/Support/QA)

PM Comment
* PL review: patch validated

Support Comment
* Support review: patch validated

QA Feedbacks
*

