/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author HONG
 */
@Entity
@Table(name = "LAPTOPCOLOR", catalog = "", schema = "APP")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Laptopcolor.findAll", query = "SELECT l FROM Laptopcolor l"),
    @NamedQuery(name = "Laptopcolor.findById", query = "SELECT l FROM Laptopcolor l WHERE l.id = :id"),
    @NamedQuery(name = "Laptopcolor.findByColor", query = "SELECT l FROM Laptopcolor l WHERE l.color = :color")})
public class Laptopcolor implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID", nullable = false)
    private Integer id;
    @Size(max = 45)
    @Column(name = "COLOR", length = 45)
    private String color;
    
    @ManyToMany
    private Collection<Laptop> laptopId;
    
    public Collection<Laptop> getLaptopId(){
        return laptopId;
    }
    
    public void setLaptopId(Collection<Laptop> laptopId){
        this.laptopId = laptopId;
    }

    public Laptopcolor() {
    }

    public Laptopcolor(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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
        if (!(object instanceof Laptopcolor)) {
            return false;
        }
        Laptopcolor other = (Laptopcolor) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Laptopcolor[ id=" + id + " ]";
    }
    
}
