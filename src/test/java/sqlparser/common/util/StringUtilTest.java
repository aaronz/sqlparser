package sqlparser.common.util;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

public class StringUtilTest {
    @Test
    public void splitTest(){

    }

    @Test 
    public void subArrayTest(){
        String[] array = {"abc","abcd","abcde"};
        String[] result = StringUtil.subArray(array, 1, 2);
        assertEquals(1, result.length);
        assertEquals("abcd", result[0]);
    }
}