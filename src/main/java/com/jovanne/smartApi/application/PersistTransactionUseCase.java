package com.jovanne.smartApi.application;

import com.jovanne.smartApi.application.input.PersistTransactionInput;
import com.jovanne.smartApi.application.output.TransactionOutput;
import com.jovanne.smartApi.domain.Category;
import com.jovanne.smartApi.domain.Transaction;
import com.jovanne.smartApi.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersistTransactionUseCase implements BaseUseCase<TransactionOutput, PersistTransactionInput> {

    @Autowired
    TransactionRepository repository;

    @Tool(name = "persist-transactions", description = "persiste uma nova transação financeira")
    public TransactionOutput execute(PersistTransactionInput input){
        var transacao  = repository.save(
                new Transaction(input.description(), input.category(), input.amount()));
        return TransactionOutput.from(transacao);
    }
}
