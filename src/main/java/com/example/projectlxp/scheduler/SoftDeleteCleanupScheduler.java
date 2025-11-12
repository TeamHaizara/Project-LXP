package com.example.projectlxp.scheduler;

import com.example.projectlxp.model.course.Course;
import com.example.projectlxp.model.course.CourseStatus;
import com.example.projectlxp.model.lecture.Lecture;
import com.example.projectlxp.model.section.Section;
import com.example.projectlxp.repository.course.CourseRepository;
import com.example.projectlxp.repository.lecture.LectureRepository;
import com.example.projectlxp.repository.section.SectionRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SoftDeleteCleanupScheduler {

    private static final Logger log = LoggerFactory.getLogger(SoftDeleteCleanupScheduler.class);

    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;
    private final LectureRepository lectureRepository;

    public SoftDeleteCleanupScheduler(
            CourseRepository courseRepository,
            SectionRepository sectionRepository,
            LectureRepository lectureRepository
    ) {
        this.courseRepository = courseRepository;
        this.sectionRepository = sectionRepository;
        this.lectureRepository = lectureRepository;
    }

    @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
    @Transactional
    public void purgeSoftDeletedEntities() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(7);

        int deletedLectures = deleteLectures(threshold);
        int deletedSections = deleteSections(threshold);
        int deletedCourses = deleteCourses(threshold);

        if (log.isDebugEnabled()) {
            log.debug("Soft delete cleanup finished. lectures={}, sections={}, courses={}",
                    deletedLectures, deletedSections, deletedCourses);
        }
    }

    private int deleteLectures(LocalDateTime threshold) {
        List<Lecture> lectures = lectureRepository.findByStatusIsTrueAndDeletedAtBefore(threshold);
        if (lectures.isEmpty()) {
            return 0;
        }
        lectureRepository.deleteAll(lectures);
        return lectures.size();
    }

    private int deleteSections(LocalDateTime threshold) {
        List<Section> sections = sectionRepository.findByDeletedAtIsNotNullAndDeletedAtBefore(threshold);
        if (sections.isEmpty()) {
            return 0;
        }
        sectionRepository.deleteAll(sections);
        return sections.size();
    }

    private int deleteCourses(LocalDateTime threshold) {
        List<Course> courses = courseRepository.findByStatusAndDeletedAtBefore(CourseStatus.DELETED, threshold);
        if (courses.isEmpty()) {
            return 0;
        }
        courseRepository.deleteAll(courses);
        return courses.size();
    }
}
