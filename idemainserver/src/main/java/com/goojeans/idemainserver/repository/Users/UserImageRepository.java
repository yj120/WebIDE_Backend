package com.goojeans.idemainserver.repository.Users;

import com.goojeans.idemainserver.domain.entity.Users.UserImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserImageRepository extends JpaRepository<UserImage,Long> {

}
