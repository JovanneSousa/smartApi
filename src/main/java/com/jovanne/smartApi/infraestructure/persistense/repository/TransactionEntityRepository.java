package com.jovanne.smartApi.infraestructure.persistense.repository;


import com.jovanne.smartApi.domain.Category;
import com.jovanne.smartApi.infraestructure.persistense.entity.TransactionEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

public interface TransactionEntityRepository extends CrudRepository<TransactionEntity, UUID> {
    List<TransactionEntity> findAllByCategory(Category category);
}
