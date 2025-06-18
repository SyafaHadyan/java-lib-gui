import java.util.List;
import java.util.Map;

public class Member extends AnggotaImpl {
    public Member() {
        super();
    }

    public Member(Map.Entry<String, List<String>> map) {
        super(map);
    }

    public Member(String raw) {
        super(raw);
    }
}
