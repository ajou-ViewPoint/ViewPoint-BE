package com.www.viewpoint.constituency.repository;

import com.www.viewpoint.constituency.model.entity.Constituency;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConstituencyRepository extends JpaRepository<Constituency, Integer> {

//    @Query("""
//        SELECT DISTINCT wi.regionId
//        FROM WinnerInfo wi
//        WHERE wi.regionId IS NOT NULL
//        ORDER BY function('RAND')
//    """)
//    List<Long> findRandomRegionId(Pageable pageable);
}

