package com.www.viewpoint.constituency.model.dto;

import com.www.viewpoint.share.dto.AssemblyMemberSummaryDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class WinnerInfoDto extends AssemblyMemberSummaryDto {
    private String sidoName;
    private String sggName;
    private String regionCd;
    private BigDecimal voteRate;

    public WinnerInfoDto(
            Integer id,
            String name,
            String party,
            Integer age,
            String duty,
            String profileImage,
            String district,
            String sidoName,
            String sggName,
            String regionCd,
            BigDecimal voteRate
    ) {
        super(id.longValue(), name, party, age, duty, profileImage, district);
        this.sidoName = sidoName;
        this.sggName = sggName;
        this.regionCd = regionCd;
        this.voteRate = voteRate;
    }
}