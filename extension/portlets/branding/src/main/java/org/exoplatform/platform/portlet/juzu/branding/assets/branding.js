$(function() {
	
	/**
	 * initialize variables and methods when first load page
	 * first display a preview, hide save, cancel, png div
	 * then clone toolbar navigation to preview display
	 */
	
	var fileUpload;
	var backupParams;
	UpdatePreviewLogoAndStyle();
	$("#saveinfo").hide();
	$("#cancelinfo").hide();
	$("#mustpng").hide();
	$("#PlatformAdminToolbarContainer").clone().appendTo($("#StylePreview"));
	
	//when cancel is clicked, restore the old logo and display cancel messsage
	$("#cancel").on("click", function() {
		restorePreviewLogoAndStyle();
		$("input#file").replaceWith($("input#file").val("").clone(true));
		$("#saveinfo").hide();
		$("#mustpng").hide();
		$("#cancelinfo").show();
	});
	
	//when save is clicked, restore the new logo and display save messsage	
	$("#save").on("click", function() {
		$("#style").val(($('#navigationStyle option:selected').val()));
		$('#form').submit();
		$("#saveinfo").show();
		$("#cancelinfo").hide();
		$("#mustpng").hide();
	});

	//launch preview when a file is insert in input
	$("input#file").on("change", function() {
		previewLogoFromFile(this.files[0]);
	});
	
	/*
	 * Submit the form, the content file and navigation file will be sent to server, 
	 * the content file is recovered from the input file or event trigger dropped on the div 
	 */
	$('#form').submit(function() {
		fd = new FormData($("#form").get(0));
		if (fileUpload) {
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
				// backupParams will be used for cancel the change
				backupParams.logoUrl=backupParams.logoUrl+ Math.random();
				backupParams.style=data.style;
				$("#ajaxUploading1").hide();
				$("#ajaxUploading2").hide();
				$("#PreviewImg").show();
				$("#StylePreview #PlatformAdminToolbarContainer").show();
				UpdateTopBarNavigation(data);
				fileUpload == null;
				$("#saveinfo").show();
				$("#cancelinfo").hide();
				$("#mustpng").hide();
			}
		});
		return false;
	});

	/* Change CSS by selecting */
	$("#navigationStyle").change(function() {
		var style = $("#navigationStyle").val();
		changePreviewStyle(style);
	});

	/**
	 * change preview style by adding new class in UIToolbarContainer which is concatenated a style selected
	 */
	function changePreviewStyle(style) {
		$("#StylePreview #UIToolbarContainer").attr('class', "UIToolbarContainer"+ style +" UIContainer");
	}
	
	/**
	 * preview a logo, display a message if not png file, otherwise display this image file
	 */
	function previewLogoFromFile(file) {
		var checkValide = validate(file);
		if (checkValide == false) {
			//clear the text in the input file field
			$("input#file").replaceWith($("input#file").val("").clone(true));
			$("#saveinfo").hide();
			$("#cancelinfo").hide();
			$("#mustpng").show();
			return;
		} else {
			fileUpload = file;
			var reader = new FileReader();
			reader.onload = function(e) {
				previewLogoFromUrl(e.target.result);
			};
			reader.readAsDataURL(file);
		}
	}

	/**
	 * validate an png image
	 * returns true if png, otherwise return false
	 */
	function validate(file) {
		if (file.type == "image/png") {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * scale an image to fit with parent div
	 */
	function scaleToFitPreviewImg(elt) {
		$(elt).imgscale({
			parent : '#PreviewImgDiv',
			fade : 1000
		});
	}

	/**
	 * preview a logo and perform a scale
	 */
	function previewLogoFromUrl(logoUrl) {
		scaleToFitPreviewImg($('#PreviewImg'));
		$('#PreviewImg').attr('src', logoUrl);
		$('#StylePreview #HomeLink img').attr('src', logoUrl);
	}

	/**
	 * restore a logo in preview when cancel is clicked
	 */
	function restorePreviewLogoAndStyle() {
						previewLogoFromUrl(backupParams.logoUrl);
						changePreviewStyle(backupParams.style);
						$("#navigationStyle").val(backupParams.style).attr('selected',
								'selected');
	}
	
	/**
	 * update logo and style in preview toolbar, set the value to the selected markup
	 */
	function UpdatePreviewLogoAndStyle() {
		$("#navigationStyle").jzAjax(
				{
					url : "BrandingController.getResource()",
					beforeSend : function() {
					},
					success : function(data) {
						backupParams=data;
						// update the logo url in preview zone and preview
						// navigation bar
						previewLogoFromUrl(data.logoUrl);
						// update the navigation style and style selected;
						changePreviewStyle(data.style);
						$("#navigationStyle").val(data.style).attr('selected',
								'selected');
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
				"UIToolbarContainer" + data.style + " UIContainer");
	}

	/**
	 * method executed when a drag event is launched
	 */
	function FileDragHover(e) {
		e.stopPropagation();
		e.preventDefault();
		e.target.className = (e.type == "dragover" ? "hover" : "");
	}

	/**
	 * method executed when a drop event is launched
	 */
	function FileDropHandle(e) {
		e.stopPropagation();
		e.preventDefault();
		e.target.className = "";
		var files = e.target.files || e.dataTransfer.files;
		previewLogoFromFile(files[0]);
	}

	/**
	 * make a XMLHttpRequest when dragndrop a file in PreviewImmDiv, it will send an image to JCR server with this protocol
	 */
	if (window.File && window.FileList && window.FileReader) {
		var filedrag = document.getElementById("PreviewImgDiv");
		var xhr = new XMLHttpRequest();
		if (xhr.upload) {
			filedrag.addEventListener("dragover", FileDragHover, false);
			filedrag.addEventListener("dragleave", FileDragHover, false);
			filedrag.addEventListener("drop", FileDropHandle, false);
			filedrag.style.display = "block";
		}
	}

});
