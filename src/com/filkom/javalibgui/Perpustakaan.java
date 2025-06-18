import java.util.UUID;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Perpustakaan {
    private static final String DELIMITER = ";";
    private static final String DATA = "data.txt";
    private static final String ANGGOTA = "anggota.txt";

    private static List<Buku> daftarBuku = new ArrayList<>();
    private static Map<String, List<String>> daftarAnggota = new HashMap<>();

    private static boolean disableSave = false;
    private static boolean disableLoad = false;

    private static FileHandler<Buku> fileHandlerBuku = new FileHandler<>();
    private static FileHandler<Anggota> fileHandlerAnggota = new FileHandler<>();

    private static void initData() throws IOException {
        BufferedReader bufferedReader = fileHandlerBuku.loadFile(DATA);
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            System.err.println(line);

            tambahBuku(new Buku(line));
        }
    }

    private static void initAnggota() throws IOException {
        BufferedReader bufferedReader = fileHandlerAnggota.loadFile(ANGGOTA);
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            System.err.println(line);

            StringTokenizer stringTokenizer = new StringTokenizer(line, DELIMITER);

            String type = stringTokenizer.nextToken();

            if (type.equals("member")) {
                tambahAnggota(new Member(line));
            } else if (type.equals("admin")) {
                tambahAnggota(new Admin(line));
            }
        }
    }

    private static void closeData() throws IOException {
        fileHandlerBuku.writeFile(DATA, daftarBuku);
    }

    private static void closeAnggota() throws IOException {
        List<Anggota> daftarAnggotaList = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : daftarAnggota.entrySet()) {
            System.err.println(entry);

            String role = entry.getValue().get(2);

            if (role.equals("admin")) {
                daftarAnggotaList.add(new Admin(entry));
            } else if (role.equals("member")) {
                daftarAnggotaList.add(new Member(entry));
            }
        }

        fileHandlerAnggota.writeFile(ANGGOTA, daftarAnggotaList);
    }

    public static void disableLoad() {
        disableLoad = true;
    }

    public static void disableSave() {
        disableSave = true;
    }

    public static void init() throws IOException {
        if (disableLoad) {
            System.err.println("data load disabled\n");

            return;
        }

        Runnable data = () -> {
            try {
                initData();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        };

        Runnable member = () -> {
            try {
                initAnggota();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        };

        Thread[] threads = new Thread[] {
                new Thread(data),
                new Thread(member) };

        for (Thread thread : threads) {
            thread.start();
        }

        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException ie) {
            System.err.println(ie.getMessage());
        }
    }

    public static void close() throws IOException {
        if (disableSave) {
            System.err.println("data save disabled\n");

            return;
        }

        closeData();
        closeAnggota();
    }

    public static void print() {
        System.err.println(daftarAnggota);
        System.err.println(daftarBuku);
    }

    public static String getNamaFromUsername(String username) {
        return daftarAnggota.get(username).get(0);
    }

    public static void tambahBuku(Buku buku) {
        daftarBuku.add(buku);
    }

    public static void tambahAnggota(AnggotaImpl anggota) {
        daftarAnggota.put(anggota.getUsername(),
                new ArrayList<>(Arrays.asList(
                        anggota.getNama(),
                        anggota.getPassword(),
                        anggota.getClass().getSimpleName().toLowerCase())));
    }

    public static void updateAnggota(AnggotaImpl anggota) {
        if (daftarAnggota.containsKey(anggota.getUsername())) {
            daftarAnggota.put(anggota.getUsername(),
                    new ArrayList<>(Arrays.asList(
                            anggota.getNama(),
                            anggota.getPassword(),
                            anggota.getClass().getSimpleName().toLowerCase())));
        }
    }

    public static void updateBuku(UUID IDBuku, Buku buku) throws BukuTidakDitemukanException {
        if (daftarBuku.isEmpty()) {
            return;
        }

        int index = 0;

        for (Buku i : daftarBuku) {
            if (i.getIDBuku().equals(IDBuku)) {
                daftarBuku.set(index, buku);

                return;
            }

            ++index;
        }

        throw new BukuTidakDitemukanException(
                "Buku dengan ID " + IDBuku + " tidak ditemukan");

    }

    public static List<Buku> daftarBuku() {
        return daftarBuku;
    }

    public static boolean cekAnggota(String username) {
        return daftarAnggota.containsKey(username);
    }

    public static boolean cekPassword(String username, String password) {
        return daftarAnggota.get(username).get(1).equals(password);
    }

    public static String getAnggotaRole(String username, String password) {
        return daftarAnggota.get(username).get(2);
    }

    public static void pinjamBuku(String usernameAnggota, UUID IDBuku)
            throws BukuTidakTersediaException, BukuTidakDitemukanException {
        if (daftarBuku.isEmpty()) {
            return;
        }

        int index = 0;

        for (Buku i : daftarBuku) {
            if (i.getIDBuku().equals(IDBuku)) {
                if (!i.getStatus()) {
                    throw new BukuTidakTersediaException(
                            "Buku dengan ID " + IDBuku + " tidak tersedia");
                }

                i.setStatus(false);
                i.setUsernamePeminjam(usernameAnggota);
                i.setNamaPeminjam(getNamaFromUsername(usernameAnggota));
                daftarBuku.set(index++, i);
                return;
            }
            index++;
        }

        throw new BukuTidakDitemukanException(
                "Buku dengan ID " + IDBuku + " tidak ditemukan");

    }

    public static void kembaliBuku(String usernameAnggota, UUID IDBuku)
            throws BukuTidakDipinjamException, BukuTidakDitemukanException {
        if (daftarBuku.isEmpty()) {
            return;
        }

        int index = 0;

        for (Buku i : daftarBuku) {
            if (i.getIDBuku().equals(IDBuku)) {
                if (i.getStatus()) {
                    throw new BukuTidakDipinjamException(
                            "Buku dengan ID " + IDBuku + " tidak dipinjam");
                }

                if (!i.getUsernamePeminjam().equals(usernameAnggota)) {
                    throw new BukuTidakDipinjamException(
                            "Buku dengan ID " + IDBuku + " tidak dipinjam");
                }

                i.setStatus(true);
                i.setUsernamePeminjam(null);
                i.setNamaPeminjam(null);
                daftarBuku.set(index, i);
                return;
            }
            index++;
        }
        throw new BukuTidakDitemukanException(
                "Buku dengan ID " + IDBuku + " tidak ditemukan");
    }

    public static Buku infoBuku(UUID IDBuku) throws BukuTidakDitemukanException {
        if (daftarBuku.isEmpty()) {
            return null;
        }

        for (Buku i : daftarBuku) {
            if (i.getIDBuku().equals(IDBuku)) {
                return i;
            }
        }

        throw new BukuTidakDitemukanException(
                "Buku dengan ID " + IDBuku + " tidak ditemukan");
    }

    public static Buku cariIDBuku(UUID IDBuku) throws BukuTidakDitemukanException {
        if (daftarBuku.isEmpty()) {
            return null;
        }

        for (Buku i : daftarBuku) {
            if (i.getIDBuku().equals(IDBuku)) {
                return i;
            }
        }

        throw new BukuTidakDitemukanException(
                "Buku dengan ID " + IDBuku + " tidak ditemukan");
    }

    public static List<Buku> cariJudulBuku(String judulBuku) throws BukuTidakDitemukanException {
        if (daftarBuku.isEmpty()) {
            return null;
        }

        List<Buku> result = new ArrayList<>();

        for (Buku i : daftarBuku) {
            if (i.getJudul().toLowerCase().contains(judulBuku.toLowerCase())) {
                result.add(i);
            }
        }

        if (result.size() == 0) {
            throw new BukuTidakDitemukanException(
                    "Buku dengan judul " + judulBuku + " tidak ditemukan");
        }

        return result;
    }

    public static List<Buku> cariPenulis(String penulis) throws BukuTidakDitemukanException {
        if (daftarBuku.isEmpty()) {
            return null;
        }

        List<Buku> result = new ArrayList<>();

        for (Buku i : daftarBuku) {
            if (i.getPenulis().toLowerCase().contains(penulis.toLowerCase())) {
                result.add(i);
            }
        }
        if (result.size() == 0) {
            throw new BukuTidakDitemukanException(
                    "Buku dengan penulis " + penulis + " tidak ditemukan");
        }

        return result;
    }
}
