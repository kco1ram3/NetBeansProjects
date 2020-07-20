package entity;

import java.util.Date;

public class m_member {

    public static final String TABLE_NAME = "m_member";
    
    public class ColumnName {
        public static final String ROWID = "RowID";
        public static final String NAME = "Name";
        public static final String SURNAME = "Surname";
        public static final String EMAIL = "Email";
        public static final String USERNAME = "Username";
        public static final String PASSWORD = "Password";
        public static final String REGISTERDATE = "RegisterDate";
        public static final String UPDATEDATE = "UpdateDate";
    }
    
    private long rowID;
    private String name;
    private String surname;
    private String email;
    private String username;
    private String password;
    private Date registerDate;
    private Date updateDate;

    public long getRowID() {
        return this.rowID;
    }

    public void setRowID(long rowID) {
        this.rowID = rowID;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return this.surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getRegisterDate() {
        return this.registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Date getUpdateDate() {
        return this.updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}
