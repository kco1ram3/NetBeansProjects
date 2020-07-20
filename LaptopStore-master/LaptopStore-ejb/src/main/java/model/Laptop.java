/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
@Table(name = "LAPTOP", catalog = "", schema = "APP")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Laptop.findAll", query = "SELECT l FROM Laptop l"),
    @NamedQuery(name = "Laptop.findById", query = "SELECT l FROM Laptop l WHERE l.id = :id"),
    @NamedQuery(name = "Laptop.findByBrand", query = "SELECT l FROM Laptop l WHERE l.brand = :brand"),
    @NamedQuery(name = "Laptop.findByPrice", query = "SELECT l FROM Laptop l WHERE l.price = :price"),
    @NamedQuery(name = "Laptop.findByQuantity", query = "SELECT l FROM Laptop l WHERE l.quantity = :quantity"),
    @NamedQuery(name = "Laptop.findByInstock", query = "SELECT l FROM Laptop l WHERE l.instock = :instock")})
public class Laptop implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID", nullable = false)
    private Integer id;
    @Size(max = 45)
    @Column(name = "BRAND", length = 45)
    private String brand;
    @Size(max = 45)
    @Column(name = "PRICE", length = 45)
    private String price;
    @Size(max = 45)
    @Column(name = "QUANTITY", length = 45)
    private String quantity;
    @Column(name = "INSTOCK")
    private Boolean instock;
    
    @OneToOne
    private Laptop mLaptop;
    
    @ManyToOne
    private Customer CustomerId;
    @ManyToMany
    private Collection<Laptopcolor> laptopcolor = new ArrayList<Laptopcolor>();
    
    public Customer getCustomerId(){
        return CustomerId;
    }
    
    public void SetCustomerId(Customer CustomerId){
        this.CustomerId = CustomerId;
    }
    
    public Collection<Laptopcolor> getLaptopcolor(){
        return laptopcolor;
    }
    
    public void setLaptopcolor(Collection<Laptopcolor> laptopcolor){
        this.laptopcolor = laptopcolor;
    }

    public Laptop() {
    }

    public Laptop(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public Boolean getInstock() {
        return instock;
    }

    public void setInstock(Boolean instock) {
        this.instock = instock;
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
        if (!(object instanceof Laptop)) {
            return false;
        }
        Laptop other = (Laptop) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Laptop[ id=" + id + " ]";
    }
    
}
