Summary

    * Status: Migration Tool - Can't change lifecycle of imported contents
    * CCP Issue: CCP-772, Product Jira Issue: PLF-1168.
    * Complexity: N/A

The Proposal
Problem description

What is the problem to fix?

    * After importing contents from AIO to PLF, the migration guide asks to use crash to change the contents' lifecycle (from stageAndVersion to Authoring).
      After connecting to the collaboration workspace in crash, the first command fails.
      The command "select * from publication:stateAndVersionBasedPublication" outputs the following message "Unexpected exception: Node not found 4d160d480a32024e01e5ccd4a5b62dae at collaboration".

Fix description

How is the problem fixed?

    * The version history stored in JCR 'System' workspace wasn't exported. By this fix, the versionHistory will be backed up.

Patch file: PLF-1168.patch

Tests to perform

Reproduction test
*Steps to reproduce:
1. Login /acme demo
2. Create a new page or edit existed page
3. Add Content Details portlet
4. Switch to "Edit" mode
5. Select a draft version only node (refer Document_Draft_version_.png) in Content Path then save
6. Switch back to "Published" mode
7. Open edit form then click save --> the layout of current page is broken (refer layout_is_break_in_published_mode.png)

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
*PL review : patch approved

Support Comment
*Support review : patch validated

QA Feedbacks
*
Labels parameters

