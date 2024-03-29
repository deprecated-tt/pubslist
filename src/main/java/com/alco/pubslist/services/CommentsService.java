package com.alco.pubslist.services;

import com.alco.pubslist.configuration.UserContext;
import com.alco.pubslist.entities.Comment;
import com.alco.pubslist.exceptions.BaseException;
import com.alco.pubslist.repository.CommentRepository;
import com.alco.pubslist.security.RestResponses;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;

@Component
public class CommentsService {

	@Autowired
	private CommentRepository repository;
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	public Comment addComment(Comment comment) {

		if (comment.getText() == null
				|| comment.getPlaceId() == null) {
			throw new BaseException(RestResponses.MISSING_REQUIRED_FIELD);
		}

		return repository.save(comment);
	}

	public Iterable<Comment> findAllCommentsOnPlace(Integer placeId) {

		return repository.findCommentsByPlaceId(placeId);
	}

	public void edit(BufferedReader reader, Integer id) {

		Comment comment = findCommentById(id);

		try {
			// Read and map JSON to entity from DB, merge in object as output
			Comment editedComment = OBJECT_MAPPER.readerForUpdating(comment).readValue(reader);

			// Only admin or user who owns this place can update in case
			// if the place has not approved yet
			if (!comment.isCommentWrittenByUser(UserContext.getUsername())) {
				throw new BaseException(RestResponses.ACCESS_DENIED);
			}

			// Required fields should be filled
			if (comment.getText() == null
					|| comment.getPlaceId() == null
					|| comment.getId() == null) {
				throw new BaseException(RestResponses.MISSING_REQUIRED_FIELD);
			}

			repository.save(editedComment);
		}
		catch (IOException e) {
			throw new BaseException(RestResponses.MALFORMED_JSON);
		}
	}

	public void delete(Integer id) {

		Comment comment = findCommentById(id);

		// Only admin or user who owns the comment can delete it
		if (!UserContext.isAdmin()
				&& !comment.isCommentWrittenByUser(UserContext.getUsername())) {
			throw new BaseException(RestResponses.ACCESS_DENIED);
		}

		repository.deleteById(id);
	}

	private Comment findCommentById(Integer id) {

		Optional<Comment> optionalPlace = repository.findById(id);

		return optionalPlace.orElseThrow(() -> new BaseException(RestResponses.NO_COMMENT_FOUND));
	}

}
