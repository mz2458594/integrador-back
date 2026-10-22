package com.integrador.rocket.roadmap.services;
import com.integrador.rocket.roadmap.models.vocacionalQuestions.VocationalQuestion;
import com.integrador.rocket.roadmap.models.vocationalOption.VocationalOption;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class VocationalScoringServiceTest {
    private final VocationalScoringService service = new VocationalScoringService();
    private List<VocationalQuestion> bank() {
        List<VocationalQuestion> questions = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            var question = new VocationalQuestion((long) i + 1, "Question " + i, new ArrayList<>());
            String career = "Career " + (i % 6);
            for (int score = 0; score <= 4; score++)
                question.getVocationalOptions().add(new VocationalOption((long) i * 5 + score + 1, question, "Answer " + score, Map.of(career, score), new ArrayList<>()));
            questions.add(question);
        }
        return questions;
    }
    private List<Long> answers(List<VocationalQuestion> bank, int score) {
        return bank.stream().map(q -> q.getVocationalOptions().get(score).getId()).toList();
    }
    @Test void calculatesBalancedTotalsPercentagesAndDeterministicTies() {
        var bank = bank();
        var result = service.evaluate(bank, answers(bank, 4));
        assertEquals("Career 0", result.suggestedCareer());
        assertEquals(6, result.ranking().size());
        result.ranking().forEach(score -> { assertEquals(20, score.score()); assertEquals(20, score.maxScore()); assertEquals(100.0, score.percentage()); });
        assertEquals(30, result.answers().size());
    }
    @Test void strongestCareerComesFromDatabaseScores() {
        var bank = bank();
        var ids = bank.stream().map(q -> q.getVocationalOptions().get(q.getId() % 6 == 3 ? 4 : 0).getId()).toList();
        var result = service.evaluate(bank, ids);
        assertEquals("Career 2", result.suggestedCareer());
        assertEquals(100.0, result.ranking().get(0).percentage());
        assertEquals(0, result.ranking().get(1).score());
    }
    @Test void zeroInterestStillProducesAnHonestCompletedResult() {
        var bank = bank();
        var result = service.evaluate(bank, answers(bank, 0));
        assertEquals("Sin preferencia definida", result.suggestedCareer());
        assertTrue(result.ranking().stream().allMatch(score -> score.percentage() == 0));
    }
    @Test void rejectsMissingDuplicateForeignAndMultipleOptionsForSameQuestion() {
        var bank = bank();
        var ids = new ArrayList<>(answers(bank, 4));
        assertThrows(ValidationException.class, () -> service.evaluate(bank, null));
        assertThrows(ValidationException.class, () -> service.evaluate(bank, ids.subList(0, 29)));
        ids.set(0, ids.get(1));
        assertThrows(ValidationException.class, () -> service.evaluate(bank, ids));
        ids.set(0, 99999L);
        assertThrows(ValidationException.class, () -> service.evaluate(bank, ids));
        ids.set(0, bank.get(1).getVocationalOptions().get(0).getId());
        assertThrows(ValidationException.class, () -> service.evaluate(bank, ids));
        ids.set(0, null);
        assertThrows(ValidationException.class, () -> service.evaluate(bank, ids));
    }
    @Test void rejectsInvalidBankAndScoreConfiguration() {
        var bank = bank();
        assertThrows(ValidationException.class, () -> service.evaluate(bank.subList(0, 19), List.of()));
        bank.get(0).getVocationalOptions().clear();
        assertThrows(ValidationException.class, () -> service.evaluate(bank, java.util.stream.LongStream.rangeClosed(1, 30).boxed().toList()));
        var invalid = bank();
        invalid.get(0).getVocationalOptions().get(0).getCareerScore();
        invalid.get(0).getVocationalOptions().set(0, new VocationalOption(1L, invalid.get(0), "Bad", Map.of("Career 0", -1), new ArrayList<>()));
        assertThrows(ValidationException.class, () -> service.evaluate(invalid, answers(invalid, 4)));
    }
}
