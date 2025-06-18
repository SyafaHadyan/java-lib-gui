public interface Anggota {
    public boolean register(String nama, String username, String password);

    public boolean login(String username, String password);

    public String getRole(String usrname, String password);

    public void changeInfo(String nama, String username, String password);

    public String raw();
}
