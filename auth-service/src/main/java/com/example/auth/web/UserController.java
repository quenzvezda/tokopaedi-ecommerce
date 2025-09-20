package com.example.auth.web;

import com.example.auth.application.account.AccountQueries;
import com.example.auth.domain.account.Account;
import com.example.auth_service.web.api.UserApi;
import com.example.auth_service.web.model.User;
import com.example.auth_service.web.model.UserPage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {
    private final AccountQueries accountQueries;

    @InitBinder
    void normalizeSort(WebDataBinder binder) {
        binder.registerCustomEditor(List.class, "sort", new CustomCollectionEditor(List.class) {
            @Override
            protected Object convertElement(Object element) {
                return element == null ? null : element.toString();
            }

            @Override
            public void setValue(Object value) {
                if (value instanceof List<?> list) {
                    super.setValue(normalizeSortPairs(list));
                } else {
                    super.setValue(value);
                }
            }
        });
    }

    @Override
    public ResponseEntity<UserPage> listUsers(Integer page, Integer size, String q, List<String> sort) {
        int resolvedPage = page == null ? 0 : page;
        int resolvedSize = size == null ? 20 : size;
        var result = accountQueries.search(q, sort, resolvedPage, resolvedSize);
        var content = result.content().stream().map(UserController::toUserModel).toList();
        int totalElements = (int) Math.min(Integer.MAX_VALUE, Math.max(0L, result.totalElements()));
        var body = new UserPage()
                .content(content)
                .number(result.page())
                .size(result.size())
                .totalElements(totalElements)
                .totalPages(result.totalPages());
        return ResponseEntity.ok(body);
    }

    private static User toUserModel(Account account) {
        return new User()
                .id(account.getId())
                .username(account.getUsername());
    }

    private static List<String> normalizeSortPairs(List<?> rawValues) {
        var normalized = new ArrayList<String>();
        for (int i = 0; i < rawValues.size(); i++) {
            Object element = rawValues.get(i);
            if (element == null) {
                continue;
            }
            String token = element.toString().trim();
            if (token.isEmpty()) {
                continue;
            }
            if (token.contains(",")) {
                normalized.add(token);
                continue;
            }
            if (isField(token) && i + 1 < rawValues.size()) {
                Object next = rawValues.get(i + 1);
                String nextToken = next == null ? "" : next.toString().trim();
                if (!nextToken.isEmpty() && !nextToken.contains(",") && isDirection(nextToken)) {
                    normalized.add(token + "," + nextToken);
                    i++;
                    continue;
                }
            }
            normalized.add(token);
        }
        return normalized;
    }

    private static boolean isField(String token) {
        return token.equalsIgnoreCase("id") || token.equalsIgnoreCase("username");
    }

    private static boolean isDirection(String token) {
        return token.equalsIgnoreCase("asc") || token.equalsIgnoreCase("desc");
    }
}
