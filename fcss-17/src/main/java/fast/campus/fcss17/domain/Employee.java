package fast.campus.fcss17.domain;

import java.util.List;
import lombok.Getter;

@Getter
public class Employee {

  private final String name;
  private final List<String> books;
  private final List<String> roles;

  public Employee(String name, List<String> books, List<String> roles) {
    this.name = name;
    this.books = books;
    this.roles = roles;
  }
}
