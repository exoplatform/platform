Summary

    * Status: Avoid LDAP direct requests
    * CCP Issue: CCP-1032, Product Jira Issue: PLF-1990.
    * Complexity: N/A

The Proposal
Problem description

What is the problem to fix?

    * Avoid LDAP direct requests

Fix description

How is the problem fixed?

    * Using ConversationState.getCurrent().getIdentity() to get memberships of current user instead of using OrganizationService.
    * Using CacheUserProfileFilter to get user profile.

Patch file: PLF-1990.patch

Tests to perform

Reproduction test
*

Tests performed at DevLevel
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

    * Function or ClassName change: no

Is there a performance risk/cost?

    * No, this fix is to improve performance.

Validation (PM/Support/QA)

PM Comment

    * Patch approved.

Support Comment
*

QA Feedbacks
*
Labels parameters

