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

    init: function (docId, downloadUrl, openUrl, options, labels) {
      this.docId = docId;
      this.downloadUrl = downloadUrl;
      this.openUrl = openUrl;

      this.options = $.extend(this.defaultOptions, options);
      this.labels = $.extend(this.defaultLabels, labels);

      this.create();
    },

    create: function () {
      var docPreviewContainer = document.createElement("div");
      docPreviewContainer.innerHTML = ' \
        <div id="documentPreviewContainer" class="maskLayer" style="display: none"> \
          <div class="uiDocumentPreview" id="uiDocumentPreview' + this.docId + '"> \
            <div class="exitWindow"> \
              <a class="uiIconClose uiIconWhite" title="' + this.labels.close + '" onclick="documentPreview.hide()"></a> \
            </div> \
            <div class="uiDocumentPreviewMainWindow clearfix"> \
              <!-- put comment area here --> \
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
      var docContentContainer = $('#documentPreviewContent');
      var docContent = '';
      if(this.options.isWebContent) {
        docContent += '<div class="uiPreviewWebContent">';
      }

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

  return documentPreview;
})($);