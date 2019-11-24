package net.novucs.esd.test.util;

import static net.novucs.esd.test.util.TestUtil.assertNotConstructable;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import net.novucs.esd.orm.Column;
import net.novucs.esd.orm.ParsedColumn;
import net.novucs.esd.orm.Table;
import net.novucs.esd.util.ReflectUtil;
import org.junit.Test;

/**
 * The type Test reflect util.
 */
public class TestReflectUtil {

  /**
   * Test construct fails on no empty constructor.
   */
  @Test(expected = IllegalStateException.class)
  public void testConstructFailsOnNoEmptyConstructor() {
    ReflectUtil.constructModel(NoEmptyConstructor.class, Collections.emptyList());
  }

  /**
   * Test set value fails on bad attribute.
   */
  @Test(expected = IllegalStateException.class)
  public void testSetValueFailsOnBadAttribute() {
    // Given
    ParsedColumn parsedColumn = createDummyParsedColumn();

    // When
    ReflectUtil.setValue(DummyTable.class, parsedColumn, null);
  }

  private ParsedColumn createDummyParsedColumn() {
    return new ParsedColumn(String.class, "this is not an attribute",
        false, null, false, null);
  }

  /**
   * Test get value fails on bad attribute.
   */
  @Test(expected = IllegalStateException.class)
  public void testGetValueFailsOnBadAttribute() {
    // Given
    ParsedColumn parsedColumn = createDummyParsedColumn();

    // When
    ReflectUtil.getValue(DummyTable.class, parsedColumn);
  }

  /**
   * Test instigating reflect util fails.
   *
   * @throws ReflectiveOperationException the reflective operation exception
   */
  @Test
  public void testInstigatingReflectUtilFails() throws ReflectiveOperationException {
    assertNotConstructable(ReflectUtil.class);
  }

  /**
   * Test construct empty model.
   */
  @Test
  public void testConstructEmptyModel() {
    DummyEmptyTable table = ReflectUtil.constructModel(DummyEmptyTable.class, new ArrayList<>());
    assertNotNull("Successfully created table", table);
  }

  /**
   * Test construct model too many attributes.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testConstructModelTooManyAttributes() {
    ReflectUtil.constructModel(DummyTable.class, Arrays.asList("", "", ""));
  }

  /**
   * Test construct model too little attributes.
   */
  @Test
  public void testConstructModelTooLittleAttributes() {
    DummyTable table = ReflectUtil.constructModel(DummyTable.class, Collections.emptyList());
    assertNotNull("Creates empty table", table);
  }

  /**
   * The type No empty constructor.
   */
  @Table
  public static class NoEmptyConstructor {

    private final int ignored;

    /**
     * Instantiates a new No empty constructor.
     *
     * @param ignore the ignore
     */
    public NoEmptyConstructor(int ignore) {
      this.ignored = ignore;
    }

    /**
     * Gets ignored.
     *
     * @return the ignored
     */
    public int getIgnored() {
      return ignored;
    }
  }

  /**
   * The type Dummy empty table.
   */
  @Table
  public static class DummyEmptyTable {

  }

  /**
   * The type Dummy table.
   */
  @Table
  public static class DummyTable {

    @Column(primary = true)
    private Integer id;

    @Column()
    private String dummyString;

    /**
     * Instantiates a new Dummy table.
     */
    public DummyTable() {
      // This constructor is intentionally left empty.
    }

    /**
     * Instantiates a new Dummy table.
     *
     * @param id          the id
     * @param dummyString the dummy string
     */
    public DummyTable(Integer id, String dummyString) {
      this.id = id;
      this.dummyString = dummyString;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public Integer getId() {
      return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(Integer id) {
      this.id = id;
    }

    /**
     * Gets dummy string.
     *
     * @return the dummy string
     */
    public String getDummyString() {
      return dummyString;
    }

    /**
     * Sets dummy string.
     *
     * @param dummyString the dummy string
     */
    public void setDummyString(String dummyString) {
      this.dummyString = dummyString;
    }
  }
}
