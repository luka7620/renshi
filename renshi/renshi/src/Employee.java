public class Employee {
    private int no;
    private String name;
    private String dept;
    private int bornday;
    private String minority;
    private String address;
    private int salary;
    private String text; // 假设 text 和 other 为 String 类型
    private String other;

    // 添加接受 9 个参数的构造函数
    public Employee(int no, String name, String dept, int bornday, String minority, String address, int salary, String text, String other) {
        this.no = no;
        this.name = name;
        this.dept = dept;
        this.bornday = bornday;
        this.minority = minority;
        this.address = address;
        this.salary = salary;
        this.text = text;
        this.other = other;
    }

    public Employee() {
        
    }

    public Employee(int no, String name, String dept, int bornday, String minority, String address, int salary, int text, int other) {
    }

    // Getters and Setters
    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public int getBornday() {
        return bornday;
    }

    public void setBornday(int bornday) {
        this.bornday = bornday;
    }

    public String getMinority() {
        return minority;
    }

    public void setMinority(String minority) {
        this.minority = minority;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }


    @Override
    public String toString() {
        return "Employee{" +
                "no=" + no +
                ", name='" + name + '\'' +
                ", dept='" + dept + '\'' +
                ", bornday=" + bornday +
                ", minority='" + minority + '\'' +
                ", address='" + address + '\'' +
                ", salary=" + salary +
                ", text='" + text + '\'' +
                ", other='" + other + '\'' +
                '}';
    }
}