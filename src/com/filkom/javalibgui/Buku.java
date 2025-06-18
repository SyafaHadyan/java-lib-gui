import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.util.StringTokenizer;
import java.util.UUID;

public class Buku {
    private static final String DELIMITER = ";";

    private String judul;
    private String penulis;
    private String usernamePeminjam;
    private String namaPeminjam;
    private boolean status;
    private UUID IDBuku;
    private URL coverBuku;
    private LocalDate tanggalTerbit;

    public Buku() {
        this.judul = "Operating System Concepts, 10th Edition";
        this.penulis = "Abraham Silberschatz";
        this.usernamePeminjam = null;
        this.namaPeminjam = null;
        this.status = true;
        this.IDBuku = UUID.randomUUID();

        try {
            this.coverBuku = new URI("https://codex.cs.yale.edu/avi/os-book/OS10/images/os10-cover.jpg").toURL();
        } catch (URISyntaxException | MalformedURLException ex) {
            //
        }

        this.tanggalTerbit = LocalDate.of(2018, 04, 15);
    }

    public Buku(String judul, String penulis, URL coverBuku, LocalDate tanggalTerbit) {
        this.judul = judul;
        this.penulis = penulis;
        this.usernamePeminjam = null;
        this.namaPeminjam = null;
        this.status = true;
        this.IDBuku = UUID.randomUUID();
        this.coverBuku = coverBuku;
        this.tanggalTerbit = tanggalTerbit;
    }

    public Buku(String raw) {
        StringTokenizer stringTokenizer = new StringTokenizer(raw, DELIMITER);

        this.judul = stringTokenizer.nextToken();
        this.penulis = stringTokenizer.nextToken();

        String username = stringTokenizer.nextToken();

        if (username.isEmpty()) {
            this.usernamePeminjam = null;
        } else {
            this.usernamePeminjam = username;
        }

        String namaPeminjam = stringTokenizer.nextToken();

        if (namaPeminjam.isEmpty()) {
            this.namaPeminjam = null;
        } else {
            this.namaPeminjam = namaPeminjam;
        }

        this.status = Boolean.valueOf(stringTokenizer.nextToken());
        this.IDBuku = UUID.fromString(stringTokenizer.nextToken());

        try {
            this.coverBuku = new URI(stringTokenizer.nextToken()).toURL();
        } catch (URISyntaxException | MalformedURLException ex) {
            ex.printStackTrace();
        }

        String[] dateRaw = stringTokenizer.nextToken().split("-");

        int year = Integer.parseUnsignedInt(dateRaw[0]);
        int month = Integer.parseUnsignedInt(dateRaw[1]);
        int day = Integer.parseUnsignedInt(dateRaw[2]);

        this.tanggalTerbit = LocalDate.of(year, month, day);
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public void setPenulis(String penulis) {
        this.penulis = penulis;
    }

    public void setUsernamePeminjam(String usernamePeminjam) {
        this.usernamePeminjam = usernamePeminjam;
    }

    public void setNamaPeminjam(String namaPeminjam) {
        this.namaPeminjam = namaPeminjam;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setIDBuku(UUID IDBuku) {
        this.IDBuku = IDBuku;
    }

    public void setCoverBuku(URL coverBuku) {
        this.coverBuku = coverBuku;
    }

    public void setTanggalTerbit(LocalDate tanggalTerbit) {
        this.tanggalTerbit = tanggalTerbit;
    }

    public String getJudul() {
        return this.judul;
    }

    public String getPenulis() {
        return this.penulis;
    }

    public String getUsernamePeminjam() {
        return this.usernamePeminjam;
    }

    public String getNamaPeminjam() {
        return this.namaPeminjam;
    }

    public boolean getStatus() {
        return this.status;
    }

    public UUID getIDBuku() {
        return this.IDBuku;
    }

    public URL getCoverBuku() {
        return this.coverBuku;
    }

    public LocalDate getTanggalTerbit() {
        return this.tanggalTerbit;
    }

    public String raw() {
        return String.format(
                "Judul: %s\n" +
                        "Penlulis: %s\n" +
                        "Username peminjam: %s\n" +
                        "Nama peminjam: %s\n" +
                        "Tersedia: %b\n" +
                        "ID Buku: %s\n" +
                        "Cover buku: %s\n" +
                        "Tanggal terbit: %s\n",
                getJudul(),
                getPenulis(),
                getUsernamePeminjam(),
                getNamaPeminjam(),
                getStatus(),
                getIDBuku(),
                getCoverBuku(),
                getTanggalTerbit());
    }

    @Override
    public String toString() {
        return String.format(
                "%s;%s;%s;%s;%s;%s;%s;%s",
                getJudul(),
                getPenulis(),
                getUsernamePeminjam(),
                getNamaPeminjam(),
                getStatus(),
                getIDBuku(),
                getCoverBuku(),
                getTanggalTerbit());
    }
}
