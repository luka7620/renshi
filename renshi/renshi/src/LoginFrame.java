import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private UserDAO userDAO = new UserDAO();

    public LoginFrame() {
        setTitle("人事管理系统 - 登录");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 创建登录面板
        JPanel loginPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        loginPanel.add(new JLabel("用户名:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("密码:"));
        loginPanel.add(passwordField);

        // 登录按钮
        JButton loginButton = new JButton("登录");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                try {
                    if (userDAO.authenticate(username, password)) {
                        JOptionPane.showMessageDialog(LoginFrame.this, "登录成功");
                        dispose();
                        new MainFrame(username); // 传递用户名到主界面
                    } else {
                        JOptionPane.showMessageDialog(LoginFrame.this, "用户名或密码错误");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(LoginFrame.this, "登录失败: " + ex.getMessage());
                }
            }
        });

        // 注册按钮
        JButton registerButton = new JButton("注册");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RegisterFrame(LoginFrame.this);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        loginPanel.add(new JLabel()); // 占位
        loginPanel.add(buttonPanel);

        add(loginPanel, BorderLayout.CENTER);
        setVisible(true);
    }
}    