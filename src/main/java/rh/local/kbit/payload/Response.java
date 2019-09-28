package rh.local.kbit.payload;

import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class Response {


    private String status = "ok";
    private Object data = null;

    public String getStatus() {
        return status;
    }

    public Object getData() {
        return data;
    }

    public Response setData(Object data) {
        this.data = data;
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getJsonPayload() {
        return (new GsonBuilder())
                .create()
                .toJson(this);
    }
    
}
