public class Main {
    private static void debug() {
        Anggota anggota = new Anggota();
        anggota.register("内海アオバ", "aoba", "passwordTest");

        Perpustakaan.tambahBuku(new Buku());
        Perpustakaan.tambahAnggota(anggota);
    }

    public static void main(String[] args) {
        // SwingUtilities.invokeLater(() -> {
        // new Frame().setVisible(true);
        // });

        debug();

        new Frame();
    }
}
