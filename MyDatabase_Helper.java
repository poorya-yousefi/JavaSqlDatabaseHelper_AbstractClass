package contents;

import java.sql.*;
import java.util.ArrayList;

public abstract class MyDatabase_Helper<T> {
    private static final String DATABASE_URL = "jdbc:sqlite:YOU_DB_NAME.sqlite";


    static Connection connect() {
        // SQLite connection string
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DATABASE_URL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    protected abstract String get_createTableSql();

    private void create_Table() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(get_createTableSql());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected abstract String get_insertSql();

    protected abstract void insertFunc(PreparedStatement pstmt, T t) throws SQLException;

    public boolean insert(T t) {
        create_Table();
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(get_insertSql())) {
            insertFunc(pstmt, t);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    protected abstract String get_deleteSql();

    public boolean delete(int id) {
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(get_deleteSql())) {
            // set the corresponding parameter
            pstmt.setInt(1, id);
            // execute the delete statement
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    protected abstract String get_updateSql();

    protected abstract void updateFunc(PreparedStatement pstmt, T t) throws SQLException;

    public boolean update(T t) {
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(get_updateSql())) {
            updateFunc(pstmt, t);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    protected abstract String get_objectSql(int id);

    protected abstract T getObjectInfo(ResultSet rs) throws SQLException;

    public T getObject(int id) {
        create_Table();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(get_objectSql(id))) {
            return getObjectInfo(rs);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected abstract String get_listSql();

    public ArrayList<T> getAllObjectsList() {
        create_Table();
        ArrayList<T> list = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(get_listSql())) {
            // loop through the result set
            while (rs.next()) {
                list.add(getObjectInfo(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<T> getListWithCustomCondition(String condition) {
        create_Table();
        ArrayList<T> list = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(condition)) {
            while (rs.next()) {
                list.add(getObjectInfo(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

}
