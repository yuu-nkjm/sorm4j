package org.nkjmlab.sorm4j.example;

import java.util.List;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.example.first.Address;
import org.nkjmlab.sorm4j.example.first.Customer;
import org.nkjmlab.sorm4j.result.Tuple2;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;

public class JoinExample {

  public static void main(String[] args) {
    Sorm sorm = Sorm.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;");

    sorm.acceptHandler(conn -> {
      conn.executeUpdate(Customer.CREATE_TABLE_SQL);
      conn.insert(Customer.ALICE, Customer.BOB, Customer.CAROL, Customer.DAVE);

      conn.executeUpdate(Address.CREATE_TABLE_SQL);
      conn.insert(Address.KYOTO, Address.TOKYO, Address.NARA);

      List<Tuple2<Address, Customer>> result =
          conn.join(Address.class, Customer.class, "address.name=customer.address");

      System.out.println(result);

      result = conn.readTupleList(Address.class, Customer.class,
          "select a.name as aname, a.postal_code as apostal_code, "
              + "c.id as cid, c.name as cname, c.address as caddress "
              + "from address a join customer c on a.name=c.address");


      System.out.println(result);


      String aAliasses = conn.getTableMetaData(Address.class).getColumnAliases();
      String cAliasses = conn.getTableMetaData(Customer.class).getColumnAliases();
      ParameterizedSql psql = ParameterizedSql.parse(
          "select {?}, {?} from address join customer on address.name=customer.address", aAliasses,
          cAliasses);
      System.out.println(psql.getSql());
      result = conn.readTupleList(Address.class, Customer.class, psql);
      System.out.println(result);



    });

  }
}
