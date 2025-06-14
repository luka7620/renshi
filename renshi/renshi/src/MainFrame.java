import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class MainFrame extends JFrame {
    private final JPanel contentPanel;
    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private final TransferHistoryDAO transferDAO = new TransferHistoryDAO();
    private final AssessmentHistoryDAO assessmentDAO = new AssessmentHistoryDAO();
    private String currentUser;

    public MainFrame(String username) {
        this.currentUser = username;
        setTitle("人事管理系统 - 欢迎 " + username);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 创建菜单树
        createMenuTree();

        // 创建内容面板
        contentPanel = new JPanel(new BorderLayout());
        add(new JScrollPane(contentPanel), BorderLayout.CENTER);

        // 添加状态栏显示当前用户
        JLabel statusLabel = new JLabel("当前用户: " + username);
        add(statusLabel, BorderLayout.SOUTH);

        // 显示欢迎信息
        showWelcomeMessage();

        setVisible(true);
    }

    private void showWelcomeMessage() {
        contentPanel.removeAll();

        JLabel welcomeLabel = new JLabel("欢迎使用人事管理系统", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        contentPanel.add(welcomeLabel, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void createMenuTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("人事管理系统");

        // 基本信息管理
        DefaultMutableTreeNode basicInfoNode = new DefaultMutableTreeNode("基本信息管理");
        basicInfoNode.add(new DefaultMutableTreeNode("员工信息管理"));
        basicInfoNode.add(new DefaultMutableTreeNode("部门信息管理"));
        root.add(basicInfoNode);

        // 人事变动管理
        DefaultMutableTreeNode personnelChangeNode = new DefaultMutableTreeNode("人事变动管理");
        personnelChangeNode.add(new DefaultMutableTreeNode("员工调动"));
        personnelChangeNode.add(new DefaultMutableTreeNode("薪资调整"));
        root.add(personnelChangeNode);

        // 考核管理
        DefaultMutableTreeNode assessmentNode = new DefaultMutableTreeNode("考核管理");
        assessmentNode.add(new DefaultMutableTreeNode("添加考核"));
        assessmentNode.add(new DefaultMutableTreeNode("查看考核记录"));
        root.add(assessmentNode);

        // 统计分析
        DefaultMutableTreeNode statisticsNode = new DefaultMutableTreeNode("统计分析");
        statisticsNode.add(new DefaultMutableTreeNode("部门人数统计"));
        statisticsNode.add(new DefaultMutableTreeNode("薪资分布"));
        root.add(statisticsNode);

        JTree menuTree = new JTree(root);
        menuTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        menuTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
                if (selectedNode == null) return;

                String nodeName = selectedNode.toString();
                switch (nodeName) {
                    case "员工信息管理":
                        showEmployeeList();
                        break;
                    case "部门信息管理":
                        showDepartmentList();
                        break;
                    case "员工调动":
                        showTransferPanel();
                        break;
                    case "薪资调整":
                        showSalaryAdjustmentPanel();
                        break;
                    case "添加考核":
                        try {
                            showAddAssessmentPanel();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                        break;
                    case "查看考核记录":
                        showAssessmentHistoryPanel();
                        break;
                    case "部门人数统计":
                        showDepartmentStatistics();
                        break;
                    case "薪资分布":
                        showSalaryDistribution();
                        break;
                }
            }
        });

        JScrollPane treeScrollPane = new JScrollPane(menuTree);
        treeScrollPane.setPreferredSize(new Dimension(200, getHeight()));
        add(treeScrollPane, BorderLayout.WEST);
    }


    private void showEmployeeList() {
        contentPanel.removeAll();

        try {
            List<Employee> employees = employeeDAO.getAllEmployees();
            String[] columnNames = {"员工编号", "姓名", "部门", "出生日期", "民族", "地址", "薪资", "性别(男1女2）", "户籍(本地1外地2)"};
            Object[][] data = new Object[employees.size()][9];

            for (int i = 0; i < employees.size(); i++) {
                Employee emp = employees.get(i);
                data[i][0] = emp.getNo();
                data[i][1] = emp.getName();
                data[i][2] = emp.getDept();
                data[i][3] = emp.getBornday();
                data[i][4] = emp.getMinority();
                data[i][5] = emp.getAddress();
                data[i][6] = emp.getSalary();
                data[i][7] = emp.getText();
                data[i][8] = emp.getOther();
            }

            DefaultTableModel model = new DefaultTableModel(data, columnNames);
            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);

            // 添加按钮面板
            JPanel buttonPanel = new JPanel();
            JButton addButton = new JButton("添加");
            JButton editButton = new JButton("修改");
            JButton deleteButton = new JButton("删除");
            JButton viewHistoryButton = new JButton("查看历史记录");

            addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showAddEmployeeDialog();
                }
            });

            editButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow >= 0) {
                        int empNo = (int) table.getValueAt(selectedRow, 0);
                        showEditEmployeeDialog(empNo);
                    } else {
                        JOptionPane.showMessageDialog(MainFrame.this, "请选择要修改的员工");
                    }
                }
            });

            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow >= 0) {
                        int empNo = (int) table.getValueAt(selectedRow, 0);
                        int confirm = JOptionPane.showConfirmDialog(MainFrame.this, "确定要删除该员工吗？", "确认删除", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            try {
                                employeeDAO.deleteEmployee(empNo);
                                JOptionPane.showMessageDialog(MainFrame.this, "删除成功");
                                showEmployeeList();
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(MainFrame.this, "删除失败: " + ex.getMessage());
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(MainFrame.this, "请选择要删除的员工");
                    }
                }
            });

            viewHistoryButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow >= 0) {
                        int empNo = (int) table.getValueAt(selectedRow, 0);
                        showEmployeeHistoryDialog(empNo);
                    } else {
                        JOptionPane.showMessageDialog(MainFrame.this, "请选择要查看的员工");
                    }
                }
            });

            buttonPanel.add(addButton);
            buttonPanel.add(editButton);
            buttonPanel.add(deleteButton);
            buttonPanel.add(viewHistoryButton);

            contentPanel.add(buttonPanel, BorderLayout.NORTH);
            contentPanel.add(scrollPane, BorderLayout.CENTER);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "获取员工列表失败: " + ex.getMessage());
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showAddEmployeeDialog() {
        JDialog dialog = new JDialog(this, "添加员工", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // 设置组件之间的间距
        gbc.fill = GridBagConstraints.HORIZONTAL; // 让组件在水平方向上填充

        JLabel noLabel = new JLabel("员工编号:");
        JTextField noField = new JTextField();
        JLabel nameLabel = new JLabel("姓名:");
        JTextField nameField = new JTextField();
        JLabel deptLabel = new JLabel("部门:");
        JTextField deptField = new JTextField();
        JLabel borndayLabel = new JLabel("出生日期:");
        JTextField borndayField = new JTextField();
        JLabel minorityLabel = new JLabel("民族:");
        JTextField minorityField = new JTextField();
        JLabel addressLabel = new JLabel("地址:");
        JTextField addressField = new JTextField();
        JLabel salaryLabel = new JLabel("薪资:");
        JTextField salaryField = new JTextField();
        JLabel textLabel = new JLabel("性别(男1女2）:");
        JTextField textField = new JTextField();
        JLabel otherLabel = new JLabel("户籍(本地1外地2):");
        JTextField otherField = new JTextField();

        // 标签布局
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.weightx = 0.2; // 设置标签的权重
        dialog.add(noLabel, gbc);
        gbc.gridy = 1;
        dialog.add(nameLabel, gbc);
        gbc.gridy = 2;
        dialog.add(deptLabel, gbc);
        gbc.gridy = 3;
        dialog.add(borndayLabel, gbc);
        gbc.gridy = 4;
        dialog.add(minorityLabel, gbc);
        gbc.gridy = 5;
        dialog.add(addressLabel, gbc);
        gbc.gridy = 6;
        dialog.add(salaryLabel, gbc);
        gbc.gridy = 7;
        dialog.add(textLabel, gbc);
        gbc.gridy = 8;
        dialog.add(otherLabel, gbc);

        // 文本框布局
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0.8; // 设置文本框的权重
        dialog.add(noField, gbc);
        gbc.gridy = 1;
        dialog.add(nameField, gbc);
        gbc.gridy = 2;
        dialog.add(deptField, gbc);
        gbc.gridy = 3;
        dialog.add(borndayField, gbc);
        gbc.gridy = 4;
        dialog.add(minorityField, gbc);
        gbc.gridy = 5;
        dialog.add(addressField, gbc);
        gbc.gridy = 6;
        dialog.add(salaryField, gbc);
        gbc.gridy = 7;
        dialog.add(textField, gbc);
        gbc.gridy = 8;
        dialog.add(otherField, gbc);

        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(e -> {
            try {
                int no = Integer.parseInt(noField.getText());
                String name = nameField.getText();
                String dept = deptField.getText();
                int bornday = Integer.parseInt(borndayField.getText());
                String minority = minorityField.getText();
                String address = addressField.getText();
                int salary = Integer.parseInt(salaryField.getText());
                String text = textField.getText();
                String other = otherField.getText();

                Employee emp = new Employee(no, name, dept, bornday, minority, address, salary, text, other);
                employeeDAO.addEmployee(emp);
                JOptionPane.showMessageDialog(this, "员工信息添加成功");
                dialog.dispose();
                showEmployeeList();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "请输入有效的数字");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "添加员工信息失败: " + ex.getMessage());
            }
        });

        // 保存按钮布局
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0; // 重置按钮的权重
        dialog.add(saveButton, gbc);

        // 根据组件的首选大小调整对话框大小
        dialog.setMinimumSize(new Dimension(500, 0)); // 最小宽度500，高度自适应
        dialog.pack(); // 先设置最小宽度，再自动调整高度
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }


    private void showEditEmployeeDialog(int empNo) {
        try {
            Employee emp = employeeDAO.getEmployeeByNo(empNo);
            if (emp == null) {
                JOptionPane.showMessageDialog(this, "员工不存在");
                return;
            }

            JDialog dialog = new JDialog(this, "修改员工信息", true);
            dialog.setSize(400, 500);
            dialog.setLayout(new BorderLayout());

            JPanel panel = new JPanel(new GridLayout(9, 2, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JTextField noField = new JTextField(String.valueOf(emp.getNo()));
            JTextField nameField = new JTextField(emp.getName());
            JTextField deptField = new JTextField(emp.getDept());
            JTextField bornField = new JTextField(String.valueOf(emp.getBornday()));
            JTextField minorityField = new JTextField(emp.getMinority());
            JTextField addressField = new JTextField(emp.getAddress());
            JTextField salaryField = new JTextField(String.valueOf(emp.getSalary()));
            JTextField textField = new JTextField(String.valueOf(emp.getText()));
            JTextField otherField = new JTextField(String.valueOf(emp.getOther()));

            noField.setEditable(false);

            panel.add(new JLabel("员工编号:"));
            panel.add(noField);
            panel.add(new JLabel("姓名:"));
            panel.add(nameField);
            panel.add(new JLabel("部门:"));
            panel.add(deptField);
            panel.add(new JLabel("出生日期:"));
            panel.add(bornField);
            panel.add(new JLabel("民族:"));
            panel.add(minorityField);
            panel.add(new JLabel("地址:"));
            panel.add(addressField);
            panel.add(new JLabel("薪资:"));
            panel.add(salaryField);
            panel.add(new JLabel("备注:"));
            panel.add(textField);
            panel.add(new JLabel("其他:"));
            panel.add(otherField);

            JButton saveButton = new JButton("保存");
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        Employee updatedEmp = new Employee();
                        updatedEmp.setNo(Integer.parseInt(noField.getText()));
                        updatedEmp.setName(nameField.getText());
                        updatedEmp.setDept(deptField.getText());
                        updatedEmp.setBornday(Integer.parseInt(bornField.getText()));
                        updatedEmp.setMinority(minorityField.getText());
                        updatedEmp.setAddress(addressField.getText());
                        updatedEmp.setSalary(Integer.parseInt(salaryField.getText()));
                        // 从文本框获取内容并直接设置为字符串
                        updatedEmp.setText(textField.getText());
                        updatedEmp.setOther(otherField.getText());

                        if (!emp.getDept().equals(updatedEmp.getDept())) {
                            recordTransferHistory(emp.getNo(), emp.getDept(), updatedEmp.getDept());
                        }

                        if (emp.getSalary() != updatedEmp.getSalary()) {
                            recordSalaryAdjustment(emp.getNo(), emp.getSalary(), updatedEmp.getSalary());
                        }

                        employeeDAO.updateEmployee(updatedEmp);
                        JOptionPane.showMessageDialog(dialog, "修改成功");
                        dialog.dispose();
                        showEmployeeList();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(dialog, "修改失败: " + ex.getMessage());
                    }
                }
            });

            JButton cancelButton = new JButton("取消");
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dialog.dispose();
                }
            });

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            dialog.add(panel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "获取员工信息失败: " + ex.getMessage());
        }
    }

    private void recordTransferHistory(int empNo, String oldDept, String newDept) {
        try {
            TransferHistory history = new TransferHistory();
            history.setOperation("部门调动");
            history.setOldVal(oldDept);
            history.setNewVal(newDept);
            history.setDate(new Date());
            history.setSno(empNo);

            transferDAO.addTransferHistory(history);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "记录调动历史失败: " + ex.getMessage());
        }
    }

    private void recordSalaryAdjustment(int empNo, int oldSalary, int newSalary) {
        try {
            TransferHistory history = new TransferHistory();
            history.setOperation("薪资调整");
            history.setOldVal(String.valueOf(oldSalary));
            history.setNewVal(String.valueOf(newSalary));
            history.setDate(new Date());
            history.setSno(empNo);

            transferDAO.addTransferHistory(history);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void showEmployeeHistoryDialog(int empNo) {
        JDialog dialog = new JDialog(this, "员工历史记录", true);
        dialog.setSize(600, 400);
        dialog.setLayout(new BorderLayout());

        try {
            List<TransferHistory> transferHistories = transferDAO.getTransferHistoryBySno(empNo);
            String[] transferColumnNames = {"操作", "旧部门", "新部门", "日期"};
            Object[][] transferData = new Object[transferHistories.size()][4];

            for (int i = 0; i < transferHistories.size(); i++) {
                TransferHistory history = transferHistories.get(i);
                transferData[i][0] = history.getOperation();
                transferData[i][1] = history.getOldVal();
                transferData[i][2] = history.getNewVal();
                transferData[i][3] = history.getDate();
            }

            DefaultTableModel transferModel = new DefaultTableModel(transferData, transferColumnNames);
            JTable transferTable = new JTable(transferModel);
            JScrollPane transferScrollPane = new JScrollPane(transferTable);

            List<AssessmentHistory> assessmentHistories = assessmentDAO.getAssessmentHistoryBySno(empNo);
            String[] assessmentColumnNames = {"操作", "旧分数", "新分数", "日期"};
            Object[][] assessmentData = new Object[assessmentHistories.size()][4];

            for (int i = 0; i < assessmentHistories.size(); i++) {
                AssessmentHistory history = assessmentHistories.get(i);
                assessmentData[i][0] = history.getOperation();
                assessmentData[i][1] = history.getOldVal();
                assessmentData[i][2] = history.getNewVal();
                assessmentData[i][3] = history.getDate();
            }

            DefaultTableModel assessmentModel = new DefaultTableModel(assessmentData, assessmentColumnNames);
            JTable assessmentTable = new JTable(assessmentModel);
            JScrollPane assessmentScrollPane = new JScrollPane(assessmentTable);

            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.addTab("调动历史", transferScrollPane);
            tabbedPane.addTab("考核历史", assessmentScrollPane);

            dialog.add(tabbedPane, BorderLayout.CENTER);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(dialog, "获取历史记录失败: " + ex.getMessage());
        }

        JButton closeButton = new JButton("关闭");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showDepartmentList() {
        contentPanel.removeAll();

        try {
            List<Department> departments = departmentDAO.getAllDepartments();
            String[] columnNames = {"部门编号", "一级部门", "二级部门"};
            Object[][] data = new Object[departments.size()][3];

            for (int i = 0; i < departments.size(); i++) {
                Department dept = departments.get(i);
                data[i][0] = dept.getSno();
                data[i][1] = dept.getFirstDept();
                data[i][2] = dept.getSecondDept();
            }

            DefaultTableModel model = new DefaultTableModel(data, columnNames);
            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);

            JPanel buttonPanel = new JPanel();
            JButton addButton = new JButton("添加");
            JButton editButton = new JButton("修改");
            JButton deleteButton = new JButton("删除");

            addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showAddDepartmentDialog();
                }
            });

            editButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow >= 0) {
                        int deptNo = (int) table.getValueAt(selectedRow, 0);
                        showEditDepartmentDialog(deptNo);
                    } else {
                        JOptionPane.showMessageDialog(MainFrame.this, "请选择要修改的部门");
                    }
                }
            });

            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow >= 0) {
                        int deptNo = (int) table.getValueAt(selectedRow, 0);
                        int confirm = JOptionPane.showConfirmDialog(MainFrame.this, "确定要删除该部门吗？", "确认删除", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            try {
                                List<Employee> employees = employeeDAO.getAllEmployees();
                                boolean hasEmployees = false;
                                for (Employee emp : employees) {
                                    if (emp.getDept().equals(deptNo)) {
                                        hasEmployees = true;
                                        break;
                                    }
                                }

                                if (hasEmployees) {
                                    JOptionPane.showMessageDialog(MainFrame.this, "该部门下有员工，不能直接删除");
                                } else {
                                    departmentDAO.deleteDepartment(deptNo);
                                    JOptionPane.showMessageDialog(MainFrame.this, "删除成功");
                                    showDepartmentList();
                                }
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(MainFrame.this, "删除失败: " + ex.getMessage());
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(MainFrame.this, "请选择要删除的部门");
                    }
                }
            });

            buttonPanel.add(addButton);
            buttonPanel.add(editButton);
            buttonPanel.add(deleteButton);

            contentPanel.add(buttonPanel, BorderLayout.NORTH);
            contentPanel.add(scrollPane, BorderLayout.CENTER);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "获取部门列表失败: " + ex.getMessage());
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showAddDepartmentDialog() {
        JDialog dialog = new JDialog(this, "添加部门", true);
        dialog.setSize(300, 200);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField snoField = new JTextField();
        JTextField firstDeptField = new JTextField();
        JTextField secondDeptField = new JTextField();

        panel.add(new JLabel("部门编号:"));
        panel.add(snoField);
        panel.add(new JLabel("一级部门:"));
        panel.add(firstDeptField);
        panel.add(new JLabel("二级部门:"));
        panel.add(secondDeptField);

        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Department dept = new Department();
                    dept.setSno(Integer.parseInt(snoField.getText()));
                    dept.setFirstDept(firstDeptField.getText());
                    dept.setSecondDept(secondDeptField.getText());

                    departmentDAO.addDepartment(dept);
                    JOptionPane.showMessageDialog(dialog, "添加成功");
                    dialog.dispose();
                    showDepartmentList();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(dialog, "添加失败: " + ex.getMessage());
                }
            }
        });

        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showEditDepartmentDialog(int deptNo) {
        try {
            Department dept = departmentDAO.getDepartmentBySno(deptNo);
            if (dept == null) {
                JOptionPane.showMessageDialog(this, "部门不存在");
                return;
            }

            JDialog dialog = new JDialog(this, "修改部门信息", true);
            dialog.setSize(300, 200);
            dialog.setLayout(new BorderLayout());

            JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JTextField snoField = new JTextField(String.valueOf(dept.getSno()));
            JTextField firstDeptField = new JTextField(dept.getFirstDept());
            JTextField secondDeptField = new JTextField(dept.getSecondDept());

            snoField.setEditable(false);

            panel.add(new JLabel("部门编号:"));
            panel.add(snoField);
            panel.add(new JLabel("一级部门:"));
            panel.add(firstDeptField);
            panel.add(new JLabel("二级部门:"));
            panel.add(secondDeptField);

            JButton saveButton = new JButton("保存");
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        Department updatedDept = new Department();
                        updatedDept.setSno(Integer.parseInt(snoField.getText()));
                        updatedDept.setFirstDept(firstDeptField.getText());
                        updatedDept.setSecondDept(secondDeptField.getText());

                        departmentDAO.updateDepartment(updatedDept);
                        JOptionPane.showMessageDialog(dialog, "修改成功");
                        dialog.dispose();
                        showDepartmentList();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(dialog, "修改失败: " + ex.getMessage());
                    }
                }
            });

            JButton cancelButton = new JButton("取消");
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dialog.dispose();
                }
            });

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            dialog.add(panel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "获取部门信息失败: " + ex.getMessage());
        }
    }

    private void showTransferPanel() {
        contentPanel.removeAll();

        try {
            // 获取所有员工
            List<Employee> employees = employeeDAO.getAllEmployees();
            // 获取所有部门
            List<Department> departments = departmentDAO.getAllDepartments();

            // 创建员工选择框
            JComboBox<String> employeeComboBox = new JComboBox<>();
            for (Employee emp : employees) {
                employeeComboBox.addItem(emp.getNo() + " - " + emp.getName());
            }

            // 创建部门选择框
            JComboBox<String> departmentComboBox = new JComboBox<>();
            for (Department dept : departments) {
                departmentComboBox.addItem(dept.getFirstDept() + " - " + dept.getSecondDept());
            }

            // 创建提交按钮
            JButton transferButton = new JButton("提交调动申请");

            // 使用GridBagLayout布局
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10); // 间距
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // 第一行：员工选择
            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(new JLabel("选择员工:"), gbc);

            gbc.gridx = 1;
            gbc.gridy = 0;
            panel.add(employeeComboBox, gbc);

            // 第二行：部门选择
            gbc.gridx = 0;
            gbc.gridy = 1;
            panel.add(new JLabel("调往部门:"), gbc);

            gbc.gridx = 1;
            gbc.gridy = 1;
            panel.add(departmentComboBox, gbc);

            // 第三行：提交按钮
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 2; // 跨两列
            gbc.anchor = GridBagConstraints.CENTER; // 居中对齐
            gbc.fill = GridBagConstraints.NONE; // 不填充

            panel.add(transferButton, gbc);

            // 初始化TransferHistoryDAO
            TransferHistoryDAO transferDAO = new TransferHistoryDAO();

            // 添加按钮事件监听器
            transferButton.addActionListener(e -> {
                // 提交逻辑
                String selectedEmployee = (String) employeeComboBox.getSelectedItem();
                String selectedDepartment = (String) departmentComboBox.getSelectedItem();

                if (selectedEmployee == null || selectedDepartment == null) {
                    JOptionPane.showMessageDialog(MainFrame.this, "请选择员工和部门");
                    return;
                }

                try {
                    // 解析员工编号
                    int employeeNo = Integer.parseInt(selectedEmployee.split(" - ")[0]);

                    // 获取员工信息
                    Employee emp = employeeDAO.getEmployeeByNo(employeeNo);
                    String oldDept = emp.getDept();

                    // 更新员工部门
                    emp.setDept(selectedDepartment);
                    employeeDAO.updateEmployee(emp);

                    // 记录调动历史
                    TransferHistory history = new TransferHistory(
                            0, // 自动生成的ID
                            "部门调动",
                            oldDept,
                            selectedDepartment,
                            new Date(), // 当前日期
                            employeeNo
                    );
                    transferDAO.addTransferHistory(history);

                    JOptionPane.showMessageDialog(MainFrame.this, "调动成功");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(MainFrame.this, "调动失败: " + ex.getMessage());
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(MainFrame.this, "员工编号解析错误");
                }
            });

            contentPanel.add(panel, BorderLayout.CENTER);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "获取数据失败: " + ex.getMessage());
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }
    private void showSalaryAdjustmentPanel() {
        contentPanel.removeAll();

        try {
            List<Employee> employees = employeeDAO.getAllEmployees();

            // 创建面板使用GridBagLayout以获得更灵活的布局控制
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10); // 设置间距
            gbc.fill = GridBagConstraints.HORIZONTAL; // 水平填充

            // 创建员工选择下拉框 - 只显示编号和姓名
            JComboBox<String> employeeComboBox = new JComboBox<>();
            for (Employee emp : employees) {
                employeeComboBox.addItem(emp.getNo() + " - " + emp.getName());
            }
            employeeComboBox.setPreferredSize(new Dimension(200, 30)); // 设置下拉框大小

            // 创建新薪资输入框
            JTextField newSalaryField = new JTextField();
            newSalaryField.setPreferredSize(new Dimension(200, 30));

            // 创建调整原因文本区域
            JTextArea reasonArea = new JTextArea(3, 20); // 设置3行20列
            JScrollPane reasonScrollPane = new JScrollPane(reasonArea);

            // 员工选择事件监听器 - 自动填充当前薪资
            employeeComboBox.addActionListener(e -> {
                String selectedEmployee = (String) employeeComboBox.getSelectedItem();
                if (selectedEmployee == null) return;

                // 解析员工编号
                int employeeNo = Integer.parseInt(selectedEmployee.split(" - ")[0]);

                try {
                    // 获取员工当前薪资
                    Employee emp = employeeDAO.getEmployeeByNo(employeeNo);
                    newSalaryField.setText(String.valueOf(emp.getSalary()));
                    reasonArea.setText(""); // 清空原因文本框
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(MainFrame.this, "获取员工薪资失败");
                }
            });

            // 构建布局
            // 第一行：选择员工
            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(new JLabel("选择员工:"), gbc);

            gbc.gridx = 1;
            gbc.gridy = 0;
            panel.add(employeeComboBox, gbc);

            // 第二行：新薪资
            gbc.gridx = 0;
            gbc.gridy = 1;
            panel.add(new JLabel("新薪资:"), gbc);

            gbc.gridx = 1;
            gbc.gridy = 1;
            panel.add(newSalaryField, gbc);

            // 第三行：调整原因
            gbc.gridx = 0;
            gbc.gridy = 2;
            panel.add(new JLabel("调整原因:"), gbc);

            gbc.gridx = 1;
            gbc.gridy = 2;
            panel.add(reasonScrollPane, gbc);

            // 第四行：调整按钮（居中显示）
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.gridwidth = 2; // 跨两列
            gbc.anchor = GridBagConstraints.CENTER; // 居中对齐
            gbc.fill = GridBagConstraints.NONE; // 不填充
            JButton adjustButton = new JButton("执行调整");
            panel.add(adjustButton, gbc);

            // 调整按钮事件处理
            adjustButton.addActionListener(e -> {
                String selectedEmployee = (String) employeeComboBox.getSelectedItem();
                String newSalaryStr = newSalaryField.getText();
                String reason = reasonArea.getText();

                if (selectedEmployee == null || newSalaryStr.isEmpty() || reason.isEmpty()) {
                    JOptionPane.showMessageDialog(MainFrame.this, "请填写完整信息");
                    return;
                }

                try {
                    // 解析员工编号和新薪资
                    int employeeNo = Integer.parseInt(selectedEmployee.split(" - ")[0]);
                    int newSalary = Integer.parseInt(newSalaryStr);

                    // 获取员工当前信息
                    Employee emp = employeeDAO.getEmployeeByNo(employeeNo);
                    int oldSalary = emp.getSalary();

                    // 更新薪资
                    emp.setSalary(newSalary);
                    employeeDAO.updateEmployee(emp);

                    // 记录薪资调整历史
                    recordSalaryAdjustment(employeeNo, oldSalary, newSalary, reason);

                    JOptionPane.showMessageDialog(MainFrame.this, "薪资调整成功");

                    // 刷新员工列表
                    showEmployeeList();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(MainFrame.this, "请输入有效的薪资数值");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(MainFrame.this, "薪资调整失败: " + ex.getMessage());
                }
            });

            contentPanel.add(panel, BorderLayout.CENTER);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "获取员工列表失败: " + ex.getMessage());
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // 记录薪资调整历史的方法
    private void recordSalaryAdjustment(int employeeNo, int oldSalary, int newSalary, String reason) throws SQLException {
        // 这里调用DAO层记录薪资调整历史
        // 例如: salaryAdjustmentDAO.addRecord(employeeNo, oldSalary, newSalary, reason, currentUser, new Date());
    }

    private void showAddAssessmentPanel() throws SQLException {
        contentPanel.removeAll();

        List<Employee> employees = employeeDAO.getAllEmployees();

        // 使用GridBagLayout实现更灵活的布局
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 员工选择下拉框 - 只显示编号和姓名
        JComboBox<String> employeeComboBox = new JComboBox<>();
        for (Employee emp : employees) {
            employeeComboBox.addItem(emp.getNo() + " - " + emp.getName());
        }
        employeeComboBox.setPreferredSize(new Dimension(200, 30));

        JTextField assessmentTypeField = new JTextField();
        JTextField oldScoreField = new JTextField();
        JTextField newScoreField = new JTextField();
        JTextArea commentArea = new JTextArea(3, 20); // 设置3行20列
        JScrollPane commentScrollPane = new JScrollPane(commentArea);

        // 构建布局
        // 第一行：选择员工
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("选择员工:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(employeeComboBox, gbc);

        // 第二行：考核类型
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("考核类型:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(assessmentTypeField, gbc);

        // 第三行：旧分数
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("旧分数:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(oldScoreField, gbc);

        // 第四行：新分数
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("新分数:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(newScoreField, gbc);

        // 第五行：考核评语
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("考核评语:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(commentScrollPane, gbc);

        // 第六行：保存按钮（居中显示）
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2; // 跨两列
        gbc.anchor = GridBagConstraints.CENTER; // 居中对齐
        gbc.fill = GridBagConstraints.NONE; // 不填充
        JButton saveButton = new JButton("保存考核");
        panel.add(saveButton, gbc);

        // 保存按钮事件处理
        saveButton.addActionListener(e -> {
            String selectedEmployee = (String) employeeComboBox.getSelectedItem();
            String type = assessmentTypeField.getText();
            String oldScoreStr = oldScoreField.getText();
            String newScoreStr = newScoreField.getText();
            String comment = commentArea.getText();

            if (selectedEmployee == null || type.isEmpty() || oldScoreStr.isEmpty() || newScoreStr.isEmpty()) {
                JOptionPane.showMessageDialog(MainFrame.this, "请填写完整的考核信息");
                return;
            }

            try {
                // 解析员工编号
                int employeeNo = Integer.parseInt(selectedEmployee.split(" - ")[0]);
                int oldScore = Integer.parseInt(oldScoreStr);
                int newScore = Integer.parseInt(newScoreStr);

                AssessmentHistory history = new AssessmentHistory();
                history.setOperation("考核-" + type);
                history.setOldVal(String.valueOf(oldScore));
                history.setNewVal(String.valueOf(newScore));
                history.setDate(new Date());
                history.setSno(employeeNo); // 使用解析的员工编号
                assessmentDAO.addAssessmentHistory(history);

                JOptionPane.showMessageDialog(MainFrame.this, "考核记录添加成功");
                showEmployeeList();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(MainFrame.this, "请输入有效的分数数值");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(MainFrame.this, "添加考核记录失败: " + ex.getMessage());
            }
        });

        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showAssessmentHistoryPanel() {
        contentPanel.removeAll();

        try {
            List<Employee> employees = employeeDAO.getAllEmployees();

            // 使用GridBagLayout布局
            JPanel mainPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // 员工选择下拉框 - 只显示编号和姓名
            JComboBox<String> employeeComboBox = new JComboBox<>();
            for (Employee emp : employees) {
                employeeComboBox.addItem(emp.getNo() + " - " + emp.getName());
            }
            employeeComboBox.setPreferredSize(new Dimension(200, 30));

            JButton viewButton = new JButton("查看考核记录");
            viewButton.addActionListener(e -> {
                String selectedEmployee = (String) employeeComboBox.getSelectedItem();
                if (selectedEmployee == null) {
                    JOptionPane.showMessageDialog(MainFrame.this, "请选择员工");
                    return;
                }

                try {
                    // 解析员工编号
                    int employeeNo = Integer.parseInt(selectedEmployee.split(" - ")[0]);

                    List<AssessmentHistory> histories = assessmentDAO.getAssessmentHistoryBySno(employeeNo);
                    String[] columnNames = {"考核类型", "旧分数", "新分数", "日期", "评语"};
                    Object[][] data = new Object[histories.size()][5];

                    for (int i = 0; i < histories.size(); i++) {
                        AssessmentHistory history = histories.get(i);
                        String[] parts = history.getOperation().split("-");
                        String type = parts.length > 1 ? parts[1] : history.getOperation();
                        data[i][0] = type;
                        data[i][1] = history.getOldVal();
                        data[i][2] = history.getNewVal();
                        data[i][3] = history.getDate();
                        data[i][4] = ""; // 暂时留空，需要从history中获取评语
                    }

                    DefaultTableModel model = new DefaultTableModel(data, columnNames);
                    JTable table = new JTable(model);
                    JScrollPane scrollPane = new JScrollPane(table);

                    // 清空并重新添加组件
                    mainPanel.removeAll();

                    // 重新添加选择面板
                    gbc.gridx = 0;
                    gbc.gridy = 0;
                    gbc.gridwidth = 2;
                    JPanel selectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    selectPanel.add(new JLabel("选择员工:"));
                    selectPanel.add(employeeComboBox);
                    selectPanel.add(viewButton);
                    mainPanel.add(selectPanel, gbc);

                    // 添加表格
                    gbc.gridx = 0;
                    gbc.gridy = 1;
                    gbc.weighty = 1.0; // 让表格占据剩余空间
                    gbc.fill = GridBagConstraints.BOTH;
                    mainPanel.add(scrollPane, gbc);

                    mainPanel.revalidate();
                    mainPanel.repaint();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(MainFrame.this, "获取考核记录失败: " + ex.getMessage());
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(MainFrame.this, "员工编号解析错误");
                }
            });

            // 初始布局 - 只显示选择面板
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            JPanel selectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            selectPanel.add(new JLabel("选择员工:"));
            selectPanel.add(employeeComboBox);
            selectPanel.add(viewButton);
            mainPanel.add(selectPanel, gbc);

            contentPanel.add(mainPanel, BorderLayout.CENTER);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "获取数据失败: " + ex.getMessage());
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showDepartmentStatistics() {
        contentPanel.removeAll();

        try {
            List<Department> departments = departmentDAO.getAllDepartments();
            List<Employee> employees = employeeDAO.getAllEmployees();

            String[] columnNames = {"部门", "人数"};
            Object[][] data = new Object[departments.size()][2];

            for (int i = 0; i < departments.size(); i++) {
                Department dept = departments.get(i);
                String deptName = dept.getFirstDept() + "-" + dept.getSecondDept();
                int count = 0;

                for (Employee emp : employees) {
                    if (emp.getDept().equals(deptName)) {
                        count++;
                    }
                }

                data[i][0] = deptName;
                data[i][1] = count;
            }

            DefaultTableModel model = new DefaultTableModel(data, columnNames);
            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);

            contentPanel.add(scrollPane, BorderLayout.CENTER);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "获取数据失败: " + ex.getMessage());
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showSalaryDistribution() {
        contentPanel.removeAll();

        try {
            List<Employee> employees = employeeDAO.getAllEmployees();

            // 统计各薪资区间的人数
            int[] ranges = new int[5]; // 0-3000, 3001-5000, 5001-8000, 8001-10000, 10000+

            for (Employee emp : employees) {
                int salary = emp.getSalary();
                if (salary <= 3000) {
                    ranges[0]++;
                } else if (salary <= 5000) {
                    ranges[1]++;
                } else if (salary <= 8000) {
                    ranges[2]++;
                } else if (salary <= 10000) {
                    ranges[3]++;
                } else {
                    ranges[4]++;
                }
            }

            String[] columnNames = {"薪资区间", "人数"};
            Object[][] data = {
                    {"0-3000", ranges[0]},
                    {"3001-5000", ranges[1]},
                    {"5001-8000", ranges[2]},
                    {"8001-10000", ranges[3]},
                    {"10000以上", ranges[4]}
            };

            DefaultTableModel model = new DefaultTableModel(data, columnNames);
            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);

            contentPanel.add(scrollPane, BorderLayout.CENTER);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "获取数据失败: " + ex.getMessage());
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }
}