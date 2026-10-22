package com.integrador.rocket.roadmap.services;

import com.integrador.rocket.roadmap.models.vocacionalQuestions.VocationalQuestion;
import com.integrador.rocket.roadmap.models.vocationalOption.VocationalOption;
import com.integrador.rocket.roadmap.models.vocationaltestresults.dto.CareerScore;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class VocationalScoringService {
    public record Evaluation(String suggestedCareer, List<CareerScore> ranking, List<VocationalOption> answers) {}

    public Evaluation evaluate(List<VocationalQuestion> questions, List<Long> selectedIds) {
        if (questions.size() < 20 || questions.size() > 40)
            throw new ValidationException("El test debe tener entre 20 y 40 preguntas configuradas.");
        if (selectedIds == null || selectedIds.size() != questions.size() || selectedIds.stream().anyMatch(Objects::isNull)
                || new HashSet<>(selectedIds).size() != selectedIds.size())
            throw new ValidationException("Selecciona una alternativa diferente por cada pregunta.");
        Map<Long, VocationalOption> options = new HashMap<>();
        Map<String, Integer> maximums = new TreeMap<>();
        for (var question : questions) {
            if (question.getVocationalOptions().size() < 2)
                throw new ValidationException("Hay preguntas sin suficientes alternativas.");
            Map<String, Integer> questionMax = new HashMap<>();
            for (var option : question.getVocationalOptions()) {
                if (options.put(option.getId(), option) != null)
                    throw new ValidationException("Hay alternativas duplicadas en el test.");
                if (option.getCareerScore() == null || option.getCareerScore().isEmpty())
                    throw new ValidationException("Hay alternativas sin puntajes configurados.");
                option.getCareerScore().forEach((career, score) -> {
                    if (career == null || career.isBlank() || score == null || score < 0 || score > 100)
                        throw new ValidationException("Hay puntajes no válidos en el test.");
                    questionMax.merge(career, score, Math::max);
                });
            }
            questionMax.forEach((career, score) -> maximums.merge(career, score, Integer::sum));
        }
        if (maximums.isEmpty() || maximums.values().stream().anyMatch(value -> value <= 0))
            throw new ValidationException("Cada carrera debe tener un puntaje máximo positivo.");
        Set<Long> answeredQuestions = new HashSet<>();
        List<VocationalOption> selected = new ArrayList<>();
        Map<String, Integer> totals = new HashMap<>();
        for (Long id : selectedIds) {
            var option = options.get(id);
            if (option == null) throw new ValidationException("Una alternativa no pertenece al test actual. Actualiza la página.");
            if (!answeredQuestions.add(option.getQuestion().getId()))
                throw new ValidationException("Solo puedes seleccionar una alternativa por pregunta.");
            selected.add(option);
            option.getCareerScore().forEach((career, score) -> totals.merge(career, score, Integer::sum));
        }
        var ranking = maximums.entrySet().stream().map(entry -> {
            int score = totals.getOrDefault(entry.getKey(), 0);
            double percentage = Math.round(score * 10000.0 / entry.getValue()) / 100.0;
            return new CareerScore(entry.getKey(), score, entry.getValue(), percentage);
        }).sorted(Comparator.comparingDouble(CareerScore::percentage).reversed().thenComparing(CareerScore::career)).toList();
        String suggested = ranking.get(0).score() == 0 ? "Sin preferencia definida" : ranking.get(0).career();
        selected.sort(Comparator.comparing(option -> option.getQuestion().getId()));
        return new Evaluation(suggested, ranking, selected);
    }
}
