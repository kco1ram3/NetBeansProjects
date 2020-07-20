/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author HONG
 */
@Entity
@Table(name = "LAPTOPSPECIFICATION", catalog = "", schema = "APP")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Laptopspecification.findAll", query = "SELECT l FROM Laptopspecification l"),
    @NamedQuery(name = "Laptopspecification.findById", query = "SELECT l FROM Laptopspecification l WHERE l.id = :id"),
    @NamedQuery(name = "Laptopspecification.findByProcessor", query = "SELECT l FROM Laptopspecification l WHERE l.processor = :processor"),
    @NamedQuery(name = "Laptopspecification.findByRam", query = "SELECT l FROM Laptopspecification l WHERE l.ram = :ram"),
    @NamedQuery(name = "Laptopspecification.findByHdd", query = "SELECT l FROM Laptopspecification l WHERE l.hdd = :hdd"),
    @NamedQuery(name = "Laptopspecification.findByScreensize", query = "SELECT l FROM Laptopspecification l WHERE l.screensize = :screensize"),
    @NamedQuery(name = "Laptopspecification.findByBatterylife", query = "SELECT l FROM Laptopspecification l WHERE l.batterylife = :batterylife"),
    @NamedQuery(name = "Laptopspecification.findByOperationsystem", query = "SELECT l FROM Laptopspecification l WHERE l.operationsystem = :operationsystem")})
public class Laptopspecification implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID", nullable = false)
    private Integer id;
    @Size(max = 45)
    @Column(name = "PROCESSOR", length = 45)
    private String processor;
    @Size(max = 45)
    @Column(name = "RAM", length = 45)
    private String ram;
    @Size(max = 45)
    @Column(name = "HDD", length = 45)
    private String hdd;
    @Size(max = 45)
    @Column(name = "SCREENSIZE", length = 45)
    private String screensize;
    @Size(max = 45)
    @Column(name = "BATTERYLIFE", length = 45)
    private String batterylife;
    @Size(max = 45)
    @Column(name = "OPERATIONSYSTEM", length = 45)
    private String operationsystem;
    
    @OneToOne(mappedBy = "mCustomer")
    private Laptopspecification mLaptopspecification;

    public Laptopspecification() {
    }

    public Laptopspecification(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public String getHdd() {
        return hdd;
    }

    public void setHdd(String hdd) {
        this.hdd = hdd;
    }

    public String getScreensize() {
        return screensize;
    }

    public void setScreensize(String screensize) {
        this.screensize = screensize;
    }

    public String getBatterylife() {
        return batterylife;
    }

    public void setBatterylife(String batterylife) {
        this.batterylife = batterylife;
    }

    public String getOperationsystem() {
        return operationsystem;
    }

    public void setOperationsystem(String operationsystem) {
        this.operationsystem = operationsystem;
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
        if (!(object instanceof Laptopspecification)) {
            return false;
        }
        Laptopspecification other = (Laptopspecification) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Laptopspecification[ id=" + id + " ]";
    }
    
}
