package sqlparser.dimension;

import java.io.Externalizable;

import sqlparser.common.util.StringUtil;
import sqlparser.metadata.datatype.DataTypeSerializer;

public abstract class DimensionEncoding implements Externalizable {
    private static final long serialVersionUID = 1L;

    public static final byte NULL = (byte) 0xff;

    public static boolean isNull(byte[] bytes, int offset, int length) {
        if (length == 0) {
            return false;
        }

        for (int i = 0; i < length; i++) {
            if (bytes[i + offset] != NULL) {
                return false;
            }
        }
        return true;
    }

    public static Object[] parseEncodingConf(String encoding) {
        String[] parts = encoding.split("\\s*[(),:]\\s*");
        if (parts == null || parts.length == 0 || parts[0].isEmpty())
            throw new IllegalArgumentException("Not supported row key col encoding: '" + encoding + "'");

        final String encodingName = parts[0];
        final String[] encodingArgs = parts[parts.length - 1].isEmpty()
                ? StringUtil.subArray(parts, 1, parts.length - 1)
                : StringUtil.subArray(parts, 1, parts.length);
        return new Object[] { encodingName, encodingArgs };
    }
}