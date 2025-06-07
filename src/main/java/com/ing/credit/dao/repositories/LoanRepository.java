package com.ing.credit.dao.repositories;

import com.ing.credit.dao.entities.LoanEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, UUID> {

    List<LoanEntity> findByCustomerId(UUID customerId);

    @EntityGraph(attributePaths = {"installments"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<LoanEntity> findWithInstallmentsById(UUID loanId);
}
