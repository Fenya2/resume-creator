package course.work.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;

/**
 * Пользователь приложения. <br>
 * <br>
 * Каждый пользователь приложения идентифицируется своим логином
 */
@Entity
@Table(name = "tbl_user", indexes = {
        @Index(name = "idx_user_login", columnList = "login")
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String login;

    @Nullable
    private String userName;

    @Nullable
    private String password;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Nullable
    public String getUserName() {
        return userName;
    }

    public void setUserName(@Nullable String userName) {
        this.userName = userName;
    }
}
