import java.io.IOException;

public class Main {
    private static void debug() {
        System.err.println("loading dummy data...\n");

        AnggotaImpl member = new Member();
        member.register("白上フブキ", "user0", "test");

        AnggotaImpl admin = new Admin();
        admin.register("猫又おかゆ", "nyaa", "cat");

        Perpustakaan.tambahBuku(new Buku());
        Perpustakaan.tambahAnggota(member);
        Perpustakaan.tambahAnggota(admin);

        Perpustakaan.disableLoad();
        Perpustakaan.disableSave();

        Perpustakaan.print();
    }

    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--debug")) {
                System.err.println("running on debug mode\nany saved data will not persist");
                debug();
            }
        }

        try {
            Perpustakaan.init();
        } catch (IOException ioe) {
            System.err.println(ioe);
        }

        new Frame();
    }
}
