package com.www.viewpoint.assemblymember.repository;

import com.www.viewpoint.assemblymember.model.entity.AssemblyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AssemblyMemberRepository extends JpaRepository<AssemblyMember, Long> {
    @Query(
            value = "SELECT * FROM national_assembly_member WHERE age = ?1 ORDER BY RAND() LIMIT 8",
            nativeQuery = true
    )
    List<AssemblyMember> findRandomByAgeLimit8(Integer age);

    @Query(
            value = "SELECT * FROM national_assembly_member ORDER BY RAND() LIMIT 8",
            nativeQuery = true
    )
    List<AssemblyMember> findRandomLimit8();
}
