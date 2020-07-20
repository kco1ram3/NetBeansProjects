/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Administrator
 */
@Entity
@Table(name = "test")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Test.findAll", query = "SELECT t FROM Test t"),
    @NamedQuery(name = "Test.findById", query = "SELECT t FROM Test t WHERE t.id = :id"),
    @NamedQuery(name = "Test.findByColumn1", query = "SELECT t FROM Test t WHERE t.column1 = :column1"),
    @NamedQuery(name = "Test.findByColumn2", query = "SELECT t FROM Test t WHERE t.column2 = :column2"),
    @NamedQuery(name = "Test.findByColumn3", query = "SELECT t FROM Test t WHERE t.column3 = :column3"),
    @NamedQuery(name = "Test.findByColumn4", query = "SELECT t FROM Test t WHERE t.column4 = :column4"),
    @NamedQuery(name = "Test.findByColumn5", query = "SELECT t FROM Test t WHERE t.column5 = :column5")})
public class Test implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 255)
    @Column(name = "column1")
    private String column1;
    @Column(name = "column2")
    private Integer column2;
    @Column(name = "column3")
    private Long column3;
    @Column(name = "column4")
    private Boolean column4;
    @Column(name = "column5")
    @Temporal(TemporalType.TIMESTAMP)
    private Date column5;

    public Test() {
    }

    public Test(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getColumn1() {
        return column1;
    }

    public void setColumn1(String column1) {
        this.column1 = column1;
    }

    public Integer getColumn2() {
        return column2;
    }

    public void setColumn2(Integer column2) {
        this.column2 = column2;
    }

    public Long getColumn3() {
        return column3;
    }

    public void setColumn3(Long column3) {
        this.column3 = column3;
    }

    public Boolean getColumn4() {
        return column4;
    }

    public void setColumn4(Boolean column4) {
        this.column4 = column4;
    }

    public Date getColumn5() {
        return column5;
    }

    public void setColumn5(Date column5) {
        this.column5 = column5;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Test)) {
            return false;
        }
        Test other = (Test) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Test[ id=" + id + " ]";
    }
    
}
