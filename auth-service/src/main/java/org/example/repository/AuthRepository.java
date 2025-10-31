package org.example.repository;

import org.example.entity.Authentication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface AuthRepository extends JpaRepository<Authentication,Long> {

    @Transactional
    @Modifying
    @Query("SELECT a FROM Authentication a WHERE a.username = :username")
    List<Authentication> getAuthDatabyUsername(@Param("username") String username);

    @Transactional
    @Modifying
    @Query("DELETE FROM Authentication a WHERE a.id = :id")
    void deleteAuthDataById(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("UPDATE Authentication a SET a.authMessageTitle = :authMessageTitle, a.authMessageContent = :authMessageContent WHERE a.id = :id ")
    void updateAuthInfo(@Param("id") Long id, @Param("authMessageTitle") String authMessageTitle, @Param("authMessageContent") String authMessageContent);

}
