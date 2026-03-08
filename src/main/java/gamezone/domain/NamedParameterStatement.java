package gamezone.domain;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.text.DateFormat;
import java.util.*;

public class NamedParameterStatement implements AutoCloseable {
//    Logger logger = LogManager.getLogger(NamedParameterStatement.this);

    private final PreparedStatement statement;
    private final Map<String, List<Integer>> indexMap;

    public NamedParameterStatement(Connection connection, String query) throws SQLException {
        indexMap = new HashMap<>();
        String parsedQuery = parse(query);
        statement = connection.prepareStatement(parsedQuery);
    }

    private String parse(String query) {
        int length = query.length();

        StringBuilder parsedQuery = new StringBuilder(length);

        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;

        int index = 1;

        for (int i = 0; i < length; i++) {

            char c = query.charAt(i);

            if (inSingleQuote) {
                if (c == '\'') {
                    inSingleQuote = false;
                }
            } else if (inDoubleQuote) {
                if (c == '"') {
                    inDoubleQuote = false;
                }
            } else {
                if (c == '\'') {
                    inSingleQuote = true;
                } else if (c == '"') {
                    inDoubleQuote = true;
                } else if (c == ':' && i + 1 < length
                        && Character.isJavaIdentifierStart(query.charAt(i + 1))) {

                    int j = i + 2;
                    while (j < length && Character.isJavaIdentifierPart(query.charAt(j))) {
                        j++;
                    }

                    String name = query.substring(i + 1, j);

                    c = '?'; // replace the parameter with a question mark

                    i += name.length(); // skip past the end if the parameter

                    List<Integer> indexList = indexMap.get(name);

                    if (indexList == null) {
                        indexList = new LinkedList<>();
                    }
                    indexList.add(index);

                    indexMap.put(name, indexList);

                    index++;
                }
            }

            parsedQuery.append(c);
        }

        return parsedQuery.toString();
    }

    private List<Integer> getIndexes(String name) {
        List<Integer> indexes = indexMap.get(name);

        if (indexes == null) {
            throw new IllegalArgumentException("Parameter not found: " + name);
        }

        return indexes;
    }

    public void setObject(String name, Object value) throws SQLException {
        for (Integer index : getIndexes(name)) {
            statement.setObject(index, value);
        }
    }

    public void setBytes(String name, byte[] value) throws SQLException {
        for (Integer index : getIndexes(name)) {
            statement.setBytes(index, value);
        }
    }

    public void setBinaryStream(String name, InputStream inputStream) throws SQLException {
        for (Integer index : getIndexes(name)) {
            statement.setBinaryStream(index, inputStream);
        }
    }

    public void setString(String name, String value) throws SQLException {
        if (value == null) {
            setNull(name);
            return;
        }

        for (Integer index : getIndexes(name)) {
            int xx = index;
            statement.setString(xx, value);
        }
    }

    public void setNull(String name) throws SQLException {
        for (Integer index : getIndexes(name)) {
            statement.setNull(index, Types.NULL);
        }
    }

    public void setNull(String name, int type) throws SQLException {
        for (Integer index : getIndexes(name)) {
            statement.setNull(index, type);
        }
    }

    public void setInt(String name, Integer value) throws SQLException {
        if (value == null) {
            setNull(name);
            return;
        }

        for (Integer index : getIndexes(name)) {
            statement.setInt(index, value);
        }
    }

    public void setLong(String name, Long value) throws SQLException {
        if (value == null) {
            setNull(name);
            return;
        }

        for (Integer index : getIndexes(name)) {
            statement.setLong(index, value);
        }
    }

    public void setBoolean(String name, Boolean value) throws SQLException {
        if (value == null) {
            setNull(name);
            return;
        }

        for (Integer index : getIndexes(name)) {
            statement.setBoolean(index, value);
        }
    }

    public void setBoolean(String name, Boolean value, Boolean defaultValue) throws SQLException {
        if (value == null) {
            if (defaultValue == null) {
                setNull(name);
                return;
            }
            for (Integer index : getIndexes(name)) {
                statement.setBoolean(index, defaultValue);
            }
            return;
        }

        for (Integer index : getIndexes(name)) {
            statement.setBoolean(index, value);
        }
    }

    public void setDate(String name, Date value) throws SQLException {
        for (Integer index : getIndexes(name)) {
            statement.setDate(index, value);
        }
    }

    public void setDate(String name, java.util.Date value, java.util.Date defaultValue, DateFormat formatter) throws SQLException {
        if (value == null) {
            if (defaultValue == null) {
                setNull(name);
                return;
            }
            for (Integer index : getIndexes(name)) {
                statement.setString(index, formatter.format(defaultValue));
            }
            return;
        }
        for (Integer index : getIndexes(name)) {
            statement.setString(index, formatter.format(value));
        }
    }

    public void setTimestamp(String name, Timestamp value) throws SQLException {
        for (Integer index : getIndexes(name)) {
            statement.setTimestamp(index, value);
        }
    }

    public void setTime(String name, Time value) throws SQLException {
        for (Integer index : getIndexes(name)) {
            statement.setTime(index, value);
        }
    }

    public void setDouble(String name, Double value) throws SQLException {
        if (value == null) {
            setNull(name);
            return;
        }

        for (Integer index : getIndexes(name)) {
            statement.setDouble(index, value);
        }
    }

    public void setFloat(String name, Float value) throws SQLException {
        if (value == null) {
            setNull(name);
            return;
        }

        for (Integer index : getIndexes(name)) {
            statement.setFloat(index, value);
        }
    }

    public void setShort(String name, Short value) throws SQLException {
        if (value == null) {
            setNull(name);
            return;
        }

        for (Integer index : getIndexes(name)) {
            statement.setShort(index, value);
        }
    }

    public void setBigDecimal(String name, BigDecimal value) throws SQLException {
        if (value == null) {
            setNull(name);
            return;
        }

        for (Integer index : getIndexes(name)) {
            statement.setBigDecimal(index, value);
        }
    }


    public void setStringList(String name, List<String> list) throws SQLException {
        List<Integer> indexes = getIndexes(name);

        List<String> response = new ArrayList<>();
        for (String str : list) {
            response.add("'" + str + "'");
        }

        String string = String.join(",", response);

        for (Integer index : indexes) {
            statement.setString(index, string);
        }
    }

    public void setEnum(String name, Enum value) throws SQLException {
        if (value == null) {
            setNull(name);
            return;
        }
        for (Integer index : getIndexes(name)) {
            int xx = index;
            statement.setString(xx, value.name());
        }
    }

    public PreparedStatement getStatement() {
        return statement;
    }

    @Override
    public void close() throws SQLException {
        statement.close();
    }

    public void addBatch() throws SQLException {
        statement.addBatch();
    }

    public void executeBatch() throws SQLException {
        statement.executeBatch();
    }


    public boolean execute() throws SQLException {
//        int retries = 3;
//        for (int i = 0; i < retries; i++) {
//            try {
        return statement.execute();
//            } catch (FailoverSuccessSQLException e) {
//                if (i == retries - 1) {
//                    throw e; // Re-throw after max retries
//                }
//            }
//        }
//        throw new SQLException("Statement execute failed");
    }

    public ResultSet executeQuery() throws SQLException {
//        int retries = 3;
//        for (int i = 0; i < retries; i++) {
//            try {
        return statement.executeQuery();
//            } catch (FailoverSuccessSQLException e) {
//                if (i == retries - 1) {
//                    throw e; // Re-throw after max retries
//                }
//            }
//        }
//        throw new SQLException("Query execution failed");
    }

    public int executeUpdate() throws SQLException {

//        int retries = 3;
//        for (int i = 0; i < retries; i++) {
//            try {
        return statement.executeUpdate();
//            } catch (FailoverSuccessSQLException e) {
//                if (i == retries - 1) {
//                    throw e; // Re-throw after max retries
//                }
//            }
//        }
//        throw new SQLException("Query update execution failed");
    }

    public void setID() {
    }
}
