-- MySQL DDL for Course, Section, and Lecture
-- We assume `user` and `category` tables exist for foreign key constraints.
CREATE DATABASE lxp_second default CHARACTER SET UTF8;

USE lxp_second;

SET NAMES utf8mb4;
SET time_zone = '+00:00';
SET foreign_key_checks = 0;

-- =================================================================
-- Table structure for `course`
-- =================================================================
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course` (
                          `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Course 고유 ID',
                          `instructor_id` BIGINT NOT NULL COMMENT '강사 ID (user.id)',
                          `category_id` BIGINT NOT NULL COMMENT '카테고리 ID (category.id)',
                          `title` VARCHAR(255) NOT NULL COMMENT '코스 제목',
                          `description` TEXT NULL COMMENT '코스 상세 설명',
                          `price` DECIMAL(19, 4) NOT NULL DEFAULT 0.0000 COMMENT '가격 (0원이면 무료)',
                          `status` ENUM('DELETED', 'DRAFT', 'PUBLISHED', 'ARCHIVED') NOT NULL DEFAULT 'DRAFT' COMMENT '코스 상태',
                          `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 일시',
                          `updated_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 일시',
                          `deleted_at` DATETIME(6) NULL COMMENT '소프트 삭제 일시',

                          PRIMARY KEY (`id`),
                          INDEX `idx_course_instructor_id` (`instructor_id`),
                          INDEX `idx_course_category_id` (`category_id`),
                          INDEX `idx_course_status` (`status`),

    -- Assuming 'user' table exists with 'id' as PK
                          CONSTRAINT `fk_course_to_user`
                              FOREIGN KEY (`instructor_id`)
                                  REFERENCES `user` (`id`)
                                  ON DELETE RESTRICT
                                  ON UPDATE CASCADE,

    -- Assuming 'category' table exists with 'id' as PK
                          CONSTRAINT `fk_course_to_category`
                              FOREIGN KEY (`category_id`)
                                  REFERENCES `category` (`id`)
                                  ON DELETE RESTRICT
                                  ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='코스 (강좌) 테이블';


-- =================================================================
-- Table structure for `section`
-- =================================================================
DROP TABLE IF EXISTS `section`;
CREATE TABLE `section` (
                           `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Section 고유 ID',
                           `course_id` BIGINT NOT NULL COMMENT '부모 코스 ID (course.id)',
                           `title` VARCHAR(255) NOT NULL COMMENT '섹션 제목',
                           `order` INT NOT NULL COMMENT '섹션 순서 (1, 2, 3...)',
                           `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 일시',
                           `updated_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 일시',
                           `deleted_at` DATETIME(6) NULL COMMENT '소프트 삭제 일시',

                           PRIMARY KEY (`id`),
                           INDEX `idx_section_course_id` (`course_id`),

                           CONSTRAINT `fk_section_to_course`
                               FOREIGN KEY (`course_id`)
                                   REFERENCES `course` (`id`)
                                   ON DELETE RESTRICT -- Application logic should handle cascading soft delete
                                   ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='코스 섹션 (챕터) 테이블';


-- =================================================================
-- Table structure for `lecture`
-- =================================================================
DROP TABLE IF EXISTS `lecture`;
CREATE TABLE `lecture` (
                           `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Lecture 고유 ID',
                           `section_id` BIGINT NOT NULL COMMENT '부모 섹션 ID (section.id)',
                           `title` VARCHAR(255) NOT NULL COMMENT '렉처 제목',
                           `description` TEXT NULL COMMENT '렉처 상세 설명',
                           `order` INT NOT NULL COMMENT '렉처 순서 (1, 2, 3...)',
                           `type` ENUM('VIDEO', 'DOCUMENT') NOT NULL COMMENT '렉처 유형',
                           `resource_path` TEXT NULL COMMENT '리소스 경로 (영상 URL, 파일 경로 등)',
                           `duration` INT NULL COMMENT '영상 길이 (초 단위)',
                           `is_previewable` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '미리보기 가능 여부 (무료 공개)',
                           `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 일시',
                           `updated_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 일시',
                           `deleted_at` DATETIME(6) NULL COMMENT '소프트 삭제 일시',

                           PRIMARY KEY (`id`),
                           INDEX `idx_lecture_section_id` (`section_id`),

                           CONSTRAINT `fk_lecture_to_section`
                               FOREIGN KEY (`section_id`)
                                   REFERENCES `section` (`id`)
                                   ON DELETE RESTRICT -- Application logic should handle cascading soft delete
                                   ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='개별 강의 (렉처) 테이블';

SET foreign_key_checks = 1;