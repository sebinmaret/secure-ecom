package com.unbcs.ecom.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.owasp.encoder.Encode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unbcs.ecom.dto.FeedbackDTO;
import com.unbcs.ecom.dto.OperationResultStatus;
import com.unbcs.ecom.dto.OperationStatus;
import com.unbcs.ecom.misc.FeedbackConverter;
import com.unbcs.ecom.model.Feedback;
import com.unbcs.ecom.repository.FeedbackRepository;

@Service
public class FeedbackService {

	@Autowired
	FeedbackRepository feedbackRepository;

	@Autowired
	FeedbackConverter feedbackConverter;

	public OperationStatus createFeedback(@Validated FeedbackDTO feedbackDTO) {
		if (feedbackDTO != null) {
			Feedback newFeedback = new Feedback();
			newFeedback.setDescription(Encode.forJavaScript(Encode.forHtml(feedbackDTO.getDescription())));
			newFeedback.setName(Encode.forJavaScript(Encode.forHtml(feedbackDTO.getName())));
			newFeedback.setEmail(Encode.forJavaScript(Encode.forHtml(feedbackDTO.getEmail())));
			newFeedback.setId(UUID.randomUUID().toString());
			newFeedback.setDate(new Date().toString());

			feedbackRepository.save(newFeedback);
			return new OperationStatus(OperationResultStatus.SUCCESS);
		}
		return new OperationStatus("feedback is empty", OperationResultStatus.FAILED, 400);
	}

	public List<FeedbackDTO> getAllFeedback() {

		List<Feedback> feedbackList = feedbackRepository.findAll();
		return feedbackList.stream().map(f -> feedbackConverter.toFeedbackDTO(f)).toList();
	}

}
