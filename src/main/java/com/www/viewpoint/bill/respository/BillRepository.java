package com.www.viewpoint.bill.respository;
import com.www.viewpoint.bill.model.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillRepository extends JpaRepository<Bill, Integer> {
    // 필요하면 커스텀 쿼리 메서드 정의 가능
}