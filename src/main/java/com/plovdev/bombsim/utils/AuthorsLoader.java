package com.plovdev.bombsim.utils;

import com.plovdev.bombsim.dto.Author;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public final class AuthorsLoader {
    private AuthorsLoader() {
    }

    public static @NonNull List<Author> loadAllAuthors() {
        List<Author> authors = new ArrayList<>();
        authors.add(createAuthor("plov", "Anton Pavlov", "Lead Developer"));
        authors.add(createAuthor("arsen", "Artem Boyko (Arsen 512)", "Lead Designer, 3D"));
        authors.add(createAuthor("onetwoz", "OneTwoZ Dev", "Helps Developer"));
        authors.add(createAuthor("matfey", "Mat Fey", "QA Engineer"));

        return authors;
    }

    @Contract("_, _, _ -> new")
    private static @NonNull Author createAuthor(String id, String name, String bio) {
        return new Author("assets/Interface/Icons/Authors/" + id + ".png", id, name, bio);
    }
}