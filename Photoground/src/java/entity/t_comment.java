package entity;

import java.util.Date;

public class t_comment {
    
    public static final String TABLE_NAME = "t_comment";
    
    public class ColumnName {
        public static final String ROWID = "RowID";
        public static final String ROWID_M_MEMBER = "RowID_M_Member";
        public static final String ROWID_T_PHOTO = "RowID_T_Photo";
        public static final String COMMENT = "Comment";
        public static final String COMMENTDATE = "CommentDate";
    }
    
    private long rowID;
    private long rowID_M_Member;
    private long rowID_T_Photo;
    private String comment;
    private Date commentDate;
    
    //Join Columm Table m_member
    private String name;
    private String surname;
    private String username;
    
    public long getRowID() {
        return this.rowID;
    }

    public void setRowID(long rowID) {
        this.rowID = rowID;
    }

    public long getRowID_M_Member() {
        return this.rowID_M_Member;
    }

    public void setRowID_M_Member(long rowID_M_Member) {
        this.rowID_M_Member = rowID_M_Member;
    }

    public long getRowID_T_Photo() {
        return this.rowID_T_Photo;
    }

    public void setRowID_T_Photo(long rowID_T_Photo) {
        this.rowID_T_Photo = rowID_T_Photo;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCommentDate() {
        return this.commentDate;
    }

    public void setCommentDate(Date commentDate) {
        this.commentDate = commentDate;
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

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
