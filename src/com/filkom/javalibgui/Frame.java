import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

interface Refresh {
    public abstract void refresh();
}

public class Frame extends JFrame {
    private static final String VERSION = "v1.0";
    private CardLayout cards = new CardLayout();
    private JPanel cardPanel = new JPanel(cards);

    private String username = "";
    private String role = "";
    private AnggotaImpl member = new Member();
    private AnggotaImpl admin = new Admin();

    EditBookPanel editBookPanel = new EditBookPanel();
    AddBookPanel addBookPanel = new AddBookPanel();
    BookAdministrationPanel bookAdministrationPanel = new BookAdministrationPanel();
    ListBooksPanel listBooksPanel = new ListBooksPanel();
    SearchPanel searchPanel = new SearchPanel();
    UserInfoPanel userInfoPanel = new UserInfoPanel();

    public Frame() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Java libMan " + VERSION);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);

        cardPanel.add(new MainMenuPanel(), "MENU");
        cardPanel.add(new AuthPanel(), "LOGIN");
        cardPanel.add(new EditBookPanel(), "EDITBOOK");
        cardPanel.add(new BookAdministrationPanel(), "BOOKADMIN");
        cardPanel.add(new AddBookPanel(), "ADDBOOK");
        cardPanel.add(new ListBooksPanel(), "LIST");
        cardPanel.add(new SearchPanel(), "SEARCH");
        cardPanel.add(new UserInfoPanel(), "USERINFO");

        add(cardPanel);
        setResizable(false);
        authScreen();
        setVisible(true);
    }

    private void authScreen() {
        if (username.isEmpty()) {
            cards.show(cardPanel, "LOGIN");
        } else {
            cards.show(cardPanel, "MENU");
        }

        updateAccess();
    }

    private void updateAccess() {
        editBookPanel.refresh();
        addBookPanel.refresh();
    }

    private JButton createBackButton(String target) {
        JButton back = new JButton("Back");

        back.setToolTipText("Kembali ke menu utama");

        back.addActionListener(e -> {
            if ("LOGIN".equals(target)) {
                username = "";
            }
            cards.show(cardPanel, target);
        });

        return back;
    }

    private int save() {
        int status = 0;

        try {
            Perpustakaan.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();

            ++status;
        }

        return status;
    }

    private void exit() {
        int statusExit = save();

        System.exit(statusExit);
    }

    // Auth
    private class AuthPanel extends JPanel {
        public AuthPanel() {
            setLayout(new GridBagLayout());
            setBorder(new EmptyBorder(20, 20, 20, 20));
            setBackground(Color.decode("#EAE8FF"));

            GridBagConstraints gbc = new GridBagConstraints();

            gbc.insets = new Insets(10, 10, 10, 10);

            JButton adminLoginButton = new JButton("Login (admin)");
            JButton loginButton = new JButton("Login");
            JButton registerButton = new JButton("Register");
            JButton exitButton = new JButton("Exit");

            adminLoginButton.setToolTipText("Login akun (admin)");
            loginButton.setToolTipText("Login akun");
            registerButton.setToolTipText("Register pengguna baru");
            exitButton.setToolTipText("Keluar aplikasi");

            adminLoginButton.setBackground(Color.BLACK);
            adminLoginButton.setForeground(Color.WHITE);

            loginButton.setBackground(Color.decode("#95B2B0"));
            loginButton.setForeground(Color.BLACK);

            registerButton.setBackground(Color.decode("#395B50"));
            registerButton.setForeground(Color.WHITE);

            exitButton.setBackground(Color.decode("#F15025"));
            exitButton.setForeground(Color.BLACK);

            gbc.gridx = 0;
            gbc.gridy = 0;
            add(adminLoginButton, gbc);

            gbc.gridx = 1;
            add(loginButton, gbc);

            gbc.gridx = 2;
            add(registerButton, gbc);

            gbc.gridx = 3;
            add(exitButton, gbc);

            // Login Admin
            adminLoginButton.addActionListener(e -> {
                String user = JOptionPane.showInputDialog(this, "Username:");
                String password = JOptionPane.showInputDialog(this, "Password:");

                boolean ok = admin.login(user, password);
                role = Perpustakaan.getAnggotaRole(user, password);

                System.err.println(role);

                if (ok && role.equals("admin")) {
                    username = admin.getUsername();

                    editBookPanel.setEnabled(true);
                    addBookPanel.setEnabled(true);

                    authScreen();

                    updateAccess();

                    System.err.println(admin.toString());
                } else {
                    JOptionPane.showMessageDialog(this, "Username atau password tidak valid");
                }

                listBooksPanel.refresh();
                userInfoPanel.refresh();

            });

            // Login User
            loginButton.addActionListener(e -> {
                String user = JOptionPane.showInputDialog(this, "Username:");
                String password = JOptionPane.showInputDialog(this, "Password:");

                boolean ok = member.login(user, password);
                role = Perpustakaan.getAnggotaRole(user, password);

                System.err.println(role);

                if (ok && role.equals("member")) {
                    username = member.getUsername();

                    authScreen();

                    updateAccess();

                    System.err.println(member.toString());
                } else {
                    JOptionPane.showMessageDialog(this, "Username atau password tidak valid");
                }

                listBooksPanel.refresh();
                userInfoPanel.refresh();

            });

            // Register
            registerButton.addActionListener(e -> {
                String nama = JOptionPane.showInputDialog(this, "Nama:");
                String user = JOptionPane.showInputDialog(this, "Username:");
                String pass = JOptionPane.showInputDialog(this, "Password:");

                boolean ok = member.register(nama, user, pass);

                if (ok) {
                    Perpustakaan.tambahAnggota(member);

                    username = member.getUsername();

                    editBookPanel.setEnabled(false);
                    addBookPanel.setEnabled(false);

                    role = "member";

                    authScreen();

                    updateAccess();
                } else {
                    JOptionPane.showMessageDialog(this, "Username tidak tersedia");
                }

                listBooksPanel.refresh();
                userInfoPanel.refresh();

                System.err.println(member);
            });

            // Exit
            exitButton.addActionListener(e -> {
                exit();
            });
        }
    }

    // Main Menu
    private class MainMenuPanel extends JPanel {
        public MainMenuPanel() {
            setLayout(new BorderLayout());
            setBackground(Color.decode("#F3E8EE"));

            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.setFont(new Font("SansSerif", Font.PLAIN, 14));

            tabbedPane.addTab("Edit Buku", editBookPanel);
            tabbedPane.addTab("Tambah Buku", addBookPanel);
            tabbedPane.addTab("Administrasi Buku", bookAdministrationPanel);
            tabbedPane.addTab("Daftar Buku", listBooksPanel);
            tabbedPane.addTab("Cari Buku", searchPanel);
            tabbedPane.addTab("Info User", userInfoPanel);

            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton logoutbutton = new JButton("Logout");
            JButton saveButton = new JButton("Simpan");
            JButton exitButton = new JButton("Exit");
            JButton exitForceButton = new JButton("Exit (Force)");

            logoutbutton.setToolTipText("Keluar akun");
            saveButton.setToolTipText("Simpan perubahan");
            exitButton.setToolTipText("Keluar aplikasi");
            exitForceButton.setToolTipText("Keluar aplikasi tanpa menyimpan data");

            saveButton.setBackground(Color.decode("#98CBB4"));
            saveButton.setForeground(Color.BLACK);

            exitButton.setBackground(Color.decode("#F15025"));
            exitButton.setForeground(Color.BLACK);

            exitForceButton.setBackground(Color.decode("#040F16"));
            exitForceButton.setForeground(Color.WHITE);

            topPanel.add(logoutbutton);
            topPanel.add(saveButton);
            topPanel.add(exitButton);
            topPanel.add(exitForceButton);

            add(topPanel, BorderLayout.NORTH);
            add(tabbedPane, BorderLayout.CENTER);

            logoutbutton.addActionListener(e -> {
                username = "";
                role = "";

                member = new Member();
                admin = new Admin();

                authScreen();
            });

            saveButton.addActionListener(e -> {
                save();
            });

            exitButton.addActionListener(e -> {
                exit();
            });

            exitForceButton.addActionListener(e -> {
                System.exit(0);
            });
        }
    }

    // Edit Book
    private class EditBookPanel extends JPanel implements Refresh {
        JButton searchIDButton = new JButton("Cari");
        JButton editButton = new JButton("Simpan");
        JButton clearButton = new JButton("Clear");

        public EditBookPanel() {
            setLayout(new GridBagLayout());
            setBorder(new EmptyBorder(20, 20, 20, 20));
            setBackground(Color.decode("#EAE8FF"));

            GridBagConstraints gbc = new GridBagConstraints();

            JLabel IDLabel = new JLabel("ID Buku:");
            JTextField IDField = new JTextField(32);

            JLabel titleLabel = new JLabel("Judul:");
            JTextField titleField = new JTextField(32);

            JLabel authorLabel = new JLabel("Penulis:");
            JTextField authorField = new JTextField(32);

            JLabel coverLabel = new JLabel("Cover URL:");
            JTextField coverField = new JTextField(32);

            JLabel yearLabel = new JLabel("Tahun terbit:");
            JTextField yearField = new JTextField(8);

            JLabel monthLabel = new JLabel("Bulan terbit:");
            JTextField monthField = new JTextField(8);

            JLabel dayLabel = new JLabel("Hari terbit:");
            JTextField dayField = new JTextField(8);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            buttonPanel.add(editButton);
            buttonPanel.add(clearButton);

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 0;
            add(IDLabel, gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            add(IDField, gbc);
            gbc.gridx = 2;
            gbc.weightx = 0;
            add(searchIDButton, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weightx = 0;
            add(titleLabel, gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            add(titleField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.weightx = 0;
            add(authorLabel, gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            add(authorField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.weightx = 0;
            add(coverLabel, gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            add(coverField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 4;
            gbc.weightx = 0;
            add(yearLabel, gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            add(yearField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 5;
            gbc.weightx = 0;
            add(monthLabel, gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.9;
            add(monthField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 6;
            gbc.weightx = 0;
            add(dayLabel, gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            add(dayField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 7;
            gbc.gridwidth = 2;
            gbc.weightx = 0;
            add(buttonPanel, gbc);

            searchIDButton.setToolTipText("Cari buku dengan ID buku");
            editButton.setToolTipText("Edit buku");
            clearButton.setToolTipText("Clear field buku");

            buttonPanel.setBackground(Color.decode("#EAE8FF"));

            searchIDButton.setBackground(Color.decode("#1E2D2F"));
            searchIDButton.setForeground(Color.WHITE);

            editButton.setBackground(Color.decode("#395B50"));
            editButton.setForeground(Color.WHITE);

            clearButton.setBackground(Color.decode("#C6EBBE"));
            clearButton.setForeground(Color.BLACK);

            searchIDButton.addActionListener(e -> {
                try {
                    UUID IDBuku = UUID.fromString(IDField.getText());
                    Buku buku = Perpustakaan.infoBuku(IDBuku);

                    IDField.setEditable(false);
                    titleField.setText(buku.getJudul());
                    authorField.setText(buku.getPenulis());
                    coverField.setText(buku.getCoverBuku().toString());
                    yearField.setText(String.valueOf(buku.getTanggalTerbit().getYear()));
                    monthField.setText(String.valueOf(buku.getTanggalTerbit().getMonthValue()));
                    dayField.setText(String.valueOf(buku.getTanggalTerbit().getDayOfMonth()));

                    System.err.println(buku.raw());
                } catch (IllegalArgumentException | BukuTidakDitemukanException ex) {
                    JOptionPane.showMessageDialog(this, "ID Buku tidak valid");
                }
            });

            editButton.addActionListener(e -> {
                try {
                    String judul = titleField.getText();
                    String penulis = authorField.getText();
                    URL coverBuku = new URI(coverField.getText()).toURL();
                    int year = Integer.parseUnsignedInt(yearField.getText());
                    int month = Integer.parseUnsignedInt(monthField.getText());
                    int day = Integer.parseUnsignedInt(dayField.getText());
                    LocalDate tanggalTerbit = LocalDate.of(year, month, day);

                    Buku buku = Perpustakaan.infoBuku(UUID.fromString(IDField.getText()));

                    buku.setJudul(judul);
                    buku.setPenulis(penulis);
                    buku.setCoverBuku(coverBuku);
                    buku.setTanggalTerbit(tanggalTerbit);

                    Perpustakaan.updateBuku(UUID.fromString(IDField.getText()), buku);
                    JOptionPane.showMessageDialog(
                            this,
                            String.format(
                                    "Berhasil mengedit buku\n\n%s",
                                    buku.raw()));

                    IDField.setEditable(true);

                    System.err.println(buku.raw());
                } catch (URISyntaxException | MalformedURLException ex) {
                    JOptionPane.showMessageDialog(this, "URL tidak valid");
                } catch (NumberFormatException | DateTimeException ex) {
                    JOptionPane.showMessageDialog(this, "Tanggal tidak valid");
                } catch (IllegalArgumentException | BukuTidakDitemukanException ex) {
                    JOptionPane.showMessageDialog(this, "ID Buku tidak valid");
                } finally {
                    listBooksPanel.refresh();
                }
            });

            clearButton.addActionListener(e -> {
                IDField.setEditable(true);

                IDField.setText("");
                titleField.setText("");
                authorField.setText("");
                coverField.setText("");
                yearField.setText("");
                monthField.setText("");
                dayField.setText("");
            });
        }

        @Override
        public void refresh() {
            boolean isAdmin = role.equals("admin");

            searchIDButton.setEnabled(isAdmin);
            editButton.setEnabled(isAdmin);
            clearButton.setEnabled(isAdmin);
        }
    }

    // Add Book
    private class AddBookPanel extends JPanel implements Refresh {
        JButton addBookButton = new JButton("Tambah");

        public AddBookPanel() {
            setLayout(new GridBagLayout());
            setBorder(new EmptyBorder(20, 20, 20, 20));
            setBackground(Color.decode("#EAE8FF"));

            GridBagConstraints gbc = new GridBagConstraints();

            JLabel titleLabel = new JLabel("Judul:");
            JTextField titleField = new JTextField(32);

            JLabel authorLabel = new JLabel("Penulis:");
            JTextField authorField = new JTextField(32);

            JLabel coverLabel = new JLabel("Cover URL:");
            JTextField coverField = new JTextField(32);

            JLabel yearLabel = new JLabel("Tahun terbit:");
            JTextField yearField = new JTextField(8);

            JLabel monthLabel = new JLabel("Bulan terbit:");
            JTextField monthField = new JTextField(8);

            JLabel dayLabel = new JLabel("Hari terbit:");
            JTextField dayField = new JTextField(8);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            buttonPanel.add(addBookButton);

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 0;
            add(titleLabel, gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            add(titleField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weightx = 0;
            add(authorLabel, gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            add(authorField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.weightx = 0;
            add(coverLabel, gbc);

            gbc.gridx = 1;
            gbc.weightx = 1.0;
            add(coverField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.weightx = 0;
            add(yearLabel, gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            add(yearField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 4;
            gbc.weightx = 0;
            add(monthLabel, gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.9;
            add(monthField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 5;
            gbc.weightx = 0;
            add(dayLabel, gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            add(dayField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 6;
            gbc.gridwidth = 2;
            gbc.weightx = 0;
            add(buttonPanel, gbc);

            addBookButton.setToolTipText("Tambah buku ke perpustakaan");

            buttonPanel.setBackground(Color.decode("#EAE8FF"));

            addBookButton.setBackground(Color.decode("#395B50"));
            addBookButton.setForeground(Color.WHITE);

            addBookButton.addActionListener(e -> {
                try {
                    URL coverBuku = new URI(coverField.getText()).toURL();
                    int year = Integer.parseUnsignedInt(yearField.getText());
                    int month = Integer.parseUnsignedInt(monthField.getText());
                    int day = Integer.parseUnsignedInt(dayField.getText());
                    LocalDate tanggalTerbit = LocalDate.of(year, month, day);

                    Buku buku = new Buku(
                            titleField.getText(),
                            authorField.getText(),
                            coverBuku,
                            tanggalTerbit);

                    Perpustakaan.tambahBuku(buku);
                    JOptionPane.showMessageDialog(
                            this,
                            String.format(
                                    "Berhasil menambahkan buku\n\n%s",
                                    buku.raw()));

                    System.err.println(buku.raw());
                } catch (URISyntaxException | MalformedURLException ex) {
                    JOptionPane.showMessageDialog(this, "URL tidak valid");
                } catch (NumberFormatException | DateTimeException ex) {
                    JOptionPane.showMessageDialog(this, "Tanggal tidak valid");
                } finally {
                    listBooksPanel.refresh();
                }
            });
        }

        @Override
        public void refresh() {
            boolean isAdmin = role.equals("admin");

            addBookButton.setEnabled(isAdmin);
        }
    }

    // Book Administration
    private class BookAdministrationPanel extends JPanel {
        public BookAdministrationPanel() {
            setLayout(new GridBagLayout());
            setBorder(new EmptyBorder(20, 20, 20, 20));
            setBackground(Color.decode("#EAE8FF"));

            GridBagConstraints gbc = new GridBagConstraints();

            JLabel IDLabel = new JLabel("ID Buku (UUID):");
            JTextField IDField = new JTextField(36);

            JButton borrowButton = new JButton("Pinjam");
            JButton returnButton = new JButton("Kembalikan");

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            buttonPanel.add(borrowButton);
            buttonPanel.add(returnButton);

            gbc.gridx = 0;
            gbc.gridy = 0;
            add(IDLabel, gbc);
            gbc.gridx = 1;
            add(IDField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            add(buttonPanel, gbc);

            borrowButton.setToolTipText("Pinjam buku dengan ID buku");
            returnButton.setToolTipText("Kembalikan buku dengan ID buku");

            buttonPanel.setBackground(Color.decode("#EAE8FF"));

            borrowButton.setBackground(Color.decode("#395B50"));
            borrowButton.setForeground(Color.WHITE);

            returnButton.setBackground(Color.decode("#C6EBBE"));
            returnButton.setForeground(Color.BLACK);

            borrowButton.addActionListener(e -> {
                try {
                    UUID id = UUID.fromString(IDField.getText());
                    Member.pinjamBuku(username, id);
                    JOptionPane.showMessageDialog(this, "Berhasil meminjam buku");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                } finally {
                    listBooksPanel.refresh();
                }
            });

            returnButton.addActionListener(e -> {
                try {
                    UUID id = UUID.fromString(IDField.getText());
                    Member.kembaliBuku(username, id);
                    JOptionPane.showMessageDialog(this, "Berhasil mengembalikan buku");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                } finally {
                    listBooksPanel.refresh();
                }
            });
        }
    }

    // List Books
    private class ListBooksPanel extends JPanel implements Refresh {
        JTextArea area = new JTextArea();

        public ListBooksPanel() {
            setLayout(new BorderLayout(10, 10));
            setBorder(new EmptyBorder(20, 20, 20, 20));
            setBackground(Color.decode("#EAE8FF"));

            area.setEditable(false);

            area.setFont(new Font("Monospaced", Font.PLAIN, 12));

            JButton back = createBackButton("MENU");

            add(new JScrollPane(area), BorderLayout.CENTER);
            add(back, BorderLayout.SOUTH);

            refresh();
        }

        @Override
        public void refresh() {
            StringBuilder sb = new StringBuilder();

            Perpustakaan.daftarBuku().forEach(b -> sb.append(b.raw()).append("\n"));
            area.setText(sb.toString());

            System.err.println(sb);
        }
    }

    // Search
    private class SearchPanel extends JPanel {
        public SearchPanel() {
            setLayout(new GridLayout(0, 1, 10, 10));
            setBorder(new EmptyBorder(20, 20, 20, 20));
            setBackground(Color.decode("#EAE8FF"));

            JButton byID = new JButton("Cari berdasarkan ID buku");
            JButton byTitle = new JButton("Cari berdasarkan judul buku");
            JButton byAuthor = new JButton("Cari berdasarkan penulis");
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

            byID.setToolTipText("Cari buku berdasarkan ID buku");
            byTitle.setToolTipText("Cari buku berdasarkan judul buku");
            byAuthor.setToolTipText("Cari buku berdasarkan penulis");

            buttonPanel.add(byID);
            buttonPanel.add(byTitle);
            buttonPanel.add(byAuthor);

            add(buttonPanel);

            buttonPanel.setBackground(Color.decode("#EAE8FF"));

            byID.addActionListener(e -> {
                String input = JOptionPane.showInputDialog(this, "ID Buku:");
                try {
                    Buku b = Perpustakaan.cariIDBuku(UUID.fromString(input));
                    JOptionPane.showMessageDialog(this, b.raw());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "ID buku tidak valid");
                }
            });

            byTitle.addActionListener(e -> {
                String judulBuku = JOptionPane.showInputDialog(this, "Judul:");
                try {
                    List<Buku> buku = Perpustakaan.cariJudulBuku(judulBuku);

                    buku.forEach(b -> JOptionPane.showMessageDialog(this, b.raw()));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Buku tidak ditemukan");
                }
            });

            byAuthor.addActionListener(e -> {
                String penulis = JOptionPane.showInputDialog(this, "Penulis:");

                try {
                    List<Buku> buku = Perpustakaan.cariPenulis(penulis);

                    buku.forEach(b -> JOptionPane.showMessageDialog(this, b.raw()));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Buku tidak ditemukan");
                }
            });
        }
    }

    // User Info
    private class UserInfoPanel extends JPanel implements Refresh {
        JLabel namaLabel = new JLabel("Nama");
        JTextField namaField = new JTextField();

        JLabel usernameLabel = new JLabel("Username");
        JTextField usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Password");
        JTextField passwordField = new JTextField();

        JButton saveButton = new JButton("Simpan");
        JButton backButton = createBackButton("MENU");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        public UserInfoPanel() {
            setLayout(new GridBagLayout());
            setBorder(new EmptyBorder(20, 20, 20, 20));
            setBackground(Color.decode("#EAE8FF"));

            GridBagConstraints gbc = new GridBagConstraints();

            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            buttonPanel.add(saveButton);
            buttonPanel.add(backButton);

            gbc.gridx = 0;
            gbc.gridy = 0;
            add(namaLabel, gbc);

            gbc.gridx = 1;
            add(namaField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            add(usernameLabel, gbc);

            gbc.gridx = 1;
            add(usernameField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            add(passwordLabel, gbc);

            gbc.gridx = 1;
            add(passwordField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 3;

            gbc.gridwidth = 2;
            add(buttonPanel, gbc);

            usernameField.setEditable(false);
            usernameField.setToolTipText("Username tidak bisa diedit");

            saveButton.setToolTipText("Simpan info user");

            buttonPanel.setBackground(Color.decode("#EAE8FF"));

            saveButton.setBackground(Color.decode("#395B50"));
            saveButton.setForeground(Color.WHITE);

            saveButton.addActionListener(e -> {
                try {
                    member.changeInfo(namaField.getText(), usernameField.getText(), passwordField.getText());
                } catch (Exception ex) {
                    System.err.println(ex);
                } finally {
                    refresh();
                }
            });
        }

        @Override
        public void refresh() {
            if (role.equals("member")) {
                namaField.setText(member.getNama());
                usernameField.setText(member.getUsername());
                passwordField.setText(member.getPassword());

                System.err.println(member);
            } else if (role.equals("admin")) {
                namaField.setText(admin.getNama());
                usernameField.setText(admin.getUsername());
                passwordField.setText(admin.getPassword());

                System.err.println(admin);
            }
        }
    }
}
