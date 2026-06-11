package com.letsgotoperfection.kino.feature.notes.internal.domain.usecase

import androidx.compose.ui.text.AnnotatedString
import com.letsgotoperfection.kino.core.model.Label
import com.letsgotoperfection.kino.feature.notes.internal.domain.model.Note
import com.letsgotoperfection.kino.feature.notes.internal.domain.repository.NotesRepository
import javax.inject.Inject

/**
 * Creates a new note after validating its title.
 */
internal class CreateNoteUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    suspend operator fun invoke(
        title: String,
        content: AnnotatedString,
        labels: List<Label> = emptyList()
    ): Result<Note> {
        if (title.isBlank() && content.text.isBlank()) {
            return Result.failure(IllegalArgumentException("Note must have a title or content"))
        }
        return repository.createNote(title.trim(), content, labels)
    }
}
