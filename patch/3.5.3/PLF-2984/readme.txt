Summary
    * Issue title: Publication problem in Activity stream 
    * CCP Issue: N/A
    * Product Jira Issue: PLF-2984.
    * Complexity: N/A

Proposal
 
Problem description

What is the problem to fix?

    * PLF-2984: Wrong activity stream when add new user
          o Login to Intranet/home > no activity is updated: OK
          o Create new user successfully
          o Go to Intranet/home
            Actual result: Display new activity: NOK
    * PLF-2361: Publication status and Activity stream

Fix description

Problem analysis
    * When a node is created under the user directory, wcm publication service detects it and attempts to set its publication status. This is also detected by the listenerservice that publishes an activity for it. 
      Here is the chain : user created > symlink created > publication state set > activity created

How is the problem fixed?
    * Add a new publication context named "context2" that matches the default publication lifecycles, and enroll only content under collaboration:/sites content/live.
      Add this component plugin to  wcm-publication-configuration.xml
    * Remove the old publication context contextdefault
    * Use the new implementation of PublicationManager Plugin 

Patch file: PLF-2984.patch

Tests to perform

Reproduction test
* Case 1:
  - Create a content inside a space or the intranet area
  - Actual result: All contents of default lifecycles (draft, pending, approved, staged and published states) are published on activity stream.
  - Expected result: contents without publication states at all.
* Case 2:
  - Login to Intranet/home > no activity is updated: OK
  - Create new user successfully
  - Go to Intranet/home
  Actual result: Display new activity: NOK

Tests performed at DevLevel

    * cf. above

Tests performed at Support Level

    * cf. above

Tests performed at QA

    * cf. above

Changes in Test Referential

Changes in SNIFF/FUNC/REG tests

    * No

Changes in Selenium scripts 

    * No

Documentation changes

Documentation (User/Admin/Dev/Ref) changes:

    * N/A

Configuration changes

Configuration changes:

    * No

Will previous configuration continue to work?

    * Yes

Risks and impacts

Can this bug fix have any side effects on current client projects?

    * Function or ClassName change: None
    * Data (template, node type) migration/upgrade: 

Is there a performance risk/cost?

    * No


Validation (PM/Support/QA)

PM Comment

    * Patch validated

Support Comment

    * Approved.

QA Feedbacks

    * 


