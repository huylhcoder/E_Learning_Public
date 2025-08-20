package com.fpoly.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "video_progress")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, creatorVisibility = JsonAutoDetect.Visibility.NONE)
public class VideoProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment
    @Column(name = "video_progress_id")
    private int videoProgressId;

    @Column(name = "users_id", nullable = false)
    private int userId;

    @Column(name = "registered_course_id", nullable = false)
    private int registeredCourseId;

    @Column(name = "lesson_id", nullable = false)
    private int lessonId;

    @Column(name = "path_video", length = 250, nullable = false)
    private String pathVideo;

    @Column(name = "update_at", nullable = false)
    private LocalDateTime update_at;

    @Column(name = "video_progress", nullable = false)
    private int videoProgress;

    @Override
    public String toString() {
        return "VideoProgress{" +
                "videoProgressId=" + videoProgressId +
                ", userId=" + userId +
                ", registeredCourseId=" + registeredCourseId +
                ", lessonId=" + lessonId +
                ", pathVideo='" + pathVideo + '\'' +
                ", update_at=" + update_at +
                ", videoProgress=" + videoProgress +                
                '}';
    }
}
