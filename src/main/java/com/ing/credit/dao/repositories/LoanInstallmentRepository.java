package com.ing.credit.dao.repositories;

import com.ing.credit.dao.entities.LoanInstallmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LoanInstallmentRepository extends JpaRepository<LoanInstallmentEntity, UUID> {

    List<LoanInstallmentEntity> findByLoanId(UUID loanId);
}
