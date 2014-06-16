(function($, uiMaskLayer) {
	/**
	 * initialize variables and methods when first load page first display a
	 * preview, hide save, cancel, png div then clone toolbar navigation to
	 * preview display
	 */
	var isChangeLogo = false;
	var fileUpload;
	var ajaxLoading = $("#AjaxLoadingMask.clearfix");
	if (!supportHTML5()) {
		$("#btUpload").remove();
		$("input#file").show();
	} else {
		$("#btUpload").on("click", function() {
			$("input#file").click();
		});
	}
	UpdatePreviewLogoAndStyle();
	$("#PlatformAdminToolbarContainer").clone().appendTo($("#StylePreview"));
	$('#StylePreview').bind({
		hover : function(e) {
			e.stopPropagation();
			e.preventDefault();
		},
		click : function(e) {
			e.stopPropagation();
			e.preventDefault();
		},
		blur : function(e) {
			e.stopPropagation();
			e.preventDefault();
		}
	});
	// when cancel is clicked, restore the old logo and display cancel messsage
	$("#cancel").on("click", function() {
		UpdatePreviewLogoAndStyle();
		cleanMessage();
		isChangeLogo = false;
		$("input#file").replaceWith($("input#file").val("").clone(true));
		$("#cancelinfo").show();
	});

	// when save is clicked, restore the new logo and display save messsage
	$("#save").on("click", function() {
		var span = $("#navigationStyle div span")[0];
		var maskLayer = uiMaskLayer.createTransparentMask();
		$("#navigationStyle").jzAjax({
			url : "BrandingController.save()",
			data : {
				"style" : $(span).attr("id"),
				"isChangeLogo" : isChangeLogo
			},
			beforeSend : function() {
				$("#saveinfo").hide();
				$(maskLayer).fadeTo(0, 0.3);
				maskLayer.style.backgroundColor = "black";
				ajaxLoading.show();
			},
			success : function(data) {
				cleanMessage();
				if (data.error == "0") {
					UpdateTopBarNavigation(data);
					$("#saveinfo").show();
				} else {
					UpdatePreviewLogoAndStyle();
					$("input#file").replaceWith($("input#file").val("").clone(true));
					$("#savenotok").show();
				}
				isChangeLogo = false;
				uiMaskLayer.removeMasks(maskLayer);
				ajaxLoading.hide();
			}
		});

	});

	// launch preview when a file is insert in input
	$("input#file").on("change", function() {
		if (supportHTML5()) {
			if (validate(this.files[0]) == false) {
				// clear the text in the input file field
				$("#saveinfo").hide();
				$("#cancelinfo").hide();
				$("#mustpng").show();
				return;
			}
		}
		uploadFile();
	});

	/* Change CSS by selecting */
	$("#navigationStyle").change(function() {
		var style = $("#navigationStyle").val();
		changePreviewStyle(style);
	});

	/**
	 * change preview style by adding new class in UIToolbarContainer which is
	 * concatenated a style selected
	 */
	function changePreviewStyle(style) {
		$("#StylePreview #UIToolbarContainer").attr('class',
				"UIContainer UIToolbarContainer  UIToolbarContainer" + style);
	}

	/**
	 * validate an png image returns true if png, otherwise return false
	 */
	function validate(file) {
		if (file.type == "image/png") {
			return true;
		} else {
			$("input#file").replaceWith($("input#file").val("").clone(true));
			return false;
		}
	}
	/**
	 * scale an image to fit with parent div
	 */
	// function scaleToFitPreviewImg(elt) {
	// $(elt).imgscale({
	// parent : '#PreviewImgDiv',
	// fade : 1000
	// });
	// }
	/**
	 * preview a logo and perform a scale
	 */
	function previewLogoFromUrl(logoUrl) {
		$('#PreviewImg').attr('src', logoUrl);
		$('#StylePreview #HomeLink img').attr('src', logoUrl);
		// scaleToFitPreviewImg($('#PreviewImg'));
	}

	/**
	 * update logo and style in preview toolbar, set the value to the selected
	 * markup
	 */
	function UpdatePreviewLogoAndStyle() {
		//var maskLayer = uiMaskLayer.createTransparentMask();
		$("#navigationStyle").jzAjax({
			url : "BrandingController.getResource()",
			beforeSend : function() {
			},
			success : function(data) {
				// update the logo url in preview zone and preview
				// navigation bar
				previewLogoFromUrl(data.logoUrl);
				// update the navigation style and style selected;
				changePreviewStyle(data.style);
				var span = $("#navigationStyle div span")[0];
				$(span).text($("#navigationStyle").find("a#" + data.style).text());
				$(span).attr("id",data.style);
			}
		});
	}
	/**
	 * Update new logo displays in top bar navigation
	 */

	function UpdateTopBarNavigation(data) {
		$("#PlatformAdminToolbarContainer .HomeLink img:first").attr('src',
				data.logoUrl);
		$("#PlatformAdminToolbarContainer #UIToolbarContainer:first")
				.removeAttr("class");
		$("#PlatformAdminToolbarContainer #UIToolbarContainer:first").addClass(
				"UIContainer UIToolbarContainer  UIToolbarContainer"
						+ data.style);
	}
	/**
	 * method executed when a drag event is launched
	 */
	function FileDragHover(e) {
		e.stopPropagation();
		e.preventDefault();
		var className=e.target.className;
		if(className.indexOf("hover")===-1) {
		className=className +" hover";
		e.target.className =className;
		}
	}

	/**
	 * method executed when a drop event is launched
	 */
	function FileDropHandle(e) {
		e.stopPropagation();
		e.preventDefault();
		var className=e.target.className;
		if(className.indexOf("hover")!=-1) {
		className=className.replace("hover","");
		e.target.className=className;
		}
		var files = e.target.files || e.dataTransfer.files;
		if (validate(files[0]) == false) {
			showMessageError();
			return;
		} else {
			fileUpload = files[0];
		}
		uploadFile();
	}

	function FileDragLeave(e){
	e.stopPropagation();
	e.preventDefault();
	var className=e.target.className;
	if(className.indexOf("hover")!=-1) {
		className=className.replace("hover","");
		e.target.className=className;
	}
	}

	/**
	 * make a XMLHttpRequest when dragndrop a file in PreviewImmDiv, it will
	 * send an image to JCR server with this protocol
	 */
	if (supportHTML5()) {
		var filedrag = document.getElementById("PreviewImgDiv");
		var xhr = new XMLHttpRequest();
		if (xhr.upload) {
			filedrag.addEventListener("dragover", FileDragHover, false);
			filedrag.addEventListener("dragleave", FileDragLeave, false);
			filedrag.addEventListener("drop", FileDropHandle, false);
			filedrag.style.display = "block";
		}
	}

	function uploadFile() {
		if (supportHTML5()) {
			// check validate
			$("#browser").val("html5");
			//fd = new FormData($("#form").get(0));
			var fd;
			try {
			    // other modern browsers
			    fd = new FormData($("#form").get(0));
			} catch(e) {
			    // IE10 MUST have all form items appended as individual form key / value pairs
			    fd = new FormData();			    
  			    fd.append("file",$("#file")[0].files[0]);
			    fd.append("browser",$("#browser").val());
			}	
		
			if (fileUpload) { //drag&drop
				fd.append("file", fileUpload);
			}

			$.ajax({
				type : "POST",
				url : $("#form").attr("action"),
				data : fd,
				beforeSend : function() {
					$("#PreviewImg").hide();
					$("#StylePreview #PlatformAdminToolbarContainer").hide();
					$("#ajaxUploading1").show();
					$("#ajaxUploading2").show();
				},
				dataType : "json",
				contentType : false,
				processData : false,
				success : function(data) {
					$("#ajaxUploading1").hide();
					$("#ajaxUploading2").hide();
					cleanMessage();
					fileUpload = null;
					$("input#file").replaceWith(
							$("input#file").val("").clone(true));
					previewLogoFromUrl(data.logoUrl);
					$("#StylePreview #PlatformAdminToolbarContainer").show();
					$("#PreviewImg").show();
					isChangeLogo = true;
				}
			});
			return false;
		} else {
			$("#browser").val("");
			$('#form').ajaxForm(
					{
						dataType : "text/html",
						success : function(data) {
							if (data == "false") {
								showMessageError();
								$("input#file").replaceWith(
										$("input#file").val("").clone(true));
							} else {
								previewLogoFromUrl(data);
								isChangeLogo = true;
							}
						}
					});
			$('#form').submit();
		}

	}
	/**
	 * verify if browser support html5
	 * 
	 * @returns
	 */

	function supportHTML5() {
		if (window.File && window.FileList && window.FileReader) {
			return true;
		}
		return false;
	}
	/**
	 * clean message
	 * 
	 * @returns
	 */

	function cleanMessage() {
		$("#savenotok").hide();
		$("#saveinfo").hide();
		$("#cancelinfo").hide();
		$("#mustpng").hide();
	}

	function showMessageError() {
		$("#saveinfo").hide();
		$("#savenotok").hide();
		$("#cancelinfo").hide();
		$("#mustpng").show();
	}

	$("#navigationStyle a").on("click", function() {
		var span = $("#navigationStyle div span")[0];
		$(span).text($(this).text());
		$(span).attr("id",$(this).attr("id"));
		changePreviewStyle($(this).attr("id"));
	})

})($, uiMaskLayer);
