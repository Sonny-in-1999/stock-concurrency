package com.neurotoxin.stockexample.repository;

import com.neurotoxin.stockexample.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 편의를 위해 Stock 엔티티를 사용하였으나, 실제 production에서는 별도의 JDBC/Data Source를 사용해야함
 */
public interface LockRepository extends JpaRepository<Stock, Long> {

    @Query(value = "select get_lock(:key, 3000)", nativeQuery = true)
    void getLock(@Param("key") String key);

    @Query(value = "select release_lock(:key)", nativeQuery = true)
    void releaseLock(@Param("key") String key);
}
