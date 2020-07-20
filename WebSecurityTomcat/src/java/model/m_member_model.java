package model;

import entity.m_member;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javaxt.utils.Base64;
import utility.DatabaseHelper;

public class m_member_model {

    private DatabaseHelper dbHelper;

    public String getSalt() {
        Random rand = new Random((new Date()).getTime());
        byte[] salt = new byte[16];
        rand.nextBytes(salt);
        return new String(salt);
    }

    public String encrypt(String salt, String message) {
        return Base64.encodeBytes(salt.getBytes()) + Base64.encodeBytes(message.getBytes());
    }

    public long insert(m_member member) throws SQLException {
        long rowID = 0;
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            member.setSalt(this.getSalt());

            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            rs.open("SELECT * FROM " + m_member.TABLE_NAME + " WHERE " + m_member.ColumnName.ROWID + " = 0", conn, false);

            rs.addNew();
            rs.setValue(m_member.ColumnName.NAME, member.getName());
            rs.setValue(m_member.ColumnName.SURNAME, member.getSurname());
            rs.setValue(m_member.ColumnName.EMAIL, member.getEmail());
            rs.setValue(m_member.ColumnName.MOBILEPHONENO, member.getMobilePhoneNo());
            rs.setValue(m_member.ColumnName.USERNAME, member.getUsername());
            rs.setValue(m_member.ColumnName.PASSWORD, this.encrypt(member.getSalt(), member.getPassword()));
            rs.setValue(m_member.ColumnName.SALT, member.getSalt());
            rs.setValue(m_member.ColumnName.REGISTERDATE, member.getRegisterDate());
            rs.update();
            rs.close();

            rowID = rs.getGeneratedKey().toLong();
        } catch (SQLException ex) {
            Logger.getLogger(m_member_model.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();

        return rowID;
    }

    public void update(m_member member) throws SQLException {
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            rs.open("SELECT * FROM " + m_member.TABLE_NAME + " WHERE " + m_member.ColumnName.ROWID + " = " + member.getRowID(), conn, false);

            if (member.getName() != null) {
                rs.setValue(m_member.ColumnName.NAME, member.getName());
            }
            if (member.getSurname() != null) {
                rs.setValue(m_member.ColumnName.SURNAME, member.getSurname());
            }
            if (member.getEmail() != null) {
                rs.setValue(m_member.ColumnName.EMAIL, member.getEmail());
            }
            if (member.getMobilePhoneNo() != null) {
                rs.setValue(m_member.ColumnName.MOBILEPHONENO, member.getMobilePhoneNo());
            }
            if (member.getUsername() != null) {
                rs.setValue(m_member.ColumnName.USERNAME, member.getUsername());
            }
            if (member.getPassword() != null) {
                //rs.setValue(m_member.ColumnName.PASSWORD, member.getPassword());
                rs.setValue(m_member.ColumnName.PASSWORD, this.encrypt(rs.getValue(m_member.ColumnName.SALT).toString(), member.getPassword()));
            }
            if (member.getOtpReference() != null) {
                rs.setValue(m_member.ColumnName.OTP_REFERENCE, member.getOtpReference());
            }
            if (member.getOtpPassword() != null) {
                rs.setValue(m_member.ColumnName.OTP_PASSWORD, member.getOtpPassword());
            }
            if (member.getOtpRequestTime() != null) {
                rs.setValue(m_member.ColumnName.OTP_REQUESTTIME, member.getOtpRequestTime());
            }
            if (member.getUpdateDate() != null) {
                rs.setValue(m_member.ColumnName.UPDATEDATE, member.getUpdateDate());
            }
            rs.update();
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(m_member_model.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();
    }

    public List<m_member> listAll() throws SQLException {
        m_member member;
        List<m_member> list = new ArrayList<m_member>();
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            rs.open("SELECT * FROM " + m_member.TABLE_NAME, conn);

            while (rs.hasNext()) {
                member = new m_member();
                member.setRowID(rs.getValue(m_member.ColumnName.ROWID).toLong());
                member.setName(rs.getValue(m_member.ColumnName.NAME).toString());
                member.setSurname(rs.getValue(m_member.ColumnName.SURNAME).toString());
                member.setEmail(rs.getValue(m_member.ColumnName.EMAIL).toString());
                member.setMobilePhoneNo(rs.getValue(m_member.ColumnName.MOBILEPHONENO).toString());
                member.setUsername(rs.getValue(m_member.ColumnName.USERNAME).toString());
                member.setPassword(rs.getValue(m_member.ColumnName.PASSWORD).toString());
                if (rs.getValue(m_member.ColumnName.OTP_REFERENCE).isNull()) {
                    member.setOtpReference(null);
                } else {
                    member.setOtpReference(rs.getValue(m_member.ColumnName.OTP_REFERENCE).toString());
                }
                if (rs.getValue(m_member.ColumnName.OTP_PASSWORD).isNull()) {
                    member.setOtpPassword(null);
                } else {
                    member.setOtpPassword(rs.getValue(m_member.ColumnName.OTP_PASSWORD).toString());
                }
                if (rs.getValue(m_member.ColumnName.OTP_REQUESTTIME).isNull()) {
                    member.setOtpRequestTime(null);
                } else {
                    member.setOtpRequestTime(new Date(rs.getValue(m_member.ColumnName.OTP_REQUESTTIME).toTimeStamp().getTime()));
                }
                if (rs.getValue(m_member.ColumnName.REGISTERDATE).isNull()) {
                    member.setRegisterDate(null);
                } else {
                    member.setRegisterDate(new Date(rs.getValue(m_member.ColumnName.REGISTERDATE).toTimeStamp().getTime()));
                }
                if (rs.getValue(m_member.ColumnName.UPDATEDATE).isNull()) {
                    member.setUpdateDate(null);
                } else {
                    member.setUpdateDate(new Date(rs.getValue(m_member.ColumnName.UPDATEDATE).toTimeStamp().getTime()));
                }
                list.add(member);
                rs.moveNext();
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(m_member_model.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();

        return list;
    }

    public List<m_member> loadByPrimaryKey(Long rowID) throws SQLException {
        m_member member;
        List<m_member> list = new ArrayList<m_member>();
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            rs.open("SELECT * FROM " + m_member.TABLE_NAME + " WHERE " + m_member.ColumnName.ROWID + " = " + rowID, conn);

            while (rs.hasNext()) {
                member = new m_member();
                member.setRowID(rs.getValue(m_member.ColumnName.ROWID).toLong());
                member.setName(rs.getValue(m_member.ColumnName.NAME).toString());
                member.setSurname(rs.getValue(m_member.ColumnName.SURNAME).toString());
                member.setEmail(rs.getValue(m_member.ColumnName.EMAIL).toString());
                member.setMobilePhoneNo(rs.getValue(m_member.ColumnName.MOBILEPHONENO).toString());
                member.setUsername(rs.getValue(m_member.ColumnName.USERNAME).toString());
                member.setPassword(rs.getValue(m_member.ColumnName.PASSWORD).toString());
                if (rs.getValue(m_member.ColumnName.OTP_REFERENCE).isNull()) {
                    member.setOtpReference(null);
                } else {
                    member.setOtpReference(rs.getValue(m_member.ColumnName.OTP_REFERENCE).toString());
                }
                if (rs.getValue(m_member.ColumnName.OTP_PASSWORD).isNull()) {
                    member.setOtpPassword(null);
                } else {
                    member.setOtpPassword(rs.getValue(m_member.ColumnName.OTP_PASSWORD).toString());
                }
                if (rs.getValue(m_member.ColumnName.OTP_REQUESTTIME).isNull()) {
                    member.setOtpRequestTime(null);
                } else {
                    member.setOtpRequestTime(new Date(rs.getValue(m_member.ColumnName.OTP_REQUESTTIME).toTimeStamp().getTime()));
                }
                if (rs.getValue(m_member.ColumnName.REGISTERDATE).isNull()) {
                    member.setRegisterDate(null);
                } else {
                    member.setRegisterDate(new Date(rs.getValue(m_member.ColumnName.REGISTERDATE).toTimeStamp().getTime()));
                }
                if (rs.getValue(m_member.ColumnName.UPDATEDATE).isNull()) {
                    member.setUpdateDate(null);
                } else {
                    member.setUpdateDate(new Date(rs.getValue(m_member.ColumnName.UPDATEDATE).toTimeStamp().getTime()));
                }
                list.add(member);
                rs.moveNext();
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(m_member_model.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();

        return list;
    }

    public List<m_member> login(String username, String password) throws SQLException {
        m_member member;
        List<m_member> list = new ArrayList<m_member>();
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            
            /*
            rs.open("SELECT * FROM " + m_member.TABLE_NAME + " "
                    + "WHERE " + m_member.ColumnName.USERNAME + " = '" + username + "' "
                    + "AND " + m_member.ColumnName.PASSWORD + " = '" + password + "'", conn);

            while (rs.hasNext()) {
                member = new m_member();
                member.setRowID(rs.getValue(m_member.ColumnName.ROWID).toLong());
                member.setName(rs.getValue(m_member.ColumnName.NAME).toString());
                member.setSurname(rs.getValue(m_member.ColumnName.SURNAME).toString());
                member.setEmail(rs.getValue(m_member.ColumnName.EMAIL).toString());
                member.setMobilePhoneNo(rs.getValue(m_member.ColumnName.MOBILEPHONENO).toString());
                member.setUsername(rs.getValue(m_member.ColumnName.USERNAME).toString());
                member.setPassword(rs.getValue(m_member.ColumnName.PASSWORD).toString());
                if (rs.getValue(m_member.ColumnName.OTP_REFERENCE).isNull()) {
                    member.setOtpReference(null);
                } else {
                    member.setOtpReference(rs.getValue(m_member.ColumnName.OTP_REFERENCE).toString());
                }
                if (rs.getValue(m_member.ColumnName.OTP_PASSWORD).isNull()) {
                    member.setOtpPassword(null);
                } else {
                    member.setOtpPassword(rs.getValue(m_member.ColumnName.OTP_PASSWORD).toString());
                }
                if (rs.getValue(m_member.ColumnName.OTP_REQUESTTIME).isNull()) {
                    member.setOtpRequestTime(null);
                } else {
                    member.setOtpRequestTime(new Date(rs.getValue(m_member.ColumnName.OTP_REQUESTTIME).toTimeStamp().getTime()));
                }
                if (rs.getValue(m_member.ColumnName.REGISTERDATE).isNull()) {
                    member.setRegisterDate(null);
                } else {
                    member.setRegisterDate(new Date(rs.getValue(m_member.ColumnName.REGISTERDATE).toTimeStamp().getTime()));
                }
                if (rs.getValue(m_member.ColumnName.UPDATEDATE).isNull()) {
                    member.setUpdateDate(null);
                } else {
                    member.setUpdateDate(new Date(rs.getValue(m_member.ColumnName.UPDATEDATE).toTimeStamp().getTime()));
                }
                list.add(member);
                rs.moveNext();
            }
            */
            
            rs.open("SELECT * FROM " + m_member.TABLE_NAME + " "
                    + "WHERE " + m_member.ColumnName.USERNAME + " = '" + username + "'", conn);

            if (rs.hasNext()) {
                member = new m_member();
                member.setUsername(rs.getValue(m_member.ColumnName.USERNAME).toString());
                member.setPassword(rs.getValue(m_member.ColumnName.PASSWORD).toString());
                member.setSalt(rs.getValue(m_member.ColumnName.SALT).toString());
                if (this.encrypt(member.getSalt(), password).equals(member.getPassword())) {
                    member.setRowID(rs.getValue(m_member.ColumnName.ROWID).toLong());
                    member.setName(rs.getValue(m_member.ColumnName.NAME).toString());
                    member.setSurname(rs.getValue(m_member.ColumnName.SURNAME).toString());
                    member.setEmail(rs.getValue(m_member.ColumnName.EMAIL).toString());
                    member.setMobilePhoneNo(rs.getValue(m_member.ColumnName.MOBILEPHONENO).toString());

                    if (rs.getValue(m_member.ColumnName.OTP_REFERENCE).isNull()) {
                        member.setOtpReference(null);
                    } else {
                        member.setOtpReference(rs.getValue(m_member.ColumnName.OTP_REFERENCE).toString());
                    }
                    if (rs.getValue(m_member.ColumnName.OTP_PASSWORD).isNull()) {
                        member.setOtpPassword(null);
                    } else {
                        member.setOtpPassword(rs.getValue(m_member.ColumnName.OTP_PASSWORD).toString());
                    }
                    if (rs.getValue(m_member.ColumnName.OTP_REQUESTTIME).isNull()) {
                        member.setOtpRequestTime(null);
                    } else {
                        member.setOtpRequestTime(new Date(rs.getValue(m_member.ColumnName.OTP_REQUESTTIME).toTimeStamp().getTime()));
                    }
                    if (rs.getValue(m_member.ColumnName.REGISTERDATE).isNull()) {
                        member.setRegisterDate(null);
                    } else {
                        member.setRegisterDate(new Date(rs.getValue(m_member.ColumnName.REGISTERDATE).toTimeStamp().getTime()));
                    }
                    if (rs.getValue(m_member.ColumnName.UPDATEDATE).isNull()) {
                        member.setUpdateDate(null);
                    } else {
                        member.setUpdateDate(new Date(rs.getValue(m_member.ColumnName.UPDATEDATE).toTimeStamp().getTime()));
                    }
                    list.add(member);
                }
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(m_member_model.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();

        return list;
    }

    public boolean checkExistUsername(String username) throws SQLException {
        boolean isExist = false;
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            rs.open("SELECT * FROM " + m_member.TABLE_NAME + " "
                    + "WHERE " + m_member.ColumnName.USERNAME + " = '" + username + "'", conn);

            if (rs.hasNext()) {
                isExist = true;
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(m_member_model.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();

        return isExist;
    }

    public boolean checkExistOTP(Long rowID, String otpReference, String otpPassword, int otpTimeout) throws Exception {
        boolean isExist = false;
        dbHelper = new DatabaseHelper();
        javaxt.sql.Connection conn = new javaxt.sql.Connection();
        try {
            conn.open(dbHelper.getDatabase());
            javaxt.sql.Recordset rs = new javaxt.sql.Recordset();
            rs.open("SELECT * FROM " + m_member.TABLE_NAME + " "
                    + "WHERE " + m_member.ColumnName.ROWID + " = " + rowID + " "
                    + "AND " + m_member.ColumnName.OTP_REFERENCE + " = '" + otpReference + "' "
                    + "AND " + m_member.ColumnName.OTP_PASSWORD + " = '" + otpPassword + "'", conn);

            if (rs.hasNext()) {
                Date dateStart = new Date(rs.getValue(m_member.ColumnName.OTP_REQUESTTIME).toTimeStamp().getTime());
                Date dateFinish = new Date();
                if ((dateFinish.getTime() - dateStart.getTime()) / (1000 * 60) > otpTimeout) {
                    throw new Exception("timeout");
                }

                isExist = true;
            }
            rs.close();
        } catch (Exception ex) {
            Logger.getLogger(m_member_model.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        conn.close();

        return isExist;
    }
}
