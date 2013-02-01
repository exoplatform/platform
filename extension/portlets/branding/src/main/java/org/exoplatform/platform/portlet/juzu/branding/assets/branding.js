$(function() {
	var fileUpload;
	UpdatePreviewLogoAndStyle(true);
	// clone Toolbar Administrator and add to Preview
	$("#PlatformAdminToolbarContainer").clone().appendTo($("#StylePreview"));
	fixSearchInput();
	scaleToFitPreviewImg($('#PreviewImg'));
	$("#cancel").on(
			"click",
			function() {
				$("#ajaxUploading").show();
				UpdatePreviewLogoAndStyle(false);
				$("div#result").text(
						"Changes in branding settings have been cancelled");
			});
	$("#save").on("click", function() {
		$("#style").val(($('#navigationStyle option:selected').val()));
		var result = $('#form').submit();
		return result;
	});

	$("input#file").on("change", function() {
		previewLogoBycontent(this);
	});

	$('#form').submit(function() {
		$(this).ajaxSubmit({
			beforeSubmit : function(data) {
				if (validate($("input#file"))){
				$("#ajaxUploading").show();
				}
			},
			target : '#result',
			success : function(data) {
				UpdateTopBarNavigation(data);
				$("#ajaxUploading").hide();
				$("div#result").text(
				"Changes in branding settings have been saved");
			}
		});

		return false;
	});

//	$('.target').change(function() {
//		var valueSelected = ($('.target option:selected').val());
//	});

	/* Change CSS by selecting */
	$("#navigationStyle").change(function() {
		var style = $("#navigationStyle").val();
		changePreviewStyle(style);

	});

	function changePreviewStyle(style) {
		var idContainer = $("#StylePreview #UIToolbarContainer");
		idContainer.removeAttr("class");
		idContainer.addClass("UIToolbarContainer" + style + " UIContainer");
	}

	function fixSearchInput() {
		$(
				"#StylePreview #UIToolbarContainer #SearchNavigationTabsContainer input")
				.remove();
	}
	function previewLogoBycontent(input) {
		fileUpload = input;
		var checkValide = validate(input);
		if (checkValide == false) {
			// not validated
			$("div#result").text("the file must be in photo format png ");
			return;
		} else {
			// validated
			var reader = new FileReader();
			reader.onload = function(e) {
				previewPhoto(e.target.result);
			};
			reader.readAsDataURL(input.files[0]);
			$("div#result").text("");
		}
	}
	
	
	

	function validate(input) {
		if (input != null && input.files && input.files[0]) {
			var fileName = input.value;
			var extension=fileName.split('.').pop().toLowerCase();
//			var extension = fileName.substring(fileName.lastIndexOf('.') + 1)
//					.toLowerCase();
			if (extension == "png") {
			return true;
			}
		}
		$("#file").replaceWith($("#file").val("").clone(true));
		return false;
	}

	function scaleToFitPreviewImg(elt) {
		$(elt).imgscale({
			parent : '.non-immediate-parent-container',
			fade : 1000
		});
	}

	function previewPhoto(data) {
		$('#PreviewImg').attr('src', data);
		scaleToFitPreviewImg($('#PreviewImg'));
		$('#StylePreview #HomeLink img').attr('src', data).width(25).height(21);
	}

	function UpdatePreviewLogoAndStyle(firstTime) {
		$("#navigationStyle").jzAjax({
			
			url : "BrandingControler.getResource()",
			beforeSend: function(){
//				alert("beforesend");
				if(!firstTime) {
					$("#ajaxUploading").show();
				}
			},
			success : function(data) {
				// update the logo url in preview zone and preview navigation bar
				previewPhoto(data.logoUrl);
				// update the navigation style and style selected;
				changePreviewStyle(data.style);
				$("#navigationStyle").val(data.style).attr('selected', 'selected');
				$("#ajaxUploading").hide();
			}
		});
	}
	function UpdateTopBarNavigation(data) {
		$("#PlatformAdminToolbarContainer .HomeLink img:first").attr('src',
				data.logoUrl).width(25).height(21);
		$("#PlatformAdminToolbarContainer #UIToolbarContainer:first")
				.removeAttr("class");
		$("#PlatformAdminToolbarContainer #UIToolbarContainer:first").addClass(
				"UIToolbarContainer" + data.style + " UIContainer");
	}
});
