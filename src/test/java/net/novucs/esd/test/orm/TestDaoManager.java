package net.novucs.esd.test.orm;

import static net.novucs.esd.test.util.TestUtils.createTestDaoManager;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.novucs.esd.orm.Column;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.DaoManager;
import net.novucs.esd.orm.Table;
import net.novucs.esd.orm.Where;
import net.novucs.esd.util.Password;
import org.junit.Before;
import org.junit.Test;

public class TestDaoManager {

  private transient Dao<TableA> tableADao;
  private transient Dao<TableAB> tableABDao;
  private transient Dao<TableB> tableBDao;
  private transient Dao<AllFieldsTable> allFieldsTableDao;

  @Before
  public void setUp() throws SQLException {
    DaoManager daoManager = createTestDaoManager();
    daoManager.init(Arrays.asList(TableA.class, TableAB.class, TableB.class, AllFieldsTable.class));

    tableADao = daoManager.get(TableA.class);
    tableABDao = daoManager.get(TableAB.class);
    tableBDao = daoManager.get(TableB.class);
    allFieldsTableDao = daoManager.get(AllFieldsTable.class);
  }

  @Test
  public void testSelectAll() throws SQLException {
    List<AllFieldsTable> created = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      AllFieldsTable table = new AllFieldsTable();
      table.string = "test_string";
      allFieldsTableDao.insert(table);
      created.add(table);
    }

    List<AllFieldsTable> selected = allFieldsTableDao.select()
        .where(new Where()
            .eq("string", "test_string")
            .and()
            .eq("id", 1)
            .or()
            .eq("id", 2)
        )
        .limit(5)
        .all();

    List<AllFieldsTable> expected = created.subList(0, 2);
    assertEquals("Should select first two created entries", expected, selected);
  }

  @Test
  public void testSelectById() throws SQLException {
    AllFieldsTable created = new AllFieldsTable();
    allFieldsTableDao.insert(created);
    AllFieldsTable selected = allFieldsTableDao.selectById(created.id);
    assertEquals("Selecting the record just created should be the same", created, selected);
  }

  @Test
  public void testUpdate() throws SQLException {
    AllFieldsTable created = new AllFieldsTable();
    created.integer = 1;
    allFieldsTableDao.insert(created);
    created.integer = 2;
    allFieldsTableDao.update(created);
    AllFieldsTable selected = allFieldsTableDao.selectById(created.id);
    assertEquals("Updates should edit the record by its given ID", created, selected);
  }

  @Test
  public void testDeletion() throws SQLException {
    AllFieldsTable created = new AllFieldsTable();
    allFieldsTableDao.insert(created);
    allFieldsTableDao.delete(created);
    AllFieldsTable selected = allFieldsTableDao.select()
        .where(new Where().eq("id", created.id)).first();
    assertNull("Deleting a record should remove from database", selected);
  }

  @Test
  public void testForeignRelationsSupported() throws SQLException {
    TableA tableA = new TableA();
    tableADao.insert(tableA);

    TableB tableB = new TableB();
    tableBDao.insert(tableB);

    TableAB tableAB = new TableAB();
    tableAB.tableAId = tableA.id;
    tableAB.tableBId = tableB.id;
    tableABDao.insert(tableAB);

    TableAB selectedAB = tableABDao.selectById(tableAB.id);
    TableA selectedA = tableADao.selectById(selectedAB.tableAId);
    TableB selectedB = tableBDao.selectById(selectedAB.tableBId);

    assertTrue("Foreign key reference expected to be established",
        tableA.id.equals(selectedA.id) && tableB.id.equals(selectedB.id));
  }

  @Table
  public static class TableA {

    @Column(primary = true)
    private Integer id;

    @SuppressWarnings("unused")
    @Column(nullable = true)
    private String value;
  }

  @Table
  public static class TableAB {

    @Column(primary = true)
    public Integer id;

    @Column(foreign = TableA.class)
    public Integer tableAId;

    @Column(foreign = TableB.class)
    public Integer tableBId;
  }

  @Table
  public static class TableB {

    @Column(primary = true)
    public Integer id;

    @SuppressWarnings("unused")
    @Column(nullable = true)
    private String value;
  }

  @Table
  public static class AllFieldsTable {

    @Column(primary = true)
    public Integer id;

    @Column
    public String string;

    @Column
    public Password password;

    @Column
    public Integer integer;

    @Column
    public ZonedDateTime zonedDateTime;

    public AllFieldsTable() {
      this(UUID.randomUUID().toString());
    }

    public AllFieldsTable(String password) {
      this.string = UUID.randomUUID().toString();
      this.password = Password.fromPlaintext(password);
      this.integer = ThreadLocalRandom.current().nextInt();
      this.zonedDateTime = ZonedDateTime.now(ZoneOffset.UTC);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      AllFieldsTable that = (AllFieldsTable) o;
      return Objects.equals(id, that.id) &&
          Objects.equals(string, that.string) &&
          Objects.equals(password, that.password) &&
          Objects.equals(integer, that.integer) &&
          Objects.equals(zonedDateTime, that.zonedDateTime);
    }

    @Override
    public int hashCode() {
      return Objects.hash(id, string, password, integer, zonedDateTime);
    }

    @Override
    public String toString() {
      return "AllFieldsTable{" +
          "id=" + id +
          ", string='" + string + '\'' +
          ", password=" + password +
          ", integer=" + integer +
          ", zonedDateTime=" + zonedDateTime +
          '}';
    }
  }
}
