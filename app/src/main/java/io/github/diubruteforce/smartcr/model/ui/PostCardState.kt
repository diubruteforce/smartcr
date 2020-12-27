package io.github.diubruteforce.smartcr.model.ui

import io.github.diubruteforce.smartcr.model.data.PostType

data class PostCardState(
    val type: PostType,
    val title: String,
    val firstRow: String,
    val dateTimeMillis: Long,
    val secondRow: String,
    val sectionId: String,
    val postId: String
)