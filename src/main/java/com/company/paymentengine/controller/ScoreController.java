package com.company.paymentengine.controller;

import com.company.paymentengine.model.Score;
import com.company.paymentengine.repository.ScoreRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/scores")
@CrossOrigin(origins = "*")
public class ScoreController {

    private final ScoreRepository scoreRepository;

    public ScoreController(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    @GetMapping
    public ResponseEntity<List<Score>> getAllScores() {
        return ResponseEntity.ok(scoreRepository.findAll());
    }

    @GetMapping("/{userAId}/{userBId}")
    public ResponseEntity<Score> getScore(@PathVariable UUID userAId, @PathVariable UUID userBId) {
        Optional<Score> scoreOpt = scoreRepository.findByUserAIdAndUserBId(userAId, userBId)
                .or(() -> scoreRepository.findByUserAIdAndUserBId(userBId, userAId));
        return scoreOpt.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
