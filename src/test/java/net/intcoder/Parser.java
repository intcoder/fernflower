package net.intcoder;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

  @Test
  public void name() throws Exception {
    Path preferencesPath = Paths.get("src/main/java/org/jetbrains/java/decompiler/main/extern/IFernflowerPreferences.java");
    String preferencesContent = new String(Files.readAllBytes(preferencesPath));

    Path readmePath = Paths.get("src/test/resources/net/intcoder/descriptions.txt" );
    String readmeContent = new String(Files.readAllBytes(readmePath));

    Map<String, String> fields = getFields(preferencesContent);
    Map<String, String> defaults = getDefaults(preferencesContent);
    Map<String, String> descriptions = getDescriptions(Files.readAllLines(readmePath));

    for (String field : fields.keySet()) {
      String camelCaseFieldName = CaseUtils.toCamelCase(field, false, '_');
      String name = fields.get(field);
      String defaultVal = defaults.get(field);
      String description = descriptions.get(name);

//      System.out.println("Field: " + field);
//      System.out.println("Camel case field name: " + camelCaseFieldName);
//      System.out.println("Name: " + name);
//      System.out.println("Default: " + defaultVal);
//      System.out.println("Description: " + description);

      //System.out.println(field + " " + name + " " + defaultVal + " " + description);

      if (description != null) {
        System.out.println(createOptionAnnotationAndField(camelCaseFieldName, name, defaultVal, description));

        System.out.println();
      }
    }
  }

  String createOptionAnnotationAndField(String fieldName, String optionName, String defaultVal, String description) {
    // @Option(names = "-rbr", description = "hide bridge methods")

    optionName = optionName.substring(1);
    optionName = "\"" + "-" + optionName;

    description = description + " (default: " + defaultVal + ")";

    StringBuilder sb = new StringBuilder();

    sb.append("@Option(names = ").append(optionName).append(", ").append("paramLabel = \"<value>\", ");
    sb.append("description = \"").append(description.replaceAll("\"", "\\\\\"")).append("\")");
    sb.append("\n");

    //sb.append("private String ").append(fieldName).append(";");
    sb.append("String ").append(fieldName).append(";");

    return sb.toString();
  }


  Map<String, String> getFields(String content) {
    String parameterRegex = "String .* = \".*\";";
    Pattern pattern = Pattern.compile(parameterRegex);
    Matcher matcher = pattern.matcher(content);

    Map<String, String> map = new LinkedHashMap<>();

    while (matcher.find()) {
      String s = matcher.group();

      String name = StringUtils.substringAfter(s.split(" = ")[0], " ");
      String val = StringUtils.substringBefore(s.split(" = ")[1], ";");

      map.put(name, val);
    }

    return map;
  }


  Map<String, String> getDefaults(String content) {
    String parameterRegex = "defaults.put\\(.*, .*\\);";
    Pattern pattern = Pattern.compile(parameterRegex);
    Matcher matcher = pattern.matcher(content);

    Map<String, String> map = new LinkedHashMap<>();

    while (matcher.find()) {
      String s = matcher.group();
      s = StringUtils.substringAfter(s, "defaults.put(");
      s = StringUtils.substringBefore(s, ");");

      String key = s.split(", ")[0];
      String val = s.split(", ")[1];

      map.put(key, val);
    }

    return map;
  }

  Map<String, String> getDescriptions(List<String> lines) {
    Map<String, String> map = new LinkedHashMap<>();

    for (String line : lines) {
      String key;
      if (line.contains("): ")) {
        key = StringUtils.substringBefore(line, " (");
      } else {
        key = StringUtils.substringBefore(line, ":");
      }

      key = StringUtils.substringAfter(key, "- ");
      key = "\"" + key + "\"";

      String description = StringUtils.substringAfter(line, ": ");

      map.put(key, description);
    }

    return map;
  }
}
