package entity;

import java.util.Date;

public class t_photo {
    
    public static final String TABLE_NAME = "t_photo";
    
    public class ColumnName {
        public static final String ROWID = "RowID";
        public static final String ROWID_M_MEMBER = "RowID_M_Member";
        public static final String PHOTONAME = "PhotoName";
        public static final String PHOTODESCRIPTION = "PhotoDescription";
        public static final String PHOTODATE = "PhotoDate";
        public static final String FILEPATH = "FilePath";
        public static final String FILETHUMBNAILPATH = "FileThumbnailPath";
        public static final String LATITUDEORIGINAL = "LatitudeOriginal";
        public static final String LONGITUDEORIGINAL = "LongitudeOriginal";
        public static final String LATITUDEMAP = "LatitudeMap";
        public static final String LONGITUDEMAP = "LongitudeMap";
        public static final String VIDEOPATH = "VideoPath";
        public static final String NUMOFCOMMENT = "NumOfComment";
        public static final String CREATEDATE = "CreateDate";
        public static final String UPDATEDATE = "UpdateDate";
    }
    
    private long rowID;
    private long rowID_M_Member;
    private String photoName;
    private String photoDescription;
    private Date photoDate;
    private String filePath;
    private String fileThumbnailPath;
    private double latitudeOriginal;
    private double longitudeOriginal;
    private double latitudeMap;
    private double longitudeMap;
    private String videoPath;
    private int numOfComment;
    private Date createDate;
    private Date updateDate;
    
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
    
    public String getPhotoName() {
        return this.photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }
    
    public String getPhotoDescription() {
        return this.photoDescription;
    }

    public void setPhotoDescription(String photoDescription) {
        this.photoDescription = photoDescription;
    }
    
    public Date getPhotoDate() {
        return this.photoDate;
    }

    public void setPhotoDate(Date photoDate) {
        this.photoDate = photoDate;
    }
    
    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public String getFileThumbnailPath() {
        return this.fileThumbnailPath;
    }

    public void setFileThumbnailPath(String fileThumbnailPath) {
        this.fileThumbnailPath = fileThumbnailPath;
    }
    
    public double getLatitudeOriginal() {
        return this.latitudeOriginal;
    }

    public void setLatitudeOriginal(double latitudeOriginal) {
        this.latitudeOriginal = latitudeOriginal;
    }
    
    public double getLongitudeOriginal() {
        return this.longitudeOriginal;
    }

    public void setLongitudeOriginal(double longitudeOriginal) {
        this.longitudeOriginal = longitudeOriginal;
    }
    
    public double getLatitudeMap() {
        return this.latitudeMap;
    }

    public void setLatitudeMap(double latitudeMap) {
        this.latitudeMap = latitudeMap;
    }
    
    public double getLongitudeMap() {
        return this.longitudeMap;
    }

    public void setLongitudeMap(double longitudeMap) {
        this.longitudeMap = longitudeMap;
    }
    
    public String getVideoPath() {
        return this.videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }
    
    public int getNumOfComment() {
        return this.numOfComment;
    }

    public void setNumOfComment(int numOfComment) {
        this.numOfComment = numOfComment;
    }
    
    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    
    public Date getUpdateDate() {
        return this.updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}
