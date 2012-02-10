package org.exoplatform.platform.gadget.services.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.jcr.NodeIterator;

import org.exoplatform.container.component.ComponentPlugin;
import org.exoplatform.forum.service.Category;
import org.exoplatform.forum.service.Forum;
import org.exoplatform.forum.service.ForumAdministration;
import org.exoplatform.forum.service.ForumAttachment;
import org.exoplatform.forum.service.ForumEventListener;
import org.exoplatform.forum.service.ForumEventQuery;
import org.exoplatform.forum.service.ForumLinkData;
import org.exoplatform.forum.service.ForumPrivateMessage;
import org.exoplatform.forum.service.ForumSearch;
import org.exoplatform.forum.service.ForumService;
import org.exoplatform.forum.service.ForumStatistic;
import org.exoplatform.forum.service.ForumSubscription;
import org.exoplatform.forum.service.JCRPageList;
import org.exoplatform.forum.service.LazyPageList;
import org.exoplatform.forum.service.MessageBuilder;
import org.exoplatform.forum.service.Post;
import org.exoplatform.forum.service.PruneSetting;
import org.exoplatform.forum.service.SendMessageInfo;
import org.exoplatform.forum.service.Tag;
import org.exoplatform.forum.service.Topic;
import org.exoplatform.forum.service.TopicType;
import org.exoplatform.forum.service.UserProfile;
import org.exoplatform.forum.service.Watch;
import org.exoplatform.services.organization.User;

public class MockForumService implements ForumService {

	@Override
	public void addPlugin(ComponentPlugin plugin) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void addRolePlugin(ComponentPlugin plugin) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void addInitialDataPlugin(ComponentPlugin plugin) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void addInitialDefaultDataPlugin(ComponentPlugin plugin)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Category> getCategories(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Category getCategory(String categoryId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getPermissionTopicByCategory(String categoryId, String type)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveCategory(Category category, boolean isNew) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void calculateModerator(String categoryPath, boolean isNew)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveModOfCategory(List<String> moderatorCate, String userId,
			boolean isAdd) {
		// TODO Auto-generated method stub

	}

	@Override
	public Category removeCategory(String categoryId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Forum> getForums(String categoryId, String strQuery)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
  public Forum getForum(String categoryId, String forumId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void modifyForum(Forum forum, int type) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveForum(String categoryId, Forum forum, boolean isNew)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveModerateOfForums(List<String> forumPaths, String userName,
			boolean isDelete) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public Forum removeForum(String categoryId, String forumId)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void moveForum(List<Forum> forums, String destCategoryPath)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public JCRPageList getPageTopic(String categoryId, String forumId,
			String strQuery, String strOrderBy) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JCRPageList getPageTopicByUser(String userName, boolean isMod,
			String strOrderBy) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JCRPageList getPageTopicOld(long date, String forumPatch)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Topic> getAllTopicsOld(long date, String forumPatch)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getTotalTopicOld(long date, String forumPatch) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Topic> getTopics(String categoryId, String forumId)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Topic getTopic(String categoryId, String forumId, String topicId,
			String userRead) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
  public void setViewCountTopic(String path, String userRead) {
		// TODO Auto-generated method stub

	}

	@Override
	public Topic getTopicByPath(String topicPath, boolean isLastPost)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Topic getTopicSummary(String topicPath) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Topic getTopicUpdate(Topic topic, boolean isSummary)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
  public void modifyTopic(List<Topic> topics, int type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveTopic(String categoryId, String forumId, Topic topic,
			boolean isNew, boolean isMove, MessageBuilder messageBuilder)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
  public Topic removeTopic(String categoryId, String forumId, String topicId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void moveTopic(List<Topic> topics, String destForumPath,
			String mailContent, String link) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void mergeTopic(String srcTopicPath, String destTopicPath,
			String mailContent, String link) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public JCRPageList getPosts(String categoryId, String forumId,
			String topicId, String isApproved, String isHidden,
			String strQuery, String userLogin) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JCRPageList getPostForSplitTopic(String topicPath) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getAvailablePost(String categoryId, String forumId,
			String topicId, String isApproved, String isHidden, String userLogin)
			throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getLastReadIndex(String path, String isApproved,
			String isHidden, String userLogin) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public JCRPageList getPagePostByUser(String userName, String userId,
			boolean isMod, String strOrderBy) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Post getPost(String categoryId, String forumId, String topicId,
			String postId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void savePost(String categoryId, String forumId, String topicId,
			Post post, boolean isNew, MessageBuilder messageBuilder)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
  public void modifyPost(List<Post> posts, int type) {
		// TODO Auto-generated method stub

	}

	@Override
  public Post removePost(String categoryId, String forumId, String topicId, String postId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void movePost(String[] postPaths, String destTopicPath,
			boolean isCreatNewTopic, String mailContent, String link)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getObjectNameByPath(String path) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getObjectNameById(String id, String type) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ForumLinkData> getAllLink(String strQueryCate,
			String strQueryForum) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getForumHomePath() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addTag(List<Tag> tags, String userName, String topicPath)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
  public void unTag(String tagId, String userName, String topicPath) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getAllTagName(String strQuery, String userAndTopicId)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getTagNameInTopic(String userAndTopicId)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Tag> getAllTags() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Tag> getMyTagInTopic(String[] tagIds) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JCRPageList getTopicByMyTag(String userIdAndtagId, String strOrderBy)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveTag(Tag newTag) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveUserProfile(UserProfile userProfile, boolean isOption,
			boolean isBan) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateUserProfile(User user) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveUserModerator(String userName, List<String> ids,
			boolean isModeCate) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public JCRPageList searchUserProfile(String userSearch) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserProfile getUserInfo(String userName) throws Exception {
		UserProfile uf = new UserProfile();
		uf.setUserId(userName);
		uf.setFullName(userName + " " + userName);
		return uf;
	}

	@Override
	public List<String> getUserModerator(String userName, boolean isModeCate)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveUserBookmark(String userName, String bookMark, boolean isNew)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveLastPostIdRead(String userId, String[] lastReadPostOfForum,
			String[] lastReadPostOfTopic) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveCollapsedCategories(String userName, String categoryId,
			boolean isAdd) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public JCRPageList getPageListUserProfile() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ForumSearch> getQuickSearch(String textQuery, String type,
			String pathQuery, String userId, List<String> listCateIds,
			List<String> listForumIds, List<String> forumIdsOfModerator)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
  public List<ForumSearch> getAdvancedSearch(ForumEventQuery eventQuery, List<String> listCateIds, List<String> listForumIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveForumStatistic(ForumStatistic forumStatistic)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public ForumStatistic getForumStatistic() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveForumAdministration(ForumAdministration forumAdministration)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public ForumAdministration getForumAdministration() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateStatisticCounts(long topicCoutn, long postCount)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void userLogin(String userId) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void userLogout(String userId) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isOnline(String userId) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<String> getOnlineUsers() throws Exception {
		return Arrays.asList("root", "john", "mary");
	}

	@Override
	public String getLastLogin() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JCRPageList getPrivateMessage(String userName, String type)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getNewPrivateMessage(String userName) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void savePrivateMessage(ForumPrivateMessage privateMessage)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveReadMessage(String messageId, String userName, String type)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void removePrivateMessage(String messageId, String userName,
			String type) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
  public ForumSubscription getForumSubscription(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveForumSubscription(ForumSubscription forumSubscription,
			String userId) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void addWatch(int watchType, String path, List<String> values,
			String currentUser) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeWatch(int watchType, String path, String values)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
  public List<ForumSearch> getJobWattingForModerator(String[] paths) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getJobWattingForModeratorByUser(String userId) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public SendMessageInfo getMessageInfo(String name) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<SendMessageInfo> getPendingMessages() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAdminRole(String userName) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Post> getNewPosts(int number) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeIterator search(String queryString) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
  public void evaluateActiveUsers(String query) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createUserProfile(User user) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
  public void updateTopicAccess(String userId, String topicId) {
		// TODO Auto-generated method stub

	}

	@Override
  public void updateForumAccess(String userId, String forumId) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object exportXML(String categoryId, String forumId,
			List<String> objectIds, String nodePath, ByteArrayOutputStream bos,
			boolean isExportAll) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void importXML(String nodePath, ByteArrayInputStream bis,
			int typeImport) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public List<UserProfile> getQuickProfiles(List<String> userList)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserProfile getQuickProfile(String userName) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserProfile getUserInformations(UserProfile userProfile)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserProfile getDefaultUserProfile(String userName, String ip)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserProfile updateUserProfileSetting(UserProfile userProfile)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getBookmarks(String userName) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserProfile getUserSettingProfile(String userName) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserProfile getUserProfileManagement(String userName)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveUserSettingProfile(UserProfile userProfile)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateForum(String path) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getBanList() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean addBanIP(String ip) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeBan(String ip) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getForumBanList(String forumId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean addBanIPForum(String ip, String forumId) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeBanIPForum(String ip, String forumId) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public JCRPageList getListPostsByIP(String ip, String strOrderBy)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerListenerForCategory(String categoryId) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void unRegisterListenerForCategory(String path) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public ForumAttachment getUserAvatar(String userName) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveUserAvatar(String userId, ForumAttachment fileAttachment)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDefaultAvatar(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Watch> getWatchByUser(String userId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateEmailWatch(List<String> listNodeId, String newEmailAdd,
			String userId) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public List<PruneSetting> getAllPruneSetting() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PruneSetting getPruneSetting(String forumPath) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void savePruneSetting(PruneSetting pruneSetting) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void runPrune(PruneSetting pSetting) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void runPrune(String forumPath) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public long checkPrune(PruneSetting pSetting) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
  public List<TopicType> getTopicTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TopicType getTopicType(String Id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveTopicType(TopicType topicType) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeTopicType(String topicTypeId) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public JCRPageList getPageTopicByType(String type) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Forum> getForumSummaries(String categoryId, String strQuery)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateUserProfileInfo(String name) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void addMember(User user, UserProfile profileTemplate)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeMember(User user) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateLoggedinUsers() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void calculateDeletedUser(String userName) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public InputStream createForumRss(String objectId, String link)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream createUserRss(String userId, String link)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addListenerPlugin(ForumEventListener listener) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Post> getRecentPostsForUser(String arg0, int arg1)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tag getTag(String tagId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getScreenName(String userName) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LazyPageList<Topic> getTopicList(String categoryId, String forumId,
			String string, String strOrderBy, int pageSize) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeCacheUserProfile(String userName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void calculateDeletedGroup(String groupId, String groupName)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
