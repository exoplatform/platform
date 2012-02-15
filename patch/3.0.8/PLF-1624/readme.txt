Summary

    * Status: Cannot apply style for content of post
    * CCP Issue: CCP-969, Product Jira Issue: PLF-1624.
    * Complexity: N/A

The Proposal
Problem description

What is the problem to fix?
    * In Forum application, BBcode format isn't taken into account.

Fix description

How is the problem fixed?
* In PLF's stylesheet, replace * by the precise supported tags. That makes Forum's stylesheet work as expected.


Patch file: PLF-1624.patch

Tests to perform

Reproduction test

    * In Forum application: create new topic, or create new post.
    * Use BB Code to format text.

[size=+2]this text is two sizes larger than norm[/size]
 
[font=courier]this text is in the courier font[/font]

Tests performed at DevLevel

    * cf. above

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

    * Function or ClassName change: None

Is there a performance risk/cost?

    * No

Validation (PM/Support/QA)

PM Comment
* Validated on behalf of PM.

Support Comment
* Support review: Patch validated

QA Feedbacks
*
