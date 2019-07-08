import java.io.Serializable;

@SuppressWarnings("WeakerAccess")
public class Result implements Serializable {

    private static final long serialVersionUID = 1;
    private static final String OK = "result//__//ok";
    private static final String FAIL = "result//__//fail";

    private String info;

    Result(String info) { this.info = info; }

    Result (byte[] info) { this.info = new String(info); }

    String[] getInfoSplitted() { return this.info.split("//__//"); }

    String getInfo() { return this.info; }

    static Result OK() { return new Result(Result.OK); }

    static Result FAIL() { return new Result(Result.FAIL); }

    boolean isOK() { return this.info.equals(Result.OK); }

    boolean isFAIL() { return this.info.equals(Result.FAIL); }

    Request toRequest() { return new Request(this.info); }
}
