import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

public class AnggotaImpl implements Anggota {
    private static final String DELIMITER = ";";

    private String nama;
    private String username;
    private String password;

    public AnggotaImpl() {
    }

    public AnggotaImpl(Map.Entry<String, List<String>> map) {
        this.username = map.getKey();

        List<String> data = map.getValue();

        this.nama = data.get(0);
        this.password = data.get(1);
    }

    public AnggotaImpl(String raw) {
        StringTokenizer stringTokenizer = new StringTokenizer(raw, DELIMITER);

        stringTokenizer.nextToken();

        this.nama = stringTokenizer.nextToken();
        this.username = stringTokenizer.nextToken();
        this.password = stringTokenizer.nextToken();
    }

    @Override
    public boolean register(String nama, String username, String password) {
        if (Perpustakaan.cekAnggota(username)) {
            this.nama = "";
            this.username = "";
            this.password = "";
            return false;
        } else {
            this.nama = nama;
            this.username = username;
            this.password = password;
            return true;
        }
    }

    @Override
    public boolean login(String username, String password) {
        if (Perpustakaan.cekAnggota(username) && Perpustakaan.cekPassword(username, password)) {
            this.nama = Perpustakaan.getNamaFromUsername(username);
            this.username = username;
            this.password = password;
            return true;
        } else {
            this.nama = "";
            this.username = "";
            this.password = "";
            return false;
        }
    }

    @Override
    public String getRole(String usrname, String password) {
        return Perpustakaan.getAnggotaRole(username, password);
    }

    @Override
    public void changeInfo(String nama, String username, String password) {
        if (Perpustakaan.cekAnggota(username)) {
            this.nama = nama;
            this.password = password;

            Perpustakaan.updateAnggota(this);
        }
    }

    public static void pinjamBuku(String username, UUID IDBuku)
            throws UnauthorizedException, BukuTidakDitemukanException, BukuTidakTersediaException {
        if (username == null || username.isEmpty()) {
            throw new UnauthorizedException("User belum melakukan login");
        }

        Perpustakaan.pinjamBuku(username, IDBuku);
    }

    public static void kembaliBuku(String username, UUID IDBuku)
            throws UnauthorizedException, BukuTidakDitemukanException, BukuTidakDipinjamException {
        if (username == null || username.isEmpty()) {
            throw new UnauthorizedException("User belum melakukan login");
        }
        Perpustakaan.kembaliBuku(username, IDBuku);
    }

    public String getNama() {
        return this.nama;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    @Override
    public String toString() {
        return String.format(
                "%s;%s;%s;%s",
                getClass().getSimpleName().toLowerCase(),
                getNama(),
                getUsername(),
                getPassword());
    }

    @Override
    public String raw() {
        return String.format(
                "Nama: %s\n" +
                        "Username: %s\n" +
                        "Password: %s",
                getNama(),
                getUsername(),
                getPassword());
    }

}
