// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.java.decompiler.struct.match;

import org.jetbrains.java.decompiler.modules.decompiler.exps.ExitExprent;
import org.jetbrains.java.decompiler.modules.decompiler.exps.Exprent;
import org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent;
import org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement;
import org.jetbrains.java.decompiler.modules.decompiler.stats.Statement.StatementType;
import org.jetbrains.java.decompiler.struct.gen.VarType;
import org.jetbrains.java.decompiler.struct.match.IMatchable.MatchProperties;
import org.jetbrains.java.decompiler.struct.match.MatchNode.RuleValue;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class MatchEngine {
  @SuppressWarnings("SpellCheckingInspection")
  private static final Map<String, MatchProperties> stat_properties = Stream.of(
    new AbstractMap.SimpleEntry<>("type", MatchProperties.STATEMENT_TYPE),
    new AbstractMap.SimpleEntry<>("ret", MatchProperties.STATEMENT_RET),
    new AbstractMap.SimpleEntry<>("position", MatchProperties.STATEMENT_POSITION),
    new AbstractMap.SimpleEntry<>("statsize", MatchProperties.STATEMENT_STATSIZE),
    new AbstractMap.SimpleEntry<>("exprsize", MatchProperties.STATEMENT_EXPRSIZE),
    new AbstractMap.SimpleEntry<>("iftype", MatchProperties.STATEMENT_IFTYPE)
  ).collect(toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

  @SuppressWarnings("SpellCheckingInspection")
  private static final Map<String, MatchProperties> expr_properties = Stream.of(
    new AbstractMap.SimpleEntry<>("type", MatchProperties.EXPRENT_TYPE),
    new AbstractMap.SimpleEntry<>("ret", MatchProperties.EXPRENT_RET),
    new AbstractMap.SimpleEntry<>("position", MatchProperties.EXPRENT_POSITION),
    new AbstractMap.SimpleEntry<>("functype", MatchProperties.EXPRENT_FUNCTYPE),
    new AbstractMap.SimpleEntry<>("exittype", MatchProperties.EXPRENT_EXITTYPE),
    new AbstractMap.SimpleEntry<>("consttype", MatchProperties.EXPRENT_CONSTTYPE),
    new AbstractMap.SimpleEntry<>("constvalue", MatchProperties.EXPRENT_CONSTVALUE),
    new AbstractMap.SimpleEntry<>("invclass", MatchProperties.EXPRENT_INVOCATION_CLASS),
    new AbstractMap.SimpleEntry<>("signature", MatchProperties.EXPRENT_INVOCATION_SIGNATURE),
    new AbstractMap.SimpleEntry<>("parameter", MatchProperties.EXPRENT_INVOCATION_PARAMETER),
    new AbstractMap.SimpleEntry<>("index", MatchProperties.EXPRENT_VAR_INDEX),
    new AbstractMap.SimpleEntry<>("name", MatchProperties.EXPRENT_FIELD_NAME)
  ).collect(toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

  @SuppressWarnings("SpellCheckingInspection")
  private static final Map<String, StatementType> stat_type = Stream.of(
    new AbstractMap.SimpleEntry<>("if", StatementType.IF),
    new AbstractMap.SimpleEntry<>("do", StatementType.DO),
    new AbstractMap.SimpleEntry<>("switch", StatementType.SWITCH),
    new AbstractMap.SimpleEntry<>("trycatch", StatementType.TRY_CATCH),
    new AbstractMap.SimpleEntry<>("basicblock", StatementType.BASIC_BLOCK),
    new AbstractMap.SimpleEntry<>("sequence", StatementType.SEQUENCE)
  ).collect(toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

  private static final Map<String, Integer> expr_type = Stream.of(
    new AbstractMap.SimpleEntry<>("array", Exprent.EXPRENT_ARRAY),
    new AbstractMap.SimpleEntry<>("assignment", Exprent.EXPRENT_ASSIGNMENT),
    new AbstractMap.SimpleEntry<>("constant", Exprent.EXPRENT_CONST),
    new AbstractMap.SimpleEntry<>("exit", Exprent.EXPRENT_EXIT),
    new AbstractMap.SimpleEntry<>("field", Exprent.EXPRENT_FIELD),
    new AbstractMap.SimpleEntry<>("function", Exprent.EXPRENT_FUNCTION),
    new AbstractMap.SimpleEntry<>("if", Exprent.EXPRENT_IF),
    new AbstractMap.SimpleEntry<>("invocation", Exprent.EXPRENT_INVOCATION),
    new AbstractMap.SimpleEntry<>("monitor", Exprent.EXPRENT_MONITOR),
    new AbstractMap.SimpleEntry<>("new", Exprent.EXPRENT_NEW),
    new AbstractMap.SimpleEntry<>("switch", Exprent.EXPRENT_SWITCH),
    new AbstractMap.SimpleEntry<>("var", Exprent.EXPRENT_VAR),
    new AbstractMap.SimpleEntry<>("annotation", Exprent.EXPRENT_ANNOTATION),
    new AbstractMap.SimpleEntry<>("assert", Exprent.EXPRENT_ASSERT)
  ).collect(toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

  private static final Map<String, Integer> expr_func_type = Stream.of(
    new AbstractMap.SimpleEntry<>("eq", FunctionExprent.FUNCTION_EQ)
  ).collect(toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

  private static final Map<String, Integer> expr_exit_type = Stream.of(
    new AbstractMap.SimpleEntry<>("return", ExitExprent.EXIT_RETURN),
    new AbstractMap.SimpleEntry<>("throw", ExitExprent.EXIT_THROW)
  ).collect(toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

  @SuppressWarnings("SpellCheckingInspection")
  private static final Map<String, Integer> stat_if_type = Stream.of(
    new AbstractMap.SimpleEntry<>("if", IfStatement.IFTYPE_IF),
    new AbstractMap.SimpleEntry<>("ifelse", IfStatement.IFTYPE_IFELSE)
  ).collect(toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

  private static final Map<String, VarType> expr_const_type = Stream.of(
    new AbstractMap.SimpleEntry<>("null", VarType.VARTYPE_NULL),
    new AbstractMap.SimpleEntry<>("string", VarType.VARTYPE_STRING)
  ).collect(toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

  private final MatchNode rootNode;
  private final Map<String, Object> variables = new HashMap<>();

  public MatchEngine(String description) {
    // each line is a separate statement/expression
    String[] lines = description.split("\n");

    int depth = 0;
    LinkedList<MatchNode> stack = new LinkedList<>();

    for (String line : lines) {
      List<String> properties = new ArrayList<>(Arrays.asList(line.split("\\s+"))); // split on any number of whitespaces
      if (properties.get(0).isEmpty()) {
        properties.remove(0);
      }

      int node_type = "statement".equals(properties.get(0)) ? MatchNode.MATCHNODE_STATEMENT : MatchNode.MATCHNODE_EXPRENT;

      // create new node
      MatchNode matchNode = new MatchNode(node_type);
      for (int i = 1; i < properties.size(); ++i) {
        String[] values = properties.get(i).split(":");

        MatchProperties property = (node_type == MatchNode.MATCHNODE_STATEMENT ? stat_properties : expr_properties).get(values[0]);
        if (property == null) { // unknown property defined
          throw new RuntimeException("Unknown matching property");
        }
        else {
          Object value;
          int parameter = 0;

          String strValue = values[1];
          if (values.length == 3) {
            parameter = Integer.parseInt(values[1]);
            strValue = values[2];
          }

          switch (property) {
            case STATEMENT_TYPE:
              value = stat_type.get(strValue);
              break;
            case STATEMENT_STATSIZE:
            case STATEMENT_EXPRSIZE:
              value = Integer.valueOf(strValue);
              break;
            case STATEMENT_POSITION:
            case EXPRENT_POSITION:
            case EXPRENT_INVOCATION_CLASS:
            case EXPRENT_INVOCATION_SIGNATURE:
            case EXPRENT_INVOCATION_PARAMETER:
            case EXPRENT_VAR_INDEX:
            case EXPRENT_FIELD_NAME:
            case EXPRENT_CONSTVALUE:
            case STATEMENT_RET:
            case EXPRENT_RET:
              value = strValue;
              break;
            case STATEMENT_IFTYPE:
              value = stat_if_type.get(strValue);
              break;
            case EXPRENT_FUNCTYPE:
              value = expr_func_type.get(strValue);
              break;
            case EXPRENT_EXITTYPE:
              value = expr_exit_type.get(strValue);
              break;
            case EXPRENT_CONSTTYPE:
              value = expr_const_type.get(strValue);
              break;
            case EXPRENT_TYPE:
              value = expr_type.get(strValue);
              break;
            default:
              throw new RuntimeException("Unhandled matching property");
          }

          matchNode.addRule(property, new RuleValue(parameter, value));
        }
      }

      if (stack.isEmpty()) { // first line, root node
        stack.push(matchNode);
      }
      else {
        // return to the correct parent on the stack
        int new_depth = line.lastIndexOf(' ', depth) + 1;
        for (int i = new_depth; i <= depth; ++i) {
          stack.pop();
        }

        // insert new node
        stack.getFirst().addChild(matchNode);
        stack.push(matchNode);

        depth = new_depth;
      }
    }

    this.rootNode = stack.getLast();
  }

  public boolean match(IMatchable object) {
    variables.clear();
    return match(this.rootNode, object);
  }

  private boolean match(MatchNode matchNode, IMatchable object) {
    if (!object.match(matchNode, this)) {
      return false;
    }

    int expr_index = 0;
    int stat_index = 0;
    for (MatchNode childNode : matchNode.getChildren()) {
      boolean isStatement = childNode.getType() == MatchNode.MATCHNODE_STATEMENT;

      IMatchable childObject = object.findObject(childNode, isStatement ? stat_index : expr_index);
      if (childObject == null || !match(childNode, childObject)) {
        return false;
      }

      if (isStatement) {
        stat_index++;
      }
      else {
        expr_index++;
      }
    }

    return true;
  }

  public boolean checkAndSetVariableValue(String name, Object value) {
    Object old_value = variables.get(name);
    if (old_value != null) {
      return old_value.equals(value);
    }
    else {
      variables.put(name, value);
      return true;
    }
  }

  public Object getVariableValue(String name) {
    return variables.get(name);
  }
}
