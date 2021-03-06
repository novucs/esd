package net.novucs.esd.orm;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The type Dao manager.
 */
public class DaoManager {

  private final transient Map<Class<?>, Dao<?>> daos = Collections
      .synchronizedMap(new LinkedHashMap<>());
  private final transient ConnectionSource connectionSource;

  /**
   * Instantiates a new Dao manager.
   *
   * @param connectionSource the connection source
   */
  public DaoManager(ConnectionSource connectionSource) {
    this.connectionSource = connectionSource;
  }

  /**
   * Gets daos.
   *
   * @return the daos
   */
  public Map<Class<?>, Dao<?>> getDaos() {
    return daos;
  }

  /**
   * Gets connection source.
   *
   * @return the connection source
   */
  public ConnectionSource getConnectionSource() {
    return connectionSource;
  }

  /**
   * Get dao.
   *
   * @param <M>        the type parameter
   * @param modelClass the model class
   * @return the dao
   */
  public <M> Dao<M> get(Class<M> modelClass) {
    //noinspection unchecked
    return (Dao<M>) daos.getOrDefault(modelClass, null);
  }

  /**
   * Init.
   *
   * @param modelClasses the model classes
   * @throws SQLException the sql exception
   */
  public void init(List<Class<?>> modelClasses) throws SQLException {
    List<Class<?>> remaining = new ArrayList<>(modelClasses);

    while (!remaining.isEmpty()) {
      boolean updated = false;
      Iterator<Class<?>> it = remaining.iterator();

      while (it.hasNext()) {
        Class<?> modelClass = it.next();
        if (hasUnseenForeignKeys(modelClass)) {
          continue;
        }
        validateForeignKeys(modelClass, modelClasses);
        Dao<?> dao = new Dao<>(connectionSource, modelClass);
        daos.put(modelClass, dao);
        it.remove();
        updated = true;
      }

      if (!updated) {
        List<String> remainingNames = remaining.stream().map(Class::getName)
            .collect(Collectors.toList());
        throw new IllegalArgumentException("Cyclic references found between: " + remainingNames);
      }
    }

    for (Dao<?> dao : daos.values()) {
      dao.createTable();
    }
  }

  private void validateForeignKeys(Class<?> modelClass, List<Class<?>> references) {
    for (ParsedColumn column : ParsedModel.of(modelClass).getColumns().values()) {
      if (column.isForeignKey() && !references.contains(column.getForeignReference())) {
        throw new IllegalArgumentException(modelClass.getName()
            + " has a foreign key to a model that has not been presented to the DaoManager: "
            + column.getForeignReference().getName());
      }
    }
  }

  private boolean hasUnseenForeignKeys(Class<?> modelClass) {
    for (ParsedColumn column : ParsedModel.of(modelClass).getColumns().values()) {
      if (column.isForeignKey() && !daos.containsKey(column.getForeignReference())) {
        return true;
      }
    }
    return false;
  }
}
