package com.example.auth.application.account;

import com.example.auth.domain.account.Account;
import com.example.auth.domain.account.AccountRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class AccountQueryService implements AccountQueries {
    private final AccountRepository accountRepository;

    @Override
    public List<Account> list() {
        return accountRepository.findAll();
    }
}
