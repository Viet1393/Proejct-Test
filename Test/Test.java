package com.oisix.frw;

import com.oisix.frw.validator.EmailValidator;
import com.oisix.oisystemfr.util.Logger;
import java.text.CharacterIterator;
import java.text.DecimalFormat;
import java.text.StringCharacterIterator;
import java.util.Map;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;

public class CodeConverter {

  private static final boolean debug = true;
  private static final String enc = "JISAutoDetect";

  public static Properties halfToFullMap = new Properties();
  public static Properties fullToHalfMap = new Properties();
  public static Properties eMailMap = new Properties();
  public static Properties kanaMap = new Properties();
  public static Properties telMap = new Properties();
  public static Properties numMap = new Properties();
  public static Properties hyphenMap = new Properties();
  private static final String defaultHalfToFullMapFile = "halftofull.map";
  private static final String halfToFullMapProp = "oisix.halftofullmapfile";
  private static final String defaultFullToHalfMapFile = "fulltohalf.map";
  private static final String fullToHalfMapProp = "oisix.fulltohalfmapfile";
  private static final String defaultEMailMapFile = "emailconverter.map";
  private static final String eMailMapProp = "oisix.emailmapfile";
  private static final String defaultKanaMapFile = "kanaconverter.map";
  private static final String kanaMapProp = "oisix.kanamapfile";
  private static final String defaultTelMapFile = "telconverter.map";
  private static final String telMapProp = "oisix.telmapfile";
  private static final String defaultNumMapFile = "numconverter.map";
  private static final String numMapProp = "oisix.nummapfile";
  // add 11/02
  private static final String defaultHyphenMapFile = "hyphenconverter.map";
  private static final String hyphenMapProp = "oisix.hyphenmapfile";
  // add by Yamashita 2002/04/30
  // private static final DecimalFormat df =
  //  new DecimalFormat("###,###,###,##0");

  // add 11/02
  static {
    loadHalfToFullMap();
    loadFullToHalfMap();
    loadEMailMap();
    loadKanaMap();
    loadTelMap();
    loadNumMap();
    // add 11/02
    loadHyphenMap();
    // add 11/02
  }

  public static boolean contains(Map map, String target) {
    boolean found = false;
    StringCharacterIterator iter = new StringCharacterIterator(target);
    StringBuffer word = new StringBuffer(1);
    for (char c = iter.first(); c != CharacterIterator.DONE; c = iter.next()) {
      word.delete(0, 1);
      word.append(c);
      if (found = (null != map.get(word.toString()))) break;
    }
    return found;
  }

  public static String halfToFull(Map map, String half, boolean ignore) {
    StringBuffer converted = new StringBuffer(half.length());
    StringCharacterIterator iter = new StringCharacterIterator(half);
    String found = null;
    StringBuffer word = new StringBuffer(2);
    for (char c = iter.first(); c != CharacterIterator.DONE; c = iter.next()) {
      word.delete(0, 2);
      word.append(c);

      if (null != (found = (String) map.get(word.toString()))) {
        if (c != CharacterIterator.DONE) {
          c = iter.next(); // checking for sound marks
          word.append(c);
          String compound = (String) map.get(word.toString());
          if (null != compound) found = compound;
          else iter.previous(); // push
        }
        converted.append(found);
      } else {
        if (!ignore) return null;
        converted.append(c);
      }
    }
    return converted.toString();
  }

  public static String fullToHalf(Map map, String full, boolean ignore) {
    StringBuffer converted = new StringBuffer(full.length());
    StringCharacterIterator iter = new StringCharacterIterator(full);
    String found = null;
    StringBuffer word = new StringBuffer(1);
    for (char c = iter.first(); c != CharacterIterator.DONE; c = iter.next()) {
      word.delete(0, 1);
      word.append(c);
      if (null != (found = (String) map.get(word.toString()))) {
        converted.append(found);
      } else {
        if (!ignore) return null;
        converted.append(c);
      }
    }
    return converted.toString();
  }

  public static boolean containsHalf(String half) {
    return contains(halfToFullMap, half);
  }

  public static String halfToFull(String half) {
    return halfToFull(halfToFullMap, half, true);
  }

  public static boolean containsFull(String full) {
    return contains(fullToHalfMap, full);
  }

  public static String fullToHalf(String full) {
    return fullToHalf(fullToHalfMap, full, true);
  }

  private static void loadHalfToFullMap() {
    PropertyLoader loader = new PropertyLoader(halfToFullMap, defaultHalfToFullMapFile);
    loader.setFileNameFromSystemProperty(halfToFullMapProp);
    loader.load();
  }

  private static void loadFullToHalfMap() {
    PropertyLoader loader = new PropertyLoader(fullToHalfMap, defaultFullToHalfMapFile);
    loader.setFileNameFromSystemProperty(fullToHalfMapProp);
    loader.load();
  }

  private static void loadEMailMap() {
    PropertyLoader loader = new PropertyLoader(eMailMap, defaultEMailMapFile);
    loader.setFileNameFromSystemProperty(eMailMapProp);
    loader.load();
  }

  private static void loadKanaMap() {
    PropertyLoader loader = new PropertyLoader(kanaMap, defaultKanaMapFile);
    loader.setFileNameFromSystemProperty(kanaMapProp);
    loader.load();
  }

  private static void loadTelMap() {
    PropertyLoader loader = new PropertyLoader(telMap, defaultTelMapFile);
    loader.setFileNameFromSystemProperty(telMapProp);
    loader.load();
  }

  private static void loadNumMap() {
    PropertyLoader loader = new PropertyLoader(numMap, defaultNumMapFile);
    loader.setFileNameFromSystemProperty(numMapProp);
    loader.load();
  }

  // add 11/02
  private static void loadHyphenMap() {
    PropertyLoader loader = new PropertyLoader(hyphenMap, defaultHyphenMapFile);
    loader.setFileNameFromSystemProperty(hyphenMapProp);
    loader.load();
  }
  // end add 11/02

  public static String convertEMail(String target) {
    Properties prop = eMailMap;
    if (!StringUtils.isEmpty(target) && EmailValidator.isEmailPlusPermittedDomain(target)) {
      prop = (Properties) eMailMap.clone();
      prop.put("+", "+");
    }
    String rc = fullToHalf(prop, target, false);
    if (null != rc) {
      int index = rc.indexOf("@");
      int dotIndex = rc.lastIndexOf(".");
      int count = countChar(rc, '@');
      if ((count != 1) || (index <= 0) || (dotIndex < index + 2) || (dotIndex == rc.length() - 1))
        rc = null;
    }
    return rc;
  }

  public static String convertKana(String target) {
    String rc = halfToFull(kanaMap, target, false);
    if (null != rc) {
      rc = shrinkContinualChar(rc, '\u0020');
      if (spaceOnly(rc)) rc = null;
    }
    return rc;
  }

  public static String convertZip(String target) {
    String rc = fullToHalfNumber(target);
    if ((null != rc) && (rc.length() != 7)) rc = null;
    return rc;
  }

  // add by Yamashita 2001/10/18
  public static String convertZipWithHyphen(String target) {
    String rc = shrinkHyphen(target);
    if (rc == null) {
      return null;
    }
    rc = fullToHalfNumber(rc);
    if ((null != rc) && (rc.length() != 7)) rc = null;
    return rc;
  }

  public static String convertTel(String target) {
    String rc = fullToHalf(telMap, target, false);
    if (null != rc) {
      int count = countChar(rc, '-');
      int length = rc.length() - count;
      // if (((length != 10) && (length != 11)) ||
      if (((length != 10) && (length != 11) && (length != 9)) || (rc.charAt(0) != '0')) rc = null;
    }
    return rc;
  }

  public static String convertFax(String target) {
    String rc = fullToHalf(telMap, target, false);
    if (null != rc) {
      int count = countChar(rc, '-');
      int length = rc.length() - count;
      if ((length != 10) || (rc.charAt(0) != '0')) rc = null;
    }
    return rc;
  }

  // add 10/12
  public static String convertCardBangou(String target) {
    // String rc = fullToHalfNumber(target);
    // if (rc == null) {
    //    return rc;
    // }
    // カードデジットのチェック
    // FIXXXXXXXXXXX OPEN後追加！
    /*
    //if (rc.length() != 13 || rc.length() != 16) {
    //    rc = null;
    //}
    */
    String rc = shrinkHyphen(target);
    if (rc == null) {
      return null;
    }
    rc = fullToHalfNumber(rc);
    if ((null != rc) && (rc.length() != 16)) rc = null;
    return rc;
  }

  // add 11/02
  public static String convertHyphen(String target) {
    String rc = fullToHalfHyphen(target);
    if (rc == null) {
      return rc;
      // 上のコードにあわせてみたが、意味は不明
    }
    return rc;
  }

  public static String fullToHalfHyphen(String full) {
    StringBuffer converted = new StringBuffer(full.length());
    StringCharacterIterator iter = new StringCharacterIterator(full);
    String found = null;
    StringBuffer word = new StringBuffer(2);
    for (char c = iter.first(); c != CharacterIterator.DONE; c = iter.next()) {
      word.delete(0, 2);
      word.append(c);
      if (null != (found = (String) hyphenMap.get(word.toString()))) {
        converted.append(found);
      } else {
        converted.append(c);
      }
    }
    return converted.toString();
  }

  // add by Yamashita 2001/10/18
  public static String shrinkHyphen(String full) {
    StringBuffer converted = new StringBuffer(full.length());
    StringCharacterIterator iter = new StringCharacterIterator(full);
    String found = null;
    StringBuffer word = new StringBuffer(2);
    for (char c = iter.first(); c != CharacterIterator.DONE; c = iter.next()) {
      word.delete(0, 2);
      word.append(c);
      if (null == (found = (String) hyphenMap.get(word.toString()))) {
        if (c != '-') {
          converted.append(c);
        }
      }
    }
    return converted.toString();
  }

  private static final String spaces = "\u0020\u3000";

  public static String shrinkSpaces(String target, char convertedSpace) {
    StringBuffer rc = new StringBuffer(target.length());
    StringCharacterIterator iter = new StringCharacterIterator(target);
    boolean found = false;
    for (char t = iter.first(); t != CharacterIterator.DONE; t = iter.next()) {
      if (-1 != spaces.indexOf(t)) {
        if (found) continue;
        found = true;
        t = convertedSpace;
      } else found = false;
      rc.append(t);
    }
    return rc.toString();
  }

  public static boolean spaceOnly(String target) {
    boolean rc = true;
    StringCharacterIterator iter = new StringCharacterIterator(target);
    for (char t = iter.first(); t != CharacterIterator.DONE; t = iter.next()) {
      if (-1 == spaces.indexOf(t)) {
        rc = false;
        break;
      }
    }
    return rc;
  }

  public static String fullToHalfNumber(String target) {
    String rc = fullToHalf(numMap, target, false);
    if ((null != rc) && (rc.length() == 0)) rc = null;
    return rc;
  }

  public static String shrinkContinualChar(String target, char c) {
    StringBuffer rc = new StringBuffer(target.length());
    StringCharacterIterator iter = new StringCharacterIterator(target);
    boolean found = false;
    for (char t = iter.first(); t != CharacterIterator.DONE; t = iter.next()) {
      if (t == c) {
        if (found) continue;
        found = true;
      } else found = false;
      rc.append(t);
    }
    return rc.toString();
  }

  public static int countChar(String target, char c) {
    int count = 0;
    StringCharacterIterator iter = new StringCharacterIterator(target);
    for (char t = iter.first(); t != CharacterIterator.DONE; t = iter.next()) if (c == t) count++;
    return count;
  }

  public static int parseInt(String value, int defaultValue) {
    int rc = defaultValue;
    if (value == null) {
      return rc;
    }
    try {
      rc = Integer.parseInt(value.trim());
    } catch (java.lang.NumberFormatException jlnfe) {
      /* FIXME */
    }
    return rc;
  }

  public static int parseInt(String value) {
    return parseInt(value, 0);
  }

  public static float parseFloat(String value, float defaultValue) {
    float rc = defaultValue;
    try {
      rc = Float.parseFloat(value.trim());
    } catch (java.lang.NumberFormatException jlnfe) {
      /* FIXME */
    }
    return rc;
  }

  public static float parseFloat(String value) {
    return parseFloat(value, 0.0f);
  }

  public static HttpServletRequest encodeRequest(HttpServletRequest request) {
    /*
    ServletRequestImpl impl = (ServletRequestImpl)request;
    QueryParams params = impl.getQueryParams();
    Iterator iter = params.keySet().iterator();
    QueryParams new_params = new QueryParams();
    while (iter.hasNext()) {
        String key = (String)iter.next();
        String[] vals = params.getValues(key);
        try {
            key = new String(key.getBytes("8859_1"), enc);
        }
        catch (UnsupportedEncodingException ex) {
            log("Exception: Unsupported encoding: " + enc);
        }
        for (int i = 0; i < vals.length; i++) {
            String val = vals[i];
            try {
                val = new String(val.getBytes("8859_1"), enc);
            }
            catch (UnsupportedEncodingException ex) {
                log("Exception: Unsupported encoding: " + enc);
            }
            new_params.put(key, val);
        }
    }
    impl.setQueryParams(new_params);
    */
    return request;
  }

  public static void main(String[] args) {
	System.out.print("Convert start");
    // Map map = halfToFullMap;
    Map map = fullToHalfMap;
    java.util.Enumeration iter = ((Properties) map).keys();
    while (iter.hasMoreElements()) {
      String key = (String) iter.nextElement();
      String val = (String) map.get(key);
    }
    byte[] buffer = new byte[1024];
    try {
      int size = System.in.read(buffer);
      String input = new String(buffer, 0, size - 2);
      String temp = fullToHalf(input);
      temp = halfToFull(input);
      temp = convertEMail(input);
      temp = convertKana(input);
      temp = convertTel(input);
      temp = fullToHalfNumber(input);
      temp = convertZipWithHyphen(input);
    } catch (Exception ex) {
      Logger.warn(CodeConverter.class, ex);
    }

    System.out.print("Commit first");
    System.out.print("Commit middle");
    System.out.print("Commit last");

    System.out.print("Add hello world!!!!");
    System.out.print("Add new test");

    System.out.print("Add Test commit");
    System.out.print("Add new commit");

	System.out.print("Convert end");
  }

  public static String encodeHTML(String str) {
    if (null != str) {
      ReplaceableString rc = new ReplaceableString(str);
      rc.replace("&", "&amp;");
      rc.replace("<", "&lt;");
      rc.replace(">", "&gt;");
      rc.replace("\"", "&quot;");
      return rc.toString();
    } else {
      return null;
    }
  }

  public static String formatKingaku(int kingaku) {
    DecimalFormat df = new DecimalFormat("###,###,###,##0");
    return df.format(kingaku);
  }

  /*
   * This class provide static methods, so you can't create the instance
   */
  private CodeConverter() {}

  protected static void log(String message) {
    if (debug) Logger.info("OISIX DEBUG: " + message);
  }

  protected static void log(Exception ex) {
    if (debug) Logger.warn(CodeConverter.class, ex);
  }
}
