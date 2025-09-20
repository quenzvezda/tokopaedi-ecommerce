package com.example.auth.application.account;

import com.example.auth.domain.account.Account;
import com.example.auth.domain.common.PageResult;

import java.util.List;

public interface AccountQueries {
    PageResult<Account> search(String q, List<String> sort, int page, int size);
}
