package com.bean;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
@Entity
@Table(name = "files")
public class FileDB {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private String id;
  private String name;
  private String type;
  private int refuser;
  @Lob
  private byte[] data;
  public FileDB() {
  }
  public FileDB(String name, String type, byte[] data,int refuser) {
    this.name = name;
    this.type = type;
    this.data = data;
    this.refuser = refuser;
  }
  
  public int getRefUser() {
	return refuser;
  }
  public void setRefUser(int refuser) {
	this.refuser = refuser;
  }
  public String getId() {
    return id;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }
  public byte[] getData() {
    return data;
  }
  public void setData(byte[] data) {
    this.data = data;
  }
}