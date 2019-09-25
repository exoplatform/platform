/**
 * Copyright (C) 2017 eXo Platform SAS.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

(function(ecm_bootstrap, gj, base, wcm_utils, UIComposer) {
  var $ = gj;
  var uiUploadInput = {
    uploadingCount : 0,
    maxUploadCount : 2,
    uploadingFileQueue : [],

    /**
     * Initialize upload and create a upload request to server
     *
     * @param {String}
     *          uploadIds identifier upload
     */
    initUploadEntry : function(id, uploadIds, limitCount, limitSizeInMB, megabyteMessage) {
      if (!uiUploadInput.initialized) {
        UIComposer.addPlugin(uiUploadInput);
      }
      uiUploadInput.megabyteMessage = megabyteMessage;
      uiUploadInput.putData("DropFileBox", "id", id);

      var obj = $("#"+id + " #DropFileBox");
      obj.on('dragover', function(e) {
        e.stopPropagation();
        e.preventDefault();
        $(this).addClass('dragEntered');
      });
      obj.on('dragleave', function(e) {
        e.stopPropagation();
        e.preventDefault();
        $(this).removeClass('dragEntered');
      });
      obj.on('drop', function(e) {
        $(this).removeClass('dragEntered');
        e.preventDefault();
        var files = e.originalEvent.dataTransfer.files;
        // We need to send dropped files to Server
        var id = uiUploadInput.getData("DropFileBox", "id");
        uiUploadInput.handleFileUpload(files, id);
      });
      $(document).on('dragenter', function(e) {
        e.stopPropagation();
        e.preventDefault();
      });
      $(document).on('dragover', function(e) {
        e.stopPropagation();
        e.preventDefault();
      });
      $(document).on('drop', function(e) {
        e.stopPropagation();
        e.preventDefault();
      });

      if (!uiUploadInput.progressURL) {
        var context = eXo.env.server.context;
        uiUploadInput.progressURL = context
            + "/upload?action=progress&uploadId=";
        uiUploadInput.uploadURL = context + "/upload?action=upload&uploadId=";
        uiUploadInput.abortURL = context + "/upload?action=abort&uploadId=";
        uiUploadInput.deleteURL = context + "/upload?action=delete&uploadId=";
      }

      if (uploadIds && uploadIds.length && uploadIds.length > 0) {
        uiUploadInput.putData(id, "inputs", uploadIds);
      } else {
        uiUploadInput.putData(id, "inputs", []);
      }
      uiUploadInput.putData(id, "limitCount", limitCount);
      uiUploadInput.putData(id, "limitedSize", limitSizeInMB);

      uiUploadInput.refreshUploadedList(id, uploadIds, true);
      UIComposer.refreshShareButton();

      var parentContainer = uiUploadInput.getParentContainer(id);
      var input = parentContainer.find("input[type=file]");
      var uploadBtn = parentContainer.find(".uploadButton");
      if (base.Browser.isIE()) {
        uploadBtn.find("label").attr("for", input.attr("id"));
        input.css({
          "position" : "absolute",
          "left" : "-5000px"
        }).show();
      } else {
        uploadBtn.off("click").click(function() {
          input.click();
        })
      }
      input.on("change", function() {
        var jCont = $(this).parents(".MultiUploadContainer").find(".uiUploadInput");
        uiUploadInput.handleFileUpload(this.files, id);
      });
      parentContainer.find('.countExceeded').delay(5000).fadeOut('slow');
      parentContainer.find('.sizeExceeded').delay(5000).fadeOut('slow');
      parentContainer.find('.fileAlreadyExists').delay(5000).fadeOut('slow');
    },
    initSearchBox : function(id) {

      if($.expr && $.expr.createPseudo) {
        $.expr[":"].containsCaseInsensitive = $.expr.createPseudo(function(arg) {
            return function( elem ) {
                return $(elem).text().toUpperCase().indexOf(arg.toUpperCase()) >= 0;
            };
        });
      } else {
        $.expr[':'].containsCaseInsensitive = function(a, i, m) {
          return $(a).text().toUpperCase()
              .indexOf(m[3].toUpperCase()) >= 0;
        };
      }

      $("#" + id).find(".searchBox input").on("keyup", function(event) {
        var term = this.value;
        if(!term || !(term.trim())) {
          $(".filesTitle").show();
          $(".fileSelection").show();
          $(".foldersTitle").show();
          $(".folderSelection").show();
          $(".driveData").show();
          $(".emptyResults").hide();
        } else {
          var $drivesToShow = $(".driveData:containsCaseInsensitive('" + term + "')");
          $drivesToShow.show();

          var $foldersToShow = $(".folderSelection:containsCaseInsensitive('" + term + "')");
          $foldersToShow.show();

          var $filesToShow = $(".fileSelection:containsCaseInsensitive('" + term + "')");
          $filesToShow.show();

          $(".driveData:not(:containsCaseInsensitive('" + term + "'))").hide();
          $(".folderSelection:not(:containsCaseInsensitive('" + term + "'))").hide();
          $(".fileSelection:not(:containsCaseInsensitive('" + term + "'))").hide();

          if($foldersToShow.length > 0) {
            $(".foldersTitle").show();
          } else {
            $(".foldersTitle").hide();
          }
          if($filesToShow.length > 0) {
            $(".filesTitle").show();
          } else {
            $(".filesTitle").hide();
          }
          if(($foldersToShow.length + $filesToShow.length + $drivesToShow.length) == 0) {
            if(!$(".emptyFolder").is(":visible")) {
              $(".emptyResults").show();
            }
          } else {
            $(".emptyResults").hide();
          }
        }
      })
    },
    createEntryUpload : function(id) {
      var inputs = uiUploadInput.getData(id, "inputs");
      var limited = uiUploadInput.getData(id, "limitCount");
      var parentContainer = uiUploadInput.getParentContainer(id);
      if(limited <= uiUploadInput.getFilesCount(id)) {
        parentContainer.find('.countExceeded').show();
        parentContainer.find('.countExceeded').delay(5000).fadeOut('slow');
        return;
      }

      var uploadId = null;
      if(inputs && inputs.length > 0) {
        for (var i = 0; i < inputs.length; i++) {
          var jCont = $("#uploadContainer" + inputs[i]);
          if (!jCont.length) {
            uploadId = inputs[i];
            break;
          }
        }
      }

      if (!uploadId) {
        var createURL = parentContainer.find(".createUploadURL").data('url');
        ajaxAsyncGetRequest(createURL, false);
        var uploadId = inputs[inputs.length - 1];
        var idx = uploadId.search(/-\d+$/) + 1;
        uploadId = uploadId.substring(0, idx)
            + (parseInt(uploadId.substring(idx)) + 1);
        inputs.push(uploadId);
        uiUploadInput.putData(id, "inputs", inputs);
      }

      uiUploadInput.cloneContainer(id, uploadId);
      return uploadId;
    },
    cloneContainer : function(id, uploadId) {
      var template = uiUploadInput.getParentContainer(id).find("script[type='text/template']");
      var uploadCont = $(template.html());
      uploadCont.attr("id", "uploadContainer" + uploadId);
      template.after(uploadCont);
      return uploadCont;
    },
    selectFolder : function(id) {
      var inputs = uiUploadInput.getData("uploadContainer" + id, "inputs");
      if (inputs.length > 1) {
        var selectFolderInput = uiUploadInput.getParentContainer("uploadContainer" + id).find(".selectFolderUrl").data('url');
        ajaxGet(selectFolderInput + "&objectId=" + id + "&ajaxRequest=true");
      }
    },
    deleteUpload : function(id) {
      var url = uiUploadInput.deleteURL + id;
      ajaxAsyncGetRequest(url, false);

      var inputs = uiUploadInput.getData("uploadContainer" + id, "inputs");
      if (inputs.length > 1) {
        var rmInput = uiUploadInput.getParentContainer("uploadContainer" + id).find(".removeInputUrl").data('url');
        ajaxAsyncGetRequest(rmInput + "&objectId=" + id, false);

        inputs.splice($.inArray(id, inputs), 1);
        uiUploadInput.putData("uploadContainer" + id, "inputs", inputs);
      }

      var parentContainer = uiUploadInput.getParentContainer("uploadContainer" + id);
      $("#uploadContainer" + id).remove();
      uiUploadInput.onChange(parentContainer.attr("id"));
    },
    abortUpload : function(id, status) {
      var idx = $.inArray(status, uiUploadInput.uploadingFileQueue);
      if (idx !== -1) {
        uiUploadInput.uploadingFileQueue.splice(idx, 1);
      }

      var url = uiUploadInput.abortURL + id;
      ajaxAsyncGetRequest(url, false);

      uiUploadInput.remove(id);
      $("#uploadContainer" + id).remove();

      // Process next upload
      try {
        if(uiUploadInput.uploadingFileQueue.length > 0) {
          uiUploadInput.sendFileToServer(uiUploadInput.uploadingFileQueue.shift());
        }
      } catch(err) {
        console.log(err);
      }

      uiUploadInput.onChange(status.parentId);
    },
    remove : function(id, uploadIds) {
      if (!uploadIds) {
        uploadIds = uiUploadInput.listUpload;
      }
      var idx = $.inArray(id, uploadIds);
      if (idx !== -1) {
        uploadIds.splice(idx, 1);
      }
    },
    getData : function(id, key) {
      return uiUploadInput.getDataContainer(id).data(key);
    },
    putData : function(id, key, value) {
      uiUploadInput.getDataContainer(id).data(key, value);
    },
    getParentContainer : function(id) {
      var parentContainer = $("#" + id);
      if(parentContainer.find(".MultiUploadContainer").length == 1) {
        parentContainer = parentContainer.find(".MultiUploadContainer");
      }
      if(!parentContainer.hasClass("MultiUploadContainer")) {
        parentContainer = parentContainer.parents(".MultiUploadContainer");
      }
      return parentContainer;
    },
    getDataContainer : function(id) {
      var dataContainer = $("#" + id);
      if(dataContainer.hasClass("uiUploadInput")) {
        return dataContainer;
      }
      var parentContainer = uiUploadInput.getParentContainer(id);
      dataContainer = parentContainer.find(".uiUploadInput");
      return dataContainer;
    },
    sendFileToServer : function(status) {
      var extraData = {}; // Extra Data.
      uiUploadInput.uploadingCount ++;
      status.jqXHR = $.ajax({
        xhr : function() {
          var xhrobj = $.ajaxSettings.xhr();
          if (xhrobj.upload) {
            xhrobj.upload.addEventListener('progress', function(event) {
              var percent = position = event.loaded || event.position;
              var total = event.total;
              if (event.lengthComputable) {
                percent = Math.ceil(position / total * 100);
              }

              // Set progress
              status.setProgress(percent);
            }, false);
          }
          return xhrobj;
        },
        url : uiUploadInput.uploadURL + status.uploadId,
        type : "POST",
        contentType : false,
        processData : false,
        cache : false,
        data : status.formData,
        success : function(data) {
          uiUploadInput.uploadingCount --;
          // Process next upload
          try {
            if(uiUploadInput.uploadingFileQueue.length > 0) {
              uiUploadInput.sendFileToServer(uiUploadInput.uploadingFileQueue.shift());
            }
          } catch(err) {
            console.log(err);
          }

          // Refresh upload list
          var url = uiUploadInput.progressURL + status.uploadId;
          var responseText = ajaxAsyncGetRequest(url, false);
          try {
            eval("var response = " + responseText);
          } catch (err) {
            return;
          }

          if(!response.upload[status.uploadId] || !response.upload[status.uploadId].percent ||
              response.upload[status.uploadId].percent != 100) {
            status.uploadCnt.remove();
          } else {
            status.setProgress(100);
            status.deleteFile.show();
            status.abortFile.hide();
            status.progress.delay(2000).fadeOut('slow');
          }
          uiUploadInput.onChange(status.uploadCnt.attr("id"));
          var selectURL = $("#" + status.parentId + " .selectUploadURL").data('url');
          ajaxAsyncGetRequest(selectURL, false);
        },
        error : function(data) {
          uiUploadInput.uploadingCount --;

          // Process next upload
          try {
            if(uiUploadInput.uploadingFileQueue.length > 0) {
              uiUploadInput.sendFileToServer(uiUploadInput.uploadingFileQueue.shift());
            }
          } catch(err) {
            console.log(err);
          }
          status.uploadCnt.remove();
          uiUploadInput.onChange(status.uploadCnt.attr("id"));
        }
      });
    },
    hasContent : function () {
        var $docComposer = $('.UIDocActivityComposer');
        return $docComposer.hasClass('ActivityComposerExtItemSelected') &&
            $('.UIActivityComposerContainer .MultiUploadContainer').find('.selectedFile').length > 0;
    },
    onChange : function(id) {
      UIComposer.refreshShareButton();

      var limitCount = uiUploadInput.getData(id, "limitCount");
      var reached = uiUploadInput.getFilesCount(id) >= limitCount;

      var uiInput = uiUploadInput.getParentContainer(id);
      if (reached) {
        uiInput.find(".multiploadFilesSelector").hide();
      } else {
        uiInput.find(".multiploadFilesSelector").show();
      }
    },
    refreshUploadedList : function(id, list) {
      if (list.length < 1)
        return;
      var url = uiUploadInput.progressURL;
      for (var i = 0; i < list.length; i++) {
        url = url + "&uploadId=" + list[i];
      }
      var responseText = ajaxAsyncGetRequest(url, false);
      try {
        eval("var response = " + responseText);
      } catch (err) {
        return;
      }

      var listClone = list.slice(0);
      for (uploadId in listClone) {
        uploadId = listClone[uploadId];
        if (response.upload[uploadId]) {
          var jCont = $("#uploadContainer" + uploadId);
          if(!jCont.length) {
            console.log("Cannot find uploadId " + uploadId);
          }
        }
      }
      uiUploadInput.onChange(id);

      uiUploadInput.putData(id, "inputs", list);

      if (list.length) {
        uiUploadInput.putData(id, "inputs", list);
      } else {
        uiUploadInput.putData(id, "inputs", []);
      }
    },
    getFilesCount : function(id) {
      return uiUploadInput.getParentContainer(id).find(".selectedFile").length;
    },
    createStatusbar : function(id, uploadId) {
      this.containerId = id;
      // Test if limit was reached
      this.limitedSize = uiUploadInput.getData(id, "limitedSize");
      this.limitCount = uiUploadInput.getData(id, "limitCount");

      // If the file is already uploaded (just a browser refresh), just create
      // status bar
      var isNew = (!uploadId);
      if (isNew) {
        // Create new upload Id + create status bar
        uploadId = uiUploadInput.createEntryUpload(id);
      }
      if(!uploadId) {
        this.isValid = false;
        return;
      }
      this.isValid = true;
      this.uploadId = uploadId;
      this.parentId = uiUploadInput.getParentContainer("uploadContainer" + this.uploadId).attr("id");
      this.uploadCnt = $("#uploadContainer" + this.uploadId);
      this.uploadCnt.addClass("selectedFile");
      this.fileNameCnt = this.uploadCnt.find(".fileNameLabel");
      this.fileType = this.uploadCnt.find(".fileType");
      this.fileSize = this.uploadCnt.find(".fileSize");
      this.fileSizeExceeded = false;
      this.progress = this.uploadCnt.find(".progress");
      this.progressBar = this.uploadCnt.find(".bar");
      this.abortFile = this.uploadCnt.find(".abortFile");
      var self = this;
      this.abortFile.find("a").click(function() {
        if(self.jqxhr) {
          self.jqxhr.abort();
          uiUploadInput.uploadingCount --;
        }
        uiUploadInput.abortUpload(self.uploadId, self);
        self.uploadCnt.remove();
      });
      this.deleteFile = this.uploadCnt.find(".removeFile");
      if (isNew) {
        this.deleteFile.find("a").off("click").click(function() {
          uiUploadInput.deleteUpload(uploadId);
        })
        this.selectFolderCnt = this.uploadCnt.find(".selectFolder");
        this.selectFolderCnt.find("a").off("click").click(function() {
          uiUploadInput.selectFolder(uploadId);
        })
      }
      this.setFileNameSizeAndType = function(name, size, type) {
        var sizeStr = "";
        var sizeMB = size / 1024 / 1024;
        this.fileSizeExceeded = sizeMB > this.limitedSize;
        if(this.fileSizeExceeded) {
          this.limitSizeReached(name);
          return;
        }
        sizeStr = sizeMB.toFixed(2) + " " + uiUploadInput.megabyteMessage;
        this.isValid = true;
        this.uploadCnt.show();
        this.fileNameCnt.html(name);
        this.fileNameCnt.data("name", name);
        this.fileSize.html(sizeStr);
        if(type) {
          this.fileType.addClass(
              "uiBgd64x64"
                  + type.replace(/\./g, '').replace('/', '').replace('\\', ''));
        }
        this.uploadCnt.find("*[rel='tooltip']").tooltip();
      }

      this.limitSizeReached = function(filename) {
        this.isValid = false;
        this.uploadCnt.remove();

        var parentContainer = uiUploadInput.getParentContainer(id);
        var sizeExceededContainer = parentContainer.find('.sizeExceeded');
        var sizeExceededFileNames = filename;
        if(sizeExceededContainer.is(":visible")) {
          sizeExceededFileNames = sizeExceededContainer.find('b').text() + ", " + filename;
        }
        sizeExceededContainer.find('b').html(sizeExceededFileNames);
        sizeExceededContainer.show();
        sizeExceededContainer.delay(5000).fadeOut('slow');
      }

      this.setProgress = function(progress) {
        this.progressBar.css("width", progress + "%");
        this.progressBar.html(progress + " %");
      }
    },
    handleFileUpload : function(files, id) {
      var filesArray = files;
      if(Array.from) {
        var filesArray = Array.from(files);
      } else {
        filesArray = new Array(files.length);
        for (var i = 0; i < filesArray.length; i++) {
          filesArray[i] = files[i];
        }
      }

      filesArray.sort(function(file1, file2) {
        return file1.size - file2.size;
      });

      var limitCount = uiUploadInput.getData(id, "limitCount");

      var validStatuses = [];

      var parentContainer = uiUploadInput.getParentContainer(id);
      for (var i = 0; i < filesArray.length; i++) {
        var fileNames = parentContainer.find(".fileNameLabel");
        var isFileNameValid = true;
        var fileName = filesArray[i].name.trim();
        fileNames.each(function(index, fileNameElement) {
          var $fileNameElement = $(fileNameElement);
          if ($fileNameElement.length > 0 && $fileNameElement.data("name")) {
            var decoded = $('<div/>').html($fileNameElement.data("name")).text().trim();
            isFileNameValid = isFileNameValid && (decoded != fileName);
          }
        });

        if(!isFileNameValid) {
          var warningMsgBox = parentContainer.find('.fileAlreadyExists');
          var fileAlreadyExistsNames = filesArray[i].name;
          if(warningMsgBox.is(":visible")) {
            fileAlreadyExistsNames = warningMsgBox.find('b').text() + ", " + filesArray[i].name;
          }
          warningMsgBox.find('b').html(fileAlreadyExistsNames);

          warningMsgBox.show();
          warningMsgBox.delay(5000).fadeOut('slow');
          continue;
        }

        var fd = new FormData();
        fd.append('file', filesArray[i]);

        var status = new uiUploadInput.createStatusbar(id);
        if(status.isValid) {
          status.setFileNameSizeAndType(filesArray[i].name, filesArray[i].size, filesArray[i].type);
          if(status.isValid) {
            status.formData = fd;
            validStatuses.push(status);
          }
        }
      }

      for (var i = validStatuses.length - 1; i >= 0; i--) {
        var status = validStatuses[i];
        if(uiUploadInput.uploadingCount < uiUploadInput.maxUploadCount) {
          uiUploadInput.sendFileToServer(status);
        } else {
          uiUploadInput.uploadingFileQueue.push(status);
        }
      }

      uiUploadInput.onChange(id);

      parentContainer.find("input[type=file]").val('');
    }
  };

  return {
    UIUploadInput : uiUploadInput
  };
})(ecm_bootstrap, gj, base, wcm_utils, socialUiActivityComposer);
