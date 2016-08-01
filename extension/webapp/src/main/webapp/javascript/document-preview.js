(function ($) {
  documentPreview = {
    defaultSettings: {
      doc: {
        id: null,
        repository: null,
        workspace: null,
        path: null,
        title: null,
        downloadUrl: null,
        openUrl: null,
        isWebContent: false
      },
      showComments: false,
      labels: {
        close: "Close",
        download: "Download",
        openInDocuments: "Open in Documents",
        likeActivity: "Like",
        postCommentHint: "Add your comment...",
        noComment: "No comment yet"
      },
      user: {
        username: null,
        fullname: null,
        avatarUrl: null,
        profileUrl: null
      },
      author: {
        username: null,
        fullname: null,
        avatarUrl: null,
        profileUrl: null
      },
      activity: {
        id: null,
        postTime: "",
        status: "",
        liked: null,
        likes: null
      },
      comments: null
    },
    settings: {},

    init: function (docPreviewSettings) {
      this.settings = $.extend(this.defaultSettings, docPreviewSettings);

      var promises = [];

      // if we miss author information, let's fetch them
      if(this.settings.author.username != null
        && (this.settings.author.fullname == null  || this.settings.author.avatarUrl == null || this.settings.author.profileUrl == null)) {
        promises.push(this.fetchActivityAuthorInformation());
      }
      // if we miss current user information, let's fetch them
      if(this.settings.user.fullname == null  || this.settings.user.avatarUrl == null || this.settings.user.profileUrl == null) {
        promises.push(this.fetchUserInformation());
      }
      // if we don't have the number of likes, let's fetch it
      if(this.settings.activity.id != null && this.settings.activity.likes == null) {
        promises.push(this.fetchLikes());
      }

      var self = this;
      // wait for all users info fetches to be complete before rendering the component
      Promise.all(promises).then(function() {
        self.createSkeleton();
        self.render();
        self.show();
        self.loadComments();
      }, function(err) {
        // error occurred
      });
    },

    fetchUserInformation: function(callback) {
      var self = this;
      return $.ajax({
        url: "/rest/v1/social/users/" + eXo.env.portal.userName
      }).done(function (data) {
        if (data.fullname != null) {
          self.settings.user.fullname = data.fullname;
        }
        if (data.avatar != null) {
          self.settings.user.avatarUrl = data.avatar;
        } else {
          self.settings.user.avatarUrl = "/eXoSkin/skin/images/system/SpaceAvtDefault.png";
        }
        self.settings.user.profileUrl = "/" + eXo.env.portal.containerName + "/" + eXo.env.portal.portalName + "/" + eXo.env.portal.userName;
      }).always(function () {
        if(typeof callback === 'function') {
          callback();
        }
      });
    },

    fetchActivityAuthorInformation: function(callback) {
      var self = this;
      return $.ajax({
        url: "/rest/v1/social/users/" + self.settings.author.username
      }).done(function (data) {
        if (data.fullname != null) {
          self.settings.author.fullname = data.fullname;
        }
        if (data.avatar != null) {
          self.settings.author.avatarUrl = data.avatar;
        } else {
          self.settings.author.avatarUrl = "/eXoSkin/skin/images/system/SpaceAvtDefault.png";
        }
        self.settings.author.profileUrl = "/" + eXo.env.portal.containerName + "/" + eXo.env.portal.portalName + "/" + self.settings.author.username;
      }).always(function () {
        if(typeof callback === 'function') {
          callback();
        }
      });
    },

    fetchLikes: function() {
      var self = this;
      return $.ajax({
        url: '/rest/v1/social/activities/' + this.settings.activity.id + '/likes'
      }).done(function (data) {
        if (data.likes != null) {
          self.settings.activity.likes = data.likes.length;
        }
      }).fail(function () {
        self.settings.activity.likes = 0;
      });
    },

    like: function(like) {
      var self = this;
      if(like) {
        return $.post('/rest/v1/social/activities/' + this.settings.activity.id + '/likes', {liker: eXo.env.portal.userName})
          .done(function (data) {
            self.settings.activity.liked = true;
            self.settings.activity.likes++;
            $('#documentPreviewContainer .nbOfLikes').html(self.settings.activity.likes);
            self.refreshLikeLink();
          }).fail(function () {
            // error occurred
          });
      } else {
        return $.ajax({
            type: 'DELETE',
            url: '/rest/v1/social/activities/' + this.settings.activity.id + '/likes/' + eXo.env.portal.userName
          }).done(function (data) {
            self.settings.activity.liked = false;
            self.settings.activity.likes--;
            $('#documentPreviewContainer .nbOfLikes').html(self.settings.activity.likes);
            self.refreshLikeLink();
          }).fail(function () {
            // error occurred
          });
      }
    },

    refreshLikeLink: function() {
      var likeIcon = $('#documentPreviewContainer #previewLikeLink .uiIconThumbUp');
      if(this.settings.activity.liked == true) {
        likeIcon.addClass('uiIconBlue');
        likeIcon.removeClass('uiIconLightGray');
      } else {
        likeIcon.removeClass('uiIconBlue');
        likeIcon.addClass('uiIconLightGray');
      }
    },

    createSkeleton: function () {
      var docPreviewContainer = $("#documentPreviewContainer");

      if(docPreviewContainer.length == 0) {
        docPreviewContainer = $("<div />", {
          id: "documentPreviewContainer",
          class: "maskLayer"
        }).appendTo('body');
      }
      docPreviewContainer.hide();

      docPreviewContainer.html(' \
        <div class="uiDocumentPreview" id="uiDocumentPreview"> \
          <div class="exitWindow"> \
            <a class="uiIconClose uiIconWhite" title="' + this.settings.labels.close + '" onclick="documentPreview.hide()"></a> \
          </div> \
          <div class="uiDocumentPreviewMainWindow clearfix"> \
            <!-- doc comments --> \
            <div class="uiBox commentArea pull-right" id="$uicomponent.id"> \
              <div class="title">\
                <i class="uiIcon16x16FileDefault uiIcon16x16nt_file uiIcon16x16imagepng uiIconLightGray"></i>&nbsp;' + this.settings.doc.title + ' \
              </div> \
              <div class="uiContentBox"> \
                <div class="highlightBox"> \
                  <div class="profile clearfix"> \
                    <a title="' + this.settings.author.fullname + '" href="' + this.settings.author.profileUrl + '" class="avatarMedium pull-left"><img alt="' + this.settings.author.fullname + '" src="' + this.settings.author.avatarUrl + '"></a> \
                    <div class="rightBlock"> \
                      <a href="' + this.settings.author.profileUrl + '">' + this.settings.author.fullname + '</a> \
                      <p class="dateTime">' + this.settings.activity.postTime + '</p> \
                      <p class="descript" title="activityStatus">' + (this.settings.activity.status != null ? this.settings.activity.status : '') + '</p> \
                    </div> \
                  </div> \
                </div> \
                <div class="actionBar clearfix "> \
                  <ul class="pull-right"> \
                    <li> \
                      <a href="#" id="previewCommentLink"> \
                        <i class="uiIconComment uiIconLightGray"></i>&nbsp;<span class="nbOfComments"></span> \
                      </a> \
                    </li> \
                    <li> \
                      <a href="javascript:void(0);" id="previewLikeLink" onclick="documentPreview.like(!documentPreview.settings.activity.liked)" rel="tooltip" data-placement="bottom" title="' + this.settings.labels.likeActivity + '"> \
                        <i class="uiIconThumbUp uiIconLightGray"></i>&nbsp;<span class="nbOfLikes"></span> \
                      </a> \
                    </li> \
                  </ul> \
                </div> \
                <div class="comments"> \
                  <ul class="commentList"> \
                  </ul> \
                </div> \
                <div class="commentInputBox"> \
                  <a class="avatarXSmall pull-left" href="' + this.settings.user.profileUrl + '" title="' + this.settings.user.fullname + '"> \
                    <img src="' + this.settings.user.avatarUrl + '" alt="' + this.settings.user.fullname + '" /></a> \
                    <div class="commentBox"> \
                      <textarea id="commentInput" placeholder="' + this.settings.labels.postCommentHint + '" cols="30" rows="10" id="commentTextAreaPreview" activityId="activityId" class="textarea"></textarea> \
                    </div> \
                  </div> \
              </div> \
            </div> \
            <div class="resizeButton " id="ShowHideAll"> \
              <i style="display: block;" class="uiIconMiniArrowRight uiIconWhite"></i> \
            </div> \
            <div id="documentPreviewContent"' + (this.settings.doc.isWebContent == true ? ' class="uiPreviewWebContent"' : '') + '> \
            </div> \
            <!-- put vote area here --> \
            <div class="previewBtn"> \
              <div class="openBtn"> \
                <a href="' + this.settings.doc.openUrl + '"><i class="uiIconGotoFolder uiIconWhite"></i>&nbsp;' + this.settings.labels.openInDocuments + '</a> \
              </div> \
              <div class="downloadBtn"> \
                <a href="' + this.settings.doc.downloadUrl + '"><i class="uiIconDownload uiIconWhite"></i>&nbsp;' + this.settings.labels.download + '</a> \
              </div> \
            </div> \
          </div> \
        </div>');
    },

    loadComments: function() {
      var self = this;
      if(this.settings.activity.id != null) {
        // load comments activity
        $.ajax({
          url: '/rest/v1/social/activities/' + this.settings.activity.id + '/comments?expand=identity',
          cache: false
        }).done(function(data) {
          self.renderComments(data.comments);
        });
      } else {
        // load document comments
        $.ajax({
          url: '/rest/contents/comment/all?jcrPath=/' + this.settings.doc.repository + '/' + this.settings.doc.workspace + this.settings.doc.path,
          dataType: 'xml',
          cache: false
        }).done(function(data) {
          var promises = [];
          var comments = [];
          var commentorsUsernames = [];
          var commentors = [];
          $(data).find("comment").each(function() {
            var commentor = $(this).find("commentor").text();
            var content = $(this).find("content").text();
            var date = $(this).find("date").text();
            // insert the comment as the first element since we want to display comments from the oldest to the
            // newest whereas the web service returns in the opposite order
            comments.unshift({
              poster: commentor,
              body: content,
              updateDate: date,
              identity: {
                profile: null
              }
            });
            // store commentors in an associative array to ensure uniqueness
            commentorsUsernames[commentor] = commentor;
          });

          // fetch all commentors profiles
          for(var key in commentorsUsernames) {
            if (commentorsUsernames.hasOwnProperty(key)) {
              promises.push($.ajax({
                url: '/rest/v1/social/users/' + key
              }).done(function (data) {
                commentors[data.username] = data;
              }));
            }
          }

          // launch commentors profiles fetches using promise to allow to launch them in parallel
          // and to wait for the end of all requests to continue
          Promise.all(promises).then(function() {
            // complete comments objects with commentors profiles
            $.each(comments, function(index, comment) {
              comment.identity.profile = commentors[comment.poster];
            });
            self.renderComments(comments);
          }, function(err) {
            // error occurred
          });
        });
      }
    },

    renderComments: function(comments) {
      var commentsContainer = $('#documentPreviewContainer .comments');
      var commentsHtml = '';
      if(comments != null && comments.length > 0) {
        $('#documentPreviewContainer .nbOfComments').html(comments.length);
        commentsHtml = '<ul class="commentList">';
        $.each(comments, function (index, comment) {
          var commenterProfileUrl = "/" + eXo.env.portal.containerName + "/" + eXo.env.portal.portalName + "/" + comment.identity.profile.username;
          var commenterAvatar = comment.identity.profile.avatar;
          if (commenterAvatar == null) {
            commenterAvatar = '/eXoSkin/skin/images/system/UserAvtDefault.png';
          }
          commentsHtml += '<li class="clearfix"> \
            <a class="avatarXSmall pull-left" href="' + commenterProfileUrl + '" title="' + comment.identity.profile.fullname + '"><img src="' + commenterAvatar + '" alt="" /></a> \
            <div class="rightBlock"> \
              <div class="tit"> \
                <a href="' + commenterProfileUrl + '" >' + comment.identity.profile.fullname + '</a> \
                <span class="pull-right dateTime">' + comment.updateDate + '</span> \
              </div> \
              <p class="cont">' + comment.body + '</p> \
              <a href="javascript:void(0)" id="$idDeleteComment" data-confirm="$labelToDeleteThisComment" data-delete="<%=uicomponent.event(uicomponent.REMOVE_COMMENT, it.id); %>"  class="close previewControllDelete"><i class="uiIconLightGray uiIconClose " commentId="$it.id"></i></a> \
            </div> \
          </li>';
        })
        commentsHtml += '</ul>';
      } else {
        $('#documentPreviewContainer .nbOfComments').html('0');
        commentsHtml = '<div class="noComment"> \
            <div class="info">' + this.settings.labels.noComment + '</div> \
          </div>';
      }
      commentsContainer.html(commentsHtml);
    },

    postComment: function () {
      var self = this;
      var commentInput = $('#documentPreviewContainer #commentInput');
      if(commentInput != null && $.trim(commentInput.val())) {
        var commentContent = commentInput.val();
        commentInput.val('');
        if(this.settings.activity.id != null) {
          // post comment on the activity
          return $.ajax({
            type: 'POST',
            url: '/rest/v1/social/activities/' + this.settings.activity.id + '/comments',
            data: '{ "poster": ' + eXo.env.portal.userName + ',"title": "' + commentContent + '"}',
            contentType: 'application/json'
          }).done(function (data) {
            self.loadComments();
          }).fail(function () {
            // error occurred
          });
        } else {
          // post comment on the document
          return $.ajax({
            type: 'POST',
            url: '/rest/contents/comment/add',
            data: 'jcrPath=/' + this.settings.doc.repository + '/' + this.settings.doc.workspace + this.settings.doc.path + '&comment=' + commentContent,
            contentType: 'application/x-www-form-urlencoded'
          }).done(function (data) {
            self.loadComments();
          }).fail(function () {
            // error occurred
          });
        }
      }
    },

    render: function () {
      var self = this;

      $(window).on('resize', resizeEventHandler);
      $(document).on('keyup', closeEventHandler);

      // Bind close event. Return body scroll, turn off keyup
      $(".exitWindow > .uiIconClose", $('#uiDocumentPreview')).click(function() {
        $('body').removeClass('modal-open');
        setTimeout(function() {
          $('body').css('overflow', 'visible');
          $(document).off('keyup', closeEventHandler);
          $(window).off('resize', resizeEventHandler);
        }, 500);
      });

      // Bind expanded/collapsed event
      var uiDocumentPreview = $('#documentPreviewContainer');
      $('.resizeButton .uiIconMiniArrowLeft, .resizeButton .uiIconMiniArrowRight', uiDocumentPreview).click(function() {
        var uiIconMiniArrow = $(this);
        var commentArea = $('.commentArea', uiDocumentPreview);
        var uiPreviewWebContent = $('.uiPreviewWebContent', uiDocumentPreview);
        var fileContent = $('.fileContent', uiDocumentPreview);
        var resizeButton = $('.resizeButton', uiDocumentPreview);
        var previewButtons = $('.previewBtn', uiDocumentPreview);
        var EmbedHtml = $('.EmbedHtml', uiDocumentPreview);
        if (uiIconMiniArrow.hasClass('uiIconMiniArrowRight')) {
          commentArea.css('display', 'none');
          uiPreviewWebContent.css('margin-right', '30px');
          EmbedHtml.css('margin-right', '30px');
          fileContent.css('margin-right', '30px');
          resizeButton.css('right', '5px');
          previewButtons.css('margin-right', '30px');
        } else {
          commentArea.css('display', 'block');
          uiPreviewWebContent.css('margin-right', '335px');
          EmbedHtml.css('margin-right', '335px');
          fileContent.css('margin-right', '335px');
          resizeButton.css('right', '310px');
          previewButtons.css('margin-right', '335px');
        }
        uiIconMiniArrow.toggleClass('uiIconMiniArrowLeft');
        uiIconMiniArrow.toggleClass('uiIconMiniArrowRight');
        resizeEventHandler();
      });

      if(this.settings.activity.id != null) {
        // render like link and nb of likes
        this.refreshLikeLink();
        $('#documentPreviewContainer .nbOfLikes').html(this.settings.activity.likes);
      } else {
        // hide like link since there is no linked activity
        $('#documentPreviewContainer #previewLikeLink').hide();
      }

      // comments events binding
      $('#documentPreviewContainer #previewCommentLink').on('click', function() {
        $('#documentPreviewContainer #commentInput').focus();
      });

      $('#commentInput').on('keypress', function(event) {
          if (event.which == 13) {
            event.preventDefault();
            self.postComment();
          }
      });

      var docContentContainer = $('#documentPreviewContent');

      var self = this;
      docContentContainer.load('/rest/private/contentviewer/' + this.settings.doc.repository + '/' + this.settings.doc.workspace + '/' + this.settings.doc.id, function() {
        resizeEventHandler();
        self.show();
      });
    },

    show: function () {
      $('#documentPreviewContainer').show();
    },

    hide: function () {
      $('#documentPreviewContainer').hide();
    }

  };

  // Bind Esc key
  var closeEventHandler = function(e) {
    $('#presentationMode').blur();
    if (e.keyCode == 27 && ("presentationMode" != e.target.id || $.browser.mozilla)) {
      $(".exitWindow > .uiIconClose", $('#uiDocumentPreview')).trigger("click");
    }
  }

  // Resize Event
  var resizeEventHandler = function() {
    // Calculate margin
    var pdfDisplayAreaHeight = window.innerHeight - 92;
    var $uiDocumentPreview = $('#uiDocumentPreview');
    $('#outerContainer', $uiDocumentPreview).height(pdfDisplayAreaHeight); // pdf viewer
    var $commentArea = $('.commentArea', $uiDocumentPreview);
    var $commentAreaTitle = $('.title', $commentArea);
    var $commentInputBox = $('.commentInputBox', $commentArea);
    var $commentList = $('.commentList', $commentArea);
    var $highlightBox = $('.highlightBox', $commentArea);
    var $actionBarCommentArea = $('.actionBar', $commentArea);
    var commentAreaHeight = window.innerHeight - 30;
    $commentArea.height(commentAreaHeight);
    $commentList.css('max-height', commentAreaHeight - $commentAreaTitle.innerHeight() - $commentInputBox.innerHeight() - $highlightBox.innerHeight() - $actionBarCommentArea.innerHeight() - 16); //16 is padding of commentList
    $commentList.scrollTop(20000);

    // Media viewer, no preview file
    var $navigationContainer = $(".navigationContainer", $uiDocumentPreview);
    var $uiContentBox = $('.uiContentBox', $navigationContainer);
    var $video = $('.videoContent', $uiContentBox);
    var $flowplayerContentDetail = $('.ContentDetail', $uiContentBox);
    var $flowplayerPlayerContent = $('.PlayerContent', $flowplayerContentDetail);
    var $flowplayer = $('object', $flowplayerPlayerContent);
    var $flashViewer = $('.FlashViewer', $uiContentBox);
    var $embedFlashViewer = $('embed', $flashViewer);
    var $windowmediaplayer = $('#MediaPlayer1', $uiContentBox);
    var $embedWMP = $('embed', $windowmediaplayer);

    $navigationContainer.height(pdfDisplayAreaHeight);
    $uiContentBox.height(pdfDisplayAreaHeight);
    $flowplayerContentDetail.height(pdfDisplayAreaHeight);
    $flowplayerPlayerContent.height(pdfDisplayAreaHeight - 5);
    $flashViewer.height(pdfDisplayAreaHeight - 5);

    $flowplayer.css('max-width', $uiContentBox.width() - 2);
    $flowplayer.css('max-height', $uiContentBox.height() - 3);
    $flowplayer.css('width', '100%');
    $flowplayer.css('height', '100%');

    $video.css('max-width', $uiContentBox.width() - 2);
    $video.css('max-height', $uiContentBox.height() - 3);
    $video.css('width', '100%');
    $video.css('height', '100%');

    $windowmediaplayer.css('max-width', $uiContentBox.width() - 2);
    $windowmediaplayer.css('max-height', $uiContentBox.height() - 7);
    $windowmediaplayer.css('width', '100%');
    $windowmediaplayer.css('height', '100%');
    $embedWMP.css('width', '100%');
    $embedWMP.css('height', '100%')

    $embedFlashViewer.css('max-width', $uiContentBox.width() - 2);
    $embedFlashViewer.css('max-height', $uiContentBox.height() - 3);
    $embedFlashViewer.css('width', '100%');
    $embedFlashViewer.css('height', '100%');

    var $img = $('a > img', $uiContentBox);

    if ($img.length > 0) {
      $img.css('max-width', $uiContentBox.width() + 1);
      $img.css('max-height', $uiContentBox.height() + 1);
      $img.css('width', 'auto');
      $img.css('height', 'auto');
      $navigationContainer.css('overflow', 'hidden');
    }

    $('.uiPreviewWebContent', $uiDocumentPreview).height(pdfDisplayAreaHeight - 30) // webcontent
    var $EmbedHtml =  $('.EmbedHtml', $uiDocumentPreview);
    $EmbedHtml.height(pdfDisplayAreaHeight) // External embedded

    // Resize image flick
    $img = $('.uiDocumentPreviewMainWindow > .EmbedHtml > a > img');
    $img.css('max-width', $EmbedHtml.width());
    $img.css('max-height', $EmbedHtml.height());
  }

  return documentPreview;
})($);