package io.hpp.noosphere.scheduler.service.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static io.hpp.noosphere.scheduler.config.Constants.NULL_STRING;


public class CommonUtils {

  public static final List<String> KOREAN_LAST_NAMES = Arrays.asList("독고", "동방", "남궁", "등정", "망절", "무본", "사공", "서문", "선우", "어금", "제갈", "황목", "황보", "가", "간",
    "갈", "감", "강", "견", "경", "계", "고", "곡", "공", "곽", "관", "교", "구", "국", "궁", "궉", "권", "근", "금", "기", "길", "김", "나", "난", "남", "낭", "내", "노", "뇌", "다", "단",
    "담", "당", "대", "도", "독", "돈", "동", "두", "등", "라", "란", "랑", "려", "로", "뢰", "류", "리", "림", "마", "만", "매", "맹", "명", "모", "목", "묘", "무", "묵", "문", "미", "민",
    "박", "반", "방", "배", "백", "번", "범", "변", "보", "복", "봉", "부", "비", "빈", "빙", "사", "산", "삼", "상", "서", "석", "선", "설", "섭", "성", "소", "손", "송", "수", "순", "승",
    "시", "신", "심", "아", "안", "애", "야", "양", "어", "엄", "여", "연", "염", "엽", "영", "예", "오", "옥", "온", "옹", "완", "왕", "요", "용", "우", "운", "원", "위", "유", "육", "윤",
    "은", "음", "이", "인", "임", "자", "장", "전", "점", "정", "제", "조", "종", "좌", "주", "증", "지", "진", "차", "창", "채", "천", "초", "총", "최", "추", "탁", "탄", "탕", "태", "판",
    "팽", "편", "평", "포", "표", "풍", "피", "필", "하", "학", "한", "함", "해", "허", "현", "형", "호", "홍", "화", "황", "후");
  private static final Logger LOG = LoggerFactory.getLogger(CommonUtils.class);

  public static boolean isValid(String value) {
    return value != null && !value.isEmpty() && !NULL_STRING.equals(value.trim().toLowerCase());
  }


  public static boolean isUUID(String value) {
    if (CommonUtils.isValid(value)) {
      try {
        UUID uuid = UUID.fromString(value);
        return true;
      } catch (IllegalArgumentException exception) {
        // do nothing
      }
    }
    return false;
  }

  public static boolean isValid(UUID value) {
    return value != null && !value.toString().isEmpty() && !NULL_STRING.equals(
      value.toString().trim().toLowerCase());
  }

  public static String removeInvalidValues(String value) {
    if ("null".equals(value)) {
      return null;
    } else {
      return value;
    }
  }

  public static boolean isValid(Boolean value) {
    return value != null;
  }

  public static boolean isValid(Long value) {
    return value != null;
  }

  public static boolean isValid(Instant value) {
    return value != null;
  }

  public static boolean isValid(LocalDate value) {
    return value != null;
  }

  public static boolean isValid(Date value) {
    return value != null;
  }

  public static boolean isValid(Integer value) {
    return value != null;
  }

  public static boolean isValid(Double value) {
    return value != null;
  }

  public static boolean isValid(BigDecimal value) {
    return value != null;
  }

  public static boolean isValid(List<?> value) {
    return value != null && !value.isEmpty();
  }

  public static boolean isValid(Set<?> value) {
    return value != null && !value.isEmpty();
  }

  public static boolean isValid(Map<?, ?> value) {
    return value != null && !value.isEmpty();
  }

  public static String stripPhoneNumber(String value) {
    if (isValid(value)) {
      value = value.trim();
      value = value.replaceAll("-", "");
      value = value.replaceAll(" ", "");
      value = value.replaceAll("\\)", "");
      value = value.replaceAll("\\(", "");
    }
    return value;
  }

  public static String stripComma(String value) {
    if (isValid(value)) {
      value = value.trim();
      value = value.replaceAll(",", "");
    }
    return value;
  }


  public static boolean containsOnlyAlphabetsRegex(String str) {
    if (str == null || str.isEmpty()) {
      return false;
    }
    // Matches one or more uppercase or lowercase English letters
    return str.matches("[a-zA-Z]+");
  }

  public static String buildFullName(String langKey, String firstName, String lastName) {
    StringBuilder sb = new StringBuilder();
    if (CommonUtils.containsOnlyAlphabetsRegex(firstName) ||
      CommonUtils.containsOnlyAlphabetsRegex(lastName) ||
      Locale.ENGLISH.getLanguage().equals(langKey)) {
      if (CommonUtils.isValid(firstName)) {
        sb.append(firstName);
      }
      if (CommonUtils.isValid(lastName)) {
        if (CommonUtils.isValid(firstName)) {
          sb.append(" ");
        }
        sb.append(lastName);
      }
    } else if (KOREAN_LAST_NAMES.contains(lastName) || Locale.KOREAN.getLanguage().equals(langKey)) {
      if (CommonUtils.isValid(lastName)) {
        sb.append(lastName);
      }
      if (CommonUtils.isValid(firstName)) {
        sb.append(firstName);
      }
    } else if (Locale.CHINESE.getLanguage().equals(langKey)) {
      if (CommonUtils.isValid(firstName)) {
        sb.append(firstName);
      }
      if (CommonUtils.isValid(lastName)) {
        if (CommonUtils.isValid(firstName)) {
          sb.append(" ");
        }
        sb.append(lastName);
      }
    } else if (Locale.JAPANESE.getLanguage().equals(langKey)) {
      if (CommonUtils.isValid(lastName)) {
        sb.append(lastName);
      }
      if (CommonUtils.isValid(firstName)) {
        sb.append(firstName);
      }
    }
    return sb.isEmpty() ? null : sb.toString();
  }


  public static List<String> splitNames(String inputName) {
    List<String> returnValue = new ArrayList();
    boolean lastNameFound = false;
    String lastName = null;
    String firstName = null;

    for (int i = 0; i < KOREAN_LAST_NAMES.size(); ++i) {
      String temp = (String) KOREAN_LAST_NAMES.get(i);
      if (inputName.startsWith(temp)) {
        lastName = temp;
        firstName = inputName.substring(temp.length()).trim();
        lastNameFound = true;
        break;
      }
    }

    if (!lastNameFound) {
      int spaceIndex = inputName.indexOf(" ");
      if (spaceIndex > 0) {
        firstName = inputName.substring(0, spaceIndex).trim();
        lastName = inputName.substring(spaceIndex + 1).trim();
      } else {
        firstName = inputName;
        lastName = null;
      }
    }

    if (firstName != null && lastName != null) {
      returnValue.add(firstName);
      returnValue.add(lastName);
    } else if (firstName != null) {
      returnValue.add(firstName);
    }

    return returnValue;
  }

  public static String stripMobilePhoneNumber(String inputValue) {
    if (inputValue != null && !inputValue.isEmpty()) {
      String returnValue = inputValue.trim();
      if (returnValue.startsWith("+82")) {
        returnValue = "0" + returnValue.substring(3);
      }

      returnValue = returnValue.replaceAll("-", "");
      returnValue = returnValue.replaceAll("/", "");
      returnValue = returnValue.replaceAll("\\(", "");
      returnValue = returnValue.replaceAll("\\)", "");
      returnValue = returnValue.replaceAll(" ", "");
      return returnValue;
    } else {
      return null;
    }
  }


  protected static String buildAsterisk(int count) {
    StringBuilder asterisk = new StringBuilder();

    for (int i = 0; i < count; ++i) {
      asterisk.append("*");
    }

    return asterisk.toString();
  }

  protected static String maskValue(String inputValue) {
    if (inputValue == null) {
      return null;
    } else if (inputValue.length() >= 8) {
      String asterisk = buildAsterisk(inputValue.length() - 4);
      String returnValue = inputValue.substring(0, 2) + asterisk + inputValue.substring(inputValue.length() - 2);
      return returnValue;
    } else if (inputValue.length() > 2) {
      String asterisk = buildAsterisk(inputValue.length() - 2);
      return asterisk + inputValue.substring(inputValue.length() - 2);
    } else if (inputValue.length() == 2) {
      return "*" + inputValue.substring(1);
    } else {
      return inputValue.length() == 1 ? "*" : "";
    }
  }

  public static String maskEmail(String inputValue) {
    if (inputValue != null && !inputValue.isEmpty()) {
      StringBuilder returnValue = new StringBuilder(inputValue.trim());
      String[] splitList = returnValue.toString().split("@");
      if (splitList.length == 2) {
        returnValue = new StringBuilder(maskValue(splitList[0]) + "@");
        int lastIndex = splitList[1].lastIndexOf(".");
        if (lastIndex > -1) {
          returnValue.append(maskValue(splitList[1].substring(0, lastIndex)));
          returnValue.append(splitList[1].substring(lastIndex));
        } else {
          returnValue.append(maskValue(splitList[1]));
        }
      } else {
        returnValue = new StringBuilder();
      }

      return returnValue.toString();
    } else {
      return null;
    }
  }
}

