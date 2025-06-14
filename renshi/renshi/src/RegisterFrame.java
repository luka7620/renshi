import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class RegisterFrame extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField registrationCodeField;
    private UserDAO userDAO = new UserDAO();

    public RegisterFrame(JFrame parent) {
        super(parent, "注册新账号", true);
        setSize(400, 350);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // 创建注册面板
        JPanel registerPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        registerPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();
        registrationCodeField = new JTextField();

        registerPanel.add(new JLabel("用户名:"));
        registerPanel.add(usernameField);
        registerPanel.add(new JLabel("密码:"));
        registerPanel.add(passwordField);
        registerPanel.add(new JLabel("确认密码:"));
        registerPanel.add(confirmPasswordField);
        registerPanel.add(new JLabel("注册码:"));
        registerPanel.add(registrationCodeField);

        // 注册按钮
        JButton registerButton = new JButton("注册");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());
                String registrationCode = registrationCodeField.getText();

                // 验证输入
                if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || registrationCode.isEmpty()) {
                    JOptionPane.showMessageDialog(RegisterFrame.this, "请填写所有字段");
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(RegisterFrame.this, "两次输入的密码不一致");
                    return;
                }

                try {
                    if (userDAO.register(username, password, registrationCode)) {
                        JOptionPane.showMessageDialog(RegisterFrame.this, "注册成功，请登录");
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(RegisterFrame.this, "注册失败：无效的注册码或用户名已存在");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(RegisterFrame.this, "注册失败: " + ex.getMessage());
                }
            }
        });

        // 取消按钮
        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        registerPanel.add(new JLabel()); // 占位
        registerPanel.add(buttonPanel);

        add(registerPanel, BorderLayout.CENTER);
        setVisible(true);
    }
}    