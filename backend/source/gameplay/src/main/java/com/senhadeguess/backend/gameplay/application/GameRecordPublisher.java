package com.senhadeguess.backend.gameplay.application;

import com.senhadeguess.backend.gameplay.domain.GameRecord;

public interface GameRecordPublisher {
    void publish(GameRecord record);
}