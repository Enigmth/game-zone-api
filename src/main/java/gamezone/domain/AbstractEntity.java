package gamezone.domain;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import gamezone.services.AbstractService;
import gamezone.services.CommonServiceImpl;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class AbstractEntity {

    @SerializedName("id")
    private String id;
    @SerializedName("created_at")
    private Date createdAt;
    @SerializedName("updated_at")
    private Date updatedAt;
    @SerializedName("deleted_at")
    private Date deletedAt;
    public abstract String validate();

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractEntity other = (AbstractEntity) obj;
        return !(this.id == null || other.id == null || !this.id.equals(other.id));
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean removeSpaceEqualsIgnoreCase(String valueToMatch, String text) {
        if (valueToMatch == null) {
            return false;
        }
        String tmp = text;
        tmp = tmp.replaceAll("\\s+", "");
        return tmp.equalsIgnoreCase(valueToMatch);
    }

    public Object getValueByFieldName(Object o, Class c, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = c.getDeclaredField(fieldName);
        return field.get(o);
    }

    private static transient final AbstractService abstractService = CommonServiceImpl.getInstance();


    public String generateAlias(String alias) {
        return (alias == null || alias.isEmpty()) ? "" : alias.contains(".") ? alias : alias + ".";
    }

    public String formatDate(Date date) {
        return formatDate(Formats.getDateFormat(), date);
    }

    public String formatDate(DateFormat formatter, Date date) {
        return abstractService.formatDate(formatter, date);
    }

    public String formatTimestamp(Date date) {
        return formatDate(Formats.getTimestampBD(), date);
    }

    public String string(String str, int length) {
        return ((str == null || str.length() <= (length - 1)) ? str : str.substring(0, (length - 1)));
    }

    public Date getDateRs(ResultSet resultSet, String fieldName) {
        return abstractService.getDate(resultSet, fieldName);
    }

    public Date getDateRs(ResultSet resultSet, String fieldName, DateFormat formatter) {
        return abstractService.getDate(resultSet, fieldName, formatter);
    }

    public Integer getInteger(ResultSet resultSet, String fieldName) {
        return abstractService.getInteger(resultSet, fieldName);
    }

    public Double getDouble(ResultSet resultSet, String fieldName) {
        return abstractService.getDouble(resultSet, fieldName);
    }

    public Float getFloat(ResultSet resultSet, String fieldName) {
        return abstractService.getFloat(resultSet, fieldName);
    }

    public Boolean getBoolean(ResultSet resultSet, String fieldName) {
        return abstractService.getBoolean(resultSet, fieldName);
    }


    public Integer readInteger(String[] arr, int index) {
        if (index >= arr.length) {
            return null;
        }
        return !"\\N".equalsIgnoreCase(arr[index]) && !arr[index].trim().isEmpty() ? Integer.parseInt(arr[index]) : null;
    }

    public Double readDouble(String[] arr, int index) {
        if (index >= arr.length) {
            return null;
        }
        return !"\\N".equalsIgnoreCase(arr[index]) && !arr[index].trim().isEmpty() ? Double.parseDouble(arr[index]) : null;
    }

    public Date readDate(String[] arr, int index, DateFormat format) throws Exception {
        if (index >= arr.length) {
            return null;
        }
        return !"\\N".equalsIgnoreCase(arr[index]) && !arr[index].trim().isEmpty() ? format.parse(arr[index]) : null;
    }

    public String readString(String[] arr, int index) {
        if (index >= arr.length) {
            return null;
        }
        return "\\N".equalsIgnoreCase(arr[index]) ? null : arr[index];
    }

    public String readStringId(String[] arr, int index) {
        if (index >= arr.length) {
            return null;
        }
        return arr[index].length() == 36 ? arr[index] : null;
    }

    public String getRsString(ResultSet rs, String fieldName, String alias) throws SQLException {
        return rs.getString(alias + fieldName);
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
}
