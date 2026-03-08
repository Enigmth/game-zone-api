package gamezone.domain;

import java.util.List;

public class ApiError extends AbstractEntity {
    private String code;
    private String message;
    private List<ApiErrorDetail> details;
    private String traceId;

    public ApiError() {
    }

    public ApiError(String code, String message, List<ApiErrorDetail> details, String traceId) {
        this.code = code;
        this.message = message;
        this.details = details;
        this.traceId = traceId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ApiErrorDetail> getDetails() {
        return details;
    }

    public void setDetails(List<ApiErrorDetail> details) {
        this.details = details;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    @Override
    public String validate() {
        return null;
    }
}
