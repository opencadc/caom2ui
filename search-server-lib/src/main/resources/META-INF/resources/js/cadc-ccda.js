function translateFields(locale)
{
  var $advancedSearchLink = $("a#as_link");

  $advancedSearchLink.attr("href", $.i18n("search_uri"));
  $advancedSearchLink.text($.i18n("as_search_label"));

  var $quickSearchForm = $("form#quickSearchForm");
  $quickSearchForm.find("#search_input_label").text(
      $.i18n("search_input_label"));
  $quickSearchForm.attr("action", $.i18n("search_uri"));
  $quickSearchForm.find("#search_input").attr("title", $.i18n("search_input_title"))
      .attr("placeholder", $.i18n("search_input_label"));
  $quickSearchForm.find("#search_box_submit").val($.i18n("search_button_label"));

	$("a.thumbnail_link").each(function()
	{
		var $link = $(this);
		var archiveRoleName = $link.data("role");

    // Allow setting links statically.
    if (archiveRoleName)
    {
      var thumbnailLinkPathKey = "thumbnail_link_path_" + archiveRoleName;
      var thumbnailLinkTextKey = "thumbnail_link_text_" + archiveRoleName;
      var thumbnailLinkTitleKey = "thumbnail_link_title_" + archiveRoleName;
      var thumbnailLinkPath = $.i18n(thumbnailLinkPathKey);
      var thumbnailLinkText = $.i18n(thumbnailLinkTextKey);
      var thumbnailLinkTitle = $.i18n(thumbnailLinkTitleKey);

      if (thumbnailLinkText && (thumbnailLinkText != thumbnailLinkTextKey))
      {
        $link.find(".thumbnail_link_text").text(thumbnailLinkText);
      }

      if (thumbnailLinkPath && (thumbnailLinkPath != thumbnailLinkPathKey))
      {
        $link.attr("href", "/" + locale + "/" + thumbnailLinkPath);
      }

      if (thumbnailLinkTitleKey && (thumbnailLinkTitle != thumbnailLinkTitleKey))
      {
        $link.attr("title", thumbnailLinkTitle);
      }
    }
	});

  $("div.category").each(
      function()
      {
        var $self = $(this);
        $self.find(".category_title span").i18n();
      });

  // Login page
  $("#success_message_container").i18n();
}

