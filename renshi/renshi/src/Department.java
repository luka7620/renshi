public class Department {
    private int sno;
    private String firstDept;
    private String secondDept;

    public Department() {}

    public Department(int sno, String firstDept, String secondDept) {
        this.sno = sno;
        this.firstDept = firstDept;
        this.secondDept = secondDept;
    }

    // getter和setter方法
    public int getSno() {
        return sno;
    }

    public void setSno(int sno) {
        this.sno = sno;
    }

    public String getFirstDept() {
        return firstDept;
    }

    public void setFirstDept(String firstDept) {
        this.firstDept = firstDept;
    }

    public String getSecondDept() {
        return secondDept;
    }

    public void setSecondDept(String secondDept) {
        this.secondDept = secondDept;
    }

    @Override
    public String toString() {
        return "Department{" +
                "sno=" + sno +
                ", firstDept='" + firstDept + '\'' +
                ", secondDept='" + secondDept + '\'' +
                '}';
    }
}    