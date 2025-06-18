import java.util.List;
import java.util.Map;

public class Admin extends AnggotaImpl {
    public Admin() {
        super();
    }

    public Admin(Map.Entry<String, List<String>> map) {
        super(map);
    }

    public Admin(String raw) {
        super(raw);
    }
}
