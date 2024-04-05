package com.unbcs.ecom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unbcs.ecom.model.Feedback;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, String> {

}
