package br.com.facio.labs.avro.model;

import br.com.facio.labs.avro.utils.NullableDateAsLongEncoding;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.avro.reflect.AvroEncode;

/**
 *
 * @author fabianocp
 */
public class User implements Serializable {

    private long primaryKey;
    private String fisrtName;
    private String lastName;
   
    @AvroEncode(using = NullableDateAsLongEncoding.class)
    private Date created;
    private int age;
    private List<User> dependents;
    private Address addr;

    public User() {        
    }
    
    public User(long primaryKey, String fisrtName, String lastName, int age, List<User> dependents, Address addr, Date created) {
        this.primaryKey = primaryKey;
        this.fisrtName = fisrtName;
        this.lastName = lastName;
        this.age = age;
        if (dependents == null) {
            this.dependents = new ArrayList<>();
        } else {        
            this.dependents = dependents;
        }
        this.addr = addr;
        this.created = created;
    }

    public long getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(long primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getFisrtName() {
        return fisrtName;
    }

    public void setFisrtName(String fisrtName) {
        this.fisrtName = fisrtName;
    }
    
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<User> getDependents() {
        return dependents;
    }

    public void setDependents(List<User> dependents) {
        this.dependents = dependents;
    }

    public Address getAddr() {
        return addr;
    }

    public void setAddr(Address addr) {
        this.addr = addr;
    }

    @Override
    public String toString() {
        return "User{" + "primaryKey=" + primaryKey + ", fisrtName=" + fisrtName + ", lastName=" + lastName 
                + ", created=" + created + ", age=" + age 
                + ", dependents=" + dependents + ", addr=" + addr + '}';
    }
    
    
}
