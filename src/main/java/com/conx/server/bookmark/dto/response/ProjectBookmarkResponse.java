package com.conx.server.bookmark.dto.response;

public record ProjectBookmarkResponse(
        Long projectId,
        boolean bookmarked
) {
}