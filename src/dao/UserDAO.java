package dao;

import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements IUserDAO {
    private static final String jdbcURL = "jdbc:mysql://localhost:3306/demo_user";
    private static final String jdbcUsername = "root";
    private static final String jdbcPassword = "loi123456";

    private static final String INSERT_USERS_SQL = "INSERT INTO users (name, email,country) VALUES (?,?,?);";
    private static final String SELECT_USER_BY_ID = "SELECT u.id, u.name, u.email, u.country FROM users u WHERE u.id = ?;";
    private static final String SELECT_ALL_USERS = "SELECT * FROM users";
    private static final String DELETE_USERS_SQL = "DELETE FROM users WHERE id = ?;";
    private static final String UPDATE_USERS_SQL = "UPDATE users SET name = ?, email= ?, country = ? WHERE id = ?;";

    public UserDAO(){

    }
    protected Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void insertUser(User user) throws SQLException {
        System.out.println(INSERT_USERS_SQL);
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USERS_SQL)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getCountry());
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    public User selectUser(int id) {
//        User user = null;
//        try (Connection connection = getConnection();
//             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_ID);) {
//            preparedStatement.setInt(1, id);
//            System.out.println(preparedStatement);
//            ResultSet rs = preparedStatement.executeQuery();
//
//            while (rs.next()) {
//                String name = rs.getString("name");
//                String email = rs.getString("email");
//                String country = rs.getString("country");
//                user = new User(id, name, email, country);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return user;
//    }

    @Override
    public List<User> selectById() {
        return null;
    }

    public List<User> selectAllUsers() {

        List<User> users = new ArrayList<>();
        try (Connection connection = getConnection();

             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERS);) {
            System.out.println(preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String country = resultSet.getString("country");
                users.add(new User(id, name, email, country));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public boolean deleteUser(int id) throws SQLException {
        boolean rowDeleted;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_USERS_SQL);) {
            statement.setInt(1, id);
            rowDeleted = statement.executeUpdate() > 0;
        }
        return rowDeleted;
    }

    public boolean updateUser(User user) throws SQLException {
        boolean rowUpdated;
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(UPDATE_USERS_SQL);) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getCountry());
            statement.setInt(4, user.getId());

            rowUpdated = statement.executeUpdate() > 0;
        }
        return rowUpdated;
    }

    @Override
    public User getUserById(int id) {
       User user = null;
       String query = "{CALL get_user_by_id(?)}";
       try(Connection connection =getConnection();
          CallableStatement callableStatement = connection.prepareCall(query);)
       {
           callableStatement.setInt(1,id);

           ResultSet resultSet = callableStatement.executeQuery();
           while (resultSet.next()){
               String name = resultSet.getString("name");

               String email = resultSet.getString("email");

               String country = resultSet.getString("country");

               user = new User(name,email,country);
           }
       }catch (SQLException e){
           e.printStackTrace();
       }
       return user;
    }

    @Override
    public List<User> selectById(int id) {
        List<User> list = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_ID);) {
             preparedStatement.setInt(1,id);
            System.out.println(preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String country = resultSet.getString("country");
                list.add(new User(name,email,country));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void insertUserStore(User user) throws SQLException {
       String query = "{CALL insert_user(?,?,?)}";
       try(Connection connection = getConnection();
           CallableStatement callableStatement = connection.prepareCall(query);) {

           callableStatement.setString(1, user.getName());

           callableStatement.setString(2, user.getEmail());

           callableStatement.setString(3, user.getCountry());

           System.out.println(callableStatement);

           callableStatement.executeUpdate();

       }catch (SQLException e){
           e.printStackTrace();
       }
    }

    @Override
    public void addUserTransaction(User user, int[] permisions) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        PreparedStatement preparedStatement1 = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(INSERT_USERS_SQL, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1,user.getName());
            preparedStatement.setString(2,user.getEmail());
            preparedStatement.setString(3,user.getCountry());
            int rowAffected = preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();
            int userId = 0;
            if (resultSet.next())
                userId = resultSet.getInt(1);
            if (rowAffected == 1){
                String sql = "INSERT INTO user_permision(user_id,permision_id) VALUES(?,?)";
                preparedStatement1 = connection.prepareStatement(sql);
                for (int permisionId : permisions ){
                    preparedStatement1.setInt(1, userId);
                    preparedStatement1.setInt(2,permisionId);
                    preparedStatement1.executeUpdate();
                }
                connection.commit();
            }else {
                connection.rollback();
            }
        } catch (SQLException ex) {
            try {

                if (connection != null)
                    connection.rollback();
            } catch (SQLException e) {

                System.out.println(e.getMessage());
            }
            System.out.println(ex.getMessage());
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (preparedStatement1 != null) preparedStatement1.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
