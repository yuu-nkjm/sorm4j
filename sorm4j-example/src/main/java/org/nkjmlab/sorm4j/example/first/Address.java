package org.nkjmlab.sorm4j.example.first;

import org.nkjmlab.sorm4j.annotation.OrmColumnAliasPrefix;
import org.nkjmlab.sorm4j.annotation.OrmConstructor;

@OrmColumnAliasPrefix("a")
public class Address {

  public static final Address KYOTO = new Address("Kyoto", "520-0461");
  public static final Address TOKYO = new Address("Tokyo", "103-0027");
  public static final Address NARA = new Address("Nara", "630-8580");


  public static final String CREATE_TABLE_SQL =
      "CREATE TABLE IF NOT EXISTS address (name VARCHAR PRIMARY KEY, postal_code varchar)";

  private String name;
  private String postalCode;


  @OrmConstructor({"name", "postal_code"})
  public Address(String name, String postalCode) {
    this.name = name;
    this.postalCode = postalCode;
  }

  public String getName() {
    return name;
  }

  public String getPostalCode() {
    return postalCode;
  }

  @Override
  public String toString() {
    return "Address [name=" + name + ", postalCode=" + postalCode + "]";
  }


}
