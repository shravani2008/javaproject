package model;

/**
 * Represents staff responsible for serving customers.
 */
public class Staff extends User {

    private int staffId;
    private String employeeCode;
    private String department;

    public Staff() {
        super();
        setRole(Role.STAFF);
    }

    public Staff(String username, String passwordHash, String fullName,
                 String email, String phone,
                 String employeeCode, String department) {

        super(username, passwordHash, fullName, email, phone, Role.STAFF);

        setEmployeeCode(employeeCode);
        setDepartment(department);
    }

    public Staff(int userId, int staffId,
                 String username, String passwordHash,
                 String fullName, String email, String phone,
                 String employeeCode, String department,
                 boolean active) {

        super(userId, username, passwordHash, fullName,
                email, phone, Role.STAFF, active);

        setStaffId(staffId);
        setEmployeeCode(employeeCode);
        setDepartment(department);
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        if (staffId < 0) {
            throw new IllegalArgumentException("Staff ID cannot be negative.");
        }
        this.staffId = staffId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        if (employeeCode == null || employeeCode.isBlank()) {
            throw new IllegalArgumentException("Employee code cannot be empty.");
        }
        this.employeeCode = employeeCode.trim();
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department == null ? null : department.trim();
    }

    @Override
    public String toString() {
        return "Staff{" +
                "staffId=" + staffId +
                ", userId=" + getUserId() +
                ", employeeCode='" + employeeCode + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}