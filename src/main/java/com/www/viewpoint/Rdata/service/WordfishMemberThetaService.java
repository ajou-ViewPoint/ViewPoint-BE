package com.www.viewpoint.Rdata.service;

import com.www.viewpoint.Rdata.model.WordfishMemberTheta;
import com.www.viewpoint.Rdata.repository.WordfishMemberThetaRepository;
import com.www.viewpoint.committee.model.entity.Committee;
import com.www.viewpoint.committee.repository.CommitteeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WordfishMemberThetaService {

    private final WordfishMemberThetaRepository wordfishRepository;
    private final CommitteeRepository committeeRepository;

    /**
     * committeeId로 Wordfish 데이터 조회
     */
    public List<WordfishMemberTheta> findByCommitteeId(Integer committeeId, Integer age) {
        Committee committee = committeeRepository.findById(committeeId)
                .orElseThrow(() -> new IllegalArgumentException("Committee not found: " + committeeId));

        String committeeName = committee.getCommitteeName();   // 필드명에 맞게 수정

        if (age != null) {
            return wordfishRepository.findByCommitteeAndAge(committeeName, age);
        }
        return wordfishRepository.findByCommittee(committeeName);
    }

    /**
     * committee 이름으로 직접 Wordfish 데이터 조회
     */
    public List<WordfishMemberTheta> findByCommitteeName(String committeeName, Integer age) {
        if (age != null) {
            return wordfishRepository.findByCommitteeAndAge(committeeName, age);
        }
        return wordfishRepository.findByCommittee(committeeName);
    }
}