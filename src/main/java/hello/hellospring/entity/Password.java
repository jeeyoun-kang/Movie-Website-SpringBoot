package hello.hellospring.entity;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "password_id")
@Table(name="password")
@Entity
public class Password {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long password_id;

    @Column(name = "salt")
    private String salt;

    @Column(name="password")
    private String password;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name="update_date")
    private Date update_date;

    @Builder
    public Password(String password){
        this.password=password;
    }
    public Password updatePassword(String password) {
        this.password = password;
        return this;
    }
    public Password update(String salt, String password, Date update_date){
        this.salt=salt;
        this.password=password;
        this.update_date=update_date;
        return this;
    }
}