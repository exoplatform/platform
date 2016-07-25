(function ($) {
  documentPreview = {
    docId: null,
    docPath: null,
    downloadUrl: null,
    openUrl: null,
    options: null,
    defaultOptions: {
      isWebContent: false,
      showComments: false
    },
    labels: null,
    defaultLabels: {
      close: "Close",
      download: "Download",
      openInDocuments: "Open in Documents"
    },

    init: function (docId, docPath, downloadUrl, openUrl, options, labels) {
      this.docId = docId;
      this.docPath = docPath;
      this.downloadUrl = downloadUrl;
      this.openUrl = openUrl;

      this.options = $.extend(this.defaultOptions, options);
      this.labels = $.extend(this.defaultLabels, labels);

      this.createSkeleton();
      this.render();
    },

    createSkeleton: function () {
      var docPreviewContainer = document.createElement("div");
      docPreviewContainer.innerHTML = ' \
        <div id="documentPreviewContainer" class="maskLayer" style="display: none"> \
          <div class="uiDocumentPreview" id="uiDocumentPreview"> \
            <div class="exitWindow"> \
              <a class="uiIconClose uiIconWhite" title="' + this.labels.close + '" onclick="documentPreview.hide()"></a> \
            </div> \
            <div class="uiDocumentPreviewMainWindow clearfix"> \
              <!-- doc comments --> \
              <div class="uiBox commentArea pull-right" id="$uicomponent.id"> \
                <div class="title">\
                  Title \
                </div> \
                <div class="uiContentBox"> \
                  <div class="highlightBox"> \
                    <div class="profile clearfix"> \
                      <a title="authorFullName" href="authorProfileUri" class="avatarMedium pull-left"><img alt="$authorFullName" src="authorAvatarImgSrc"></a> \
                      <div class="rightBlock"> \
                        <a href="authorProfileUri">authorFullName</a> \
                        <p class="dateTime">activityPostedTime</p> \
                        <p class="descript" title="activityStatus">activityStatus</p> \
                      </div> \
                    </div> \
                  </div> \
                  <div class="actionBar clearfix "> \
                    <ul class="pull-right"> \
                      <li> \
                        <a href="#" id = "previewCommentLink"> \
                          <i class="uiIconComment uiIconLightGray"></i>&nbsp;commentSize \
                        </a> \
                      </li> \
                      <li> \
                        <a href="javascript:void(0);" onclick="likeActivityAction" rel="tooltip" data-placement="bottom" title="LikeActivity"> \
                          <i class="uiIconThumbUp uiIconLightGray"></i>&nbsp;identityLikesNum \
                        </a> \
                      </li> \
                    </ul> \
                  </div> \
                  <div> \
                    <ul class="commentList"> \
                    </ul> \
                  </div> \
                  <div class="commentInputBox"> \
                    <a class="avatarXSmall pull-left" href="currentCommenterUri" title="currentCommenterFullName"><img src="currentCommenterAvatar" alt="currentCommenterFullName" /></a> \
                      <div class="commentBox"> \
                        <textarea placeholder="commentTextAreaPreview" cols="30" rows="10" id="commentTextAreaPreview" activityId="activityId" class="textarea"></textarea> \
                      </div> \
                    </div> \
                </div> \
              </div> \
              <div class="resizeButton " id="ShowHideAll"> \
                <i style="display: block;" class="uiIconMiniArrowRight uiIconWhite"></i> \
              </div> \
              <div id="documentPreviewContent"> \
              </div> \
              <!-- put vote area here --> \
              <div class="previewBtn"> \
                <div class="downloadBtn"> \
                  <a href="' + this.downloadUrl + '"><i class="uiIconDownload uiIconWhite"></i>&nbsp;' + this.labels.download + '</a> \
                </div> \
                <div class="openBtn"> \
                  <a href="' + this.openUrl + '"><i class="uiIconGotoFolder uiIconWhite"></i>&nbsp;' + this.labels.openInDocuments + '</a> \
                </div> \
              </div> \
            </div> \
          </div> \
        </div>';
      document.body.appendChild(docPreviewContainer);
    },

    render: function () {
      resizeEventHandler();
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

      var docContentContainer = $('#documentPreviewContent');
      var docContent = '';
      if(this.options.isWebContent) {
        docContent += '<div class="uiPreviewWebContent">';
      }

      docContentContainer.load('/rest/private/contentviewer/repository/collaboration/' + this.docId);

      if(this.options.isWebContent) {
        docContent += '</div>';
      }
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