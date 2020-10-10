package lab_5.saper.regForm;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class Form extends JFrame{


    private JButton cancelButton;
    private JButton loginButton;
    private JButton registerButton;
    private JTextField usernameFileld;
    private JPasswordField passwordField1;
    private JPanel root;
    private JPasswordField repeatPassField;
    private JLabel repeatField;

    private String dataPath = "./data.dat";
    private HashMap<String, String> data;


    public Form() {

        loadData();
        setContentPane(root);
        setTitle("sign in");

        registerButton.addActionListener(new RegisterModeAction());
        loginButton.addActionListener(el -> login());
        cancelButton.addActionListener(el -> System.exit(0));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        pack();
        setVisible(true);
    }


    private void registerNewUser() throws IOException {

        String username = usernameFileld.getText();
        String pass = new String(passwordField1.getPassword());
        String passRepeat = new String(repeatPassField.getPassword());

        if (this.data.get(username) != null){
            JOptionPane.showMessageDialog(this,
                    "User is registered");
            usernameFileld.setText("");
            passwordField1.setText("");
            repeatPassField.setText("");
        }else if (!pass.equals(passRepeat)){
            JOptionPane.showMessageDialog(this,
                    "passwords is unequals");
            passwordField1.setText("");
            repeatPassField.setText("");
        }else{
            data.put(username, encryptString(pass));
            saveData();
            JOptionPane.showMessageDialog(this,
                    "registration succesfull");
        }

    }

    private void login(){
        String username = usernameFileld.getText();
        String pass = new String(passwordField1.getPassword());

        if (this.data.get(username) == null){
            JOptionPane.showMessageDialog(this,
                    "User is unknown");
            usernameFileld.setText("");
            passwordField1.setText("");

        }else if (!data.get(username).equals(encryptString(pass))){
            JOptionPane.showMessageDialog(this,
                    "passwords incorrect");
            repeatPassField.setText("");
        }else{
            JOptionPane.showMessageDialog(this,
                    "login succesfull");
        }

    }


    class RegisterModeAction extends AbstractAction{

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            setTitle("Register");
            System.out.println("rr");
            loginButton.setVisible(false);
            repeatPassField.setVisible(true);
            repeatField.setVisible(true);
            registerButton.removeActionListener(this);
            registerButton.addActionListener(el -> {
                try {
                    registerNewUser();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            pack();
        }
    }


    private static String encryptString(String input)
    {
        try {
            // getInstance() method is called with algorithm SHA-512
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            // digest() method is called
            // to calculate message digest of the input string
            // returned as array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            StringBuilder hashtext = new StringBuilder(no.toString(16));

            // Add preceding 0s to make it 32 bit
            while (hashtext.length() < 32) {
                hashtext.insert(0, "0");
            }

            // return the HashText
            return hashtext.toString();
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadData() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataPath));
            data = (HashMap<String, String>) ois.readObject();
        }catch(IOException | java.lang.ClassNotFoundException ClassNotFoundException) {
            data = new HashMap<>();
        }
    }

    private void saveData() throws IOException {
        FileOutputStream fos = new FileOutputStream(dataPath);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this.data);
        oos.flush();
        oos.close();
    }

    public static void main(String[] args) {
        new Form();
    }
}
