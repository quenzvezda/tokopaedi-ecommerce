package com.example.auth.application.account;

import com.example.auth.domain.account.Account;
import com.example.auth.domain.account.AccountRepository;
import com.example.auth.domain.common.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountQueryServiceTest {

    @Mock
    AccountRepository accountRepository;

    @InjectMocks
    AccountQueryService service;

    PageResult<Account> samplePage;

    @BeforeEach
    void setUp() {
        samplePage = new PageResult<>(List.<Account>of(), 0, 1, 0, 1);
    }

    @Test
    void search_sanitizesInputsBeforeDelegating() {
        when(accountRepository.search(eq("Alice"), eq(List.of("username ,desc", "id")), eq(0), eq(100)))
                .thenReturn(samplePage);

        var result = service.search("  Alice  ", Arrays.asList(" username ,desc ", null, " id "), -5, 500);

        assertThat(result).isSameAs(samplePage);
        verify(accountRepository).search(eq("Alice"), eq(List.of("username ,desc", "id")), eq(0), eq(100));
    }

    @Test
    void search_allowsEmptyParameters() {
        when(accountRepository.search(eq(null), eq(List.of()), eq(2), eq(10)))
                .thenReturn(samplePage);

        var result = service.search("   ", null, 2, 10);

        assertThat(result).isSameAs(samplePage);
        verify(accountRepository).search(eq(null), eq(List.of()), eq(2), eq(10));
    }
}
