/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package konsultasihukum;
import java.sql.Driver;
    import java.sql.DriverManager;
    import java.sql.Connection;
    import java.sql.SQLException;
    import java.sql.Statement;
    import java.sql.PreparedStatement;
    import java.sql.ResultSet;
    import javax.swing.JOptionPane;
/**
 *
 * @author Lil kuljar
 */
public class dbCRUD {
     String jdbcURL ="jdbc:mysql://localhost:3306/konsultasihukum";
    String username ="root";
    String password ="";
    
    Connection koneksi;
    
    public dbCRUD(){
        try (Connection Koneksi = DriverManager.getConnection(jdbcURL, username, password)){
            Driver mysqldriver = new com.mysql.jdbc.Driver();
            DriverManager.registerDriver(mysqldriver);
            System.out.println("Berhasil");
        } catch (SQLException error) {
            System.err.println(error.toString());
        }
    }
    
    public dbCRUD(String url, String user, String pass){
        
        try(Connection Koneksi = DriverManager.getConnection(url, user, pass)) {
            Driver mysqldriver = new com.mysql.jdbc.Driver();
            DriverManager.registerDriver(mysqldriver);
            
            System.out.println("Berhasil");
        } catch (Exception error) {
            System.err.println(error.toString());
        }
        
    }
    
   public static Connection getKoneksi() {
        try {
            String url = "jdbc:mysql://localhost:3306/konsultasihukum";  // Ganti dengan URL dan database Anda
            String username = "root";  // Ganti dengan username database Anda
            String password = "";  // Ganti dengan password database Anda
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            System.out.println("Koneksi Gagal: " + e.getMessage());
            return null;
        }
    }
    
    public boolean duplicateKey(String table, String PrimaryKey, String value){
        boolean hasil=false;
        
        try {
            Statement sts = getKoneksi().createStatement();
            ResultSet rs = sts.executeQuery("SELECT*FROM "+table+" WHERE "+PrimaryKey+" ='"+value+"'");
            hasil = rs.next();
            
            rs.close();
            sts.close();
            getKoneksi().close();
            
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        
        
        return hasil;
    }
    
    public void SimpanDVDStatement(String nip_admin, String nama, String alamat, String email, String telp){
        
        try {
            if (duplicateKey("admin","nip_admin",nip_admin)){
                JOptionPane.showMessageDialog(null, "data admin sudah terdaftar");
            } else{
                String SQL = "INSERT INTO admin (nip_admin,nama,alamat,email,telp) Value('"+nip_admin+"','"+nama+"','"+alamat+"','"+email+"','"+telp+"')";
                Statement perintah = getKoneksi().createStatement();
                
                perintah.executeUpdate(SQL);
                perintah.close();
                getKoneksi().close();
                JOptionPane.showMessageDialog(null, "Data Berhasil Disimpan");
                
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
    
    public void SimpanDVDPreparedstmt(String Kode, String Judul, String Genre, String stok, String tahun){
        try {
            if (duplicateKey("DVD", "KodeDVD", Kode)){
                JOptionPane.showMessageDialog(null, "Kode DVD sudah Terdaftar");
            } else{
                String SQL = "INSERT INTO DVD (KodeDVD,judul,Genre,stok,tahun) VALUE (?, ?, ?, ?, ?)";
                PreparedStatement simpan = getKoneksi().prepareStatement(SQL);
                simpan.setString(1, Kode);
                simpan.setString(2, Judul);
                simpan.setString(3, Genre);
                simpan.setString(4, stok);
                simpan.setString(5, tahun);
                simpan.executeUpdate();
                
                System.out.println("Data Berhasil Disimpan");
                
                simpan.close();
                getKoneksi().close();
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
    
    public void UpdateDVDPreparedstmt(String Kode, String Judul, String Genre, int stok, String tahun) {
        try {
            if (!duplicateKey("DVD", "KodeDVD", Kode)) {
                JOptionPane.showMessageDialog(null, "Kode DVD tidak ditemukan");
            } else {
                String SQL = "UPDATE DVD SET judul = ?, Genre = ?, stok = ?, tahun = ? WHERE KodeDVD = ?";
                PreparedStatement update = getKoneksi().prepareStatement(SQL);
                update.setString(1, Judul);
                update.setString(2, Genre);
                update.setInt(3, stok);
                update.setString(4, tahun);
                update.setString(5, Kode);
                update.executeUpdate();

                update.close();
                getKoneksi().close();
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    
    public void HapusDVDPreparedstmt(String Kode) {
        try {
            if (!duplicateKey("DVD", "KodeDVD", Kode)) {
                JOptionPane.showMessageDialog(null, "Kode DVD tidak ditemukan");
            } else {
                String SQL = "DELETE FROM DVD WHERE KodeDVD = ?";
                PreparedStatement hapus = getKoneksi().prepareStatement(SQL);
                hapus.setString(1, Kode);
                hapus.executeUpdate();

                hapus.close();
                getKoneksi().close();

                JOptionPane.showMessageDialog(null, "Data berhasil dihapus");
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
    
     public static String getFieldValueEdit(String[] Field, String[] value){
        String hasil = "";
        int deteksi = Field.length-1;
        try {
            for (int i = 0; i < Field.length; i++) {
                if (i==deteksi){
                    hasil = hasil +Field[i]+" ='"+value[i]+"'";
                }else{
                   hasil = hasil +Field[i]+" ='"+value[i]+"',";  
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        
        return hasil;
    }
     
   // Method untuk update data secara dinamis
    public static void UbahadminDinamis(String NamaTabel, String PrimaryKey, String IsiPrimary, String[] Field, String[] Value) {
        try {
            // Mencari apakah ID yang diberikan ada di dalam tabel
            String SQLCheck = "SELECT COUNT(*) FROM " + NamaTabel + " WHERE " + PrimaryKey + " = '" + IsiPrimary + "'";
            Statement perintah = getKoneksi().createStatement();
            ResultSet rs = perintah.executeQuery(SQLCheck);

            if (rs.next() && rs.getInt(1) > 0) { // Jika ID ditemukan
                // Melakukan update jika ID ada
                String SQLUbah = "UPDATE " + NamaTabel + " SET " + getFieldValueEdit(Field, Value) + " WHERE " + PrimaryKey + "='" + IsiPrimary + "'";
                perintah.executeUpdate(SQLUbah);
                JOptionPane.showMessageDialog(null, "Data Berhasil DiUpdate");
            } else { // Jika ID tidak ditemukan
                JOptionPane.showMessageDialog(null, "Data Tidak Ditemukan");
            }

            rs.close(); // Tutup ResultSet
            perintah.close(); // Tutup Statement
            getKoneksi().close(); // Tutup koneksi

        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
     
      public static void HapusadminDinamis(String NamaTabel, String PK, String isi){
        try {
            String SQL="DELETE FROM "+NamaTabel+" WHERE "+PK+"='"+isi+"'";
            Statement perintah = getKoneksi().createStatement();
            perintah.executeUpdate(SQL);
            perintah.close();
            JOptionPane.showMessageDialog(null,"Data Berhasil Dihapus");
        } catch (Exception e) {
            System.out.println(e.toString());
        }
}


}
