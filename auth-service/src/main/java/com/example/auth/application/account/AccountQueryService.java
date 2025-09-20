package com.example.auth.application.account;

import com.example.auth.domain.account.Account;
import com.example.auth.domain.account.AccountRepository;
import com.example.auth.domain.common.PageResult;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class AccountQueryService implements AccountQueries {
    private final AccountRepository accountRepository;

    @Override
    public PageResult<Account> search(String q, List<String> sort, int page, int size) {
        int safePage = Math.max(0, page);
        int safeSize = Math.max(1, Math.min(100, size));
        String filter = (q == null || q.isBlank()) ? null : q.trim();
        List<String> sortSafe = (sort == null) ? java.util.List.of() : sort.stream()
                .filter(s -> s != null && !s.isBlank())
                .map(String::trim)
                .toList();
        return accountRepository.search(filter, sortSafe, safePage, safeSize);
    }
}
