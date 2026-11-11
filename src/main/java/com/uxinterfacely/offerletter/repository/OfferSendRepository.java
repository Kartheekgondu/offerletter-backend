package com.uxinterfacely.offerletter.repository;

import com.uxinterfacely.offerletter.model.OfferSend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OfferSendRepository extends JpaRepository<OfferSend, Long> {
    List<OfferSend> findAllByOrderByCreatedAtDesc();

    @Query("""
        SELECT o FROM OfferSend o
        WHERE LOWER(COALESCE(o.candidateName, '')) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(COALESCE(o.recipientEmail, '')) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(COALESCE(o.jobTitle, '')) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(COALESCE(o.department, '')) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(COALESCE(o.workLocation, '')) LIKE LOWER(CONCAT('%', :q, '%'))
           OR (:id IS NOT NULL AND o.id = :id)
        """)
    Page<OfferSend> searchOffers(@Param("q") String query, @Param("id") Long id, Pageable pageable);
}
