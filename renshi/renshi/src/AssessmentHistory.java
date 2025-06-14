import java.util.Date;

public class AssessmentHistory {
    private int nonum;
    private String operation;
    private String oldVal;
    private String newVal;
    private Date date;
    private int sno;

    public AssessmentHistory() {}

    public AssessmentHistory(int nonum, String operation, String oldVal, String newVal, Date date, int sno) {
        this.nonum = nonum;
        this.operation = operation;
        this.oldVal = oldVal;
        this.newVal = newVal;
        this.date = date;
        this.sno = sno;
    }

    // getter和setter方法
    public int getNonum() {
        return nonum;
    }

    public void setNonum(int nonum) {
        this.nonum = nonum;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getOldVal() {
        return oldVal;
    }

    public void setOldVal(String oldVal) {
        this.oldVal = oldVal;
    }

    public String getNewVal() {
        return newVal;
    }

    public void setNewVal(String newVal) {
        this.newVal = newVal;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getSno() {
        return sno;
    }

    public void setSno(int sno) {
        this.sno = sno;
    }

    @Override
    public String toString() {
        return "AssessmentHistory{" +
                "nonum=" + nonum +
                ", operation='" + operation + '\'' +
                ", oldVal='" + oldVal + '\'' +
                ", newVal='" + newVal + '\'' +
                ", date=" + date +
                ", sno=" + sno +
                '}';
    }
}    