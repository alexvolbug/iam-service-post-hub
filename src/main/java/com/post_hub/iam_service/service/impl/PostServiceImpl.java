package com.post_hub.iam_service.service.impl;

import com.post_hub.iam_service.mapper.PostMapper;
import com.post_hub.iam_service.model.constants.ApiErrorMessage;
import com.post_hub.iam_service.model.dto.post.PostDTO;
import com.post_hub.iam_service.model.dto.post.PostSearchDTO;
import com.post_hub.iam_service.model.entity.Post;
import com.post_hub.iam_service.model.entity.User;
import com.post_hub.iam_service.model.exception.DataExistException;
import com.post_hub.iam_service.model.exception.NotFoundException;
import com.post_hub.iam_service.model.request.post.NewPostRequest;
import com.post_hub.iam_service.model.request.post.PostSearchRequest;
import com.post_hub.iam_service.model.request.post.UpdatePostRequest;
import com.post_hub.iam_service.model.response.IamResponse;
import com.post_hub.iam_service.model.response.PaginationResponse;
import com.post_hub.iam_service.repository.PostRepository;
import com.post_hub.iam_service.repository.UserRepository;
import com.post_hub.iam_service.repository.criteria.PostSearchCriteria;
import com.post_hub.iam_service.security.validation.AccessValidator;
import com.post_hub.iam_service.service.PostService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;
    private final AccessValidator accessValidator;

    @Override
    public IamResponse<PostDTO> getById(@NotNull Integer postId) {
        Post post = postRepository.findByIdAndDeletedFalse(postId)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.POST_NOT_FOUND_BY_ID.getMessage(postId)));

        PostDTO postDto = postMapper.toPostDTO(post);

        return IamResponse.createSuccessful(postDto);
    }

    @Override
    public IamResponse<PostDTO> createPost(@NotNull NewPostRequest postRequest, String username) {
        if (postRepository.existsByTitle(postRequest.getTitle())) {
            throw new DataExistException(ApiErrorMessage.POST_ALREADY_EXISTS.getMessage(postRequest.getTitle()));
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.USERNAME_NOT_FOUND.getMessage(username)));

        Post post = postMapper.createPost(postRequest);
        post.setUser(user);
        post.setCreatedBy(username);
        post = postRepository.save(post);

        PostDTO postDto = postMapper.toPostDTO(post);
        return IamResponse.createSuccessful(postDto);
    }

    @Override
    public IamResponse<PostDTO> updatePost(@NotNull Integer postId, @NotNull UpdatePostRequest request) {
        Post post = postRepository.findByIdAndDeletedFalse(postId)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.POST_NOT_FOUND_BY_ID.getMessage(postId)));

        accessValidator.validateAdminOrOwnerAccess(post.getUser().getId());

        if (postRepository.existsByTitle(request.getTitle())) {
            throw new DataExistException(ApiErrorMessage.POST_ALREADY_EXISTS.getMessage(request.getTitle()));
        }

        postMapper.updatePost(post, request);
        post.setUpdated(LocalDateTime.now());
        post = postRepository.save(post);

        PostDTO postDto = postMapper.toPostDTO(post);
        return IamResponse.createSuccessful(postDto);

    }

    @Override
    public void softDeletePost(Integer postId) {
        Post post = postRepository.findByIdAndDeletedFalse(postId)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.POST_NOT_FOUND_BY_ID.getMessage(postId)));

        accessValidator.validateAdminOrOwnerAccess(post.getUser().getId());

        post.setDeleted(true);
        postRepository.save(post);
    }

    @Override
    public IamResponse<PaginationResponse<PostSearchDTO>> findAllPosts(Pageable pageable) {
        Page<PostSearchDTO> posts = postRepository.findAll(pageable)
                .map(postMapper::toPostSearchDTO);

        PaginationResponse<PostSearchDTO> paginationResponse = new PaginationResponse<>(
                posts.getContent(),
                new PaginationResponse.Pagination(
                        posts.getTotalElements(),
                        pageable.getPageSize(),
                        posts.getNumber() + 1,
                        posts.getTotalPages()
                )
        );

        return IamResponse.createSuccessful(paginationResponse);
    }

    @Override
    public IamResponse<PaginationResponse<PostSearchDTO>> searchPosts(PostSearchRequest request, Pageable pageable) {
        Specification<Post> specification = new PostSearchCriteria(request);

        Page<PostSearchDTO> postsPage = postRepository.findAll(specification, pageable)
                .map(postMapper::toPostSearchDTO);

        PaginationResponse<PostSearchDTO> response = PaginationResponse.<PostSearchDTO>builder()
                .content(postsPage.getContent())
                .pagination(PaginationResponse.Pagination.builder()
                        .total(postsPage.getTotalElements())
                        .limit(pageable.getPageSize())
                        .page(postsPage.getNumber() + 1)
                        .pages(postsPage.getTotalPages())
                        .build())
                .build();

        return IamResponse.createSuccessful(response);
    }

}
