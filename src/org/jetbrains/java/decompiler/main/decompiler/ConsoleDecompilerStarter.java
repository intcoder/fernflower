package org.jetbrains.java.decompiler.main.decompiler;

import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine;
import picocli.CommandLine.*;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.Callable;

@Command(name = "java -jar fernflower.jar", mixinStandardHelpOptions = true, /*version = "",*/
  description = "Fernflower is the first actually working analytical decompiler for Java and " +
    " probably for a high-level programming language in general")
public class ConsoleDecompilerStarter implements Callable<Integer> {

  @Unmatched
  private List<String> remainder = new ArrayList<>();

  @ArgGroup(exclusive = false)
  private DecompileOptions decompileOptions = new DecompileOptions();

  @Option(names = "-e", paramLabel = "<library>",description = "Sources prefixed with -e= mean \"library\" files that won't be decompiled," +
    " but taken into account when analysing relationships between classes or methods." +
    " Especially renaming of identifiers (s. option 'ren') can benefit from information about external classes.")
  private List<File> libraries = new ArrayList<>();

  @Option(names = {"-d", "--destination"}, paramLabel = "<path>", description = "destination directory")
  private File destination;

  @Parameters(description = "Files or directories with files to be decompiled." +
    " Directories are recursively scanned. Allowed file extensions are class, zip and jar.")
  private List<File> sources;

  @Override
  public Integer call() throws Exception {

    if (!destination.isDirectory()) {
      System.out.println("error: destination '" + destination + "' is not a directory");
      return 1;
    }

    Map<String, Object> mapOptions = new HashMap<>();
    Arrays.stream(DecompileOptions.class.getDeclaredFields())
      .filter(f -> f.isAnnotationPresent(Option.class))
      .forEach(f -> {
        Option annotation = f.getAnnotation(Option.class);
        String name = annotation.names()[0].substring(1);
        String value = getFieldValue(f);

        if (value != null)
          mapOptions.put(name, value);
      });

    parseRemainder(remainder, mapOptions);

    ConsoleDecompiler.start(mapOptions, sources, libraries, destination);

    return 0;
  }

  private String getFieldValue(Field f) {
    try {
      return (String) f.get(decompileOptions);
    } catch (IllegalAccessException e) {
      throw new RuntimeException();
    }
  }

  protected void parseRemainder(Collection<String> remainder, Map<String, Object> mapOptions) {
    for (String option : remainder) {
      if (option.matches("-.*=.*")) {
        String key = StringUtils.substringBefore(option.substring(1), "=");
        String value = StringUtils.substringAfter(option.substring(1), "=");

        mapOptions.put(key, value);
      }
    }
  }

  public static void main(String[] args) {
    int exitCode = new CommandLine(new ConsoleDecompilerStarter()).execute(args);
    System.exit(exitCode);
  }

  private static class DecompileOptions {
    @Option(names = "-rbr", paramLabel = "<value>", description = "hide bridge methods (default: \"1\")")
    String removeBridge;

    @Option(names = "-rsy", paramLabel = "<value>", description = "hide synthetic class members (default: \"0\")")
    String removeSynthetic;

    @Option(names = "-din", paramLabel = "<value>", description = "decompile inner classes (default: \"1\")")
    String decompileInner;

    @Option(names = "-dc4", paramLabel = "<value>", description = "collapse 1.4 class references (default: \"1\")")
    String decompileClass14;

    @Option(names = "-das", paramLabel = "<value>", description = "decompile assertions (default: \"1\")")
    String decompileAssertions;

    @Option(names = "-hes", paramLabel = "<value>", description = "hide empty super invocation (default: \"1\")")
    String hideEmptySuper;

    @Option(names = "-hdc", paramLabel = "<value>", description = "hide empty default constructor (default: \"1\")")
    String hideDefaultConstructor;

    @Option(names = "-dgs", paramLabel = "<value>", description = "decompile generic signatures (default: \"0\")")
    String decompileGenericSignatures;

    @Option(names = "-ner", paramLabel = "<value>", description = "assume return not throwing exceptions (default: \"1\")")
    String noExceptionsReturn;

    @Option(names = "-den", paramLabel = "<value>", description = "decompile enumerations (default: \"1\")")
    String decompileEnum;

    @Option(names = "-rgn", paramLabel = "<value>", description = "remove getClass() invocation, when it is part of a qualified new statement (default: \"1\")")
    String removeGetClassNew;

    @Option(names = "-lit", paramLabel = "<value>", description = "output numeric literals \"as-is\" (default: \"0\")")
    String literalsAsIs;

    @Option(names = "-bto", paramLabel = "<value>", description = "interpret int 1 as boolean true (workaround to a compiler bug) (default: \"1\")")
    String booleanTrueOne;

    @Option(names = "-asc", paramLabel = "<value>", description = "encode non-ASCII characters in string and character literals as Unicode escapes (default: \"0\")")
    String asciiStringCharacters;

    @Option(names = "-nns", paramLabel = "<value>", description = "allow for not set synthetic attribute (workaround to a compiler bug) (default: \"0\")")
    String syntheticNotSet;

    @Option(names = "-uto", paramLabel = "<value>", description = "consider nameless types as java.lang.Object (workaround to a compiler architecture flaw) (default: \"1\")")
    String undefinedParamTypeObject;

    @Option(names = "-udv", paramLabel = "<value>", description = "reconstruct variable names from debug information, if present (default: \"1\")")
    String useDebugVarNames;

    @Option(names = "-rer", paramLabel = "<value>", description = "remove empty exception ranges (default: \"1\")")
    String removeEmptyRanges;

    @Option(names = "-fdi", paramLabel = "<value>", description = "de-inline finally structures (default: \"1\")")
    String finallyDeinline;

    @Option(names = "-inn", paramLabel = "<value>", description = "check for IntelliJ IDEA-specific @NotNull annotation and remove inserted code if found (default: \"1\")")
    String ideaNotNullAnnotation;

    @Option(names = "-lac", paramLabel = "<value>", description = "decompile lambda expressions to anonymous classes (default: \"0\")")
    String lambdaToAnonymousClass;

    @Option(names = "-log", paramLabel = "<value>", description = "a logging level, possible values are TRACE, INFO, WARN, ERROR (default: INFO)")
    String logLevel;

    @Option(names = "-mpm", paramLabel = "<value>", description = "maximum allowed processing time per decompiled method, in seconds. 0 means no upper limit (default: \"0\")")
    String maxProcessingMethod;

    @Option(names = "-ren", paramLabel = "<value>", description = "rename ambiguous (resp. obfuscated) classes and class elements (default: \"0\")")
    String renameEntities;

    @Option(names = "-urc", paramLabel = "<value>", description = "full name of a user-supplied class implementing IIdentifierRenamer interface. It is used to determine which class identifiers should be renamed and provides new identifier names (see \"Renaming identifiers\")")
    String userRenamerClass;

    @Option(names = "-nls", paramLabel = "<value>", description = "define new line character to be used for output. 0 - '\r\n' (Windows), 1 - '\n' (Unix), default is OS-dependent")
    String newLineSeparator;

    @Option(names = "-ind", paramLabel = "<value>", description = "indentation string (default is 3 spaces) (default: \"   \")")
    String indentString;
  }
}
