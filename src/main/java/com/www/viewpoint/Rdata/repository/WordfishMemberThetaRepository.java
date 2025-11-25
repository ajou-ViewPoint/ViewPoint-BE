package com.www.viewpoint.Rdata.repository;

import com.www.viewpoint.Rdata.model.WordfishMemberTheta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WordfishMemberThetaRepository
        extends JpaRepository<WordfishMemberTheta, Long> {

    List<WordfishMemberTheta> findByCommittee(String committee);

    List<WordfishMemberTheta> findByCommitteeAndAge(String committee, Integer age);
}
