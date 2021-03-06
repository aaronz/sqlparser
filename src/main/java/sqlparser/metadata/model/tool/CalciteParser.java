package sqlparser.metadata.model.tool;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.util.SqlBasicVisitor;
import org.apache.calcite.sql.util.SqlVisitor;

import sqlparser.common.util.Pair;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class CalciteParser {
    public static SqlNode parse(String sql) throws SqlParseException {
        SqlParser.ConfigBuilder parserBuilder = SqlParser.configBuilder();
        SqlParser sqlParser = SqlParser.create(sql, parserBuilder.build());
        return sqlParser.parseQuery();
    }

    public static SqlNode getOnlySelectNode(String sql) {
        SqlNodeList selectList = null;
        try {
            selectList = ((SqlSelect) CalciteParser.parse(sql)).getSelectList();
        } catch (SqlParseException e) {
            throw new RuntimeException(
                    "Failed to parse exception \'" + sql + "\', please make sure the expression is valid", e);
        }

        Preconditions.checkArgument(selectList.size() == 1,
                "Expression is invalid because size of select list exceeds one");
        return selectList.get(0);
    }

    public static SqlNode getExpNode(String expr) {
        return getOnlySelectNode("select " + expr + " from t");
    }

    public static String getLastNthName(SqlIdentifier id, int n) {
        // n = 1 get column
        // n = 2 get table alias
        // n = 3 get database name
        return id.names.get(id.names.size() - n).replace("\"", "").toUpperCase(Locale.ROOT);
    }

    public static void ensureNoAliasExpr(String expr) {
        SqlNode sqlNode = getExpNode(expr);

        SqlVisitor sqlVisitor = new SqlBasicVisitor() {
            @Override
            public Object visit(SqlIdentifier id) {
                if (id.names.size() > 1) {
                    throw new IllegalArgumentException(
                            "Column Identifier in the computed column expression should only contain COLUMN");
                }
                return null;
            }
        };
        sqlNode.accept(sqlVisitor);
    }

    public static String insertAliasInExpr(String expr, String alias) {
        String prefix = "select ";
        String suffix = " from t";
        String sql = prefix + expr + suffix;
        SqlNode sqlNode = getOnlySelectNode(sql);

        final Set<SqlIdentifier> s = Sets.newHashSet();
        SqlVisitor sqlVisitor = new SqlBasicVisitor() {
            @Override
            public Object visit(SqlIdentifier id) {
                if (id.names.size() > 1) {
                    throw new IllegalArgumentException("SqlIdentifier " + id + " contains DB/Table name");
                }
                s.add(id);
                return null;
            }
        };

        sqlNode.accept(sqlVisitor);
        List<SqlIdentifier> sqlIdentifiers = Lists.newArrayList(s);

        descSortByPosition(sqlIdentifiers);
        for (SqlIdentifier sqlIdentifier : sqlIdentifiers) {
            Pair<Integer, Integer> replacePos = getReplacePos(sqlIdentifier, sql);
            int start = replacePos.getFirst();
            sql = sql.substring(0, start) + alias + "." + sql.substring(start);
        }

        return sql.substring(prefix.length(), sql.length() - suffix.length());
    }

    public static void descSortByPosition(List<SqlIdentifier> sqlIdentifiers) {
        Collections.sort(sqlIdentifiers, new Comparator<SqlIdentifier>() {
            @Override
            public int compare(SqlIdentifier o1, SqlIdentifier o2) {
                int linegap = o2.getParserPosition().getLineNum() - o1.getParserPosition().getLineNum();
                if (linegap != 0) {
                    return linegap;
                }
                return o2.getParserPosition().getColumnNum() - o1.getParserPosition().getColumnNum();
            }
        });
    }

    public static Pair<Integer, Integer> getReplacePos(SqlNode node, String inputSql) {
        if (inputSql == null) {
            return Pair.newPair(0, 0);
        }
        String[] lines = inputSql.split("\n");
        SqlParserPos pos = node.getParserPosition();
        int lineStart = pos.getLineNum();
        int lineEnd = pos.getEndLineNum();
        int columnStart = pos.getColumnNum() - 1;
        int columnEnd = pos.getEndColumnNum();
        for (int i = 0; i < lineStart - 1; i++) {
            columnStart += lines[i].length() + 1;
        }
        for (int i = 0; i < lineEnd - 1; i++) {
            columnEnd += lines[i].length() + 1;
        }
        Pair<Integer, Integer> startEndPos = getPosWithBracketsCompletion(inputSql, columnStart, columnEnd);
        return startEndPos;
    }

    public static Pair<Integer, Integer> getPosWithBracketsCompletion(String inputSql, int left, int right) {
        int leftBracketNum = 0;
        int rightBracketNum = 0;
        String substring = inputSql.substring(left, right);
        for (int i = 0; i < substring.length(); i++) {
            char temp = substring.charAt(i);
            if (temp == '(') {
                leftBracketNum++;
            }
            if (temp == ')') {
                rightBracketNum++;
                if (leftBracketNum < rightBracketNum) {
                    while ('(' != inputSql.charAt(left - 1)) {
                        left--;
                    }
                    left--;
                    leftBracketNum++;
                }
            }
        }
        while (rightBracketNum < leftBracketNum) {
            while (')' != inputSql.charAt(right)) {
                right++;
            }
            right++;
            rightBracketNum++;
        }
        return Pair.newPair(left, right);
    }
}