package com.letsgotoperfection.kino.core.common

/**
 * Generic result wrapper for operations that can fail
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    
    inline fun <R> fold(
        onSuccess: (T) -> R,
        onError: (Throwable) -> R
    ): R = when (this) {
        is Success -> onSuccess(data)
        is Error -> onError(exception)
    }
    
    inline fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
    }
    
    inline fun <R> flatMap(transform: (T) -> Result<R>): Result<R> = when (this) {
        is Success -> transform(data)
        is Error -> this
    }
}

/**
 * Extension function to create Result from runCatching
 */
inline fun <T> runCatching(block: () -> T): Result<T> = try {
    Result.Success(block())
} catch (e: Throwable) {
    Result.Error(e)
}

