package org.exoplatform.setting.shared;

public class WizardUtility {

  /**
   * With an url, this method build a new url with a new parameter locale
   * 
   * @param url
   * @param locale
   * @return
   */
  public static String buildLocaleUrl(String url, String queryString, String oldLocale, String newLocale) {
    String newUrl = url;
    
    if(oldLocale == null || oldLocale.length() == 0) {
      if(queryString == null || queryString.length() == 0) {
        newUrl += "?";
      }
      else if(! queryString.contains("&") && ! queryString.equals("?")) {
        newUrl += "&";
      }
      newUrl += "locale=" + newLocale;
    }
    else {
      newUrl = newUrl.replaceAll("locale=" + oldLocale, "locale=" + newLocale);
    }
    
    return newUrl;
  }
}
