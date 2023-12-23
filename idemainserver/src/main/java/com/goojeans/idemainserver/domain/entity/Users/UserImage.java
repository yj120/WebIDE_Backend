package com.goojeans.idemainserver.domain.entity.Users;

import com.goojeans.idemainserver.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Entity
@Builder
@Table(name = "USERIMAGE")
@AllArgsConstructor
public class UserImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userImage_id")
    private Long id;

    //@Lob
    @Lob
    private byte[] image; // 이미지 파일의  바이너리 데이터

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id",unique = true) // user id 를 외래키로 사용
    private User user;

    public UserImage(User user){
        this.user = user;
    }


    public void updateImage(byte[] image){
        this.image = image;
    }

}
