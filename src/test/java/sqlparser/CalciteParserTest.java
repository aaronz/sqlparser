package sqlparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.calcite.sql.SqlNode;
import org.junit.Test;

import sqlparser.metadata.model.tool.CalciteParser;

/**
 * Unit test for simple App.
 */
public class CalciteParserTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void insertAliasInExprTest()
    {
        String sql = CalciteParser.insertAliasInExpr("expr", "alias");
        assertEquals("alias.expr", sql);
    }

    @Test
    public void getExpNodeTest(){
        SqlNode node = CalciteParser.getExpNode("expr");
        assertEquals("EXPR", node.toString());
    }
}