package daggerok;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JavaClass {

  public static String nevermind(final String input) {

    log.info("java method execution using input: {}", input);

    if (null == input || input.trim().length() < 1) return "empty";
    return new StringBuilder(input).reverse().toString();
  }
}
