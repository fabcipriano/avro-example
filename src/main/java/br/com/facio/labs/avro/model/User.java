package br.com.facio.labs.avro.model;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author fabianocp
 */
public class User implements Serializable {

    private String fisrtName;
    private String lastName;
    private int age;
    private List<User> dependents;
    private Address addr;

    public User() {        
    }
    
    public User(String fisrtName, String lastName, int age, List<User> dependents, Address addr) {
        this.fisrtName = fisrtName;
        this.lastName = lastName;
        this.age = age;
        this.dependents = dependents;
        this.addr = addr;
    }

    public String getFisrtName() {
        return fisrtName;
    }

    public void setFisrtName(String fisrtName) {
        this.fisrtName = fisrtName;
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
    
    
}
