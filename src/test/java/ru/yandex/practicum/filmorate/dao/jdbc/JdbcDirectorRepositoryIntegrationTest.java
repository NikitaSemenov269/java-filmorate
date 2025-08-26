package ru.yandex.practicum.filmorate.dao.jdbc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JdbcDirectorRepository.class, DirectorRowMapper.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class JdbcDirectorRepositoryIntegrationTest {

    @Autowired
    private JdbcDirectorRepository directorRepository;


    @Test
    @Sql(scripts = {"/test-schema.sql", "/test-data.sql"})
    public void testGetDirectorById() {
        Long directorId = 1L; // Квентин Тарантино

        Optional<Director> directorOptional = directorRepository.getDirectorById(directorId);

        assertThat(directorOptional).isPresent().hasValueSatisfying(director -> {
            assertThat(director.getId()).isEqualTo(directorId);
            assertThat(director.getName()).isEqualTo("Квентин Тарантино");
        });
    }

    @Test
    @Sql(scripts = {"/test-schema.sql", "/test-data.sql"})
    public void testGetDirectorById_NonExistent() {
        Long nonExistentDirectorId = 999L;

        Optional<Director> directorOptional = directorRepository.getDirectorById(nonExistentDirectorId);

        assertThat(directorOptional).isEmpty();
    }

    @Test
    @Sql(scripts = {"/test-schema.sql", "/test-data.sql"})
    public void testFindAllDirectors() {
        Collection<Director> directors = directorRepository.findAllDirectors();

        assertThat(directors).isNotEmpty().hasSize(6).extracting(Director::getName).containsExactlyInAnyOrder("Квентин Тарантино", "Кристофер Нолан", "Стивен Спилберг", "Джеймс Кэмерон", "Мартин Скорсезе", "Андрей Тарковский");
    }

    @Test
    @Sql(scripts = {"/test-schema.sql", "/test-data.sql"})
    public void testFindDirectorByFilmId() {
        Long filmId = 1L;
        Set<Director> filmDirectors = directorRepository.findDirectorByFilmId(filmId);
        assertThat(filmDirectors).isNotEmpty();
        boolean hasDirectorWithId1 = filmDirectors.stream().anyMatch(director -> director.getId().equals(1L));
        assertThat(hasDirectorWithId1).isTrue();
    }

    @Test
    @Sql(scripts = {"/test-schema.sql", "/test-data.sql"})
    public void testCreateDirector() {
        Director newDirector = Director.builder().name("Новый режиссер").build();
        Director createdDirector = directorRepository.createDirector(newDirector);

        assertThat(createdDirector).isNotNull().hasFieldOrPropertyWithValue("name", "Новый режиссер");
        assertThat(createdDirector.getId()).isNotNull().isPositive();
        Optional<Director> foundDirector = directorRepository.getDirectorById(createdDirector.getId());
        assertThat(foundDirector).isPresent().hasValueSatisfying(director -> assertThat(director.getName()).isEqualTo("Новый режиссер"));
    }

    @Test
    @Sql(scripts = {"/test-schema.sql", "/test-data.sql"})
    public void testUpdateDirector() {
        Long existingDirectorId = 1L; // Квентин Тарантино
        String updatedName = "Квентин Тарантино (обновленный)";


        Optional<Director> existingDirector = directorRepository.getDirectorById(existingDirectorId);
        assertThat(existingDirector).isPresent();

        Director directorToUpdate = existingDirector.get().toBuilder().name(updatedName).build();

        Director updatedDirector = directorRepository.updateDirector(directorToUpdate);

        assertThat(updatedDirector).isNotNull().hasFieldOrPropertyWithValue("id", existingDirectorId).hasFieldOrPropertyWithValue("name", updatedName);


        Optional<Director> verifiedDirector = directorRepository.getDirectorById(existingDirectorId);
        assertThat(verifiedDirector).isPresent().hasValueSatisfying(director -> assertThat(director.getName()).isEqualTo(updatedName));
    }

    @Test
    @Sql(scripts = {"/test-schema.sql", "/test-data.sql"})
    public void testDeleteDirector() {
        Director directorToDelete = Director.builder().name("Режиссер для удаления").build();
        Director createdDirector = directorRepository.createDirector(directorToDelete);
        Long directorIdToDelete = createdDirector.getId();

        Optional<Director> directorBeforeDelete = directorRepository.getDirectorById(directorIdToDelete);
        assertThat(directorBeforeDelete).isPresent();

        boolean isDeleted = directorRepository.deleteDirector(directorIdToDelete);
        assertThat(isDeleted).isTrue();

        Optional<Director> directorAfterDelete = directorRepository.getDirectorById(directorIdToDelete);
        assertThat(directorAfterDelete).isEmpty();
    }

    @Test
    @Sql(scripts = {"/test-schema.sql", "/test-data.sql"})
    public void testDeleteDirectorNotExist() {
        Long nonExistentDirectorId = 999L;

        boolean isDeleted = directorRepository.deleteDirector(nonExistentDirectorId);

        assertThat(isDeleted).isFalse();
    }
}