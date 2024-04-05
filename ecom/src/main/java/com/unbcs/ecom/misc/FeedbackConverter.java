package com.unbcs.ecom.misc;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.unbcs.ecom.dto.FeedbackDTO;
import com.unbcs.ecom.model.Feedback;

@Service
public class FeedbackConverter {

	public Feedback toFeedback(FeedbackDTO feedbackDTO) {
		return new Feedback("", feedbackDTO.getDescription(), feedbackDTO.getEmail(), feedbackDTO.getName(),
				new Date().toString());
	}

	public FeedbackDTO toFeedbackDTO(Feedback feedback) {
		return new FeedbackDTO(feedback.getDescription(), feedback.getEmail(), feedback.getName(), feedback.getDate());
	}
}
