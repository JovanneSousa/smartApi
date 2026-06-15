package com.jovanne.smartApi.infraestructure.persistense.repository;

import com.jovanne.smartApi.domain.Category;
import com.jovanne.smartApi.domain.Transaction;
import com.jovanne.smartApi.domain.TransactionRepository;
import com.jovanne.smartApi.infraestructure.persistense.entity.TransactionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaTransactionRepository implements TransactionRepository {
    @Autowired
    TransactionEntityRepository repository;

    @Override
    public Transaction save(Transaction transaction) {
        var entity = TransactionEntity.fromTransaction(transaction);
        return repository.save(entity).toDomain();
    }

    @Override
    public List<Transaction> findAllByCategory(Category category) {
        return repository.findAllByCategory(category)
                .stream()
                .map(TransactionEntity::toDomain)
                .toList();
    }
}
