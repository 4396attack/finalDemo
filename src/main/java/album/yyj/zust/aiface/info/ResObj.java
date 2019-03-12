package album.yyj.zust.aiface.info;

public class ResObj {
    private String msg;
    private Integer Code;
    private Object obj;

    public ResObj() {
        super();
    }

    public ResObj(String msg, Integer code, Object obj) {
        this.msg = msg;
        this.Code = code;
        this.obj = obj;
    }
    @Override
    public String toString() {
        return "ResObj{" +
                "msg='" + msg + '\'' +
                ", Code=" + Code +
                ", obj=" + obj +
                '}';
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getCode() {
        return Code;
    }

    public void setCode(Integer code) {
        Code = code;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
