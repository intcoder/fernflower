package org.jetbrains.java.decompiler.main.decompiler;

import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ConsoleDecompilerStarterTest {

  @Test
  public void parseRemainder() {
    List<String> remainder = Arrays.asList("-test=1", "-t", "--test==qw-er=ty", "a");
    Map<String, Object> map = new LinkedHashMap<>();

    new ConsoleDecompilerStarter().parseRemainder(remainder, map);

    Map<String, Object> expected = new LinkedHashMap<>();
    expected.put("test", "1");
    expected.put("-test", "=qw-er=ty");

    assertEquals(expected, map);
  }
}