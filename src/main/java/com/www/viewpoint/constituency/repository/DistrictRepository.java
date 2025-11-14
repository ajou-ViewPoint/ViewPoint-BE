package com.www.viewpoint.constituency.repository;

import com.www.viewpoint.constituency.model.entity.KoreaDistrict;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DistrictRepository extends JpaRepository<KoreaDistrict, Long> {


    @Query(
            value = """
        SELECT *
        FROM korea_districts d
        WHERE ST_Contains(
            d.boundary,
            ST_SRID(POINT(:lon, :lat), 4326)
        )
        LIMIT 1
        """,
            nativeQuery = true
    )
    Optional<KoreaDistrict> findDistrictByCoordinates(
            @Param("lon") double lon,
            @Param("lat") double lat

    );

    @Query(
            value = """
        SELECT *
        FROM korea_districts
        WHERE LEFT(code, 5) = :sggCode
        LIMIT 1
        """,
            nativeQuery = true
    )
    Optional<KoreaDistrict> findSggByCodePrefix(@Param("sggCode") String sggCode);
}
