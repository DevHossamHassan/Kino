package com.letsgotoperfection.kino.feature.notes.internal.domain.usecase

import androidx.compose.ui.text.AnnotatedString
import com.letsgotoperfection.kino.core.model.Label
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.Note
import com.letsgotoperfection.kino.feature.notes.internal.domain.repository.NotesRepository
import javax.inject.Inject

/**
 * Updates an existing note; null arguments leave the field unchanged.
 */
internal class UpdateNoteUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    suspend operator fun invoke(
        noteId: String,
        title: String? = null,
        content: AnnotatedString? = null,
        labels: List<Label>? = null
    ): Result<Note> = repository.updateNote(noteId, title, content, labels)
}
