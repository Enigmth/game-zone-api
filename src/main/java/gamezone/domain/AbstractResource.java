package gamezone.domain;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import gamezone.services.CommonServiceImpl;

import java.util.Base64;
import java.util.List;

public class AbstractResource {

    private Gson gson = null;
    private Gson gsonWithoutNulls = null;
    private Gson gsonPlus = null;

    public Gson gson() {
        if (gson == null) {
            gson = CommonServiceImpl.getInstance().gson();
        }
        return gson;
    }

    public Gson gsonWithoutNulls() {
        if (gsonWithoutNulls == null) {
            gsonWithoutNulls = CommonServiceImpl.getInstance().gsonWithoutNulls();
        }
        return gsonWithoutNulls;
    }

    public Gson gsonPlus() {
        if (gsonPlus == null) {
            gsonPlus = CommonServiceImpl.getInstance().gsonPlus();
        }
        return gsonPlus;
    }


    public Gson gsonWithDateFormat(String dateFormat) {
        return CommonServiceImpl.getInstance().gson(dateFormat);
    }

    public String toJson(Object o) {
        return gson().toJson(o);
    }

    public String toJson(Object o, String dateFormat) {
        return gsonWithDateFormat(dateFormat).toJson(o);
    }

    public String toBase64Json(Object o) {
        return Base64.getEncoder().encodeToString(gson().toJson(o).getBytes());
    }

    public String toJsonWithoutNulls(Object o) {
        return gsonWithoutNulls().toJson(o);
    }

    public <T> T fromJson(String json, Class<T> classOfT) {
        try {
            return gson().fromJson(json, classOfT);
        } catch (Exception ignored) {
            return gsonPlus().fromJson(json, classOfT);
        }
    }

    public <T> T fromJson(String json, String dateFormat, Class<T> classOfT) {
        return gsonWithDateFormat(dateFormat).fromJson(json, classOfT);
    }

    public <T> T fromJson(String json, TypeToken typeToken) {
        return gson().fromJson(json, typeToken.getType());
    }

    public <T> T fromJson(String json, String dateFormat, TypeToken typeToken) {
        return gsonWithDateFormat(dateFormat).fromJson(json, typeToken.getType());
    }

    public <T> T fromBase64Json(String json, Class<T> classOfT) {
        return gson().fromJson(new String(Base64.getDecoder().decode(json)), classOfT);
    }

    public Response toJsonResponse(Object o) {
        return new Responses().ok(toJson(o));
    }

    public Response okWithMessage(String message) {
        return new Responses().okWithMessage(message);
    }


    public Response toJsonResponse(Object o, Integer responseCode) {
        if (responseCode == null) {
            return toJsonResponse(o);
        }
        return new Responses().ok(responseCode, toJson(o));
    }

    public Response toJsonResponse(Object o, String dateFormat) {
        return new Responses().ok(toJson(o, dateFormat));
    }

    public Response toJsonWithoutNullsResponse(Object o) {
        return new Responses().ok(toJsonWithoutNulls(o));
    }

    public Response toJsonWithoutNullsResponse(Object o, Integer responseCode) {
        if (responseCode == null) {
            return toJsonWithoutNullsResponse(o);
        }
        return new Responses().ok(responseCode, toJsonWithoutNulls(o));
    }

//    public User getUser(ContainerRequestContext requestContext) {
//        User user = (User) requestContext.getProperty("user");
//        if (user == null) {
//            throw new BadRequestException("User not found");
//        }
//        return user;
//    }

    public boolean isValidList(List<?> items) {
        return items != null && !items.isEmpty();
    }


    public boolean isNotNullOrEmpty(String str) {
        return !isNullOrEmpty(str);
    }

    public boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

}
