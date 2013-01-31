$(function() {

	var fileUpload;
	UpdateStyleSelect();

	// clone Toolbar Administrator and add to Preview
	$("#PlatformAdminToolbarContainer").clone().appendTo($("#StylePreview"));

	fixSearchInput();

	$("#cancel").on(
			"click",
			function() {
				UpdateStyleSelect();
				getAndUpdateLogoByAjax();
				$("div#result").text(
						"Changes in branding settings have been cancelled");
			});
	$("#save").on("click", function() {
		var valueSelected = ($('#navigationStyle option:selected').val());
		$("#style").val(valueSelected);
		var result = $('#form').submit();
		return result;
		// $('#result').jzLoad("BrandingControler.saveParameter()", {
		// "style" : valueSelected
		// });
	});

	$("input#file").on("change", function() {
		preview(this);
	});
	
	

	$('#form').submit(function() {
		$(this).ajaxSubmit({
			beforeSubmit: function(data){
				$("#ajaxUploading").show();
			},
			target : '#result',
			success: function(data){
				UpdateBarNavigation();
				$("#ajaxUploading").hide();
			}
		});
	
		return false;
	});

	$('.target').change(function() {
		var valueSelected = ($('.target option:selected').val());
	});

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
	function preview(input) {
		fileUpload = input;
		var checkValide = validate(input);
		if (checkValide == false) {
			// not validated
			$("div#result").text("the file must be in photo format png ");
			$("#file").replaceWith($("#file").val("").clone(true));
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
		var valide = false;
		if (input != null && input.files && input.files[0]) {
			var fileName = input.value;
			var extension = fileName.substring(fileName.lastIndexOf('.') + 1)
					.toLowerCase();
			if (extension == "png") {
				valide = true;
			}
		}
		return valide;
	}

	function previewPhoto(data) {
		$('#preview_image').attr('src', data);
		$('#StylePreview #HomeLink img').attr('src', data).width(25).height(21);
	}

	function getAndUpdateLogoByAjax() {
		$("#preview_image").jzAjax({
			url : "BrandingControler.getLogoUrlByAjax()",
			success : function(data) {
				previewPhoto(data);
			}
		});
	}

	function UpdateStyleSelect() {
		$("#navigationStyle").jzAjax({
			url : "BrandingControler.getStyleValue()",
			success : function(data) {
				changePreviewStyle(data);
				$("#navigationStyle").val(data).attr('selected', 'selected');
			}
		});
	}
	function UpdateBarNavigation() {
		$("#PlatformAdminToolbarContainer .HomeLink img").jzAjax(
				{
					url : "BrandingControler.getLogoUrlByAjax()",
					success : function(data) {
						$("#PlatformAdminToolbarContainer .HomeLink img:first")
								.attr('src', data).width(25).height(21);
					}
				});

		$("#PlatformAdminToolbarContainer #UIToolbarContainer")
				.jzAjax(
						{
							url : "BrandingControler.getStyleValue()",
							success : function(data) {
								$(
										"#PlatformAdminToolbarContainer #UIToolbarContainer:first")
										.removeAttr("class");
								$(
										"#PlatformAdminToolbarContainer #UIToolbarContainer:first")
										.addClass(
												"UIToolbarContainer" + data
														+ " UIContainer");
							}
						});
	}

});
