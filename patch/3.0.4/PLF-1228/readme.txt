Summary

    * Status: Switch Platform 3.0.x branch to exogtn 3.1.x rebased
    * CCP Issue: N/A, Product Jira Issue: PLF-1228.
    * Complexity: N/A

The Proposal
Problem description

What is the problem to fix?

Use the rebased version of exogtn: 3.1.8-REBASED-SNAPSHOT
Fix description

How is the problem fixed?

    * change the portal dependency version from 3.1.8-PLF-SNAPSHOT to 3.1.8-REBASED-SNAPSHOT
    * change exo.portal.component.dashboard dependency to exo.portal.webui.dashboard
    * change exo.portal.component.web dependency to exo.portal.component.web.api

Patch information:
Patch files: PLF-1228.patch

Tests to perform

Reproduction test
*

Tests performed at DevLevel
*

Tests performed at QA/Support Level
*

Documentation changes

Documentation changes:
*

Configuration changes

Configuration changes:
*

Will previous configuration continue to work?
*

Risks and impacts

Can this bug fix have any side effects on current client projects?

    * Function or ClassName change

Is there a performance risk/cost?
*

Validation (PM/Support/QA)

PM Comment
*

Support Comment
*

QA Feedbacks
*

