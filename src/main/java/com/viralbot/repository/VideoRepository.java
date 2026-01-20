package com.viralbot.repository;

import com.viralbot.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findByChannel(String channel);
    List<Video> findByOrderByCreatedAtDesc();
}
