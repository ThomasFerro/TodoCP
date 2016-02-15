package fr.lille1.iut.helloworld.tcp;

public class Tache {
  private Integer id;
  private Status status;
  private String creator;
  private String user;
  private String description;

  public Tache(Integer id, String c, String d) {
    this.id = id;
    this.status = Status.TODO;
    this.creator = c;
    this.user = "";
    this.description = d;
  }

  public Integer getId() {
    return this.id;
  }

  public void setStatus(Status s) {
    this.status = s;
  }

  public Status getStatus() {
    return this.status;
  }

  public void setCreator(String c) {
    this.creator = c;
  }

  public String getCreator() {
    return this.creator;
  }

  public void setUser(String u) {
    this.user = u;
  }

  public String getUser() {
    return this.user;
  }

  public void setDescription(String d) {
    this.description = d;
  }

  public String getDescription() {
    return this.description;
  }
}
