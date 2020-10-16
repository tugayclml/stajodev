package com.stajodev.Repository;

import com.stajodev.Models.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnouncementPagingAndSortingRepository extends PagingAndSortingRepository<Announcement, Long> {

    Page<Announcement> findAllByDepartment(String department, Pageable pageable);

}
