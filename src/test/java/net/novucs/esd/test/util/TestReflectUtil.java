package net.novucs.esd.test.util;

import static net.novucs.esd.test.util.TestUtils.assertNotConstructable;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import net.novucs.esd.orm.Column;
import net.novucs.esd.orm.ParsedColumn;
import net.novucs.esd.orm.Table;
import net.novucs.esd.util.ReflectUtil;
import org.junit.Test;

public class TestReflectUtil {

  @Test(expected = IllegalArgumentException.class)
  public void testConstructFailsOnBadAttributes() {
    ReflectUtil.constructModel(DummyTable.class, Arrays.asList(1, 2, 3));
  }

  @Test(expected = IllegalStateException.class)
  public void testSetValueFailsOnBadAttribute() {
    // Given
    ParsedColumn parsedColumn = new ParsedColumn(String.class, "this is not an attribute", false);

    // When
    ReflectUtil.setValue(DummyTable.class, parsedColumn, null);
  }

  @Test(expected = IllegalStateException.class)
  public void testGetValueFailsOnBadAttribute() {
    // Given
    ParsedColumn parsedColumn = new ParsedColumn(String.class, "this is not an attribute", false);

    // When
    ReflectUtil.getValue(DummyTable.class, parsedColumn);
  }

  @Test
  public void testInstigatingReflectUtilFails() throws ReflectiveOperationException {
    assertNotConstructable(ReflectUtil.class);
  }

  @Test
  public void testConstructEmptyModel() {
    DummyEmptyTable table = ReflectUtil.constructModel(DummyEmptyTable.class, new ArrayList<>());
    assertNotNull("Successfully created table", table);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructModelTooManyAttributes() {
    ReflectUtil.constructModel(DummyTable.class, Arrays.asList("", "", ""));
  }

  @Test
  public void testConstructModelTooLittleAttributes() {
    DummyTable table = ReflectUtil.constructModel(DummyTable.class, Collections.emptyList());
    assertNotNull("Creates empty table", table);
  }

  @Table
  public static class DummyEmptyTable {

  }

  @Table
  public static class DummyTable {

    @Column(primary = true)
    private Integer id;

    @Column()
    private String dummyString;

    public DummyTable() {
      // This constructor is intentionally left empty.
    }

    public DummyTable(Integer id, String dummyString) {
      this.id = id;
      this.dummyString = dummyString;
    }

    public Integer getId() {
      return id;
    }

    public void setId(Integer id) {
      this.id = id;
    }

    public String getDummyString() {
      return dummyString;
    }

    public void setDummyString(String dummyString) {
      this.dummyString = dummyString;
    }
  }
}
