function load_i18n(page, lang)
{
  var i18n_dict = { };
  var url = page + '_' + lang + ".json";
  
  $.ajax(
    {
        url: url,
        method: 'GET',
        async: false
    }).done(function (jsonData)
    {          
        i18n_dict = jsonData;
    }).fail(function (xhr, ajaxOptions, thrownError)
    {
        alert('there was an error loading this page: '
              + thrownError);
    });
 
  $.i18n.load(i18n_dict); 
      
}
