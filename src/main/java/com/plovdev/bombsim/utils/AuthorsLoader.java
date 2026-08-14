package com.plovdev.bombsim.utils;

import com.plovdev.bombsim.dto.Author;
import org.jspecify.annotations.NonNull;

import java.util.List;

public final class AuthorsLoader {
    private AuthorsLoader() {
    }

    public static @NonNull List<Author> loadAllAuthors() {
        return List.of(new Author("assets/Interface/gear.png", "card_1", "Test Test", "t3irnvvrnoencwoerncweijc we"));
    }
}