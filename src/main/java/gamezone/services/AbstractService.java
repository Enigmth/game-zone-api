package gamezone.services;

import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import gamezone.domain.Formats;
import gamezone.domain.NamedParameterStatement;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class AbstractService {

    protected NamedParameterStatement namedStatement(Connection connection, String query) throws SQLException {
        return new NamedParameterStatement(connection, query);
    }

    public void closePreparedStatements(PreparedStatement... statements) {
        if (statements != null) {
            for (PreparedStatement ps : statements) {
                try {
                    if (ps != null && !ps.isClosed()) {
                        ps.close();
                    }
                } catch (Exception e) {
//                    abstractLogger.error("error closing statement", e);
                }
            }
        }
    }

    public void closeNamedPreparedStatements(NamedParameterStatement... statements) {
        if (statements != null) {
            for (NamedParameterStatement ps : statements) {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                } catch (Exception e) {
//                    abstractLogger.error("error closing statement", e);
                }
            }
        }
    }

    public void closeNamedPreparedStatements(List<NamedParameterStatement> statements) {
        if (statements != null) {
            for (NamedParameterStatement ps : statements) {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                } catch (Exception e) {
//                    abstractLogger.error("error closing statement", e);
                }
            }
        }
    }

    public void closeStatements(Statement... statements) {
        if (statements != null) {
            for (Statement st : statements) {
                try {
                    if (st != null && !st.isClosed()) {
                        st.close();
                    }
                } catch (Exception e) {
//                    abstractLogger.error("error closing statement", e);
                }
            }
        }
    }

    public void closeConnections(Connection... connections) {
        if (connections != null) {
            for (Connection c : connections) {
                try {
                    if (c != null && !c.isClosed()) {
                        if (!c.getAutoCommit()) {
                            c.setAutoCommit(true);
                        }
                        c.close();
                    }
                } catch (Exception e) {
//                    abstractLogger.error("error closing connection", e);
                }
            }
        }
    }

    public void closeConnections(List<Connection> connections) {
        if (connections != null) {
            for (Connection c : connections) {
                try {
                    if (c != null && !c.isClosed()) {
                        if (!c.getAutoCommit()) {
                            c.setAutoCommit(true);
                        }
                        c.close();
                    }
                } catch (Exception e) {
//                    abstractLogger.error("error closing connection", e);
                }
            }
        }
    }

    public void closeResultSets(ResultSet... resultSets) {
        if (resultSets != null) {
            for (ResultSet r : resultSets) {
                try {
                    if (r != null && !r.isClosed()) {
                        r.close();
                    }
                } catch (Exception e) {
//                    abstractLogger.error("error closing result set", e);
                }
            }
        }
    }

    public void close(ResultSet rs, NamedParameterStatement ps, Connection connection) {
        closeResultSets(rs);
        closeNamedPreparedStatements(ps);
        closeConnections(connection);
    }


    public Integer getInteger(ResultSet resultSet, String fieldName) {
        try {
            return resultSet.getString(fieldName) == null ? null : resultSet.getInt(fieldName);
        } catch (Exception e) {
//            abstractLogger.error("could not get field " + fieldName, e);
        }
        return null;
    }
//
    public Float getFloat(ResultSet resultSet, String fieldName) {
        try {
            return resultSet.getString(fieldName) == null ? null : resultSet.getFloat(fieldName);
        } catch (Exception e) {
        }
        return null;
    }

    public Double getDouble(ResultSet resultSet, String fieldName) {
        try {
            return resultSet.getString(fieldName) == null ? null : resultSet.getDouble(fieldName);
        } catch (Exception e) {
        }
        return null;
    }

    public Date getTimestamp(ResultSet resultSet, String fieldName) {
        try {
            String val = resultSet.getString(fieldName);
            return (val == null || val.isEmpty()) ? null : resultSet.getTimestamp(fieldName);
        } catch (Exception e) {
            return getDate(resultSet, fieldName, Formats.getTimestampBD());
        }
    }
//
    public Date getDate(ResultSet resultSet, String fieldName) {
        try {
            String val = resultSet.getString(fieldName);
            return (val == null || val.isEmpty()) ? null : resultSet.getDate(fieldName);
        } catch (Exception e) {
            return getDate(resultSet, fieldName, Formats.getDateFormat());
        }
    }

    public Date getDate(ResultSet resultSet, String fieldName, DateFormat formatter) {
        try {
            String val = resultSet.getString(fieldName);
            if (val == null || val.isEmpty()) {
                return null;
            }
            switch (((SimpleDateFormat) formatter).toPattern()) {
                case Formats.timestamp:
                case Formats.timestampDB:
                    return resultSet.getTimestamp(fieldName);
                case "yyyy-MM-dd":
                    return resultSet.getDate(fieldName);
            }
            return formatter.parse(val);
        } catch (Exception e) {
        }
        return null;
    }

    public Boolean getBoolean(ResultSet resultSet, String fieldName) {
        try {
            return resultSet.getString(fieldName) == null ? null : resultSet.getBoolean(fieldName);
        } catch (Exception e) {
        }
        return null;
    }

    public <T> List<T> getStringList(ResultSet resultSet, String fieldName) {
        try {
            return
                    resultSet.getString(fieldName) == null ? new ArrayList<>() :
                            CommonServiceImpl.getInstance().gson().fromJson(resultSet.getString(fieldName),
                                    new TypeToken<List<T>>() {
                                    }.getType());
        } catch (SQLException e) {
//            abstractLogger.error("could not get field " + fieldName, e);
        }
        return new ArrayList<>();
    }

//    public void clearHashMaps(HashMap... hashMaps) {
//        if (hashMaps != null) {
//            for (HashMap map : hashMaps) {
//                if (map != null) {
//                    map.clear();
//                    map = null;
//                }
//            }
//            hashMaps = null;
//        }
//    }
//
//    public void clearLists(List... lists) {
//        for (List list : lists) {
//            list.clear();
//        }
//    }
//
//    public void checkDefault(String tableName, String fieldName, Boolean currentDefault, String id, String propertyId, String propertyCode) throws Exception {
//        String errorString = fieldName + " already set on " + tableName;
//        if (propertyCode != null) {
//            errorString = errorString + " for property with code " + propertyCode;
//        } else if (propertyId != null) {
//            errorString = errorString + " for property with id " + propertyId;
//        }
//        if (currentDefault != null && currentDefault) {
//            if (id == null) {
//                if (CommonServiceImpl.getInstance().getDefault(tableName, fieldName, propertyId) != null) {
//                    throw new EntityDefaultAlreadyExistsException(errorString);
//                }
//            } else {
//                String def = CommonServiceImpl.getInstance().getDefault(tableName, fieldName, propertyId);
//                if (def != null && !def.equalsIgnoreCase(id)) {
//                    throw new EntityDefaultAlreadyExistsException(errorString);
//                }
//            }
//        }
//    }
//
    public String formatTimestamp(Date date) {
        return formatDate(Formats.getTimestampBD(), date);
    }

    public String formatDate(Date date) {
        return formatDate(Formats.getDateFormat(), date);
    }

    public String formatDate(DateFormat formatter, Date date) {
        if (date == null) {
            return null;
        }
        try {
            return formatter.format(date);
        } catch (Exception e) {
            return null;
        }
    }
//
    public Date toDate(String strDate) {
        try {
            return Formats.getDateFormat().parse(strDate);
        } catch (Exception ignored) {
            return null;
        }
    }
//
//    public void clearMaps(Map... maps) {
//        if (maps != null) {
//            for (Map map : maps) {
//                if (map != null) {
//                    map.clear();
//                    map = null;
//                }
//            }
//            maps = null;
//        }
//    }
//
//    public void commit(Connection connection) throws Exception {
//        try {
//            connection.commit();
//        } catch (Exception ex) {
//            connection.rollback();
//            throw ex;
//        } finally {
//            connection.setAutoCommit(Boolean.TRUE);
//        }
//    }
//
//    public String string(String str, int length) {
//        return ((str == null || str.length() <= (length - 1)) ? str : str.substring(0, (length - 1)));
//    }
//
//    public boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
//        ResultSetMetaData rsmd = rs.getMetaData();
//        int columns = rsmd.getColumnCount();
//        for (int x = 1; x <= columns; x++) {
//            if (columnName.equals(rsmd.getColumnName(x)) || columnName.equals(rsmd.getColumnLabel(x))) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public void validateConnection(Connection connection) throws Exception {
//        if (connection == null || connection.isClosed()) {
//            throw new BadRequestException(StatusMessages.CONNECTION_IS_CLOSED_OR_NULL);
//        }
//    }
//
//    /**
//     * Does some thing in old style.
//     *
//     * @deprecated use {@link #fillAbstractEntityFromRS(AbstractEntity, ResultSet, String)} instead.
//     */
//    @Deprecated
//    public void fillAbstractEntityPS(AbstractEntity entity, ResultSet resultSet, String prefix) throws Exception {
//        fillAbstractEntityFromRS(entity, resultSet, prefix);
//    }
//
//
//    public void fillAbstractEntityFromRS(AbstractEntity entity, ResultSet resultSet, String prefix) throws SQLException {
//        if (prefix == null) prefix = "";
//        //Prefix should be sent at 'table_alias.'
//        entity.setId(resultSet.getString(prefix + "id"));
//        entity.setCreatedAt(resultSet.getTimestamp(prefix + "created_at"));
//        entity.setUpdatedAt(resultSet.getTimestamp(prefix + "updated_at"));
//        entity.setDeletedAt(resultSet.getTimestamp(prefix + "deleted_at"));
//    }
//
//    public boolean isValidList(Collection<?> items) {
//        return commonUtils.isValidList(items);
//    }
//
//    public String getFieldByKey(String tableName, String fieldToGet, String key, String value, String propertyId) throws Exception {
//        return getFieldByKey(tableName, fieldToGet, key, value, propertyId, Boolean.TRUE, useWriteOnReadConnection);
//    }
//
//
//    public String getFieldByKey(String tableName, String fieldToGet, String key, String value, String propertyId, boolean usePropertyOnWhere) throws Exception {
//        return getFieldByKey(tableName, fieldToGet, key, value, propertyId, usePropertyOnWhere, useWriteOnReadConnection);
//    }
//
//    public String getFieldByKey(String tableName, String fieldToGet, String key, String value, String propertyId, boolean usePropertyOnWhere, boolean useWriterOnGet) throws Exception {
//        ResultSet rs = null;
//        NamedParameterStatement ps = null;
//        Connection connection = getConnection(propertyId, useWriterOnGet);
//        try {
//            ps = new NamedParameterStatement(connection, "select " + CommonUtils.escapeSql(fieldToGet) + " from " + CommonUtils.escapeSql(tableName)
//                    + " where " + CommonUtils.escapeSql(key) + " = :value " + (propertyId == null || !usePropertyOnWhere ? "" : " and property_id = :property_id") + ";");
//            ps.setString("value", value);
//            if (propertyId != null && usePropertyOnWhere) {
//                ps.setString("property_id", propertyId);
//            }
//            rs = ps.executeQuery();
//            if (rs.next()) {
//                return rs.getString(CommonUtils.escapeSql(fieldToGet));
//            }
//        } finally {
//            closeResultSet(rs);s(rs);
//            closeNamedPreparedStatements(ps);
//            closeConnections(connection);
//        }
//
//        return null;
//    }
//
//    public HashMap<String, String> getFieldsByKey(String tableName, List<String> fieldsToGet, String key, String value, String propertyId, boolean usePropertyOnWhere, boolean useWriterOnGet) throws Exception {
//        HashMap<String, String> map = new HashMap<>();
//        ResultSet rs = null;
//        NamedParameterStatement ps = null;
//        Connection connection = getConnection(propertyId, useWriterOnGet);
//        try {
//            StringBuilder querySb = new StringBuilder();
//            querySb.append("select ");
//            for (String str : fieldsToGet) {
//                if (!str.equals(fieldsToGet.get(0))) {
//                    querySb.append(",");
//                }
//                querySb.append(CommonUtils.escapeSql(str));
//            }
//            querySb.append(" from ")
//                    .append(CommonUtils.escapeSql(tableName))
//                    .append(" where ")
//                    .append(CommonUtils.escapeSql(key))
//                    .append(" = :value ")
//                    .append((propertyId == null || !usePropertyOnWhere ? "" : " and property_id = :property_id"))
//                    .append(";");
//            ps = new NamedParameterStatement(connection, querySb.toString());
//            ps.setString("value", value);
//            if (propertyId != null && usePropertyOnWhere) {
//                ps.setString("property_id", propertyId);
//            }
//            rs = ps.executeQuery();
//            if (rs.next()) {
//                for (String str : fieldsToGet) {
//                    map.put(str, rs.getString(CommonUtils.escapeSql(str)));
//                }
//                return map;
//            }
//        } finally {
//            closeResultSet(rs);s(rs);
//            closeNamedPreparedStatements(ps);
//            closeConnections(connection);
//        }
//
//        return null;
//    }
//
//    private Connection getConnection(String propertyId, boolean useWriterOnGet) throws Exception {
//        if (propertyId == null) {
//            return useWriterOnGet ?
//                    MasterSchemaConnection.getInstance().getWriter()
//                    : MasterSchemaConnection.getInstance().getReader();
//        }
//        return useWriterOnGet ?
//                SlaveSchemaConnection.getInstance().getWriter(propertyId, ConnectionRequestType.PROPERTY_ID)
//                : SlaveSchemaConnection.getInstance().getReader(propertyId, ConnectionRequestType.PROPERTY_ID);
//    }
//
//    public <T> T getFieldByKey(String tableName, String fieldToGet, String key, String value, String propertyId, boolean usePropertyOnWhere,
//                               Class<T> type) throws Exception {
//        ResultSet rs = null;
//        NamedParameterStatement ps = null;
//        Connection connection = getConnection(propertyId, false);
//        try {
//            ps = new NamedParameterStatement(connection, "select " + CommonUtils.escapeSql(fieldToGet) + " from " + CommonUtils.escapeSql(tableName)
//                    + " where " + CommonUtils.escapeSql(key) + " = :value " + (propertyId == null || !usePropertyOnWhere ? "" : " and property_id = :property_id") + ";");
//            ps.setString("value", value);
//            if (propertyId != null && usePropertyOnWhere) {
//                ps.setString("property_id", propertyId);
//            }
//            rs = ps.executeQuery();
//            if (rs.next()) {
//                return rs.getObject(CommonUtils.escapeSql(fieldToGet), type);
//            }
//        } finally {
//            closeResultSet(rs);s(rs);
//            closeNamedPreparedStatements(ps);
//            closeConnections(connection);
//        }
//        return null;
//    }
//
//    public Set<String> getAsSet(String value) {
//        if (value == null) {
//            return new HashSet<>();
//        }
//        return new HashSet<>(Arrays.asList(value.split(",")));
//    }
//
//    public List<String> getAsList(String value) {
//        if (value == null) {
//            return new ArrayList<>();
//        }
//        return Arrays.asList(value.split(","));
//    }
//
//    public List<String> getFieldListByKey(String tableName, String fieldToGet, HashMap<String, String> params, String propertyId, boolean useWriterOnGet, String customWhere) throws Exception {
//        List<String> list = new ArrayList<>();
//        StringBuilder sql = new StringBuilder("select " + CommonUtils.escapeSql(fieldToGet) + " from " + CommonUtils.escapeSql(tableName));
//        if (customWhere.isEmpty()) {
//            if (!params.isEmpty()) {
//                sql.append(" where ");
//            }
//            for (String key : params.keySet()) {
//                sql.append(CommonUtils.escapeSql(key)).append(" = :").append(key);
//            }
//        } else {
//            sql.append(" ").append(customWhere);
//        }
//
//        ResultSet rs = null;
//        NamedParameterStatement ps = null;
//        Connection connection = getConnection(propertyId, useWriterOnGet);
//        try {
//            ps = new NamedParameterStatement(connection, sql.toString());
//            if (customWhere.isEmpty()) {
//                for (Map.Entry<String, String> e : params.entrySet()) {
//                    ps.setString(e.getKey(), e.getValue());
//                }
//            }
//            rs = ps.executeQuery();
//            while (rs.next()) {
//                list.add(rs.getString(CommonUtils.escapeSql(fieldToGet)));
//            }
//        } finally {
//            closeResultSet(rs);s(rs);
//            closeNamedPreparedStatements(ps);
//            closeConnections(connection);
//        }
//
//        return list;
//    }
//
//    public List<String> getIds(String propertyId, String query, String columnLabel) throws Exception {
//        List<String> ids = new ArrayList<>();
//        NamedParameterStatement ps = null;
//        ResultSet rs = null;
//        Connection con = SlaveSchemaConnection.getInstance()
//                .getReader(propertyId, ConnectionRequestType.PROPERTY_ID);
//        try {
//            ps = new NamedParameterStatement(con, query);
//            ps.setString("property_id", propertyId);
//            rs = ps.executeQuery();
//            while (rs.next()) {
//                ids.add(rs.getString(columnLabel));
//            }
//        } finally {
//            closeResultSet(rs);s(rs);
//            closeNamedPreparedStatements(ps);
//            closeConnections(con);
//        }
//        return ids;
//    }
//
//    private Connection getReadConnection(String propertyId, ConnectionSchemaType connectionSchemaType) throws Exception {
//        switch (connectionSchemaType) {
//            case SLAVE:
//                return useWriteOnReadConnection ?
//                        SlaveSchemaConnection.getInstance().getWriter(propertyId, ConnectionRequestType.PROPERTY_ID) :
//                        SlaveSchemaConnection.getInstance().getReader(propertyId, ConnectionRequestType.PROPERTY_ID);
//            case MASTER:
//                return useWriteOnReadConnection ?
//                        MasterSchemaConnection.getInstance().getWriter() :
//                        MasterSchemaConnection.getInstance().getReader();
//            case RK_MASTER:
//                return useWriteOnReadConnection ?
//                        RetailKeyMasterSchemaConnection.getInstance().getWriter() :
//                        RetailKeyMasterSchemaConnection.getInstance().getReader();
//            case CRS:
//                return useWriteOnReadConnection ?
//                        CrsSlaveSchemaConnection.getInstance().getWriter(propertyId, ConnectionRequestType.PROPERTY_ID) :
//                        CrsSlaveSchemaConnection.getInstance().getReader(propertyId, ConnectionRequestType.PROPERTY_ID);
//            case TRAINKEY:
//                return useWriteOnReadConnection ?
//                        TrainKeySchemaConnection.getInstance().getWriter() :
//                        TrainKeySchemaConnection.getInstance().getReader();
//            case LOYALTY:
//                return useWriteOnReadConnection ?
//                        LoyaltySchemaConnection.getInstance().getWriter() :
//                        LoyaltySchemaConnection.getInstance().getReader();
//        }
//        return null;
//    }
//
//    public Connection getSlaveReadConnection(String propertyId) throws Exception {
//        return getReadConnection(propertyId, SLAVE);
//    }
//
//    public Connection getMasterReadConnection() throws Exception {
//        return getReadConnection(null, MASTER);
//    }
//
//    public Connection getRkMasterReadConnection() throws Exception {
//        return getReadConnection(null, RK_MASTER);
//    }
//
//    public Connection getTrainKeyReadConnection() throws Exception {
//        return getReadConnection(null, TRAINKEY);
//    }
//
//    public Connection getLoyaltyReadConnection() throws Exception {
//        return getReadConnection(null, LOYALTY);
//    }
//
//    public Connection getCrsReadConnection(String propertyId) throws Exception {
//        return useWriteOnReadConnection ?
//                SlaveSchemaConnection.getInstance().getWriter(propertyId, ConnectionRequestType.PROPERTY_ID) :
//                CrsSlaveSchemaConnection.getInstance().getReader(propertyId, ConnectionRequestType.PROPERTY_ID);
//    }
//
//    private boolean useWriteOnReadConnection = Boolean.FALSE;
//
//    public void setUseWriteOnReadConnection(boolean useWriteOnReadConnection) {
//        this.useWriteOnReadConnection = useWriteOnReadConnection;
//    }
//
//    public boolean useWriteOnReadConnection() {
//        return useWriteOnReadConnection;
//    }
//
//    public void handleGroupConcatLimit(Connection connection) throws Exception {
//        Statement st = connection.createStatement();
//        st.execute("set session group_concat_max_len = 9999;");
//        st.close();
//    }

}
