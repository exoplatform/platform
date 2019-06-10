(function ($, UIActivity, XSSUtils) {
  MAX_LENGTH = 2000,
  documentPreview = {
    defaultSettings: {
      doc: {
        id: null,
        repository: null,
        workspace: null,
        path: null,
        title: null,
        fileType: null,
        downloadUrl: null,
        remoteEditURL: null,
        remoteEditTitle: null,
        remoteEditClass: null,
        openUrl: null,
        size: null,
        cssIcon : null,
        isWebContent: false
      },
      showComments: false,
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
        likes: null,
        comments: null,
        previous: null,
        next: null
      },
      comments: null
    },
    settings: {},

    init: function (docPreviewSettings) {
      if($('.commentsLoaded').length) {
        $("#documentPreviewContent").html('<div class="loading">' +
            '<i class="uiLoadingIconMedium uiIconLightGray"></i>' +
          '</div>');
      }

      this.settings = $.extend(this.defaultSettings, docPreviewSettings);

      var promises = [];

      // if we miss author information, let's fetch them
      if(this.settings.author.username != null
        && (this.settings.author.fullname == null  || this.settings.author.avatarUrl == null || this.settings.author.profileUrl == null)) {
        promises.push(this.fetchAuthorInformation());
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

      if(ES6Promise && !window.Promise ) {
        ES6Promise.polyfill();
      }

      // wait for all users info fetches to be complete before rendering the component
      Promise.all(promises).then(function() {
        self.render();
        self.show();
        if(!$('.commentsLoaded').length) {
          self.loadComments();
        }
      }, function(err) {
        // error occurred
      });
    },

    fetchUserInformation: function(callback) {
      var self = this;
      return $.ajax({
        url: "/rest/v1/social/users/" + eXo.env.portal.userName,
        cache: false
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
      }).fail(function () {
        console.log("Can not fetch user information!");
      }).always(function () {
        if(typeof callback === 'function') {
          callback();
        }
      });
    },

    fetchAuthorInformation: function(callback) {
      var self = this;
      return $.ajax({
        url: "/rest/v1/social/users/" + self.settings.author.username,
        cache: false
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
      }).fail(function () {
        console.log("Can not fetch author information!");
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
        self.clearErrorMessage();
      }).fail(function () {
        self.settings.activity.likes = 0;
        self.showErrorMessage("${UIActivity.comment.canNotLoadLikes}");
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
            self.refreshLikeLink('clickEvent');
            self.clearErrorMessage();
          }).fail(function () {
            self.showErrorMessage("${UIActivity.comment.canNotLike}");
            console.log("Can not like document!");
          });
      } else {
        return $.ajax({
            type: 'DELETE',
            url: '/rest/v1/social/activities/' + this.settings.activity.id + '/likes/' + eXo.env.portal.userName
          }).done(function (data) {
            self.settings.activity.liked = false;
            self.settings.activity.likes--;
            $('#documentPreviewContainer .nbOfLikes').html(self.settings.activity.likes);
            self.refreshLikeLink('clickEvent');
            self.clearErrorMessage();
          }).fail(function () {
            self.showErrorMessage("${UIActivity.comment.canNotUnLike}");
            console.log("Can not delete like of document!");
          });
      }
    },

    refreshLikeLink: function(event) {
      var likeLink = $('#documentPreviewContainer #previewLikeLink');
      var likeIcon = likeLink.find('.uiIconThumbUp');
      if(this.settings.activity.liked == true) {
        likeLink.attr("data-original-title", "${UIActivity.label.Unlike}");
        likeIcon.addClass('uiIconBlue');
        likeIcon.removeClass('uiIconLightGray');
      } else {
        likeLink.attr("data-original-title", "${UIActivity.label.Like}");
        likeIcon.removeClass('uiIconBlue');
        likeIcon.addClass('uiIconLightGray');
      }
      if (event) {
        likeLink.tooltip('show');
      }
    },

    toggleLikeComment: function(commentId) {
      var self = this;

      var comment = this.findComment(commentId);

      if(!comment.liked) {
        return $.post('/rest/v1/social/comments/' + commentId + '/likes', {liker: eXo.env.portal.userName})
          .done(function (data) {
            comment.liked = true;
            comment.nbOfLikes++;

            // update tooltip on likes count
            var likeCommentCount = $('#documentPreviewContainer #likeCommentCount_' + commentId);
            if(likeCommentCount.attr('title')) {
              likeCommentCount.attr('title', self.settings.user.fullname + '\n' + likeCommentCount.attr('title'));
            } else {
              likeCommentCount.attr('title', self.settings.user.fullname);
            }
            // update nb of likes
            likeCommentCount.css('display', 'inline');
            likeCommentCount.html('(' + comment.nbOfLikes + ')');

            likeCommentCount.addClass('commentLiked');

            var likeCommentLink = $('#documentPreviewContainer #likeCommentLink_' + commentId);
            likeCommentLink.find('i').addClass('commentLiked');
            likeCommentLink.attr('title', "${UIActivity.msg.UnlikeComment}");

            self.clearErrorMessage();
          }).fail(function () {
            self.showErrorMessage("${UIActivity.comment.canNotLike}");
          });
      } else {
        return $.ajax({
          type: 'DELETE',
          url: '/rest/v1/social/comments/' + commentId + '/likes/' + eXo.env.portal.userName
        }).done(function (data) {
          comment.liked = false;
          comment.nbOfLikes--;

          var likeCommentCount = $('#documentPreviewContainer #likeCommentCount_' + commentId);
          if(comment.nbOfLikes > 0) {
            // refresh tooltip on likes count
            comment.likes.reverse();
            likeCommentCount.attr('title', comment.likes.filter(function(like) {
              return like.username != eXo.env.portal.userName;
            }).map(function(like) {
              return like.fullname;
            }).join('\n'));
            // update nb of likes
            likeCommentCount.html('(' + comment.nbOfLikes + ')');
          } else {
            likeCommentCount.css('display', 'none');
            likeCommentCount.empty();
            likeCommentCount.attr('title', '');
          }

          likeCommentCount.removeClass('commentLiked');

          var likeCommentLink = $('#documentPreviewContainer #likeCommentLink_' + commentId);
          likeCommentLink.find('i').removeClass('commentLiked');
          likeCommentLink.attr('title', "${UIActivity.msg.LikeComment}");

          self.clearErrorMessage();
        }).fail(function () {
          self.showErrorMessage("${UIActivity.comment.canNotUnLike}");
        });
      }
    },

    findComment: function(commentId) {
      for(i = 0; i < this.settings.activity.comments.length; i++) {
        if(this.settings.activity.comments[i].id == commentId) {
          return this.settings.activity.comments[i];
        }
      }
    },

    showLikersPopup: function(commentId) {
      UIActivity.showLikersPopup(commentId);
    },

    createSkeleton: function () {
      var docPreviewContainer = $("#documentPreviewContainer");

      if(docPreviewContainer.length == 0) {
        docPreviewContainer = $("<div />", {
          "id": "documentPreviewContainer",
          "class": "maskLayer"
        }).appendTo('body');
      }
      docPreviewContainer.hide();

      var cssClasses = '';
      if (this.settings.doc.fileType) {
        cssClasses = $.map(this.settings.doc.fileType.split(/\s+/g), function(type){return "uiIcon16x16" + type}).join(" ");            
      }

      if($('.commentsLoaded').length) {
        docPreviewContainer.find(".previewBtn").html(
          '<div class="showComments">' +
            '<a><i class="uiIconComment uiIconWhite"></i>&nbsp;${UIActivity.comment.showComment}</a>' +
          '</div>' +
          '<div class="openBtn">' +
            '<a href="' + this.settings.doc.openUrl + '"><i class="uiIconGotoFolder uiIconWhite"></i>&nbsp;${UIActivity.comment.openInDocuments}</a>' +
          '</div>' +
          '<div class="downloadBtn">' +
            '<a href="' + this.settings.doc.downloadUrl + '"><i class="uiIconDownload uiIconWhite"></i>&nbsp;${UIActivity.comment.download}</a>' +
          '</div>');
      } else {
        var authorFullName = XSSUtils.sanitizeString(this.settings.author.fullname != null ? this.settings.author.fullname : '');
        docPreviewContainer.html(
          '<div class="uiDocumentPreview" id="uiDocumentPreview">' +
            '<div class="exitWindow">' +
              '<a class="uiIconClose uiIconWhite" title="${UIActivity.comment.close}" onclick="documentPreview.hide()"></a>' +
            '</div>' +
            '<div class="uiDocumentPreviewMainWindow clearfix">' +
              '<!-- doc comments -->' +
              '<div class="uiBox commentArea pull-right" id="commentArea">' +
                '<div class="title">' +
                  '<i class="' + cssClasses + '"></i>&nbsp;' + this.settings.doc.title +
                '</div>' +
                '<div class="uiContentBox">' +
                  '<div class="highlightBox">' +
                    '<div class="profile clearfix">' +
                      '<a title="' + authorFullName + '" href="' + this.settings.author.profileUrl + '" class="avatarMedium pull-left"><img alt="' + authorFullName + '" src="' + this.settings.author.avatarUrl + '"></a>' +
                      '<div class="rightBlock">' +
                        '<a href="' + this.settings.author.profileUrl + '">' + authorFullName + '</a>' +
                        '<p class="dateTime">' + this.settings.activity.postTime + '</p>' +
                        '<p class="descript" title="activityStatus">' + (this.settings.activity.status != null ? this.settings.activity.status : '') + '</p>' +
                      '</div>' +
                    '</div>' +
                  '</div>' +
                  '<div class="actionBar clearfix ">' +
                    '<ul class="pull-right">' +
                      '<li>' +
                        '<a href="#" id="previewCommentLink">' +
                          '<i class="uiIconComment uiIconLightGray"></i>&nbsp;<span class="nbOfComments"></span>' +
                        '</a>' +
                      '</li>' +
                      '<li>' +
                        '<a href="javascript:void(0);" id="previewLikeLink" onclick="documentPreview.like(!documentPreview.settings.activity.liked)" rel="tooltip" data-placement="bottom" title="${UIActivity.label.Like}">' +
                          '<i class="uiIconThumbUp uiIconLightGray"></i>&nbsp;<span class="nbOfLikes"></span>' +
                        '</a>' +
                      '</li>' +
                    '</ul>' +
                  '</div>' +
                  '<div class="alert alert-error" id="uiPreviewErrorMessage">' +
                    '<i class="uiIconError" id="uiPreviewErrorMessageIcon"></i>' +
                    '<span id="uiPreviewErrorMessageContent">Your email address is incorrect. Please try again!</span>' +
                  '</div>' +
                  '<div class="comments">' +
                    '<ul class="commentList">' +
                    '</ul>' +
                  '</div>' +
                  '<div class="commentInputBox">' +
                    '<a class="avatarXSmall pull-left" href="' + this.settings.user.profileUrl + '" title="' + XSSUtils.sanitizeString(this.settings.user.fullname) + '">' +
                      '<img src="' + this.settings.user.avatarUrl + '" alt="' + XSSUtils.sanitizeString(this.settings.user.fullname) + '" /></a>' +
                      '<div class="commentBox">' +
                        '<div class="commentTextInput">' +
                          '<textarea id="commentInput" placeholder="${UIActivity.comment.placeholder}" cols="30" rows="10" id="commentTextAreaPreview" activityId="activityId" class="textarea"></textarea>' +
                        '</div>' +
                        '<button class="btn pull-left btn-primary" rel="tooltip" data-placement="bottom" title="comment" id="CommentButton" disabled>${UIActivity.label.Comment}</button>' +
                        '<button class="btn pull-left" rel="tooltip" data-placement="bottom" title="cancel" id="CancelButton">${UIActivity.label.Cancel}</button>' +
                      '</div>' +
                    '</div>' +
                    '<div class="parentCommentBlock hidden" onclick="documentPreview.showCommentBlockForActivity()">' +
                      '<a href="javascript:void(0)" class="ParentCommentLink">${UIActivity.label.Comment}</a>' +
                    '</div>' +
                '</div>' +
              '</div>' +
              '<div class="resizeButton " id="ShowHideAll">' +
                '<i class="uiIconMiniArrowLeft uiIconWhite"></i>' +
                '<i class="uiIconMiniArrowRight uiIconWhite"></i>' +
              '</div>' +
              '<div id="documentPreviewContent" ' + (this.settings.doc.isWebContent == true ? ' class="uiPreviewWebContent"' : '') + '>' +
                  '<div class="loading">' +
                    '<i class="uiLoadingIconMedium uiIconLightGray"></i>' +
                  '</div>' +
              '</div>' +
                '<div id = "previewPopup" class="UIPopupWindow uiPopup UIDragObject NormalStyle" style="width: 560px; position: absolute; top: 30%; left: 30%; margin: 0 auto 20px; z-index: 1; max-width: 100%;display:none">' +
                '<div class="popupHeader ClearFix">' +
                    '<a id="previewPopupCloseIcon" class="uiIconClose pull-right" aria-hidden="true" ></a>' +
                    '<span class="PopupTitle popupTitle">Popup header</span>' +
                '</div>' +
                '<div class="PopupContent popupContent">' +
                    '<div class="form-horizontal resizable">' +
                        '<div class="popupContent">' +
                        '<span class="confirmationIcon contentMessage">Are you sure you want to delete this comment?</span>' +
                     '</div>' +
                    '</div>' +
                    '<div class="uiAction uiActionBorder">' +
                        '<button id="previewPopupDeleteButton" class="btn" onclick="" type="button">Delete</button>' +
                        '<button id="previewPopupCloseButton" class="btn" onclick="" type="button">Cancel</button>' +
                    '</div>' +
                '</div>' +
                '<span class="uiIconResize pull-right uiIconLightGray"></span>' +
              '</div>' +

              '<!-- put vote area here -->' +
              '<div class="previewBtn">' +
                '<div class="showComments">' +
                  '<a><i class="uiIconComment uiIconWhite"></i>&nbsp;${UIActivity.comment.showComment}</a>' +
                '</div>' +
                '<div class="openBtn">' +
                  '<a href="' + this.settings.doc.openUrl + '"><i class="uiIconGotoFolder uiIconWhite"></i>&nbsp;${UIActivity.comment.openInDocuments}</a>' +
                '</div>' +
                '<div class="downloadBtn">' +
                  '<a href="' + this.settings.doc.downloadUrl + '"><i class="uiIconDownload uiIconWhite"></i>&nbsp;${UIActivity.comment.download}</a>' +
                '</div>' +
              '</div>' +
            '</div>' +
          '</div>');
      }

      if(this.settings.doc.remoteEditURL) {
        $(".previewBtn").append(
            '<div class="remoteEditBtn hidden-tabletL">' +
               '<a  href="' + this.settings.doc.remoteEditURL + '"><i  style="display:none;" class="uiIcon16x16FileDefault uiIconEcmsOpenDocument_' + this.settings.activity.id + ' uiIconWhite ' + this.settings.doc.remoteEditClass + '"></i>&nbsp;' + this.settings.doc.remoteEditTitle + '</a>' +
            '</div>'
        );
      }
      
      $('#documentPreviewContainer #previewLikeLink').tooltip();
      
      $("#uiPreviewErrorMessage").hide();
      $("#uiPreviewErrorMessageIcon").click(function() {
          $("#uiPreviewErrorMessage").hide();
      });
      $("#previewPopupCloseIcon, #previewPopupCloseButton").click(function() {
        $("#previewPopup").hide();
      });
    },
    showErrorMessage: function(message) {
      $("#uiPreviewErrorMessageContent").html(message);  
      $("#uiPreviewErrorMessage").show();
    },
    clearErrorMessage: function() {
      $("#uiPreviewErrorMessageContent").html("");
      $("#uiPreviewErrorMessage").hide();
    },
    loadComments: function(commentActivityParentId, scrollToCommentId) {
      var self = this;
      if(this.settings.activity.id != null) {
        // load comments activity
        $.ajax({
          url: '/rest/v1/social/activities/' + this.settings.activity.id + '/comments?expand=identity,likes,subComments',
          cache: false
        }).done(function(data) {
          self.clearErrorMessage();
          self.settings.activity.comments = data.comments;
          for(i = 0; i < self.settings.activity.comments.length; i++) {
            self.settings.activity.comments[i].liked = self.settings.activity.comments[i].likes.some(function(like) {
              return like.username == eXo.env.portal.userName;
            });
            self.settings.activity.comments[i].nbOfLikes = self.settings.activity.comments[i].likes.length;
          }
          self.renderComments(self.settings.activity.comments, commentActivityParentId, scrollToCommentId);
          resizeEventHandler();
          self.clearErrorMessage();
        }).fail(function () {
            self.showErrorMessage("${UIActivity.comment.canNotLoadComments}");
            console.log("Can not load comments!");
        });
      } else {
        // load document comments
        $.ajax({
          url: '/rest/contents/comment/all',
          data: {jcrPath: '/' + this.settings.doc.repository + '/' + this.settings.doc.workspace + this.settings.doc.path},
          dataType: 'xml',
          cache: false
        }).done(function(data) {
          var promises = [];
          var comments = [];
          var commentorsUsernames = [];
          var commentors = [];
          $(data).find("comment").each(function() {
            var id = $(this).find("id").text();
            var commentor = $(this).find("commentor").text();
            var content = $(this).find("content").text();
            var date = $(this).find("date").text();
            // insert the comment as the first element since we want to display comments from the oldest to the
            // newest whereas the web service returns in the opposite order
            comments.unshift({
              id: id,
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
            self.renderComments(comments, commentActivityParentId, scrollToCommentId);
          }, function(err) {
            // error occurred
          });
          resizeEventHandler();
          self.clearErrorMessage();
        }).fail(function () {
            self.showErrorMessage("${UIActivity.comment.canNotLoadComments}");
            console.log("Can not load comments!");
        });
      }
    },

    renderComments: function(comments, commentActivityParentId, scrollToCommentId) {
      var self = this;

      var commentsHtml = '';
      if(comments != null && comments.length > 0) {
        $('#documentPreviewContainer .nbOfComments').html(comments.length);
        commentsHtml = '<ul class="commentList">';
        var subCommentIndex = 0;
        var subCommentSize = 0;
        var subCommentSize = 0;
        var hideSubComments = false;
        $.each(comments, function (index, comment) {
          var commenterProfileUrl = "/" + eXo.env.portal.containerName + "/" + eXo.env.portal.portalName + "/" + comment.identity.profile.username;
          var commenterAvatar = comment.identity.profile.avatar;
          if (commenterAvatar == null) {
            commenterAvatar = '/eXoSkin/skin/images/system/UserAvtDefault.png';
          }
          var commentClass = "";
          var parentCommentIdString = comment.parentCommentId ? comment.parentCommentId : "";
          if(comment.parentCommentId) {
            subCommentIndex ++;
            commentClass = "subCommentBlock";
            if(hideSubComments) {
                commentClass += " hidden";
                if(subCommentIndex == subCommentSize) {
                  commentsHtml += 
                  '<li class="clearfix commentItem subCommentBlock subCommentShowAll" id="SubCommentShowAll_' + comment.parentCommentId + '">' +
                      '<p class="cont">' +
                        '<a href="javascript:void(0)" class="subCommentShowAllLink" data-parent-comment="' + comment.parentCommentId + '">' +
                          "${UIActivity.label.ViewAllReplies}".replace("{0}", subCommentSize) +
                        '</a>' +
                      '</p>' +
                  '</li>';
                }
            }
          } else {
            subCommentIndex = 0;
            subCommentSize = self.getSubCommentsSize(comments, comment.id);
            hideSubComments = subCommentSize > 2 & comment.id !== commentActivityParentId;
          }

          commentsHtml += '<li class="clearfix commentItem ' + commentClass + '" data-comment="' + comment.id + '" data-parent-comment="' + parentCommentIdString + '">' +
            '<a class="avatarXSmall pull-left" href="' + commenterProfileUrl + '" title="' + XSSUtils.sanitizeString(comment.identity.profile.fullname) + '"><img src="' + commenterAvatar + '" alt="" /></a>' +
            '<div class="rightBlock">' +
              '<div class="tit">' +
                '<a href="' + commenterProfileUrl + '" >' + XSSUtils.sanitizeString(comment.identity.profile.fullname) + '</a>' +
                '<span class="pull-right dateTime">' + self.convertDate(comment.updateDate) + '</span>' +
              '</div>' +
              '<p class="cont">' + comment.body + '</p>';

          // add like comment if the document is linked to an activity
          if(self.settings.activity.id != null) {
            var maxNbLikersInTooltip = 10;
            comment.likes.reverse();
            var moreThan10Likers = comment.likes.length > maxNbLikersInTooltip;
            var commentLikers = '';
            for(var i=0; i < comment.likes.length; i++) {
              commentLikers += comment.likes[i].fullname + '\n';
              if(moreThan10Likers && i == (maxNbLikersInTooltip - 2)) {
                commentLikers += "${UIActivity.msg.MoreLikers}".replace("{0}", comment.likes.length - (maxNbLikersInTooltip - 1));
                break;
              }
            }

            var likeTooltip = comment.liked ? "${UIActivity.msg.UnlikeComment}" : "${UIActivity.msg.LikeComment}";

            commentsHtml += '<ul class="pull-left statusAction">' +
                '<li>' +
                  '<a onclick="documentPreview.toggleLikeComment(\'' + comment.id + '\')" class="likeCommentLink" data-placement="bottom" rel="tooltip" title="' + likeTooltip + '" id="likeCommentLink_' + comment.id + '" href="javascript:void(0);">' +
                    '<i class="uiIconThumbUp ' + (comment.liked ? 'commentLiked' : '') + '"></i>' +
                  '</a>' +
                  '<a onclick="documentPreview.showLikersPopup(\'' + comment.id + '\');" data-placement="bottom" class="likeCommentCount ' + (comment.liked ? 'commentLiked' : '') + '" style="display: ' + (comment.likes.length > 0 ? 'inline' : 'none') +'" data-html="true" rel="tooltip" title="' + commentLikers + '" id="likeCommentCount_' + comment.id + '" href="javascript:void(0);">' + (comment.likes.length > 0 ? '(' + comment.likes.length + ')' : '') + '</a>' +
                '</li>' +
                '<li class="separator">-</li>' +
                '<li>' +
                  '<a class="replyCommentLink" data-placement="bottom" href="javascript:void(0);" data-comment="' + comment.id + '" data-parent-comment="' + parentCommentIdString + '">' +
                  '${UIActivity.label.Reply}' +
                  '</a>' +
                '</li>' +
              '</ul>';
          }

          commentsHtml += '  </div>' +
          '</li>';
        })
        commentsHtml += '</ul>';
      } else {
        $('#documentPreviewContainer .nbOfComments').html('0');
        commentsHtml = '<div class="commentList noComment">' +
            '<div class="info">${UIActivity.comment.noComment}</div>' +
          '</div>';
      }
      var commentsContainer = $('#documentPreviewContainer .commentArea .comments');
      commentsContainer.html(commentsHtml);
      commentsContainer.addClass('commentsLoaded');

      $("#documentPreviewContainer .subCommentShowAllLink").on("click", function() {
        var parentCommentId = $(this).attr('data-parent-comment');

        $('#documentPreviewContainer #SubCommentShowAll_' + parentCommentId).hide();
        $('#documentPreviewContainer [data-parent-comment=' + parentCommentId + ']').removeClass('hidden');
      });

      $(".replyCommentLink").on("click", function() {
        var parentCommentId = $(this).attr("data-parent-comment");
        var commentId = $(this).attr("data-comment");
        parentCommentId = parentCommentId ? parentCommentId : commentId;
        var commentInput = $('#documentPreviewContainer #commentInput');
        commentInput.attr("data-parent-comment", parentCommentId);

        $("#documentPreviewContainer .parentCommentBlock").removeClass("hidden");
        var $commentList = $('#documentPreviewContainer .commentList');

        var $inputContainer = $("#documentPreviewContainer .commentInputBox");
        $('#documentPreviewContainer #commentInput').ckeditorGet().destroy(true);

        $inputContainer.addClass("subCommentBlock");
        $inputContainer.insertAfter($("#documentPreviewContainer .commentItem[data-comment=" + parentCommentId + "], #documentPreviewContainer .commentItem[data-parent-comment=" + parentCommentId + "]").last());
        $inputContainer.find("#documentPreviewContainer .commentBox .commentTextInput").html($('<div>').append($inputContainer.find('textarea').clone()).html());

        $("#documentPreviewContainer .commentItem").removeClass("focus");
        $("#documentPreviewContainer .commentItem[data-comment=" + commentId + "]").addClass("focus");

        var ele = $("#documentPreviewContainer .commentInputBox");
        var elementToScroll = "#documentPreviewContainer .comments .commentList";
        if (eXo.social.SocialUtil.checkDevice().isMobile === true) {
          ele = $("#documentPreviewContainer [data-comment=" + commentId + "]");
          elementToScroll = "#documentPreviewContainer .commentArea";
        }
        if(ele.length > 0 && ele.offset() && ele.parent().offset()) {
          var nTop = ele.offset().top - ele.parent().offset().top + ele.parent().scrollTop();
          $(elementToScroll).animate({
            scrollTop: nTop
          },1000);
        }

        self.initCKEditor();
        self.clearErrorMessage();
        self.showCommentLink(true, true, false);
      });

      if(scrollToCommentId && eXo.social.SocialUtil.checkDevice().isMobile === true) {
        var ele = $("#documentPreviewContainer [data-comment=" + scrollToCommentId + "]");

        if(ele.length > 0 && ele.offset() && ele.parent().offset()) {
          var nTop = ele.offset().top - ele.parent().offset().top + ele.parent().scrollTop();
          $("#documentPreviewContainer .commentArea").animate({
            scrollTop: nTop
          },1000);
        }
      }
      self.showCommentLink(eXo.social.SocialUtil.checkDevice().isMobile, false, true);
    },

    showCommentBlockForActivity: function(comments, parentCommentId) {
      $("#documentPreviewContainer .commentItem").removeClass("focus");
      this.moveCKEditorInOriginalLocation();
      this.initCKEditor();
      this.clearErrorMessage();
      this.showCommentLink(false, true, true);
      resizeEventHandler();
    },

    getSubCommentsSize: function(comments, parentCommentId) {
      var count = 0;
      comments.forEach(function(comment) {
        if(comment.parentCommentId == parentCommentId) {
          count++;
        }
      })
      return count;
    },

    convertDate: function(dateStr) {
      var postedTime = Date.parse(dateStr);
      
      var time = (new Date().getTime() - postedTime) / 1000;
      var value;
      if (time < 60) {
        return "${UIActivity.label.Less_Than_A_Minute}";
      } else {
        if (time < 120) {
          return "${UIActivity.label.About_A_Minute}";
        } else {
          if (time < 3600) {
            value = Math.round(time / 60);
            return "${UIActivity.label.About_?_Minutes}".replace("{0}", value);
          } else {
            if (time < 7200) {
              return "${UIActivity.label.About_An_Hour}";
            } else {
              if (time < 86400) {
                value = Math.round(time / 3600);
                return "${UIActivity.label.About_?_Hours}".replace("{0}", value);
              } else {
                if (time < 172800) {
                  return "${UIActivity.label.About_A_Day}";
                } else {
                  if (time < 2592000) {
                    value = Math.round(time / 86400);
                    return "${UIActivity.label.About_?_Days}".replace("{0}", value);
                  } else {
                    if (time < 5184000) {
                      return "${UIActivity.label.About_A_Month}";
                    } else {
                      value = Math.round(time / 2592000);
                      return "${UIActivity.label.About_?_Months}".replace("{0}", value);
                    }
                  }
                }
              }
            }
          }
        }
      }
    },

    postComment: function () {
      var self = this;
      var commentInput = $('#documentPreviewContainer #commentInput');
      if(commentInput != null && $.trim(commentInput.val())) {
        var commentContent = commentInput.val();
        var commentActivityParentId = commentInput.attr("data-parent-comment");
        if(this.settings.activity.id != null) {
           var postData = { poster : eXo.env.portal.userName , title : commentContent, parentCommentId : commentActivityParentId };
          // post comment on the activity
          return $.ajax({
            type: 'POST',
            url: '/rest/v1/social/activities/' + this.settings.activity.id + '/comments',
            data: JSON.stringify(postData),
            contentType: 'application/json'
          }).done(function (data) {
            self.loadComments(commentActivityParentId, data.id);
            self.moveCKEditorInOriginalLocation();
            self.initCKEditor();
            self.clearErrorMessage();
          }).fail(function () {
            self.showErrorMessage("${UIActivity.comment.canNotAddComment}");
            console.log("Can not post comment!");
          });
        } else {
          // post comment on the document
          return $.ajax({
            type: 'POST',
            url: '/rest/contents/comment/add',
            data: {
              jcrPath: '/' + this.settings.doc.repository + '/' + this.settings.doc.workspace + this.settings.doc.path,
              comment: commentContent
            },
            contentType: 'application/x-www-form-urlencoded'
          }).done(function (data) {
            self.loadComments();
            self.moveCKEditorInOriginalLocation();
            self.initCKEditor();
            self.clearErrorMessage();
          }).fail(function () {
            self.showErrorMessage("${UIActivity.comment.canNotAddComment}");
            console.log("Can not post comment!");
          });
        }
      }
    },

    deleteComment: function(commentId) {
      var self = this;

      if(this.settings.activity.id != null) {
        return $.ajax({
          type: 'DELETE',
          url: '/rest/v1/social/comments/' + commentId
        }).done(function (data) {
          self.loadComments();
          self.clearErrorMessage();
        }).fail(function () {
            self.showErrorMessage("${UIActivity.comment.canNotDeleteComment}");
            console.log("Can not delete comment!");
        });
      } else {
        return $.ajax({
          type: 'DELETE',
          url: '/rest/contents/comment/delete/?jcrPath=/' + this.settings.doc.repository + '/' + this.settings.doc.workspace + this.settings.doc.path + '&commentId=' + commentId
        }).done(function (data) {
          self.loadComments();
          self.clearErrorMessage();
        }).fail(function () {
            self.showErrorMessage("${UIActivity.comment.canNotDeleteComment}");
            console.log("Can not delete comment!");
        });
      }
    },

    render: function () {
      var self = this;

      this.createSkeleton();

      var docContentContainer = $('#documentPreviewContent');

      if(!$('.commentsLoaded').length) {
        $(window).on('resize', resizeEventHandler);
        $(document).on('keyup', closeEventHandler);
        if(!window.previewDocumentEventsSet) {
          $(document).keyup(function(event) {
            if(event && event.keyCode &&  (event.keyCode == 37 || event.keyCode == 39) &&  $("#NavCommands").length && $("#NavCommands").is(":visible")) {
              event.stopPropagation();
              event.preventDefault();
              if(event.keyCode == 37) {
                self.goToPrevious();
              } else if(event.keyCode == 39) {
                self.goToNext();
              }
            }
          })
          $(document).on("swipe", function(event) {
            if(window.innerWidth < 767 && $("#NavCommands").length && $("#NavCommands").is(":visible")) {
              if(event.direction === 'left') {
                self.goToNext();
              } else if(event.direction === 'right') {
                self.goToPrevious();
              }
            }
          })
        }
        window.previewDocumentEventsSet = true;
  
        // Bind close event. Return body scroll, turn off keyup
        $(".exitWindow > .uiIconClose", $('#uiDocumentPreview')).click(function() {
          $('body').removeClass('modal-open');
          $("#documentPreviewContainer").remove();
          setTimeout(function() {
            $('body').css('overflow', 'visible');
            $(document).off('keyup', closeEventHandler);
            $(window).off('resize', resizeEventHandler);
          }, 500);
        });
  
        // Bind expanded/collapsed event
        var uiDocumentPreview = $('#uiDocumentPreview');
        $('.resizeButton .uiIconMiniArrowLeft, .resizeButton .uiIconMiniArrowRight', uiDocumentPreview).click(function() {
          uiDocumentPreview.toggleClass("collapsed");
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
        
        this.initCKEditor();

        if($('#CommentButton [data-action-initialized]').length == 0) {
          $('#CommentButton').on('click', function(event) {
            self.postComment();
            self.showCommentLink(eXo.social.SocialUtil.checkDevice().isMobile, false, true);
          });
          $('#CommentButton').attr("data-action-initialized", "true");
        }

        if($('#CancelButton [data-action-initialized]').length == 0) {
          $('#CancelButton').on('click', function(event) {
            $('#documentPreviewContainer .commentArea')[0].style.display = "none";
            $('#documentPreviewContent')[0].style.display = "block";
            $('.previewBtn')[0].style.display = "block"
            resizeEventHandler();
          });
          $('#CancelButton').attr("data-action-initialized", "true");
        }

        if($('.showComments [data-action-initialized]').length == 0) {
          $('.showComments').on('click', function(event) {
            var $uiDocumentPreview = $('#uiDocumentPreview');
            var $commentArea = $('.commentArea', $uiDocumentPreview);
            var $commentList = $('.commentList', $commentArea);
            if($('#cke_commentInput .cke_contents').length > 0) {
              $('#cke_commentInput .cke_contents')[0].style.height = "100px";
            }
            $('#documentPreviewContainer .commentArea')[0].style.display = "block";
            $('.previewBtn')[0].style.display = "none"
            $('#documentPreviewContent')[0].style.display = "none";
            $("#documentPreviewContainer .parentCommentBlock").addClass("hidden");

            self.moveCKEditorInOriginalLocation();
            self.initCKEditor();
            self.clearErrorMessage();
            self.showCommentLink(eXo.social.SocialUtil.checkDevice().isMobile, false, false);
          });
          $('.showComments').attr("data-action-initialized", "true");
        }
  
        $('.loading', docContentContainer).show();
        this.show();
      }

      docContentContainer.load('/rest/private/contentviewer/' + this.settings.doc.repository + '/' + this.settings.doc.workspace + '/' + this.settings.doc.id, function() {
        $('.loading', docContentContainer).hide();
        resizeEventHandler();
      });
    },
    showCommentLink: function(showComment, showCKEditor, isCKEditorInBottom) {
      if(showComment) {
        $('#documentPreviewContainer .parentCommentBlock').removeClass("hidden");
      } else {
        $('#documentPreviewContainer .parentCommentBlock').addClass("hidden");
      }

      if (eXo.social.SocialUtil.checkDevice().isMobile === true) {
        var commentLinkRelative = showComment && showCKEditor;
        if(commentLinkRelative) {
          $('#documentPreviewContainer .parentCommentBlock').removeClass("FixedBlock");
        } else {
          $('#documentPreviewContainer .parentCommentBlock').addClass("FixedBlock");
        }
        if(showCKEditor) {
          $("#documentPreviewContainer .commentArea").addClass("commentBoxOpen");
          $('#documentPreviewContainer .commentInputBox').removeClass("hidden");
        } else {
          $("#documentPreviewContainer .commentArea").removeClass("commentBoxOpen");
          $('#documentPreviewContainer .commentInputBox').addClass("hidden");
        }
      }

      if(isCKEditorInBottom) {
        $("#documentPreviewContainer .commentList").addClass("commentBoxOpen");
      } else {
        $("#documentPreviewContainer .commentList").removeClass("commentBoxOpen");
      }
    },
    moveCKEditorInOriginalLocation: function() {
      $('#documentPreviewContainer #commentInput').attr("data-parent-comment", null)
      try {
        $('#documentPreviewContainer #commentInput').ckeditorGet().destroy(true);
      } catch(e) {
      }

      var $inputContainer = $("#documentPreviewContainer .commentInputBox");
      $inputContainer.insertAfter($("#documentPreviewContainer .comments"));
      $inputContainer.removeClass("subCommentBlock");
      $inputContainer.find("#documentPreviewContainer .commentBox .commentTextInput").html($('<div>').append($inputContainer.find('textarea').clone()).html());
    },
    initCKEditor: function() {
        var commentInput = $('#documentPreviewContainer #commentInput');
        var extraPlugins = 'simpleLink,selectImage,suggester';

        // TODO this line is mandatory when a custom skin is defined, it should not be mandatory
        CKEDITOR.basePath = '/commons-extension/ckeditor/';
        var toolbarHeight = 39;
        commentInput.ckeditor({
          customConfig: '/commons-extension/ckeditorCustom/config.js',
          extraPlugins: extraPlugins,
          typeOfRelation: 'mention_comment',
          spaceURL: this.settings.activity.spaceURL,
          on : {
            instanceReady : function ( evt ) {
              // Hide the editor toolbar
              $("#CommentButton").prop("disabled", true);
              $('#' + evt.editor.id + '_bottom').removeClass('cke_bottom_visible');

            },
            focus : function ( evt ) {
              // Show the editor toolbar, except for smartphones in landscape mode
              if ($(window).width() > 767 || $(window).width() < $(window).height()) {
                //$('#' + evt.editor.id + '_bottom').css('display', 'block');
                evt.editor.execCommand('autogrow');
                var $content = $('#' + evt.editor.id + '_contents');
                var contentHeight = $content.height();
                var $ckeBottom = $('#' + evt.editor.id + '_bottom');
                $ckeBottom[0].style.display = "block";
                var originalHeight = $ckeBottom.css('height', 'auto').outerHeight();
                toolbarHeight = $ckeBottom.addClass('cke_bottom_visible').outerHeight();
                var heightToAddVisibleClass = toolbarHeight - originalHeight;
                $ckeBottom.removeClass('cke_bottom_visible').css('height', '0px');
                $ckeBottom.animate({
                  height: "" + toolbarHeight
                }, {
                  step: function(number, tween) {
                    $content.height(contentHeight - number);
                    if (number >= heightToAddVisibleClass) {
                      $ckeBottom.addClass('cke_bottom_visible');
                    }
                  }
                });
              } else {
                $('#' + evt.editor.id + '_bottom').removeClass('cke_bottom_visible');
                $('#' + evt.editor.id + '_bottom')[0].style.display = "none";
              }
            },
            blur : function ( evt ) {
              // Hide the editor toolbar
              if ($(window).width() > 767 || $(window).width() < $(window).height()) {
                $('#' + evt.editor.id + '_contents').css('height', $('#' + evt.editor.id + '_contents').height() + toolbarHeight);
                $('#' + evt.editor.id + '_bottom').css('height', '0px');
                $('#' + evt.editor.id + '_bottom').removeClass('cke_bottom_visible');
              }
            },
            change: function( evt) {
                var newData = evt.editor.getData();
                var pureText = newData? newData.replace(/<[^>]*>/g, "").replace(/&nbsp;/g,"").trim() : "";

                if (pureText.length > 0 && pureText.length <= MAX_LENGTH) {
                    $("#CommentButton").removeAttr("disabled");
                } else {
                    $("#CommentButton").prop("disabled", true);
                }
                
                if (pureText.length <= MAX_LENGTH) {
                    evt.editor.getCommand('selectImage').enable();
                } else {
                    evt.editor.getCommand('selectImage').disable();
                }
            },
            key: function( evt) {
                var newData = evt.editor.getData();
                var pureText = newData? newData.replace(/<[^>]*>/g, "").replace(/&nbsp;/g,"").trim() : "";
                if (pureText.length > MAX_LENGTH) {
                    if ([8, 46, 33, 34, 35, 36, 37,38,39,40].indexOf(evt.data.keyCode) < 0) {
                        evt.cancel();
                    }
                }
            }
          }
        });
    },
    show: function () {
      $('#documentPreviewContainer').show();
      $('body').css('overflow', 'hidden');
    },

    hide: function () {
      $('#documentPreviewContainer').hide();
      $('body').css('overflow', '');
    },

    goToNext: function () {
      if(this.settings && this.settings.activity && this.settings.activity.next) {
        $(this.settings.activity.next).click();
      }
    },

    goToPrevious: function () {
      if(this.settings && this.settings.activity && this.settings.activity.previous) {
        $(this.settings.activity.previous).click();
      }
    }
  };

  // Bind Esc key
  var closeEventHandler = function(e) {
    $('#presentationMode').blur();
    if (e.keyCode == 27 && ("presentationMode" != e.target.id || $.browser.mozilla)) {
      $(".exitWindow > .uiIconClose", $('#uiDocumentPreview')).trigger("click");
    }
  }

  var applyReverseEllipsis = function($parent) {
    $parent.find('.ellipsis-reverse').each(function() {
      var $ellipsisContent = $(this).find(".ellipsis-reverse-content");
      var $ellipsisApply = $(this).find(".ellipsis-reverse-apply");
      var $ellipsisApplyContent = $(this).find(".ellipsis-reverse-apply-content");
      var $ellipsisFirstChild = $ellipsisApplyContent.find(' :first-child');
      if(!$ellipsisApplyContent.length
          || !$ellipsisContent.length
          || !$(this).offset()
          || !$ellipsisFirstChild
          || !$ellipsisFirstChild.offset()
          || !$ellipsisFirstChild.length) {
        return;
      }
      var applyEllipsisReverse = $ellipsisFirstChild.offset().left - $(this).offset().left -15;
      if(applyEllipsisReverse > 0) {
        $ellipsisContent.addClass("hidden");
      } else {
        $ellipsisContent.removeClass("hidden");
      }
    })
  };

  // Resize Event
  var resizeEventHandler = function() {
    var pdfDisplayAreaHeight = window.innerHeight - 143 - ((documentPreview.settings.doc.fileInfo || documentPreview.settings.doc.breadCrumb) ? 55 : 0);
    var $uiDocumentPreview = $('#uiDocumentPreview');

    // Show empty preview message
    var $blockToAppendTo = $uiDocumentPreview.find(".UIResizableBlock");
    if($blockToAppendTo.length == 0 && !documentPreview.settings.doc.isWebContent) {
      $blockToAppendTo = $("#documentPreviewContent");
      if($blockToAppendTo.find(".EmptyDocumentPreview").length == 0) {
        $blockToAppendTo.append("<div class='EmptyDocumentPreview'><div class='message'><div class='content'><i class='" + documentPreview.settings.doc.cssIcon + "'></i><br><span>${UIActivity.comment.noPreviewOfDocument}</span></div></div></div>");
      }
    }
    // Show Next & previous buttons inside resizable div
    if($uiDocumentPreview.find("#NavCommands").length == 0) {
      if(documentPreview.settings.doc && documentPreview.settings.doc.breadCrumb) {
        var breadCrumbContent = '<div class="ellipsis-reverse-content hidden">...</div><div class="ellipsis-reverse-apply"><div class="ellipsis-reverse-apply-content">';
        var folderIndex = 0;
        var breadCrumbContentTooltip = "";
        for (var folderName in documentPreview.settings.doc.breadCrumb) {
          if (documentPreview.settings.doc.breadCrumb.hasOwnProperty(folderName)) {
              var folderPath = documentPreview.settings.doc.breadCrumb[folderName];
              if(folderIndex > 0) {
                breadCrumbContent += '&nbsp;<i class="uiIconArrowRight"></i>&nbsp;';
                breadCrumbContentTooltip += " > ";
              }
              breadCrumbContent += '<a href="' + folderPath + '" onclick="event.stopPropagation();window.location.href=this.href">' + XSSUtils.escapeHtml(folderName) + '</a>';
              breadCrumbContentTooltip += folderName;
              folderIndex++;
          }
        }
        breadCrumbContent += '</div></div>';
      }
      var fileTitleBlock = '<div class="fileTitle">' +
          '<h4 class="fileName" data-container="body" rel="tooltip" data-placement="top" title="' + documentPreview.settings.doc.title + '">' +
            '<div class="ellipsis">' + documentPreview.settings.doc.title + '</div>' +
                ((documentPreview.settings.version  && documentPreview.settings.version.number) ? ('<div class="label primary fileVersion"' + (documentPreview.settings.doc.openUrl ? 'onclick="window.location.href=\'' + documentPreview.settings.doc.openUrl +'&versions=true\'"' : "") + '>V' + documentPreview.settings.version.number + '</div>') : '') +
          '</h4>';
      if(breadCrumbContent) {
        fileTitleBlock += '<div class="breadCrumb ellipsis-reverse" data-container="body" rel="tooltip" data-placement="top" title="' + breadCrumbContentTooltip + '">' + breadCrumbContent + '</div>';
      }
      if(documentPreview.settings.doc.fileInfo) {
        fileTitleBlock += '<div class="fileInfo ellipsis" data-container="body" rel="tooltip" data-placement="top" title="' + documentPreview.settings.doc.fileInfo + '">' + documentPreview.settings.doc.fileInfo + '</div>';
      }
      fileTitleBlock += '</div>';
      if(documentPreview.settings.activity.next || documentPreview.settings.activity.previous) {
        $blockToAppendTo.append('<div id="NavCommands">' +
             '<div class="arrowPrevious ' + (documentPreview.settings.activity.previous ? '' : 'disabled') + '" onclick="documentPreview.goToPrevious()"  rel="tooltip" data-placement="top" title="${UIActivity.label.Previous}"><span></span></div>'
               + fileTitleBlock +
             '<div class="arrowNext ' + (documentPreview.settings.activity.next ? '' : 'disabled') + '" onclick="documentPreview.goToNext()" rel="tooltip" data-placement="top" title="${UIActivity.label.Next}"><span></span></div>' +
          '</div>'
        );
      } else {
        $blockToAppendTo.append('<div id="NavCommands">'
            + fileTitleBlock +
         '</div>'
       );
      }
    }
    applyReverseEllipsis($uiDocumentPreview);

    var $remoteEditDocument = $uiDocumentPreview.find(".remoteEditBtn");
    if($remoteEditDocument.length == 1 && !$remoteEditDocument.hasClass("updated")) {
      // Update label only once et when activity has multiple files
      $(document).ready(function(){
        eXo.ecm.OpenDocumentInOffice.updateLabel(documentPreview.settings.doc.workspace + ":" + documentPreview.settings.doc.path, documentPreview.settings.activity.id);
      });
      $remoteEditDocument.hasClass("updated");
    }

    // Calculate margin
    $('#outerContainer', $uiDocumentPreview).height(pdfDisplayAreaHeight); // pdf viewer
    var $commentArea = $('.commentArea', $uiDocumentPreview);
    var $commentAreaTitle = $('.title', $commentArea);
    var $commentInputBox = $('.commentInputBox', $commentArea);
    var $commentList = $('.commentList', $commentArea);
    var $highlightBox = $('.highlightBox', $commentArea);
    var $actionBarCommentArea = $('.actionBar', $commentArea);
    $commentList.scrollTop(20000);

    // Media viewer, no preview file
    var $navigationContainer = $(".navigationContainer", $uiDocumentPreview);
    var $emptyDocumentPreview = $(".EmptyDocumentPreview", $uiDocumentPreview);
    var $uiContentBox = $('.uiContentBox', $navigationContainer);
    var $video = $('.videoContent', $uiContentBox);
    var $flowplayerContentDetail = $('.ContentDetail', $uiContentBox);
    var $flowplayerPlayerContent = $('.PlayerContent', $flowplayerContentDetail);
    var $flowplayer = $('object', $flowplayerPlayerContent);
    var $flashViewer = $('.FlashViewer', $uiContentBox);
    var $embedFlashViewer = $('embed', $flashViewer);
    var $windowmediaplayer = $('#MediaPlayer1', $uiContentBox);
    var $embedWMP = $('embed', $windowmediaplayer);

    $emptyDocumentPreview.height(pdfDisplayAreaHeight - 40);
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

    var $EmbedHtml =  $('.EmbedHtml', $uiDocumentPreview);
    $EmbedHtml.height(pdfDisplayAreaHeight) // External embedded

    // Resize image flick
    $img = $('.uiDocumentPreviewMainWindow > .EmbedHtml > a > img');
    $img.css('max-width', $EmbedHtml.width());
    $img.css('max-height', $EmbedHtml.height());
  }

  return documentPreview;
})($, UIActivity, XSSUtils);
