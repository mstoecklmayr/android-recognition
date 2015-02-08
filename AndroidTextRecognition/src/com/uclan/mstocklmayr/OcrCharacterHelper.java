/*
 * Copyright 2011 Robert Theis
 * Copyright 2015 Michael Stöcklmayr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uclan.mstocklmayr;

import android.content.SharedPreferences;

/**
 * Helper class to enable language-specific character blacklists/whitelists.
 */
public class OcrCharacterHelper {
  public static final String KEY_CHARACTER_BLACKLIST_ENGLISH = "preference_character_blacklist_english";
  public static final String KEY_CHARACTER_WHITELIST_ENGLISH = "preference_character_whitelist_english";

  private OcrCharacterHelper() {} // Private constructor to enforce noninstantiability

  public static String getDefaultBlacklist(String languageCode) {
    //final String DEFAULT_BLACKLIST = "`~|";
    
if (languageCode.equals("eng")) { return ""; } // English
    else {
      throw new IllegalArgumentException();
    }
  }
  
  public static String getDefaultWhitelist(String languageCode) {
if (languageCode.equals("eng")) { return "!?@#$%&*()<>_-+=/.,:;'\"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"; } // English
    else {
      throw new IllegalArgumentException();
    }
  }

  public static String getBlacklist(SharedPreferences prefs, String languageCode) {
    if (languageCode.equals("eng")) { return prefs.getString(KEY_CHARACTER_BLACKLIST_ENGLISH, getDefaultBlacklist(languageCode)); }
    else {
      throw new IllegalArgumentException();
    }    
  }
  
  public static String getWhitelist(SharedPreferences prefs, String languageCode) {
    if (languageCode.equals("eng")) { return prefs.getString(KEY_CHARACTER_WHITELIST_ENGLISH, getDefaultWhitelist(languageCode)); }
    else {
      throw new IllegalArgumentException();
    }        
  }

}
